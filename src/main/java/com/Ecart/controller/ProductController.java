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
	 	super();
	 	this.productService = productService;
	 }

	@GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllActiveProducts());
    }

    @PostMapping("/additems")
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/allitems")
    public ResponseEntity<List<Product>> getallProduct() {
        return ResponseEntity.ok(productService.getallProduct());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productService.searchProducts(query));
    }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile file) {
        return ResponseEntity.ok(productService.uploadProductImage(id, file));
    }

    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.ok("Product removed from the catalog.");
        } else {
            return ResponseEntity.badRequest().body("Product not found or could not be deleted.");
        }
    }

    @PutMapping("/toggle-status/{id}")
    public ResponseEntity<?> toggleProductStatus(@PathVariable Long id) {
        boolean updated = productService.toggleProductStatus(id);
        return updated
                ? ResponseEntity.ok("Product status updated successfully.")
                : ResponseEntity.badRequest().body("Product not found.");
    }
}
