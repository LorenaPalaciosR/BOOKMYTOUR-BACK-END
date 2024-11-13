package com.bookmytour.controller;

import com.bookmytour.dto.bookingDTO;
import com.bookmytour.entity.Booking;
import com.bookmytour.entity.Tour;
import com.bookmytour.entity.Usuario;
import com.bookmytour.service.IBookingService;
import com.bookmytour.service.ITourService;
import com.bookmytour.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private IBookingService bookingService;
    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ITourService tourService;

    // Solo administradores pueden ver todas las reservaciones
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // Usuarios autenticados pueden ver su propia reservación por ID
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable int id) {
        return bookingService.getBookingById(id);
    }

    // Usuarios autenticados pueden crear una reservación
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public Booking createBooking(@RequestBody bookingDTO bookingDTO) {
        // Cargar el usuario y el tour usando sus servicios
        Usuario user = usuarioService.getUsuarioById(bookingDTO.getUserId());
        Tour tour = tourService.getTourById(bookingDTO.getTourId());

        // Crear una nueva entidad Booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTour(tour);
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setStatus(bookingDTO.getStatus());

        // Guardar la reservación y retornar el resultado
        return bookingService.saveBooking(booking);
    }

    // Usuarios autenticados pueden actualizar su propia reservación, y administradores pueden actualizar cualquier reservación
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public Booking updateBooking(@PathVariable int id, @RequestBody Booking bookingRequest) {
        // Cargar la reserva existente desde la base de datos
        Booking booking = bookingService.getBookingById(id);

        // Verificar si se ha proporcionado un nuevo tour en el request y actualizarlo
        if (bookingRequest.getTour() != null && bookingRequest.getTour().getTourId() != null) {
            Tour newTour = tourService.getTourById(bookingRequest.getTour().getTourId());
            booking.setTour(newTour);
        }

        // Actualizar la fecha de la reserva si se proporciona en el request
        if (bookingRequest.getBookingDate() != null) {
            booking.setBookingDate(bookingRequest.getBookingDate());
        }

        // Actualizar el estado si se proporciona en el request
        if (bookingRequest.getStatus() != null) {
            booking.setStatus(bookingRequest.getStatus());
        }

        // Guardar los cambios en la reserva
        return bookingService.saveBooking(booking);
    }

    // Usuarios autenticados pueden eliminar su propia reservación, y administradores pueden eliminar cualquier reservación
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable int id) {
        bookingService.deleteBooking(id);
    }



}
