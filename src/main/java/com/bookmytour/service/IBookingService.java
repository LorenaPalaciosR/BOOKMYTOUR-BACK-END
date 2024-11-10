package com.bookmytour.service;

import com.bookmytour.entity.Booking;

import java.util.List;

public interface IBookingService {

    List<Booking> getAllBookings();
    Booking getBookingById(int id);
    Booking saveBooking(Booking booking);
    void deleteBooking(int id);
}
