package com.appsdeveloperblog.app.ws.service.Impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entiry.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entiry.UserEntity;
import com.appsdeveloperblog.app.ws.repository.UserRepository;
import com.appsdeveloperblog.app.ws.service.impl.UserServiceImpl;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.AmazonSES;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.shared.dto.Utils;

class UserServiceImplTest {
	
	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	Utils utils;
	
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	AmazonSES amazonSES;

	String userId="12kjabsdikjshdg";
	String encryptedPassword="hjsahiasgdliuagsduy";
	
	UserEntity userEntity;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		userEntity=new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("RaghuRam");
		userEntity.setUserId(userId);
		userEntity.setLastName("Bodapati");
		userEntity.setEncryptedPassword(encryptedPassword);
		userEntity.setEmailVerificationToken("hsihdaighdauxgdjsagfdiatdyaxsfdhsaDSYXA8DXGIUEH3I2U63987WYHEXJSDHFI");
		userEntity.setEmail("raghurambodapati007@gmail.com");
		userEntity.setAddresses(getAddressEntity());
	}

	@Test
	final void testGetUser() {
		
		//fail("Not yet implemented");
		
		//UserEntity userEntity =new UserEntity();
				
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		UserDto userDto=userService.getUser("raghurambodapati007@gmail.com");
		
		assertNotNull(userDto);
		assertEquals("RaghuRam",userDto.getFirstname());
		
	}
	
	@Test
	final void testGetUser_UsernameNotFoundException()
	{
		when(userRepository.findByEmail(anyString())).thenReturn(null);
	
		assertThrows(UsernameNotFoundException.class,
				()-> {
					userService.getUser("raghurambodapati007@gmail.com");
				}
				
				);
	}
	
	@Test
	final void testCreateUser()
	{
		
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("JDHDSDH12QIhhih");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class));
						
		UserDto userDto=new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setEmail("raghurambodapati007@gmail.com");
		userDto.setFirstname("RaghuRam");
		userDto.setLastName("Bodapati");
		userDto.setPassword("12345678");
		
		UserDto storedUserDetails=userService.createUser(userDto);
		assertNotNull(storedUserDetails);
		assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstname());
		assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
		assertNotNull(storedUserDetails.getUserId());
		assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
		
		//verify for passing mockito obj and number of times exactly that this obj must be called and method ion that object.
		verify(utils,times(storedUserDetails.getAddresses().size())).generateAddressId(30);
		verify(bCryptPasswordEncoder,times(1)).encode("12345678");
		verify(userRepository,times(1)).save(any(UserEntity.class));
		
	}
	
	
	@Test
	final void testCreateUser_ServiceExcepion() {
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto=new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setEmail("raghurambodapati007@gmail.com");
		userDto.setFirstname("RaghuRam");
		userDto.setLastName("Bodapati");
		userDto.setPassword("12345678");
		
		assertThrows(UserServiceException.class,()->{userService.createUser(userDto);});
	}
	
	
	
	private List<AddressDTO> getAddressesDto()  
	{
		AddressDTO addressDTO=new AddressDTO();
		addressDTO.setCity("Hyderabad");
		addressDTO.setCountry("India");
		addressDTO.setPostalCode("522202");
		addressDTO.setStreetName("Morriespet");
		addressDTO.setType("Shipping");
		
		AddressDTO billingaddressDTO=new AddressDTO();
		billingaddressDTO.setCity("Hyderabad");
		billingaddressDTO.setCountry("India");
		billingaddressDTO.setPostalCode("522202");
		billingaddressDTO.setStreetName("Morriespet");
		billingaddressDTO.setType("billing");
		
		List<AddressDTO> addresses=new ArrayList<>();
		addresses.add(addressDTO);
		addresses.add(billingaddressDTO);
		return addresses;
	}
	
	private List<AddressEntity> getAddressEntity()
	{
		List<AddressDTO> addresses=getAddressesDto();
		Type listType =new TypeToken<List<AddressEntity>>() {}.getType();
		return new ModelMapper().map(addresses,listType);
	}

}
