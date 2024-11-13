package com.bookmytour.controller;

import com.bookmytour.entity.Tour;
import com.bookmytour.entity.TourImage;
import com.bookmytour.service.ITourImageService;
import com.bookmytour.service.ITourService;
import com.bookmytour.service.impl.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tour-images")

public class TourImageController {

    @Autowired
    private ITourImageService tourImageService;

    @Autowired
    private ITourService tourService;

    @Autowired
    private S3Service s3Service;

    @GetMapping
    public List<TourImage> getAllTourImages() {
        return tourImageService.getAllTourImages();
    }

    @GetMapping("/{id}")
    public TourImage getTourImageById(@PathVariable int id) {
        return tourImageService.getTourImageById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTourImage(@PathVariable int id, @RequestBody Map<String, Object> updates) {
        TourImage existingImage = tourImageService.getTourImageById(id);
        if (existingImage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Imagen no encontrada");
        }

        // Actualizar solo los campos que están en el `Map`
        if (updates.containsKey("tourId")) {
            int tourId = (int) updates.get("tourId");
            Tour tour = tourService.getTourById(tourId);
            if (tour == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tour no válido");
            }
            existingImage.setTour(tour);
        }
        if (updates.containsKey("imageUrl")) {
            String imageUrl = (String) updates.get("imageUrl");
            existingImage.setImageUrl(imageUrl);
        }

        // Guardar los cambios
        tourImageService.saveTourImage(existingImage);

        return ResponseEntity.ok("Imagen actualizada con éxito");
    }

// No se utilizará este método. Se usará el de arriba con MAP.
/*
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public TourImage updateTourImage(@PathVariable int id, @RequestBody TourImage tourImage) {
        tourImage.setImageId(id);
        return tourImageService.saveTourImage(tourImage);
    }
*/
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteTourImage(@PathVariable int id) {
        tourImageService.deleteTourImage(id);
    }
}
