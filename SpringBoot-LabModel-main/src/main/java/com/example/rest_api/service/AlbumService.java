package com.example.rest_api.service;

import com.example.rest_api.database.resources.model.Album;
import com.example.rest_api.database.resources.model.AlbumRole;
import com.example.rest_api.database.resources.model.Photo;
import com.example.rest_api.database.resources.repository.AlbumRepository;
import com.example.rest_api.database.resources.repository.AlbumRoleRepository;
import com.example.rest_api.database.resources.repository.PhotoRepository;
import com.example.rest_api.database.users.model.PermissionEntity;
import com.example.rest_api.database.users.model.RoleEntity;
import com.example.rest_api.database.users.model.UserEntity;
import com.example.rest_api.database.users.repository.PermissionRepository;
import com.example.rest_api.database.users.repository.RoleRepository;
import com.example.rest_api.database.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final AlbumRoleRepository albumRoleRepository;
    private final RoleService roleService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    public AlbumService(AlbumRepository albumRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, AlbumRoleRepository albumRoleRepository, UserRepository userRepository, UserService userService, RoleService roleService) {
        this.albumRepository = albumRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.albumRoleRepository = albumRoleRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.roleService = roleService;
    }



    public Album createAlbum(String name, String createdBy, UserEntity creator) {
        Album album = new Album();
        album.setName(name);
        album.setCreatedBy(createdBy);

        // Salvează albumul
        album = albumRepository.save(album);

        // Creează și salvează rolurile asociate, inclusiv asocierea creatorului
        createRolesForAlbum(album, creator);

        return album;
    }


    private void createRolesForAlbum(Album album, UserEntity creator) {
        // create the Admin role
        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(album.getName().toUpperCase() + "_ALBUM_ADMIN");
        roleRepository.save(adminRole);

        // create link between album and the roles associated
        AlbumRole adminAlbumRole = new AlbumRole();
        adminAlbumRole.setAlbumId(album.getId());
        adminAlbumRole.setRoleId(adminRole.getId());
        albumRoleRepository.save(adminAlbumRole);

        // what can the admin do
        List<String> adminPermissions = List.of("GET", "POST", "PATCH", "DELETE");
        for (String method : adminPermissions) {
            PermissionEntity permission = new PermissionEntity();
            permission.setHttpMethod(method);
            permission.setUrl("/album/details/" + album.getId() + "/**");
            permission.setRole(adminRole);
            permissionRepository.save(permission);
        }

        // Assign admin role to creator
        creator.addRole(adminRole);
        userRepository.save(creator);

        // create the Read-only role
        RoleEntity readOnlyRole = new RoleEntity();
        readOnlyRole.setName(album.getName().toUpperCase() + "_ALBUM");
        roleRepository.save(readOnlyRole);

        // do link between album and role
        AlbumRole readOnlyAlbumRole = new AlbumRole();
        readOnlyAlbumRole.setAlbumId(album.getId());
        readOnlyAlbumRole.setRoleId(readOnlyRole.getId());
        albumRoleRepository.save(readOnlyAlbumRole);

        //set permissions for the read only role
        PermissionEntity getPermission = new PermissionEntity();
        getPermission.setHttpMethod("GET");
        getPermission.setUrl("/album/details/" + album.getId() + "/**");
        getPermission.setRole(readOnlyRole);
        permissionRepository.save(getPermission);
    }



    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    public List<RoleEntity> getRolesForAlbum(Long albumId) {
        List<AlbumRole> albumRoles = albumRoleRepository.findByAlbumId(albumId);
        List<RoleEntity> roles = new ArrayList<>();
        for (AlbumRole albumRole : albumRoles) {
            roles.add(roleRepository.findById(albumRole.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found for ID: " + albumRole.getRoleId())));
        }
        return roles;
    }



    public Album getAlbumById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + id));
    }


    public void deleteAlbum(Long id) {
        //cauta rolurile albumului ca sa le stergi
        List<RoleEntity> rolesForAlbum = getRolesForAlbum(id);
        //sterg rolurile cu permisiunile asociate
        for(RoleEntity roleEntity: rolesForAlbum){
            roleService.deleteRole(roleEntity.getId());
        }
        //sterge albumul total
        albumRepository.deleteById(id);
    }


    public boolean canUserAccessAlbum(Principal principal, Long albumId) {
        UserEntity user = userService.findByEmail(principal.getName());

        // Verifică dacă utilizatorul are roluri și permisiuni asociate albumului
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission ->
                        permission.getHttpMethod().equalsIgnoreCase("GET") &&
                                pathMatcher.match(permission.getUrl(), "/album/details/" + albumId));
    }

    public boolean canUserPerformActionOnAlbum(Principal principal, Long albumId, String httpMethod) {
        UserEntity user = userService.findByEmail(principal.getName());

        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission ->
                        permission.getHttpMethod().equalsIgnoreCase(httpMethod) &&
                                pathMatcher.match(permission.getUrl(), "/album/details/" + albumId));
    }


    public List<Album> searchAlbums(String query) {
        return albumRepository.findByNameContainingIgnoreCase(query);
    }

}