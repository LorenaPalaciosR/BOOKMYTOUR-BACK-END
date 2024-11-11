package com.bookmytour.controller;

import com.bookmytour.entity.Tour;
import com.bookmytour.entity.TourImage;
import com.bookmytour.service.ITourImageService;
import com.bookmytour.service.ITourService;
import com.bookmytour.service.impl.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
    @PostMapping("/{tourId}/upload")
    public TourImage uploadTourImage(@PathVariable int tourId, @RequestParam("file") MultipartFile file) throws IOException {
        Tour tour = tourService.getTourById(tourId);

        if (tour == null) {
            throw new RuntimeException("Tour no encontrado");
        }

        String fileName = file.getOriginalFilename();
        Path tempPath = null;
        try {
            // Crear archivo temporal y transferir el contenido del archivo
            tempPath = Files.createTempFile("temp", fileName);
            file.transferTo(tempPath.toFile());

            // Subir el archivo al bucket de S3 y obtener la URL
            String imageUrl = s3Service.uploadFile(fileName, tempPath);

            // Crear objeto TourImage y guardar la URL
            TourImage tourImage = new TourImage();
            tourImage.setTour(tour);
            tourImage.setImageUrl(imageUrl);

            return tourImageService.saveTourImage(tourImage);

        } finally {
            // Asegurar que el archivo temporal sea eliminado, incluso en caso de error
            if (tempPath != null) {
                Files.deleteIfExists(tempPath);
            }
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public TourImage updateTourImage(@PathVariable int id, @RequestBody TourImage tourImage) {
        tourImage.setImageId(id);
        return tourImageService.saveTourImage(tourImage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteTourImage(@PathVariable int id) {
        tourImageService.deleteTourImage(id);
    }
}
