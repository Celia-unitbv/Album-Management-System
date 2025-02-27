package com.example.rest_api.database.users.repository;

import com.example.rest_api.database.users.model.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Transactional
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    //@Query(value = "SELECT user FROM UserEntity user WHERE user.username=:username")
    //@Query(value = "SELECT * FROM app_user WHERE username=:username", nativeQuery = true)
    List<UserEntity> findAllByUsername(String username);

    @Modifying
    @Query(value = "update app_user set username=:username, email=:email, password=:password WHERE id=:id", nativeQuery = true)
    void updateUserEntity(Long id, String username, String email, String password);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM app_users_roles WHERE app_user_id = :userId", nativeQuery = true)
    void deleteRolesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "DELETE FROM app_users_roles WHERE role_id = :roleId", nativeQuery = true)
    void deleteUserByRoleId(@Param("roleId") Long roleId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO app_users_roles (app_user_id, role_id) VALUES (:userId, :roleId)", nativeQuery = true)
    void addRoleToUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
    Optional<UserEntity> findByEmail(String email);

    Boolean existsByEmail(String email);
}
