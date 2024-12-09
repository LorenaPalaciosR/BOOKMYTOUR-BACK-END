package com.bookmytour.controller;

import com.bookmytour.dto.BookingResponseDTO;
import com.bookmytour.dto.bookingDTO;
import com.bookmytour.entity.Booking;
import com.bookmytour.entity.Tour;
import com.bookmytour.entity.TourImage;
import com.bookmytour.entity.Usuario;
import com.bookmytour.service.IBookingService;
import com.bookmytour.service.ITourService;
import com.bookmytour.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


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
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        List<BookingResponseDTO> response = bookingService.getAllBookings().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Obtener todas las reservaciones del usuario autenticado

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        // Extraer el email del usuario autenticado
        String email = userDetails.getUsername();

        // Buscar el usuario por email utilizando el servicio
        Usuario user = usuarioService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Error: Usuario no encontrado con el email proporcionado.");
        }

        // Obtener las reservaciones del usuario
        List<Booking> bookings = bookingService.getBookingsByUserId(user.getUserId());
        if (bookings.isEmpty()) {
            return ResponseEntity.ok("No tienes reservaciones registradas.");
        }

        // Transformar las reservas en una respuesta simplificada
        List<BookingResponseDTO> response = bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
    // Usuarios autenticados pueden crear una reservación
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody bookingDTO bookingDTO) {
        try {
            // Validar que el usuario y el tour existan
            Usuario user = usuarioService.getUsuarioById(bookingDTO.getUserId());
            Tour tour = tourService.getTourById(bookingDTO.getTourId());

            if (user == null || tour == null) {
                return ResponseEntity.badRequest().body("Error: Usuario o Tour no existen.");
            }

            // Validar si el tour ya está reservado en las fechas
            boolean isOccupied = bookingService.isTourOccupied(
                    bookingDTO.getTourId(), bookingDTO.getBookingDate(), bookingDTO.getEndDate()
            );

            if (isOccupied) {
                return ResponseEntity.badRequest().body("Error: El tour ya está reservado en las fechas seleccionadas.");
            }

            // Crear la nueva reserva
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setTour(tour);
            booking.setBookingDate(bookingDTO.getBookingDate());
            booking.setEndDate(bookingDTO.getEndDate());
            booking.setStatus(bookingDTO.getStatus());
            booking.setPaymentMethod(bookingDTO.getPaymentMethod());

            // Guardar la reserva
            Booking savedBooking = bookingService.saveBooking(booking);

            // Respuesta exitosa
            return ResponseEntity.ok(convertToDTO(savedBooking));

        } catch (DataIntegrityViolationException ex) {
            // Manejar violaciones de restricciones únicas
            return ResponseEntity.badRequest()
                    .body("Error: Ya existe una reserva con este tour en las fechas seleccionadas.");
        } catch (Exception e) {
            // Manejo genérico de excepciones
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Usuarios autenticados pueden actualizar su propia reservación, y administradores pueden actualizar cualquier reservación
    //@PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable int id, @Valid @RequestBody bookingDTO bookingDTO) {
        Booking existingBooking = bookingService.getBookingById(id);
        if (existingBooking == null) {
            return ResponseEntity.notFound().build();
        }

        // Validar si las nuevas fechas están ocupadas
        boolean isOccupied = bookingService.isTourOccupied(
                bookingDTO.getTourId(), bookingDTO.getBookingDate(), bookingDTO.getEndDate()
        );
        if (isOccupied && !existingBooking.getTour().getTourId().equals(bookingDTO.getTourId())) {
            return ResponseEntity.badRequest().body("Error: Las fechas seleccionadas están ocupadas.");
        }

        // Actualizar los datos
        if (bookingDTO.getTourId() != null) {
            Tour tour = tourService.getTourById(bookingDTO.getTourId());
            if (tour != null) {
                existingBooking.setTour(tour);
            }
        }

        if (bookingDTO.getBookingDate() != null) {
            existingBooking.setBookingDate(bookingDTO.getBookingDate());
        }

        if (bookingDTO.getEndDate() != null) {
            existingBooking.setEndDate(bookingDTO.getEndDate());
        }

        if (bookingDTO.getStatus() != null) {
            existingBooking.setStatus(bookingDTO.getStatus());
        }

        if (bookingDTO.getPaymentMethod() != null) {
            existingBooking.setPaymentMethod(bookingDTO.getPaymentMethod());
        }

        Booking updatedBooking = bookingService.saveBooking(existingBooking);

        return ResponseEntity.ok(convertToDTO(updatedBooking));
    }


    // Usuarios autenticados pueden eliminar su propia reservación, y administradores pueden eliminar cualquier reservación
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable int id) {
        Booking booking = bookingService.getBookingById(id);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }

        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Reservación eliminada correctamente.");
    }
    // Convertir Booking a BookingResponseDTO
    private BookingResponseDTO convertToDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(booking.getBookingId());

        // Detalles del usuario
        dto.setNombreUsuario(booking.getUser().getFirstName());
        dto.setApellidoUsuario(booking.getUser().getLastName());

        // Detalles del tour
        Tour tour = booking.getTour();
        dto.setNombreTour(tour.getName());
        dto.setFechasTour(tour.getDatesAvailable());
        dto.setCostoTour(tour.getCostPerPerson());
        dto.setDuracionTour(tour.getDuration());

        // Extraer ciudades del tour
        if (tour.getTourCities() != null) {
            List<String> cityNames = tour.getTourCities().stream()
                    .map(tc -> tc.getCity().getName())
                    .collect(Collectors.toList());
            dto.setCiudadesTour(String.join(", ", cityNames));
        } else {
            dto.setCiudadesTour("Sin ciudades registradas");
        }

        // Extraer imágenes del tour
        if (tour.getImages() != null) {
            List<String> imageUrls = tour.getImages().stream()
                    .map(TourImage::getImageUrl)
                    .collect(Collectors.toList());
            dto.setImagenesTour(imageUrls);
        } else {
            dto.setImagenesTour(List.of("Sin imágenes registradas"));
        }

        // Fechas y estado de la reserva
        dto.setBookingDate(booking.getBookingDate());
        dto.setEndDate(booking.getEndDate());
        dto.setStatus(booking.getStatus());
        dto.setPaymentMethod(booking.getPaymentMethod());

        return dto;
    }
}
