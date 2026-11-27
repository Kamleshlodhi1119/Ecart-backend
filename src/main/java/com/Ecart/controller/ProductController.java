package com.Ecart.controller;

import com.Ecart.dto.ProductDto;
import com.Ecart.model.Product;
import com.Ecart.service.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ADD PRODUCT
    @PostMapping("/additems")
    public ResponseEntity<ProductDto> addProduct(
            @RequestBody Product product) {

        return ResponseEntity.ok(
            productService.addProduct(product)
        );
    }

    // UPLOAD PRODUCT IMAGE
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<ProductDto> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
            productService.uploadProductImage(id, file)
        );
    }

    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return ResponseEntity.ok(
            productService.updateProduct(id, product)
        );
    }

    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            productService.getProductById(id)
        );
    }

    // GET ALL ACTIVE PRODUCTS
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {

        return ResponseEntity.ok(
            productService.getAllActiveProducts()
        );
    }

    // GET ALL PRODUCTS
    @GetMapping("/allitems")
    public ResponseEntity<List<Product>> getAllItems() {

        return ResponseEntity.ok(
            productService.getallProduct()
        );
    }

    // GET PRODUCTS BY CATEGORY
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
            productService.getProductsByCategory(category)
        );
    }

    // SEARCH PRODUCTS
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(
            @RequestParam String query) {

        return ResponseEntity.ok(
            productService.searchProducts(query)
        );
    }

    // DELETE PRODUCT - SOFT DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteProduct(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            productService.deleteProduct(id)
        );
    }

    // TOGGLE PRODUCT STATUS
    @PutMapping("/toggle-status/{id}")
    public ResponseEntity<Boolean> toggleProductStatus(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            productService.toggleProductStatus(id)
        );
    }
}
