package com.bookmytour.entity;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="tour_id", nullable = false)
    private Tour tour;

    @Temporal(TemporalType.TIMESTAMP)
    private Date bookingDate;

    @Column(length = 20)
    private String status;


}
