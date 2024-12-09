package com.bookmytour.service.impl;

import com.bookmytour.entity.Booking;
import com.bookmytour.repository.IBookingRepository;
import com.bookmytour.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class BookingService implements IBookingService {

    @Autowired
    private IBookingRepository bookingRepository;
    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getBookingById(int id) {
        return bookingRepository.findById(id).orElse(null);
    }

    @Override
    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(int id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public List<Booking> getBookingsByUserId(Integer userId) {
        return bookingRepository.findByUser_UserId(userId);
    }

    @Override
    public boolean isTourOccupied(Integer tourId, Date bookingDate, Date endDate) {
        return bookingRepository.isTourOccupied(tourId, bookingDate, endDate);
    }

    @Override
    public List<Booking> getBookingsByTourId(Integer tourId) {
        return bookingRepository.findByTour_TourId(tourId);
    }
}
