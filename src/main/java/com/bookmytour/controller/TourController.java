package com.bookmytour.controller;

import com.bookmytour.dto.TourDTO;
import com.bookmytour.dto.TourResponseDTO;
import com.bookmytour.entity.Category;
import com.bookmytour.entity.Tour;
import com.bookmytour.entity.TourImage;
import com.bookmytour.service.ICategoryService;
import com.bookmytour.service.ITourImageService;
import com.bookmytour.service.ITourService;
import com.bookmytour.service.impl.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        Tour tour = tourService.getTourById(id);
        if (tour == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(convertToResponseDTO(tour));
    }

    private TourResponseDTO convertToResponseDTO(Tour tour) {
        List<String> imageUrls = tourImageService.getTourImagesByTourId(tour.getTourId()).stream()
                .map(TourImage::getImageUrl)
                .collect(Collectors.toList());

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
                imageUrls
        );
    }
    // Crear un nuevo tour (solo administrador)

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping (consumes = "multipart/form-data")
    public ResponseEntity<?> createTour(@ModelAttribute TourDTO tourDTO) {
        try {
            // Verificar si ya existe un tour con el mismo nombre
            if (tourService.getTourByName(tourDTO.getName()) != null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Ya existe un tour con el nombre proporcionado");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
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

            // Manejar campo opcional datesAvailable
            if (tourDTO.getDatesAvailable() != null) {
                tour.setDatesAvailable(tourDTO.getDatesAvailable());
            }

            Tour savedTour = tourService.saveTour(tour);

            // Subir las imágenes al bucket y asociarlas con el tour
            List<String> uploadedFilesUrls = tourDTO.getImagenes().stream()
                    .map(file -> s3Service.uploadFileToS3(file, "tours/"))
                    .collect(Collectors.toList());
            uploadedFilesUrls.forEach(imagen -> {
                TourImage tourImage = new TourImage();
                tourImage.setTour(savedTour);
                tourImage.setImageUrl(imagen);
                tourImageService.saveTourImage(tourImage);
            });

            return new ResponseEntity<>(convertToResponseDTO(savedTour), HttpStatus.CREATED);

        } catch (Exception e) {
            // Manejar errores genéricos
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error al crear el tour.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // Actualizar un tour (solo administrador)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateTour(@PathVariable int id, @ModelAttribute TourDTO tourDTO) {
        Tour existingTour = tourService.getTourById(id);
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

            Tour updatedTour = tourService.saveTour(existingTour);
            return ResponseEntity.ok(convertToResponseDTO(updatedTour));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al actualizar el tour."));
        }
    }

    // Eliminar un tour (solo administrador)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTour(@PathVariable int id) {
        Tour tour = tourService.getTourById(id);
        if (tour == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tour no encontrado"));
        }

        try {
            List<TourImage> images = tourImageService.getTourImagesByTourId(id);
            images.forEach(image -> {
                s3Service.deleteFileFromS3(image.getImageUrl());
                tourImageService.deleteTourImage(image.getImageId());
            });

            tourService.deleteTour(id);
            return ResponseEntity.ok(Map.of("message", "Tour eliminado con éxito"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al eliminar el tour."));
        }
    }
}

