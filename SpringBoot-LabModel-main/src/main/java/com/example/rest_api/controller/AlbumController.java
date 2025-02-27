package com.example.rest_api.controller;

import com.example.rest_api.database.resources.model.Album;

import com.example.rest_api.database.resources.model.Photo;
import com.example.rest_api.database.users.model.PermissionEntity;
import com.example.rest_api.database.users.model.UserEntity;
import com.example.rest_api.service.AlbumService;
import com.example.rest_api.service.PhotoService;
import com.example.rest_api.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/album/details")
public class AlbumController {
    private final AlbumService albumService;
    private final UserService userService;
    private final PhotoService photoService;


    @Autowired
    public AlbumController(AlbumService albumService, UserService userService, PhotoService photoService) {
        this.albumService = albumService;
        this.userService = userService;
        this.photoService = photoService;
    }

    @GetMapping("/{id}")
    public String getAlbumDetails(@PathVariable Long id, Model model, Principal principal) {
        Album album = albumService.getAlbumById(id);

        // Verifică dacă utilizatorul are permisiune pentru GET
        if (!albumService.canUserAccessAlbum(principal, id)) {
            // Redirecționează la o pagină specifică de acces interzis pentru acel album
            model.addAttribute("albumName", album.getName());
            return "albums/access-denied";
        }

        List<Photo> photos = photoService.getPhotosByAlbum(id);

        // Encode images to Base64 for display
        List<PhotoView> photoViews = photos.stream()
                .map(photo -> new PhotoView(
                        photo.getId(),
                        photo.getTitle(),
                        Base64.getEncoder().encodeToString(photo.getData())))
                .collect(Collectors.toList());
        List<PermissionEntity> permissions = userService.getUserPermissions(principal.getName());

        // Verifică dacă utilizatorul are permisiunea pentru această operațiune
        boolean canAddPhoto = permissions.stream()
                .anyMatch(permission -> permission.getHttpMethod().equalsIgnoreCase("POST")
                        && permission.getUrl().startsWith("/album/details/" + id));

        model.addAttribute("album", album);
        model.addAttribute("canAddPhoto", canAddPhoto);
        model.addAttribute("photos", photoViews);

        return "albums/album-details";
    }

    @GetMapping("/{id}/add-photo")
    public String showAddPhotoPage(@PathVariable Long id, Model model, Principal principal) {
        if (!albumService.canUserAccessAlbum(principal, id)) {
            return "albums/access-denied";
        }

        model.addAttribute("albumId", id);
        return "albums/add-photo";
    }

    @PostMapping("/{id}/add-photo")
    public String addPhotoToAlbum(@PathVariable Long id,
                                  @RequestParam("title") String title,
                                  @RequestParam("photo") MultipartFile photo,
                                  Principal principal) {
        if (!albumService.canUserAccessAlbum(principal, id)) {
            return "albums/access-denied";
        }

        try {
            Album album = albumService.getAlbumById(id);

            Photo newPhoto = new Photo();
            newPhoto.setTitle(title);
            newPhoto.setData(photo.getBytes());
            newPhoto.setAlbum(album);

            photoService.savePhoto(newPhoto);
        } catch (Exception e) {
            return "redirect:/album/details/" + id + "?error";
        }

        return "redirect:/album/details/" + id;
    }

    @PostMapping("/{id}/delete") //stergere
    public String deleteAlbum(@PathVariable Long id, Model model, Principal principal) {
        albumService.deleteAlbum(id);
        return "redirect:/user/albums";
    }

    @PostMapping("/{id}/photo/{photoId}")
    public String deletePhoto(@PathVariable Long id,@PathVariable Long photoId, Principal principal, Model model) {
        Photo photo = photoService.getPhotoById(photoId);
        Album album = photo.getAlbum();

        photoService.deletePhoto(photoId);

        return "redirect:/album/details/" + id;
    }


    @Getter
    public static class PhotoView {
        // Getters and setters
        private Long id;
        private String title;
        private String base64Data;

        public PhotoView(Long id, String title, String base64Data) {
            this.id = id;
            this.title = title;
            this.base64Data = base64Data;
        }

    }

}