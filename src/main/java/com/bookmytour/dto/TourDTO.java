package com.bookmytour.dto;

import lombok.Getter;
import lombok.Setter;
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
    private int costPerPerson;
}
