package com.example.rest_api.database.resources.model;

import com.example.rest_api.database.resources.model.Album;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "photo")
public class Photo  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Basic(fetch = FetchType.LAZY) // Ensures lazy loading
    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

}
