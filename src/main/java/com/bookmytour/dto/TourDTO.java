package com.bookmytour.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class TourDTO {

    private Integer tourId;
    private String categoryName;  // Nombre de la categoría en lugar de la relación completa
    private String name;
    private String description;
    private String summary;
    private String duration;
    private String itinerary;
    private String datesAvailable;
    private Integer costPerPerson;
    private List<MultipartFile> imagenes;
    private String cityNames; // Cambia el campo a un String de nombres separados por comas

}
