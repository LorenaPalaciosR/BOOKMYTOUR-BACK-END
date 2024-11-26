package com.bookmytour.entity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tours")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "tourId")

public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tourId;

    @ManyToOne
    @JoinColumn(name="category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String summary;

    @Column(length = 50)
    private String duration;

    @Column(columnDefinition = "TEXT")
    private String itinerary;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String datesAvailable;

    private int costPerPerson;

    @OneToMany (mappedBy = "tour")
    private List<TourImage> images;

    @OneToMany (mappedBy = "tour")
    private List<TourFeature> features;

    @OneToMany(mappedBy = "tour")
    private List<TourCities> tourCities;

    public Tour(Integer tourId) {
        this.tourId = tourId;
    }


}
