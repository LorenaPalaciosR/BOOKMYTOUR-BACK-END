package com.bookmytour.entity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tours_cities")
public class TourCities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;


    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

}
