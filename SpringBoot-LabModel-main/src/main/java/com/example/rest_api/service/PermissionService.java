package com.example.rest_api.service;

import com.example.rest_api.database.users.model.PermissionEntity;
import com.example.rest_api.database.users.model.RoleEntity;
import com.example.rest_api.database.users.repository.PermissionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final RoleService roleService;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository, RoleService roleService) {
        this.permissionRepository = permissionRepository;
        this.roleService = roleService;
    }



    public void save(PermissionEntity permissionEntity) {
        this.permissionRepository.save(permissionEntity);
    }

    public void deletePermission(Long permissionId) {
        this.permissionRepository.deletePermissions(permissionId);
    }

    public void addPermissionToRole(Long id, String httpMethod, String url) {
        RoleEntity role = roleService.findById(id);

        PermissionEntity permission = new PermissionEntity();
        permission.setHttpMethod(httpMethod);
        permission.setUrl(url);
        permission.setRole(role);

        permissionRepository.save(permission);
    }
}
