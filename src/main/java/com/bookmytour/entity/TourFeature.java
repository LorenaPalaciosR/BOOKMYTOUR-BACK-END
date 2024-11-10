package com.bookmytour.entity;
import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class TourFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer featureId;

    @ManyToOne
    @JoinColumn(name="tour_id", nullable = false)
    private Tour tour;

    @Column(nullable = false, length = 100)

    private String title;
}
