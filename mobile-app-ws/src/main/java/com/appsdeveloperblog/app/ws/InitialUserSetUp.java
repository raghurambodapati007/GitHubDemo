package com.appsdeveloperblog.app.ws;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.appsdeveloperblog.app.ws.io.entiry.AuthorityEntity;
import com.appsdeveloperblog.app.ws.io.entiry.RoleEntity;
import com.appsdeveloperblog.app.ws.io.entiry.UserEntity;
import com.appsdeveloperblog.app.ws.repository.AuthorityRepository;
import com.appsdeveloperblog.app.ws.repository.RoleRepository;
import com.appsdeveloperblog.app.ws.repository.UserRepository;
import com.appsdeveloperblog.app.ws.shared.dto.Roles;
import com.appsdeveloperblog.app.ws.shared.dto.Utils;



@Component
public class InitialUserSetUp {

	@Autowired
	AuthorityRepository authorityRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	UserRepository userRepository;
	
	@Transactional
	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent event) {
		
		System.out.println("From application ready event");
		
		AuthorityEntity readAuthority=createAuthority("READ_AUTHORITY");
		AuthorityEntity writeAuthority=createAuthority("WRITE_AUTHORITY");
		AuthorityEntity deleteAuthority=createAuthority("DELETE_AUTHORITY");
		
		createRole(Roles.ROLE_USER.name(),Arrays.asList(readAuthority,writeAuthority));
		RoleEntity roleAdmin=createRole(Roles.ROLE_ADMIN.name(),Arrays.asList(readAuthority,writeAuthority,deleteAuthority));
		
		if(roleAdmin ==null) 
			return ;
		
		UserEntity  adminUser =new UserEntity();
		adminUser.setFirstName("admin");
		adminUser.setLastName("admin");
		adminUser.setEmailVerificationStatus(true);
		adminUser.setEmail("admin@test.com");
		adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("123@"));
		adminUser.setUserId(utils.generateUserId(30));
		adminUser.setRoles(Arrays.asList(roleAdmin));
		
		userRepository.save(adminUser);
	}
	
	@Transactional
	private AuthorityEntity createAuthority(String name) {
		
		AuthorityEntity authority=authorityRepository.findByName(name);
		
		if(authority==null) {
			authority=new AuthorityEntity(name);
			authorityRepository.save(authority);
		}
		
		return authority;
	}
	
	@Transactional
	private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
		
		RoleEntity role=roleRepository.findByName(name);
		
		if(role==null) {
			role=new RoleEntity(name);
			role.setAuthorities(authorities);
			roleRepository.save(role);
		}
		return role;
	}
	
	
}
