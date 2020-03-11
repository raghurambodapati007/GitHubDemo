package com.appsdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.app.ws.io.entiry.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entiry.UserEntity;
import com.appsdeveloperblog.app.ws.repository.AddressRepository;
import com.appsdeveloperblog.app.ws.repository.UserRepository;
import com.appsdeveloperblog.app.ws.service.AddressesService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
@Service
public class AddressesServiceImpl implements AddressesService{

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;
	
	@Override
	public List<AddressDTO> getAddresses(String userId) {
		
		List<AddressDTO>  returnValue=new ArrayList<>();
		ModelMapper modelMapper=new ModelMapper();
					
		UserEntity userEntity =userRepository.findByUserId(userId);
		if(userEntity == null ) return returnValue;
		
		Iterable<AddressEntity> addresses =addressRepository.findAllByUserDetails(userEntity);
		for(AddressEntity addressEntity:addresses)
		{
			returnValue.add(modelMapper.map(addressEntity,AddressDTO.class));
		}
		return returnValue; 
		
	}

	@Override
	public AddressDTO getAddress(String addrId) {
		AddressDTO addressDTO =null;
		
		AddressEntity addressEntity =addressRepository.findByAddressId(addrId);
		
		if(addressEntity!=null) {
			
			addressDTO=new ModelMapper().map(addressEntity, AddressDTO.class);
			
		}
				
		return addressDTO;
	}

}
