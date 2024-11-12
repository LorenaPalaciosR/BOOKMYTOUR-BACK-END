package com.bookmytour.entity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tour_images")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "imageId")

public class TourImage {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer imageId;

    @ManyToOne
    @JoinColumn(name="tour_id", nullable = false)
    private Tour tour;

    @Column(nullable = false, length = 255)
    private String imageUrl;

}
