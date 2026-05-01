package com.hms.service.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.UserEntity;
import com.hms.service.response.UserResponse;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    boolean existsByEmployeeId(Integer employeeId);

    boolean existsByEmail(String email);
    @Query(value = """
    	    SELECT 
    	        u.id AS id,
    	        u.username AS username,
    	        u.email AS email,
    	        ar.role_id AS roleId,
    	        r.role_name AS roleName,
    	        CASE 
    	            WHEN u.active = true THEN true 
    	            ELSE false 
    	        END AS active
    	    FROM tb_user u
    	    JOIN tb_assign_roles ar ON u.user_id = ar.user_id
    	    JOIN tb_role r ON ar.role_id = r.role_id
    	    WHERE (:roleId IS NULL OR ar.role_id = :roleId)
    	""",
    	countQuery = """
    	    SELECT COUNT(*)
    	    FROM tb_user u
    	    JOIN tb_assign_roles ar ON u.user_id = ar.user_id
    	    WHERE (:roleId IS NULL OR ar.role_id = :roleId)
    	""",
    	nativeQuery = true)
    	Page<UserResponse> findUsersByRole(@Param("roleId") Integer roleId, Pageable pageable);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT COUNT(u) FROM UserEntity u")
    Long getTotalUsers();

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.active = true")
    Long getActiveUsers();

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.active = false")
    Long getDeactivatedUsers();
    
    @Query("""
    	    SELECT ro.roleId, ro.roleName, COUNT(u)
    	    FROM UserEntity u
    	    JOIN AssignRolesEntity r ON u.userId = r.userId
    	    JOIN RolesEntity ro ON r.roleId = ro.roleId
    	    GROUP BY ro.roleId, ro.roleName
    	""")
    	List<Object[]> getUserCountByRole();
    
    	@Query("""
    		    SELECT COUNT(u)
    		    FROM UserEntity u
    		    JOIN AssignRolesEntity r ON u.userId = r.userId
    		    WHERE (:roleId IS NULL OR r.roleId = :roleId)
    		""")
    		Long getFilteredUsers(@Param("roleId") Integer roleId);
   
    Optional<UserEntity> findByUserId(Integer userId);

	UserEntity findByEmail(String email);
	
	boolean existsByEmailAndIdNot(String email, Integer id);

	boolean existsByMobileNumberAndIdNot(String mobileNumber, Integer id);

	boolean existsByFirstNameAndIdNot(String firstName, Integer id);
	

	UserEntity findByEmployeeId(String employeeId);
	
	Optional<UserEntity> findByEmailOrMobileNumber(String email, String mobileNumber);
	
	Optional<UserEntity> findByCandidateId(Integer candidateId);

	Optional<UserEntity> findByEmailAndActiveTrue(String email);

	List<UserEntity> findByIdIn(List<Integer> userIds);
	
	@Query("""
		    SELECT u FROM UserEntity u
		    JOIN AssignRolesEntity ar ON u.id = ar.userId
		    WHERE ar.roleId = :roleId
		""")
		Page<UserEntity> findUsersByRoleId(@Param("roleId") Integer roleId, Pageable pageable);

}
