package com.appsdeveloperblog.app.ws.repository;

import java.util.List;



import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.appsdeveloperblog.app.ws.io.entiry.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entiry.UserEntity;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long>{

	
	List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
	AddressEntity findByAddressId(String addrId);
	
}
