package com.example.rest_api.service;

import com.example.rest_api.database.users.model.PermissionEntity;
import com.example.rest_api.database.users.model.RoleEntity;
import com.example.rest_api.database.users.repository.PermissionRepository;
import com.example.rest_api.database.users.repository.RoleRepository;
import com.example.rest_api.database.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;
    private UserRepository userRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository,UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    public void save(RoleEntity role) {
        this.roleRepository.save(role);
    }

    public Boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    public Optional<RoleEntity> findByName(String name) {
        return roleRepository.findByName(name);
    }

    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }
    public RoleEntity findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    public List<RoleEntity> findByIds(List<Long> ids) {
        return roleRepository.findAllById(ids);
    }

    // sterge rol = stergem toate rolurile asociate cu albumul
    @Transactional
    public void deleteRole(Long roleId) {
        // 1. Șterge permisiunile asociate cu rolul
        List<PermissionEntity> permissions = permissionRepository.findByRoleId(roleId);
        for (PermissionEntity permission : permissions) {
            permissionRepository.deleteById(permission.getId());
        }

        // 2. Șterge legăturile dintre utilizatori și roluri
        userRepository.deleteUserByRoleId(roleId);

        // 3. Șterge rolul
        roleRepository.deleteById(roleId);
    }

    @Transactional
    public void updateRoleName(Long roleId, String newName) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setName(newName);
        roleRepository.save(role);
    }

    public List<PermissionEntity> getPermissionsByRoleId(Long roleId) {
        return permissionRepository.findByRoleId(roleId);
    }

    @Transactional
    public void deletePermission(Long permissionId) {
        permissionRepository.deleteById(permissionId);
    }




}
