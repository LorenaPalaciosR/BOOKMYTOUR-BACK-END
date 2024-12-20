package com.bookmytour.controller;

import com.bookmytour.dto.TourDTO;
import com.bookmytour.dto.TourResponseDTO;
import com.bookmytour.entity.*;
import com.bookmytour.service.*;
import com.bookmytour.service.impl.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tours")

public class TourController {

    @Autowired
    private ITourService tourService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ITourImageService tourImageService;

    @Autowired
    private ITourCitiesService tourCitiesService;

    @Autowired
    private IBookingService bookingService;


   // Obtener todos los tours (abierto para cualquier usuario)
   @GetMapping
   public List<TourResponseDTO> getAllTours() {
       return tourService.getAllTours().stream()
               .map(this::convertToResponseDTO) // Convertir a TourResponseDTO
               .collect(Collectors.toList());
   }

    // Obtener un tour por ID (abierto para cualquier usuario)
    @GetMapping("/{id}")
    public ResponseEntity<TourResponseDTO> getTourById(@PathVariable int id) {
        Tour tour = tourService.getTourWithCities(id); // Cargar tour con relaciones
        if (tour == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(convertToResponseDTO(tour));
    }

    private TourResponseDTO convertToResponseDTO(Tour tour) {
        // Obtener URLs de las imágenes asociadas al tour
        List<String> imageUrls = tourImageService.getTourImagesByTourId(tour.getTourId())
                .stream()
                .map(TourImage::getImageUrl)
                .collect(Collectors.toList());

        // Obtener los nombres de las ciudades asociadas al tour
        List<String> cityNames = tour.getTourCities() != null
                ? tour.getTourCities().stream()
                .filter(tc -> tc.getCity() != null) // Asegúrate de filtrar valores nulos
                .map(tc -> tc.getCity().getName())
                .collect(Collectors.toList())
                : List.of();

        List<String> fechasOcupadas = bookingService.getBookingsByTourId(tour.getTourId())
                .stream()
                .map(booking -> booking.getBookingDate() + " - " + booking.getEndDate())
                .collect(Collectors.toList());

        // Crear y devolver el DTO
        return new TourResponseDTO(
                tour.getTourId(),
                tour.getCategory().getName(),
                tour.getName(),
                tour.getDescription(),
                tour.getSummary(),
                tour.getDuration(),
                tour.getItinerary(),
                tour.getDatesAvailable(),
                tour.getCostPerPerson(),
                imageUrls,
                cityNames,
                fechasOcupadas// Añadir los nombres de las ciudades al DTO
        );
    }
    // Crear un nuevo tour (solo administrador)

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createTour(@ModelAttribute TourDTO tourDTO) {
        try {
            // Verificar si ya existe un tour con el mismo nombre
            if (tourService.getTourByName(tourDTO.getName()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Ya existe un tour con el nombre proporcionado"));
            }

            // Crear el tour
            Category category = categoryService.getCategoryByName(tourDTO.getCategoryName());
            Tour tour = new Tour();
            tour.setCategory(category);
            tour.setName(tourDTO.getName());
            tour.setDescription(tourDTO.getDescription());
            tour.setSummary(tourDTO.getSummary());
            tour.setDuration(tourDTO.getDuration());
            tour.setItinerary(tourDTO.getItinerary());
            tour.setCostPerPerson(tourDTO.getCostPerPerson());
            if (tourDTO.getDatesAvailable() != null) {
                tour.setDatesAvailable(tourDTO.getDatesAvailable());
            }

            // Guardar el tour
            Tour savedTour = tourService.saveTour(tour);

            // Manejar ciudades asociadas
            associateCitiesWithTour(savedTour, tourDTO.getCityNames());
            /*
            // Asociar ciudades al tour usando cityIds
            if (tourDTO.getCityIds() != null && !tourDTO.getCityIds().isEmpty()) {
                // Dividir la cadena de cityIds y convertirlos en enteros
                List<Integer> cityIds = Arrays.stream(tourDTO.getCityIds().split(",")) // Dividir por comas
                        .map(String::trim) // Eliminar espacios adicionales
                        .map(Integer::parseInt) // Convertir a enteros
                        .collect(Collectors.toList());

                // Crear y guardar las relaciones en tour_cities
                List<TourCities> tourCities = cityIds.stream()
                        .map(cityId -> new TourCities(
                                new TourCitiesId(savedTour.getTourId(), cityId),
                                savedTour,
                                new City(cityId)))
                        .collect(Collectors.toList());

                tourCities.forEach(tourCitiesService::saveTourCity);
            }
            */
            // Subir imágenes
            if (tourDTO.getImagenes() != null) {
                List<String> uploadedFilesUrls = tourDTO.getImagenes().stream()
                        .map(file -> s3Service.uploadFileToS3(file, "tours/"))
                        .collect(Collectors.toList());
                uploadedFilesUrls.forEach(imageUrl -> {
                    TourImage tourImage = new TourImage();
                    tourImage.setTour(savedTour);
                    tourImage.setImageUrl(imageUrl);
                    tourImageService.saveTourImage(tourImage);
                });
            }

            // Recargar el tour con relaciones
            Tour reloadedTour = tourService.getTourWithCities(savedTour.getTourId());

            // Responder con éxito y mensaje
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of(
                            "message", "Tour creado con éxito",
                            "data", convertToResponseDTO(reloadedTour)
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al crear el tour"));
        }
    }


    // Actualizar un tour (solo administrador)
   // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateTour(@PathVariable int id, @ModelAttribute TourDTO tourDTO) {
        Tour existingTour = tourService.getTourWithCities(id);
        if (existingTour == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "El tour no fue encontrado"));
        }
        Map<String, String> updateMessages = new HashMap<>();

        try {
            // Actualizar detalles del tour y agregar mensajes específicos
            if (tourDTO.getName() != null) {
                existingTour.setName(tourDTO.getName());
                updateMessages.put("name", "Nombre actualizado correctamente.");
            }
            if (tourDTO.getDescription() != null) {
                existingTour.setDescription(tourDTO.getDescription());
                updateMessages.put("description", "Descripción actualizada correctamente.");
            }
            if (tourDTO.getSummary() != null) {
                existingTour.setSummary(tourDTO.getSummary());
                updateMessages.put("summary", "Resumen actualizado correctamente.");
            }
            if (tourDTO.getDuration() != null) {
                existingTour.setDuration(tourDTO.getDuration());
                updateMessages.put("duration", "Duración actualizada correctamente.");
            }
            if (tourDTO.getItinerary() != null) {
                existingTour.setItinerary(tourDTO.getItinerary());
                updateMessages.put("itinerary", "Itinerario actualizado correctamente.");
            }
            if (tourDTO.getDatesAvailable() != null) {
                existingTour.setDatesAvailable(tourDTO.getDatesAvailable());
                updateMessages.put("datesAvailable", "Fechas disponibles actualizadas correctamente.");
            }
            if (tourDTO.getCostPerPerson() != null) {
                existingTour.setCostPerPerson(tourDTO.getCostPerPerson());
                updateMessages.put("costPerPerson", "Costo por persona actualizado correctamente.");
            }
            if (tourDTO.getCategoryName() != null) {
                existingTour.setCategory(categoryService.getCategoryByName(tourDTO.getCategoryName()));
                updateMessages.put("category", "Categoría actualizada correctamente.");
            }

            // Actualizar ciudades asociadas
            try {
                associateCitiesWithTour(existingTour, tourDTO.getCityNames());
                updateMessages.put("cities", "Ciudades asociadas actualizadas correctamente.");
            } catch (Exception e) {
                updateMessages.put("cities", "Error al actualizar las ciudades: " + e.getMessage());
            }

            // Manejar imágenes
            if (tourDTO.getImagenes() != null && !tourDTO.getImagenes().isEmpty()) {
                List<TourImage> oldImages = tourImageService.getTourImagesByTourId(id);
                oldImages.forEach(image -> {
                    s3Service.deleteFileFromS3(image.getImageUrl());
                    tourImageService.deleteTourImage(image.getImageId());
                });

                List<String> uploadedFilesUrls = tourDTO.getImagenes().stream()
                        .map(file -> s3Service.uploadFileToS3(file, "tours/"))
                        .collect(Collectors.toList());
                uploadedFilesUrls.forEach(url -> {
                    TourImage tourImage = new TourImage();
                    tourImage.setTour(existingTour);
                    tourImage.setImageUrl(url);
                    tourImageService.saveTourImage(tourImage);
                });
                updateMessages.put("images", "Imágenes actualizadas correctamente.");
            }

            // Guardar los cambios en el tour
            Tour updatedTour = tourService.saveTour(existingTour);

            // Recargar el tour con las relaciones actualizadas
            Tour reloadedTour = tourService.getTourWithCities(updatedTour.getTourId());

            return ResponseEntity.ok(Map.of(
                    "message", "Tour actualizado con éxito",
                    "details", updateMessages,
                    "data", convertToResponseDTO(reloadedTour)
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al actualizar el tour."));
        }
    }


    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTour(@PathVariable int id) {
        Tour tour = tourService.getTourWithCities(id);
        if (tour == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tour no encontrado"));
        }

        try {
            // Eliminar relaciones con ciudades
            tour.getTourCities().forEach(tc -> tourCitiesService.deleteTourCity(tc.getId()));

            /*
            if (tour.getTourCities() != null && !tour.getTourCities().isEmpty()) {
                List<TourCities> tourCities = tour.getTourCities();
                tourCities.forEach(tourCity -> tourCitiesService.deleteTourCity(tourCity.getId()));
            }
            */
            // Eliminar imágenes asociadas
            List<TourImage> images = tourImageService.getTourImagesByTourId(id);
            if (images != null && !images.isEmpty()) {
                images.forEach(image -> {
                    s3Service.deleteFileFromS3(image.getImageUrl());
                    tourImageService.deleteTourImage(image.getImageId());
                });
            }

            // Eliminar el tour
            tourService.deleteTour(id);

            // Retornar respuesta de éxito
            return ResponseEntity.ok(Map.of("message", "Tour eliminado con éxito"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al eliminar el tour."));
        }
    }

    // Manejo de asociación de ciudades
    private void associateCitiesWithTour(Tour tour, String cityNamesString) {
        if (cityNamesString != null && !cityNamesString.isEmpty()) {
            // Dividir los nombres de las ciudades y limpiar espacios
            List<String> cityNames = Arrays.stream(cityNamesString.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            // Buscar las ciudades en la base de datos
            List<City> cities = cityNames.stream()
                    .map(cityName -> tourCitiesService.getCityByName(cityName)) // Buscar ciudad por nombre
                    .filter(city -> city != null) // Filtrar nombres inválidos
                    .collect(Collectors.toList());

            // Eliminar relaciones anteriores
            if (tour.getTourCities() != null && !tour.getTourCities().isEmpty()) {
                // Elimina las relaciones tanto de la base de datos como de la lista de la entidad Tour
                List<TourCities> tourCitiesToRemove = new ArrayList<>(tour.getTourCities());
                tourCitiesToRemove.forEach(tc -> tourCitiesService.deleteTourCity(tc.getId()));
                tour.getTourCities().clear();
            }

            // Crear nuevas relaciones y asignarlas al Tour
            List<TourCities> newTourCities = cities.stream()
                    .map(city -> new TourCities(
                            new TourCitiesId(tour.getTourId(), city.getCityId()),
                            tour,
                            city))
                    .collect(Collectors.toList());

            // Agregar nuevas relaciones al tour
            tour.getTourCities().addAll(newTourCities);
        }
    }
}

