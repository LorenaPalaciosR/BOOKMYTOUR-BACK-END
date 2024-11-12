package com.bookmytour.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name="categories")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "categoryId")

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
