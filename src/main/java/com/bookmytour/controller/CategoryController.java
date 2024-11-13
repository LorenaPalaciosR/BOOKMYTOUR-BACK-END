package com.bookmytour.controller;

import com.bookmytour.dto.CategoryDTO;
import com.bookmytour.entity.Category;
import com.bookmytour.service.ICategoryService;
import com.bookmytour.service.impl.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/categories")

public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private S3Service s3Service;

    // Método para obtener todas las categorías (abierto al público)
    @GetMapping
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Método para obtener una categoría por ID (abierto al público)
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable int id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(category));
    }

    // Método para crear una categoría (solo para administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        Category category = convertToEntity(categoryDTO);
        Category savedCategory = categoryService.saveCategory(category);
        return new ResponseEntity<>(convertToDTO(savedCategory), HttpStatus.CREATED);
    }

    // Método para subir una imagen para una categoría específica (solo para administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{categoryId}/upload-image")
    public ResponseEntity<String> uploadCategoryImage(@PathVariable int categoryId, @RequestParam("file") MultipartFile file) throws IOException {
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoría no encontrada");
        }

        String fileName = file.getOriginalFilename();
        Path tempPath = Files.createTempFile("temp", fileName);
        file.transferTo(tempPath.toFile());

        String imageUrl = s3Service.uploadFile(fileName, tempPath);
        Files.delete(tempPath);

        // Guardar la URL de la imagen en la entidad Category
        category.setImageUrl(imageUrl);
        categoryService.saveCategory(category);

        return ResponseEntity.ok("Imagen subida exitosamente: " + imageUrl);
    }

    // Método para actualizar una categoría (solo para administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable int id, @RequestBody CategoryDTO categoryDTO) {
        Category existingCategory = categoryService.getCategoryById(id);
        if (existingCategory == null) {
            return ResponseEntity.notFound().build();
        }

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());
        // No actualizamos imageUrl aquí, ya que eso se hace en el endpoint de upload-image

        Category updatedCategory = categoryService.saveCategory(existingCategory);
        return ResponseEntity.ok(convertToDTO(updatedCategory));
    }

    // Método para eliminar una categoría (solo para administradores)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Métodos auxiliares para convertir entre Category y CategoryDTO
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(category.getCategoryId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setImageUrl(category.getImageUrl());
        return categoryDTO;
    }

    private Category convertToEntity(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setCategoryId(categoryDTO.getCategoryId());
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        return category;
    }
}
