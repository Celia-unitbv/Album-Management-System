package com.example.rest_api.controller;

import com.example.rest_api.database.resources.model.Photo;
import com.example.rest_api.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping
    public ResponseEntity<Photo> uploadPhoto(@RequestBody Photo photo) {
        return ResponseEntity.ok(photoService.savePhoto(photo));
    }

    @GetMapping
    public ResponseEntity<List<Photo>> getAllPhotos() {
        return ResponseEntity.ok(photoService.getAllPhotos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        photoService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }
}
