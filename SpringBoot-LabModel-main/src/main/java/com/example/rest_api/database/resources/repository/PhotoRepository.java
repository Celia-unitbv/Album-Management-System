package com.example.rest_api.database.resources.repository;
import com.example.rest_api.database.resources.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByAlbumId(Long albumId);
}
