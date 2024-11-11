package com.bookmytour.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name="cities")

public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cityId;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "city")
    private List<TourCities> tourCities;

    public City(Integer cityId) {
        this.cityId = cityId;
    }


}
