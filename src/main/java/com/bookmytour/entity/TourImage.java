package com.bookmytour.entity;
import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

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
