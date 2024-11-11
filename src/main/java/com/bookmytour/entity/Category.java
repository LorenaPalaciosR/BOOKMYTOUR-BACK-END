package com.bookmytour.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name="categories")

public class Category {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255) // Almacena la URL de la imagen representativa
    private String imageUrl;

    @OneToMany(mappedBy = "category")
    private List<Tour> tours;



}
