package com.appsdeveloperblog.app.ws.service;

import java.util.List;

import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;

public interface AddressesService {

	List<AddressDTO> getAddresses(String userId); 
	AddressDTO getAddress(String addrId);
	
}
