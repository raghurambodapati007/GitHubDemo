package com.appsdeveloperblog.app.ws.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.appsdeveloperblog.app.ws.io.entiry.UserEntity;

@Repository
/*public interface UserRepository extends CrudRepository<UserEntity,Long> {*/

/* functionalities of CrudRepository are available in PagingAnd SortingRepository */

public interface UserRepository extends PagingAndSortingRepository<UserEntity,Long> {
	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
	UserEntity	findUserByEmailVerificationToken(String token);
	
	@Query(value="select * from Users u where u.EMAIL_VERIFICATION_STATUS = 'true'", 
			countQuery="select count(*) from Users u where u.EMAIL_VERIFICATION_STATUS = 'true'", 
			nativeQuery = true)
	Page<UserEntity> findAllUsersWithConfirmedEmailAddress( Pageable pageableRequest );
	
	
	//passing parameters to SQL using position parameters
	@Query(value="select * from Users u where u.first_name=?1 and u.last_name=?2",nativeQuery = true)
	List<UserEntity> findByFirstName(String firstName,String lastName);
 	
	//Passing named query parameters
	@Query(value="select * from Users u where u.last_name=:lastName",nativeQuery = true)
	List<UserEntity> findUserByLastName(@Param("lastName") String lastName);
	
	//% represents the word may have any characters and can start with any string but must end with keyword value that is passed
	@Query(value="select * from Users u where u.first_name LIKE %:keyword% or last_name LIKE %:keyword%",nativeQuery = true)
	List<UserEntity> findUserByKeyword(@Param("keyword") String keyword);
	
	
	//Getting a column of data from tables using native SQL
	@Query(value="select u.first_name ,u.last_name from Users u where u.first_name LIKE %:keyword% or last_name LIKE %:keyword%",nativeQuery = true)
	List<Object[]> findUserFirstNameAndLastNameByKeyword(@Param("keyword") String keyword);
	
	
	//Modifying is used only if we are modifying any data in db
	//@Transactional helps in case if we delete , update any data and error takes place  and that value gets rolled back
		
	@Transactional
	@Modifying 
	@Query(value="update Users u set u.EMAIL_VERIFICATION_STATUS=:emailVerificationStatus where u.USER_ID=:userId",nativeQuery = true)
	void updateUserEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus,@Param("userId") String userId);
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
											////////////////JPQL///////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Query("select user from UserEntity user where user.userId =:userId")
	UserEntity findUserEntityByUserId(@Param("userId") String userId);
	
	@Query("select user.firstName,user.lastName from UserEntity user where user.userId =:userId")
	List<Object[]> getUserEntityFullNameById(@Param("userId") String userId);
	
	
	  @Transactional
	  @Modifying
	  @Query("UPDATE UserEntity set emailVerificationStatus =:emailVerificationStatus where user_Id =:userId" )
	  void updateUserEntityEmailVerificationStatusById(@Param("emailVerificationStatus") boolean emailVerificationStatus,
			  												 @Param("userId") String userId);
	 
	
	
}
