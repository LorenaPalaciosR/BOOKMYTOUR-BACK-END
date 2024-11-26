package com.bookmytour.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourResponseDTO {

    private Integer tourId;
    private String categoryName;
    private String name;
    private String description;
    private String summary;
    private String duration;
    private String itinerary;
    private String datesAvailable;
    private Integer costPerPerson;
    private List<String> imagenes; // Maneja las URLs de las imágenes
    private List<String> cityNames;

}
