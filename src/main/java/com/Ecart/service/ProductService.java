
package com.Ecart.service;

import com.Ecart.dto.ProductDto;
import com.Ecart.model.Product;
import com.Ecart.repository.ProductRepository;

import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CloudinaryStorageService storageService;
    private final CloudinaryStorageService cloudinaryStorageService;

   

    public ProductService(ProductRepository productRepository, ModelMapper modelMapper,
			CloudinaryStorageService storageService, CloudinaryStorageService cloudinaryStorageService) {
		super();
		this.productRepository = productRepository;
		this.modelMapper = modelMapper;
		this.storageService = storageService;
		this.cloudinaryStorageService = cloudinaryStorageService;
	}

	// ADD PRODUCT
    @Transactional
    public ProductDto addProduct(Product product) {

        validateProduct(product);

        Product newProduct = new Product();

        newProduct.setName(product.getName().trim());
        newProduct.setDescription(product.getDescription());
        newProduct.setCategory(product.getCategory().trim());
        newProduct.setPrice(product.getPrice());
        newProduct.setQuantity(product.getQuantity());
        newProduct.setActive(true);

        // Image will be uploaded separately
        newProduct.setImageUrl(null);

        Product savedProduct =
                productRepository.save(newProduct);

        return convertToDto(savedProduct);
    }

    // UPLOAD OR REPLACE PRODUCT IMAGE
    @Transactional
    public ProductDto uploadProductImage(
            Long id,
            MultipartFile file) {

        // Find existing product
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Product not found with ID: " + id
                    )
                );

        // Upload image to Cloudinary
        String imageUrl =
                storageService.uploadProductImage(id, file);

        // Update existing imageUrl column
        product.setImageUrl(imageUrl);

        // Save updated product
        Product savedProduct =
                productRepository.save(product);

        return convertToDto(savedProduct);
    }

    // UPDATE PRODUCT
    @Transactional
    public ProductDto updateProduct(
            Long id,
            Product updatedProduct) {

        validateProduct(updatedProduct);

        Product existingProduct =
                productRepository.findById(id)
                    .orElseThrow(() ->
                        new RuntimeException(
                            "Product not found with ID: " + id
                        )
                    );

        existingProduct.setName(
            updatedProduct.getName().trim()
        );

        existingProduct.setDescription(
            updatedProduct.getDescription()
        );

        existingProduct.setCategory(
            updatedProduct.getCategory().trim()
        );

        existingProduct.setPrice(
            updatedProduct.getPrice()
        );

        existingProduct.setQuantity(
            updatedProduct.getQuantity()
        );

        // Do not overwrite existing imageUrl

        Product savedProduct =
                productRepository.save(existingProduct);

        return convertToDto(savedProduct);
    }

    // GET ALL ACTIVE PRODUCTS
    public List<ProductDto> getAllActiveProducts() {

        return productRepository.findByActive(true)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // GET ALL PRODUCTS
    public List<Product> getallProduct() {

        return productRepository.findAll();
    }

    // GET PRODUCT BY ID
    public ProductDto getProductById(Long id) {

        Product product =
                productRepository.findById(id)
                    .orElseThrow(() ->
                        new RuntimeException(
                            "Product not found with ID: " + id
                        )
                    );

        return convertToDto(product);
    }

    // GET PRODUCTS BY CATEGORY
    public List<ProductDto> getProductsByCategory(
            String category) {

        return productRepository
                .findByCategoryAndActive(category, true)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // SEARCH PRODUCTS
    public List<ProductDto> searchProducts(String query) {

        return productRepository
                .findByNameContainingIgnoreCaseAndActive(
                    query, true
                )
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // GET RAW PRODUCT
    public Optional<Product> getRawProductById(Long id) {

        return productRepository.findById(id);
    }

    // SAVE PRODUCT
    @Transactional
    public Product save(Product product) {

        return productRepository.save(product);
    }

    // DELETE PRODUCT - SOFT DELETE
//    @Transactional
//    public boolean deleteProduct(Long id) {
//
//        Product product =
//                productRepository.findByIdForUpdate(id)
//                    .orElseThrow(() ->
//                        new RuntimeException(
//                            "Product not found with ID: " + id
//                        )
//                    );
//
//        product.setActive(false);
//
//        productRepository.save(product);
//
//        return true;
//    }
    
    @Transactional
    public Boolean deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String imageUrl = product.getImageUrl();

        product.setActive(false);
        productRepository.save(product);

        if (imageUrl != null && !imageUrl.isBlank()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            cloudinaryStorageService.deleteImageByUrl(imageUrl);
                        }
                    }
            );
        }

        return true;
    }

    // TOGGLE PRODUCT STATUS
    @Transactional
    public boolean toggleProductStatus(Long id) {

        Product product =
                productRepository.findByIdForUpdate(id)
                    .orElseThrow(() ->
                        new RuntimeException(
                            "Product not found with ID: " + id
                        )
                    );

        product.setActive(!product.isActive());

        productRepository.save(product);

        return true;
    }

    // ENTITY TO DTO
    private ProductDto convertToDto(Product product) {

        return modelMapper.map(
            product,
            ProductDto.class
        );
    }

    // PRODUCT VALIDATION
    private void validateProduct(Product product) {

        if (product == null) {
            throw new IllegalArgumentException(
                "Product cannot be empty"
            );
        }

        if (product.getName() == null ||
                product.getName().isBlank()) {

            throw new IllegalArgumentException(
                "Product name is required"
            );
        }

        if (product.getCategory() == null ||
                product.getCategory().isBlank()) {

            throw new IllegalArgumentException(
                "Product category is required"
            );
        }

        if (!Double.isFinite(product.getPrice()) ||
                product.getPrice() < 0) {

            throw new IllegalArgumentException(
                "Invalid product price"
            );
        }

        if (product.getQuantity() < 0) {

            throw new IllegalArgumentException(
                "Invalid product quantity"
            );
        }
    }
}
