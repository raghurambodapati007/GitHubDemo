package com.appsdeveloperblog.app.ws.io.repository;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.appsdeveloperblog.app.ws.io.entiry.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entiry.UserEntity;
import com.appsdeveloperblog.app.ws.repository.UserRepository;


//below 2 annotations for integration test
@ExtendWith(SpringExtension.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UserRepositortyTest {

	@Autowired
	UserRepository userRepository;
	
	private static boolean recordsCreated = false;
	
	@BeforeEach
	void setUp() throws Exception {
		
				if(!recordsCreated) {
					createRecords();
				}
	}

	/* testGetVerifiedUsers */
	
	@Test
	final void a() {
		Pageable pageableRequest = PageRequest.of(0,2);
		Page<UserEntity> page = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
		assertNotNull(page);
        List<UserEntity> userEntities = page.getContent();
        assertNotNull(userEntities);
        assertTrue(userEntities.size() == 2);
	}
	
	@Test
	final void testFindUserByFirstName() {
		
		
		String firstName="raghu ram";
		String lastName="bodapati";
		List<UserEntity> userEntities=userRepository.findByFirstName(firstName,lastName);
		
		assertNotNull(userEntities);
		
		String returnValue=userEntities.get(0).getFirstName();
		
		System.out.println("rethrun value is "+returnValue);
		
		assertTrue(firstName==returnValue);

	}
	
	
	
	@Test
	final void testFindUserByLastName() {
		
		String lastName="bodapati";
		List<UserEntity> users=userRepository.findUserByLastName(lastName);
		
		assertTrue(users.get(1).getLastName()==lastName);
		
	}
	
	@Test
	final void testFindUserByKeyword() {
		
		String keyword="od";
		List<UserEntity> users=userRepository.findUserByKeyword(keyword);
		
		assertNotNull(users);
		assertTrue(users.size() ==2 );
		
		UserEntity user=users.get(0);
		assertTrue(user.getLastName().contains(keyword));		
		
	}
	
	@Test
	final void testfindUserFirstNameAndLastNameByKeyword() {
		
		String keyword="od";
		List<Object[]> users=userRepository.findUserFirstNameAndLastNameByKeyword(keyword);
		assertNotNull(users);
		assertTrue(users.size() ==2 );
		
		Object[] user=users.get(0);
		
		String userFirstName=user[0].toString();
		String userLastName=String.valueOf(user[1]);
		System.out.println("user first name is "+userFirstName);
		
		assertNotNull(userFirstName);
		assertNotNull(userLastName);
		
	}
	
	@Test
	final void testupdateUserEmailVerificationStatus() {
		
		boolean newEmailVerificationStatus=false;
		
		userRepository.updateUserEmailVerificationStatus(newEmailVerificationStatus, "12003264");
		
		UserEntity userEntity=userRepository.findByUserId("12003264");
		
		boolean storedEmailVerificationStatus=userEntity.getEmailVerificationStatus();
		
		System.out.println("the email verification status is "+storedEmailVerificationStatus);
		
		assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
		
	}
	
	@Test
	final void testfindUserEntityByUserId() {
		
		String userId="12003264";
		
		UserEntity userEntity=userRepository.findUserEntityByUserId(userId);
		
		assertNotNull(userEntity);
		
		String userName=userEntity.getFirstName();
		
		assertTrue(userName=="raghu ram");
		
	}
	
	@Test
	final void testGetUserEntityFullNameById() {
		
		List<Object[]> users=userRepository.getUserEntityFullNameById("12003264");
		
		assertNotNull(users);
		Object[] user=users.get(0);
		String firstName = user[0].toString();
		assertTrue(firstName=="raghu ram");
	}
	
	
	  @Test final void testupdateUserEntityEmailVerificationStatusById() {
	  
	  userRepository.updateUserEntityEmailVerificationStatusById(false,"12003264");
	  
	  UserEntity userEntity=userRepository.findByUserId("12003264");
	  
	  assertNotNull(userEntity);
	  
	  boolean status=userEntity.getEmailVerificationStatus();
	  
	  assertTrue(!status);
	  
	  
	  
	   }
	 
	
	private final void createRecords() {
		
		
		  UserEntity userEntity=new UserEntity(); userEntity.setFirstName("raghu ram");
		  userEntity.setLastName("bodapati");
		  userEntity.setUserId("12003264");
		  userEntity.setEmail("raghurambodapati@gmail.com");
		  userEntity.setEncryptedPassword("diohadigadsgi");
		  userEntity.setEmailVerificationStatus(true);
		  
		  AddressEntity addressEntity=new AddressEntity();
		  addressEntity.setAddressId("diuieghi2gwzuYSD");
		  addressEntity.setCity("Tenali"); addressEntity.setCountry("India");
		  addressEntity.setPostalCode("123456");
		  addressEntity.setStreetName("diAIaadbs"); addressEntity.setType("Shipping");
		 
			
		UserEntity userEntity2=new UserEntity();
		userEntity2.setFirstName("raghu ram");
		userEntity2.setLastName("bodapati");
		userEntity2.setUserId("740675");
		userEntity2.setEmail("raghurambodapati007@gmail.com");
		userEntity2.setEncryptedPassword("diohadigadsgi");
		userEntity2.setEmailVerificationStatus(true);
		
		AddressEntity addressEntity2=new AddressEntity();
		addressEntity2.setAddressId("diuieghi2gwzuYSD");
		addressEntity2.setCity("Tenali");
		addressEntity2.setCountry("India");
		addressEntity2.setPostalCode("123456");
		addressEntity2.setStreetName("diAIaadbs");
		addressEntity2.setType("Shipping");
		
				
		List<AddressEntity> addresses=new ArrayList<>();
		
		addresses.add(addressEntity);
		addresses.add(addressEntity2);
		
		userEntity.setAddresses(addresses);
		
		userRepository.save(userEntity);
		
		userRepository.save(userEntity2);
		
		recordsCreated=true;
	}
}
