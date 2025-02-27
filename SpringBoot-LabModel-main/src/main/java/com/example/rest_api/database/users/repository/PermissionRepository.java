package com.example.rest_api.database.users.repository;

import com.example.rest_api.database.users.model.PermissionEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    List<PermissionEntity> findByRoleId(Long roleId);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM permissions WHERE id = :permissionId", nativeQuery = true)
    void deletePermissions(@Param("permissionId") Long permissionId);
}
