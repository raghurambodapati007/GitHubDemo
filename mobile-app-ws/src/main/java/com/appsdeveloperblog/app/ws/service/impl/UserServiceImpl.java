package com.appsdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entiry.PasswordResetTokenEntity;
import com.appsdeveloperblog.app.ws.io.entiry.RoleEntity;
import com.appsdeveloperblog.app.ws.io.entiry.UserEntity;
import com.appsdeveloperblog.app.ws.repository.PasswordResetTokenRepository;
import com.appsdeveloperblog.app.ws.repository.RoleRepository;
import com.appsdeveloperblog.app.ws.repository.UserRepository;
import com.appsdeveloperblog.app.ws.security.SecurityConstants;
import com.appsdeveloperblog.app.ws.security.UserPrinciple;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.AmazonSES;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.shared.dto.Utils;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;

import io.jsonwebtoken.Jwts;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	AmazonSES amazonSES;
	
	@Autowired
	RoleRepository roleRepository;

	@Override
	public UserDto createUser(UserDto user) {

		if (userRepository.findByEmail(user.getEmail()) != null)
			throw new UserServiceException("Record already exists ");

		//UserEntity userEntity = new UserEntity();
		//BeanUtils.copyProperties(user, userEntity);
		
		for(int i=0;i<user.getAddresses().size();i++) {
			AddressDTO address=user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i,address);
		}
		
		
		ModelMapper modelMapper=new ModelMapper();
		UserEntity userEntity=modelMapper.map(user,UserEntity.class);
		
		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);
		
		//setting roles
		Collection<RoleEntity> roleEntities =new HashSet<>();
		
		for(String role : user.getRoles()) {
			RoleEntity roleEntity=roleRepository.findByName(role);
			if(roleEntity != null) {
				roleEntities.add(roleEntity);
			}
		}
		
		userEntity.setRoles(roleEntities);
		
		UserEntity storedUserDetails = userRepository.save(userEntity);
		
		//UserDto returnValue = new UserDto();
		//BeanUtils.copyProperties(storedUserDetails, returnValue);
		
		UserDto returnValue=modelMapper.map(storedUserDetails, UserDto.class);
		//amazonSES.verifyEmail(returnValue); 
		return returnValue;

	}

	@Override
	public UserDto getUser(String email) {
		
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if (userEntity == null)
			//throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.toString());
			throw new UsernameNotFoundException(email);
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;

	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		System.out.println("Inside LoadByuserClass service");

		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);
		
		return new UserPrinciple(userEntity);
		
		/*return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(),userEntity.getEmailVerificationStatus(),
				true,true,true,new ArrayList<>());*/
		
		
		//return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	//Responsible for checking Id in db and returning UserDto obect 
	@Override
	public UserDto getUserById(String userId) {

		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null) throw new UsernameNotFoundException(userId);
		BeanUtils.copyProperties(userEntity,returnValue);
		return returnValue;
	}

	@Override
	public UserDto updateUser(String updateUserId, UserDto user) {
		
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(updateUserId);
		
		if(userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessages());
		
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		
		UserEntity updateUserDetails=userRepository.save(userEntity);
		
		BeanUtils.copyProperties(updateUserDetails, returnValue);		
		return returnValue;
	}

	@Override
	public void deleteUser(String deleteUserId) {
		
		UserEntity userEntity = userRepository.findByUserId(deleteUserId);
		if(userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.toString());
		userRepository.delete(userEntity);
				
	}

	
	public List<UserDto> getUsers(int page, int limit) {
		
		if(page >0) {
			page=page-1;
		}
		
		List<UserDto> returnValue=new ArrayList<UserDto>();
		Pageable pageableRequest=PageRequest.of(page, limit);
		Page<UserEntity> usersPage =userRepository.findAll(pageableRequest);
		List<UserEntity> users=usersPage.getContent();
		
		for(UserEntity userEntity : users) {
			UserDto userDto=new UserDto();
			BeanUtils.copyProperties(userEntity,userDto);
			returnValue.add(userDto);
		}			
		return returnValue;
	}

	@Override
	public boolean verifyEmailToken(String token) {
		
		boolean returnValue=false;
		
		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
				
		if(userEntity!=null)
		{
			boolean hasTokenExpired=Utils.hasTokenExpired(token);
			if(!hasTokenExpired)
			{
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userEntity.setEmailVerificationToken(null);
				userRepository.save(userEntity);
				returnValue=true;
			}
		}
		
		return returnValue;
	}

	@Override
	public boolean requestPasswordReset(String email) {
		
		boolean returnValue=false;
		
		UserEntity userEntity=userRepository.findByEmail(email);
		
		if(userEntity == null) {
			return returnValue;
		}
		 
		String token=utils.generatePasswordResetToken(userEntity.getUserId());	
		PasswordResetTokenEntity passwordResetTokenEntity=new PasswordResetTokenEntity();
		
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(userEntity);
		
		passwordResetTokenRepository.save(passwordResetTokenEntity);
		
		returnValue=new AmazonSES().sendPasswordResetRequest(
				userEntity.getFirstName(),
				userEntity.getEmail(),
				token);
		
		return returnValue;
	}
	

	@Override
	public boolean resetPassword(String token, String password) {
		
		boolean returnValue=false;
		
		if(utils.hasTokenExpired(token)) {
			return returnValue;
		}
		
		PasswordResetTokenEntity passwordResetTokenEntity=passwordResetTokenRepository.findByToken(token);
		
		if(passwordResetTokenEntity==null) {
			return returnValue;
		}
		
		String encodedPassword=bCryptPasswordEncoder.encode(password);
		UserEntity userEntity=passwordResetTokenEntity.getUserDetails();
		userEntity.setEncryptedPassword(encodedPassword);
		UserEntity savedUserEntity=userRepository.save(userEntity);
		
		if(savedUserEntity !=null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) {
			returnValue=true;
		}
		
		passwordResetTokenRepository.delete(passwordResetTokenEntity);
		
		return returnValue;
	}

	

}


