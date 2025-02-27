package com.example.rest_api.database.resources.repository;

import com.example.rest_api.database.resources.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findByName(String name);

    List<Album> findByNameContainingIgnoreCase(String query);
}
