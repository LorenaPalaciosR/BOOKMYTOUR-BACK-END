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
@Table(name="tour_features")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "featureId")

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
