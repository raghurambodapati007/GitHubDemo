package com.appsdeveloperblog.app.ws.ui.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
/*import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;*/
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.app.ws.service.AddressesService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.Roles;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordResetModel;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordResetRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.AddressesRest;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationEnum;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationName;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/users")

//Allow hits from mentioned URL to all the methods of the Class
//@CrossOrigin(origins= {"http://localhost:8080","http://localhost:8081"})

public class UserController {

	@Autowired
	private UserService UserService;

	@Autowired
	AddressesService addressesService;

	
	  //GET user data based on ID passed
	  
	  @PostAuthorize("hasRole('ADMIN') or returnObject.userId == principal.userId")
	  
	  @ApiOperation(value="The get user details web service end point",
	  notes="${userController.GetUser.ApiOperationStatus.Notes}")
	  
	  @GetMapping(path="/{Userid}",produces =
	  {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE}) 
	  public UserRest getUsers(@PathVariable String Userid) {
	  
	  
		  UserRest userRest =new UserRest(); UserDto
		  userDto=UserService.getUserById(Userid);
	  
		  ModelMapper modelMapper=new ModelMapper();
		  userRest=modelMapper.map(userDto,UserRest.class);
		  //BeanUtils.copyProperties(userDto,userRest);
		  return userRest; 
	  
	  }
	 

	@PostMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
			     consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

		UserRest returnValue = new UserRest();

		if (userDetails.getFirstName().isEmpty())
			throw new NullPointerException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessages());

		// UserDto userDto =new UserDto();
		// BeanUtils.copyProperties(userDetails, userDto);

		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));

		UserDto createdUser = UserService.createUser(userDto);
		// BeanUtils.copyProperties(createdUser, returnValue);
		returnValue = modelMapper.map(createdUser, UserRest.class);
		return returnValue;
	}

	
	  @PutMapping(path="/{updateUserId}", produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE} 
	  			  ,consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE} ) 
	  public UserRest updateUser(@PathVariable String updateUserId, @RequestBody UserDetailsRequestModel userDetails) {
	  
		  UserRest returnValue=new UserRest(); 
		  UserDto userDto =new UserDto(); 
		  userDto = new ModelMapper().map(userDetails, UserDto.class);
		  UserDto updateUser=UserService.updateUser(updateUserId,userDto); 
		  returnValue = new ModelMapper().map(updateUser, UserRest.class);
	  
	  return returnValue;
	  
	  }
	  
	  //@Secured("ROLE_ADMIN") //@PreAuthorize("hasAuthority('DELETE_AUTHORITY')")
	  //principal comes from UserNamePasswordToken() object from authorization  class check there.
	  
	  @PreAuthorize(" hasRole('ROLE_ADMIN') or #deleteUserId==principal.userId ")
	  @DeleteMapping(path="/{deleteUserId}", produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE} ) 
	  public OperationStatusModel deleteUser(@PathVariable String deleteUserId) {
	  
		  OperationStatusModel returnValue=new OperationStatusModel();
		  UserService.deleteUser(deleteUserId);
		  returnValue.setOperationName(RequestOperationEnum.DELETE.name());
		  returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
	  
	  return returnValue; 
	  }
	  
	  
	  @ApiOperation(value="The get user details web service end point",notes="${userController.GetUser.ApiOperationStatus.Notes}")
	  
	  @ApiImplicitParams({ @ApiImplicitParam(name="authorization",value="${userController.authorizationHeader.description}",paramType="header")
	  })
	  
	  @GetMapping(produces ={MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE}) 
	  public List<UserRest> getUsers(@RequestParam(value="page",defaultValue="0") int page, @RequestParam(value="limit" , defaultValue="25") int limit){
	  
		  List<UserRest> returnValue=new ArrayList<>(); List<UserDto>
		  users=UserService.getUsers(page,limit);
		  
		  Type listType=new TypeToken<List<UserRest>>(){ }.getType();
	      returnValue=new ModelMapper().map(users, listType);
	  
	      for(UserDto userDto: users) { 
	    	  UserRest userModel=new UserRest();
	    	  BeanUtils.copyProperties(userDto,userModel); 
	    	  returnValue.add(userModel);
	    	  }
	  
	  
	  return returnValue;
	  }
	  
	  
	  
	@GetMapping(path="/{id}/addresses", produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE, "application/hal+json"} ,consumes =
	  {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE} ) 
	//public   List<AddressesRest> getUserAddresses(@PathVariable String id){ Following HAL(Hyper Text Application Model format then we must change single object into CollectionModel in hateoas
	  
	//public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String  id){ //the above is used to send response in list form to JSON as per springboot 2.2.2

	public Resources<AddressesRest> getUserAddresses(@PathVariable String id){
	  
	  List<AddressesRest> addressesListRestModel=new ArrayList<>();
	  
	  List<AddressDTO> addressesDto =addressesService.getAddresses(id);
	  
	  if(addressesDto!=null && !addressesDto.isEmpty()) 
	  {
	  
		  Type listType=new TypeToken<List<AddressesRest>>() {}.getType(); ModelMapper
		  modelMapper=new ModelMapper();
	  
		  addressesListRestModel=modelMapper.map(addressesDto,listType);
	  
		  for(AddressesRest addressesRest : addressesListRestModel) { 
			  Link  addressLink=linkTo(methodOn(UserController.class).getUserAddresses(id)).withSelfRel(); addressesRest.add(addressLink); 
			  Link userLink =linkTo(methodOn(UserController.class).getUsers(id)).withRel("users");
			  addressesRest.add(userLink);
	  
		  }
	  } ///return addressesListRestModel; //return new CollectionModel<>(addressesListRestModel); 
	  return new Resources<>(addressesListRestModel);
	  }

	@GetMapping(path="/{userId}/addresses/{addrId}", produces =
	  {MediaType.APPLICATION_JSON_VALUE,"application/hal+json"})
	  
	  //public AddressesRest getUserAddress(@PathVariable String userId,@PathVariable String addrId) 	{ Following HAL format then we must change single object into EntityModel in hateoas
	  
	  //public EntityModel<AddressesRest> getUserAddress(@PathVariable String  userId,@PathVariable String addrId) { //the above is used to send response in list form to JSON as per spring boot 2.2.2
	  
	  
	  public Resource<AddressesRest> getUserAddress(@PathVariable String userId,@PathVariable String addrId) {
	  AddressDTO addressDTO=addressesService.getAddress(addrId); ModelMapper
	  modelMapper = new ModelMapper(); 
	  Link addressLink =linkTo(UserController.class).slash(userId).slash("addresses").slash(addrId).
	  withSelfRel(); 
	 // Link addressLink =linkTo(methodOn(UserController.class).getUserAddress(userId,addrId)). withSelfRel(); 
	  Link userLink =linkTo(UserController.class).slash(userId).withRel("user"); 
	  Link  addressesLink	  =linkTo(UserController.class).slash(userId).slash("addresses").withRel("addresses"); 
	  //Link addressesLink =linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses"); 
	  AddressesRest	  returnValue=modelMapper.map(addressDTO,AddressesRest.class);
	  
	  returnValue.add(addressLink); returnValue.add(userLink);
	  returnValue.add(addressesLink); //return returnValue; //return new
	 // EntityModel<>(returnValue);
	  
	  return new Resource<>(returnValue); }

	@GetMapping(path = "/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }) 
	// Below allows multiple CORS req to this web end url.This is for One method

	// @CrossOrigin(origins= {"http://localhost:8080","http://localhost:8081"})
	public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

		OperationStatusModel returnValue = new OperationStatusModel();

		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

		boolean isVerified = UserService.verifyEmailToken(token);

		if (isVerified == true) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}

		else {
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		return returnValue;

	}

	// http://localhost:8080/mobile-app-ws/users/password-reset-request

	@PostMapping(path = "/password-reset-request", produces = { MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE },
			      consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel getReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {

		System.out.println("In User Controller password-reset-request ");

		OperationStatusModel returnValue = new OperationStatusModel();

		boolean operationResult = UserService.requestPasswordReset(passwordResetRequestModel.getEmail());

		returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}

		return returnValue;
	}

	@PostMapping(path = "/password-reset", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {

		OperationStatusModel returnValue = new OperationStatusModel();

		boolean operationResult = UserService.resetPassword(passwordResetModel.getToken(),
				passwordResetModel.getPassword());

		returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}

		return returnValue;

	}

}
