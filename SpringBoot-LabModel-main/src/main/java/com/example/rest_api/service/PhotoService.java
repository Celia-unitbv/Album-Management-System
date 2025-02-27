package com.example.rest_api.service;

import com.example.rest_api.database.resources.model.Photo;
import com.example.rest_api.database.resources.repository.PhotoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoService {
    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public Photo savePhoto(Photo photo) {
        return photoRepository.save(photo);
    }

    public List<Photo> getPhotosByAlbum(Long albumId) {
        return photoRepository.findByAlbumId(albumId);
    }

    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }

    public void deletePhoto(Long id) {
        photoRepository.deleteById(id);
    }

    public Photo getPhotoById(Long photoId) {
        return photoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found with ID: " + photoId));
    }

}
