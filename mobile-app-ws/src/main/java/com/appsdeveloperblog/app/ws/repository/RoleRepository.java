package com.appsdeveloperblog.app.ws.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.appsdeveloperblog.app.ws.io.entiry.RoleEntity;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity,Long> {
	
	RoleEntity findByName(String name);
	
	

}
