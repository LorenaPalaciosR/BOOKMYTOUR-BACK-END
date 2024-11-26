package com.bookmytour.controller;

import com.bookmytour.dto.TourDTO;
import com.bookmytour.dto.TourResponseDTO;
import com.bookmytour.entity.*;
import com.bookmytour.service.ICategoryService;
import com.bookmytour.service.ITourCitiesService;
import com.bookmytour.service.ITourImageService;
import com.bookmytour.service.ITourService;
import com.bookmytour.service.impl.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                cityNames // Añadir los nombres de las ciudades al DTO
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
            associateCitiesWithTour(savedTour, tourDTO.getCityIds());
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
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(reloadedTour));

        } catch (NumberFormatException e) {
            // Manejar errores de formato en los IDs de ciudadesc
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Los cityIds deben ser una lista de números separados por comas"));
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

        try {
            // Actualizar detalles del tour
            if (tourDTO.getName() != null) existingTour.setName(tourDTO.getName());
            if (tourDTO.getDescription() != null) existingTour.setDescription(tourDTO.getDescription());
            if (tourDTO.getSummary() != null) existingTour.setSummary(tourDTO.getSummary());
            if (tourDTO.getDuration() != null) existingTour.setDuration(tourDTO.getDuration());
            if (tourDTO.getItinerary() != null) existingTour.setItinerary(tourDTO.getItinerary());
            if (tourDTO.getDatesAvailable() != null) existingTour.setDatesAvailable(tourDTO.getDatesAvailable());
            if (tourDTO.getCostPerPerson() != null) existingTour.setCostPerPerson(tourDTO.getCostPerPerson());
            if (tourDTO.getCategoryName() != null) {
                existingTour.setCategory(categoryService.getCategoryByName(tourDTO.getCategoryName()));
            }

            // Actualizar ciudades asociadas
            associateCitiesWithTour(existingTour, tourDTO.getCityIds());
            /*
            // Manejar ciudades asociadas
            if (tourDTO.getCityIds() != null && !tourDTO.getCityIds().isEmpty()) {
                List<Integer> cityIds = Arrays.stream(tourDTO.getCityIds().split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                // Eliminar relaciones antiguas
                List<TourCities> existingTourCities = existingTour.getTourCities();
                existingTourCities.forEach(tourCity -> tourCitiesService.deleteTourCity(tourCity.getId()));

                // Crear nuevas relaciones
                List<TourCities> newTourCities = cityIds.stream()
                        .map(cityId -> new TourCities(
                                new TourCitiesId(existingTour.getTourId(), cityId),
                                existingTour,
                                new City(cityId)))
                        .collect(Collectors.toList());
                existingTour.setTourCities(newTourCities);
                newTourCities.forEach(tourCitiesService::saveTourCity);
            }
            */
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
            }

            // Guardar los cambios en el tour
            Tour updatedTour = tourService.saveTour(existingTour);

            // Recargar el tour con las relaciones actualizadas
            Tour reloadedTour = tourService.getTourWithCities(updatedTour.getTourId());
            return ResponseEntity.ok(convertToResponseDTO(reloadedTour));
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
    private void associateCitiesWithTour(Tour tour, String cityIdsString) {
        if (cityIdsString != null && !cityIdsString.isEmpty()) {
            List<Integer> cityIds = Arrays.stream(cityIdsString.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            // Eliminar asociaciones anteriores
            tour.getTourCities().forEach(tc -> tourCitiesService.deleteTourCity(tc.getId()));

            // Crear nuevas asociaciones
            List<TourCities> newTourCities = cityIds.stream()
                    .map(cityId -> new TourCities(
                            new TourCitiesId(tour.getTourId(), cityId),
                            tour,
                            new City(cityId)))
                    .collect(Collectors.toList());

            newTourCities.forEach(tourCitiesService::saveTourCity);
        }
    }

}

