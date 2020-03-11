package com.appsdeveloperblog.app.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.*;

import com.appsdeveloperblog.app.ws.service.impl.UserServiceImpl;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;

class UserControllerTest {

	@Mock
	UserServiceImpl userService;
	
	@InjectMocks
	UserController userController;
	
	UserDto userDto;
	
	final String userId="jdahdsagd";
	
	
	@BeforeEach
	void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
		userDto=new UserDto();
		
		userDto.setFirstname("RaghuRam");
		userDto.setLastName("Bodapati");
		userDto.setEmail("raghurambodapati007@gmail.com");
		userDto.setEmailVerificationToken(null);
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setUserId(userId);
		userDto.setAddresses(getAddressesDto());
		userDto.setEncryptedPassword("hsaiughdiq213");
	}
	

	@Test
	final void testGetUsersString() {
		
		when(userService.getUserById(anyString())).thenReturn(userDto);
		
		UserRest userRest=userController.getUsers(userId);
				
		assertNotNull(userRest);
		assertEquals(userId, userRest.getUserId());
		assertEquals(userDto.getFirstname(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertTrue(userDto.getAddresses().size()==userRest.getAddresses().size());	
		
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
	
}
