package com.bookmytour.dto;
import java.util.Date;
import jakarta.validation.constraints.*;


public class bookingDTO {

    @NotNull(message = "User ID no puede ser nulo.")
    @NotNull(message = "La fecha de fin no puede ser nula.")
    private Integer userId;

    @NotNull(message = "Tour ID no puede ser nulo.")
    @NotNull(message = "La fecha de fin no puede ser nula.")
    private Integer tourId;

    @NotNull(message = "La fecha de inicio no puede ser nula.")
    @FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro.")
    @NotNull(message = "La fecha de fin no puede ser nula.")
    private Date bookingDate;

    @NotNull(message = "La fecha de finalización no puede ser nula.")
    @Future(message = "La fecha de finalización debe ser en el futuro.")
    @NotNull(message = "La fecha de fin no puede ser nula.")
    private Date endDate;

    @NotBlank(message = "El estado no puede estar vacío.")
    @Pattern(regexp = "PENDING|CONFIRMED|CANCELLED", message = "El estado debe ser PENDING, CONFIRMED o CANCELLED.")
    @NotNull(message = "La fecha de fin no puede ser nula.")
    private String status;

    private String paymentMethod;

    // Constructor vacío
    public bookingDTO() {
    }

    // Constructor parametrizado
    public bookingDTO(Integer userId, Integer tourId, Date bookingDate, Date endDate, String status, String paymentMethod) {
        this.userId = userId;
        this.tourId = tourId;
        this.bookingDate = bookingDate;
        this.endDate = endDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    // Getters y Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
