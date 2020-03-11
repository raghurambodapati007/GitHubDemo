package com.appsdeveloperblog.app.ws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.appsdeveloperblog.app.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {

	UserDto createUser(UserDto user);
	boolean requestPasswordReset(String email);
	boolean resetPassword(String token,String password);
	UserDto getUser(String email);
	UserDto getUserById(String id);
	UserDto updateUser(String updateUserId,UserDto user);
	void deleteUser(String deleteUserId);
	List<UserDto> getUsers(int page, int limit);
	boolean	verifyEmailToken(String token);

}
