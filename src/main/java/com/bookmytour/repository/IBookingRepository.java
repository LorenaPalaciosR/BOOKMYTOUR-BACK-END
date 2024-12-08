package com.bookmytour.repository;

import com.bookmytour.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IBookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.tour.tourId = :tourId AND b.status = 'CONFIRMED' " +
            "AND (:bookingDate <= b.endDate AND :endDate >= b.bookingDate)")
    boolean isTourOccupied(@Param("tourId") Integer tourId,
                           @Param("bookingDate") Date bookingDate,
                           @Param("endDate") Date endDate);

    List<Booking> findByUser_UserId(Integer userId);


}
