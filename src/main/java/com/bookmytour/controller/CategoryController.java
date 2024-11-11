package com.bookmytour.controller;

import com.bookmytour.entity.Category;
import com.bookmytour.service.ICategoryService;
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
@RequestMapping("/api/categories")

public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private S3Service s3Service;  // Servicio para manejar la carga de archivos a S3

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable int id) {
        return categoryService.getCategoryById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    // Endpoint para subir una imagen para una categoría específica (solo administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{categoryId}/upload-image")
    public String uploadCategoryImage(@PathVariable int categoryId, @RequestParam("file") MultipartFile file) throws IOException {
        Category category = categoryService.getCategoryById(categoryId);

        if (category == null) {
            throw new RuntimeException("Categoría no encontrada");
        }

        // Cargar la imagen a S3 y obtener la URL
        String fileName = file.getOriginalFilename();
        Path tempPath = Files.createTempFile("temp", fileName);
        file.transferTo(tempPath.toFile());

        String imageUrl = s3Service.uploadFile(fileName, tempPath);
        Files.delete(tempPath);  // Eliminar el archivo temporal después de cargar

        // Guardar la URL de la imagen en la entidad Category y actualizar en la base de datos
        category.setImageUrl(imageUrl);
        categoryService.saveCategory(category);

        return "Imagen subida exitosamente: " + imageUrl;
    }

    // Actualizar una categoría (solo administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable int id, @RequestBody Category category) {
        category.setCategoryId(id);
        return categoryService.saveCategory(category);
    }

    // Eliminar una categoría (solo administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
    }
}
