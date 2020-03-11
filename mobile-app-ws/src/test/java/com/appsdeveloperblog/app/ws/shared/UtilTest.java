package com.appsdeveloperblog.app.ws.shared;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.appsdeveloperblog.app.ws.shared.dto.Utils;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilTest {
	
	@Autowired
	Utils utils;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateUserId() {
		
		String userId=utils.generateUserId(30);
		String userId2=utils.generateUserId(30);
				
		assertNotNull(userId);
		assertNotNull(userId2);
		
		assertTrue(userId.length()==30);
		assertTrue(!userId.equalsIgnoreCase(userId2));
		
	}

	@Test
	// used in casee if any block not to be executed @Disabled
	void testHasTokenExpired() {
		
		//we have hard coded token value in here so using another way to check method 
		//String token="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBN2VmNFZabU1wWjRxZmFLcHNCUk1LNXJUZ05hU1UiLCJleHAiOjE1ODEwNzU4MDN9.bYnWTC9jydZwVMyYc1bu5PllYdmPFhEGRNR7ZyDlBhU";
		
		String token=utils.generateEmailVerificationToken("dhaihdioausyWIHWE_Q231");
		assertNotNull(token);
		boolean hasTokenExpired=utils.hasTokenExpired(token);
		assertFalse(hasTokenExpired);	
	}

	@Test
	final void testHasExpired() {
		String token="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBN2VmNFZabUf1wWRxZmFLcHNCUk1LNXJUZ05hU1UiLCJleHAiOjE1ODEwNzU4MDN9.bYnWTC9jydZwVMyYc1bu5PllYdmPFhEGRNR7ZyDlBhU";
		boolean hasTokenExpired=utils.hasTokenExpired(token);
		assertTrue(hasTokenExpired);
				
	}
}
