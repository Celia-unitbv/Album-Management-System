package com.example.rest_api.database.resources.repository;

import com.example.rest_api.database.resources.model.AlbumRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRoleRepository extends JpaRepository<AlbumRole, Long> {
    List<AlbumRole> findByAlbumId(Long albumId);
    List<AlbumRole> findByRoleId(Long roleId);
}
