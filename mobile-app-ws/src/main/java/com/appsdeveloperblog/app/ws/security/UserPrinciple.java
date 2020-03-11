package com.appsdeveloperblog.app.ws.security;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.appsdeveloperblog.app.ws.io.entiry.AuthorityEntity;
import com.appsdeveloperblog.app.ws.io.entiry.RoleEntity;
import com.appsdeveloperblog.app.ws.io.entiry.UserEntity;

public class UserPrinciple implements UserDetails {


	private static final long serialVersionUID = -8886792112824532747L;
	
	private UserEntity userEntity;
	private String userId;


	public UserPrinciple(UserEntity userEntity) {
		System.out.println("10 inside userPrinciple");
		this.userEntity=userEntity;
		this.userId=userEntity.getUserId();
	}
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		Collection<GrantedAuthority> authorities=new HashSet<>();
		Collection<AuthorityEntity> authorityEntities=new HashSet<>();
		
		//Get user roles
		
		Collection<RoleEntity> roles=userEntity.getRoles();
		if(roles==null) return authorities;
		
		roles.forEach((role)->{
			authorities.add(new SimpleGrantedAuthority(role.getName()));
			authorityEntities.addAll(role.getAuthorities());
			
		});
		
		authorityEntities.forEach((authorityEntity) -> {
			authorities.add(new SimpleGrantedAuthority(authorityEntity.getName()));
		});
		
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.userEntity.getEncryptedPassword();
	}

	@Override
	public String getUsername() {
		return this.userEntity.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.userEntity.getEmailVerificationStatus();
	}

	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}

	

}
