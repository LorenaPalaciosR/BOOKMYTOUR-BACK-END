package com.bookmytour.dto;
import com.bookmytour.entity.TourImage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class BookingResponseDTO {

    private Integer bookingId;

    // Detalles del usuario
    private String nombreUsuario;
    private String apellidoUsuario;

    // Detalles del tour
    private String nombreTour;
    private String fechasTour;
    private Integer costoTour;
    private String duracionTour;
    private List<String> imagenesTour;
    private String cityNames; // Cambia el campo a un String de nombres separados por comas
    // Fechas de la reserva
    private Date bookingDate;
    private Date endDate;

    // Estado y método de pago
    private String status;
    private String paymentMethod;

    public void setImagenesTour(List<String> imagenes) {
        this.imagenesTour = imagenes; // Asegúrate de que el atributo `imagenes` sea una lista de String
    }
    public void setCiudadesTour(String ciudadesTour) {
        this.cityNames = ciudadesTour; // Asigna el valor al campo correspondiente
    }
}
