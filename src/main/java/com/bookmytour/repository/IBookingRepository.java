package com.bookmytour.repository;

import com.bookmytour.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
public interface IBookingRepository extends JpaRepository<Booking, Integer> {
}
