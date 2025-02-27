package com.example.rest_api.controller;

import com.example.rest_api.database.resources.model.Album;

import com.example.rest_api.database.users.model.UserEntity;
import com.example.rest_api.service.AlbumService;
import com.example.rest_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("user/albums")
public class UserController {
    private final AlbumService albumService;
    private final UserService userService;

    @Autowired
    public UserController(AlbumService albumService, UserService userService) {
        this.albumService = albumService;
        this.userService = userService;
    }

    @GetMapping
    public String getAllAlbums(Model model, Principal principal) {
        List<Album> albums = albumService.getAllAlbums();

        Map<Long, Boolean> albumPermissions = albums.stream()
                .collect(Collectors.toMap(
                        Album::getId,
                        album -> albumService.canUserPerformActionOnAlbum(principal, album.getId(), "GET")
                ));

        model.addAttribute("albums", albums);
        model.addAttribute("albumPermissions", albumPermissions);

        return "user/albums";
    }

    @GetMapping("/search")
    public String searchAlbums(@RequestParam("query") String query, Model model, Principal principal) {
        List<Album> albums = albumService.searchAlbums(query);

        // Crearea hărții albumPermissions
        Map<Long, Boolean> albumPermissions = albums.stream()
                .collect(Collectors.toMap(
                        Album::getId,
                        album -> albumService.canUserPerformActionOnAlbum(principal, album.getId(), "GET")
                ));

        model.addAttribute("albums", albums);
        model.addAttribute("albumPermissions", albumPermissions);

        return "user/albums";
    }



    @GetMapping("/add")
    public String showAddAlbumForm(Model model, Principal principal) {
        Album album = new Album();

        // Setează automat creatorul pe baza utilizatorului conectat
        if (principal != null) {
            album.setCreatedBy(principal.getName());
        }

        model.addAttribute("album", album);
        return "albums/add"; // Se referă la fișierul Thymeleaf add.html
    }

    @PostMapping("/add")
    public String addAlbum(@ModelAttribute("album") Album album, BindingResult bindingResult, Principal principal) {
        // Validare: Verifică dacă numele albumului este gol
        if (album.getName() == null || album.getName().trim().isEmpty()) {
            bindingResult.rejectValue("name", "album.name.empty", "Numele albumului nu poate fi gol");
            return "albums/add";
        }


        if (principal != null) {
            album.setCreatedBy(principal.getName());
            UserEntity creator = userService.findByEmail(principal.getName());
            albumService.createAlbum(album.getName(), album.getCreatedBy(), creator);

        }

        return "redirect:/user/albums";
    }


    @GetMapping("/{id}")
    public String getAlbumDetails(@PathVariable Long id, Model model) {
        Album album = albumService.getAlbumById(id);

        model.addAttribute("album", album);

        return "albums/album-details"; // Noul șablon Thymeleaf
    }

}
