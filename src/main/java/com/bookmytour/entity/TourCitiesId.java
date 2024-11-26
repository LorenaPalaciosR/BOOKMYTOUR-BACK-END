package com.bookmytour.entity;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable


public class TourCitiesId implements Serializable {

    private Integer tourId;
    private Integer cityId;

}
