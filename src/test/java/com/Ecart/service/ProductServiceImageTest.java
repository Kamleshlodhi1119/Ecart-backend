
package com.Ecart.service;

import com.Ecart.dto.ProductDto;
import com.Ecart.model.Product;
import com.Ecart.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductServiceImageTest {

    private ProductRepository productRepository;
    private CloudinaryStorageService storageService;
    private Product product;
    private ProductService productService;

    @BeforeEach
    void setUp() {

        productRepository = mock(ProductRepository.class);
        storageService = mock(CloudinaryStorageService.class);

        ModelMapper modelMapper = new ModelMapper();

        product = new Product();
        product.setId(7L);
        product.setName("Desk Lamp");
        product.setCategory("Home");
        product.setPrice(45.0);
        product.setQuantity(10);
        product.setActive(true);

        // ProductService requires four constructor arguments.
        // Both Cloudinary dependencies use the same mock.
        productService = new ProductService(
                productRepository,
                modelMapper,
                storageService,
                storageService
        );

        when(productRepository.findById(7L))
                .thenReturn(Optional.of(product));

        when(productRepository.findByIdForUpdate(7L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void uploadStoresSecureCloudinaryUrlOnProduct() throws Exception {

        String secureUrl =
                "https://res.cloudinary.com/test-cloud/image/upload/product_images/product-7-uuid.png";

        MockMultipartFile file = pngUpload();

        // Your actual service expects String, not UploadResult.
        when(storageService.uploadProductImage(
                eq(7L),
                any(MultipartFile.class)
        )).thenReturn(secureUrl);

        ProductDto result =
                productService.uploadProductImage(7L, file);

        assertEquals(secureUrl, result.getImageUrl());
        assertEquals(secureUrl, product.getImageUrl());

        verify(storageService).uploadProductImage(
                eq(7L),
                any(MultipartFile.class)
        );

        verify(productRepository).save(product);
    }

    @Test
    void uploadRejectsEmptyFileAndDoesNotSaveIt() {

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "picture.png",
                "image/png",
                new byte[0]
        );

        // Simulate validation performed by CloudinaryStorageService.
        when(storageService.uploadProductImage(
                eq(7L),
                any(MultipartFile.class)
        )).thenThrow(
                new IllegalArgumentException("File cannot be empty")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.uploadProductImage(7L, emptyFile)
        );

        verify(productRepository, never()).save(any(Product.class));

        assertNull(product.getImageUrl());
    }

    @Test
    void addProductIgnoresClientIdentityAndImageAndStartsActive() {

        Product request = new Product();

        request.setId(99L);
        request.setName("  Chair ");
        request.setCategory(" Home ");
        request.setPrice(25.5);
        request.setQuantity(4);
        request.setActive(false);
        request.setImageUrl("/products/not-owned.png");

        ProductDto saved = productService.addProduct(request);

        assertNull(saved.getId());
        assertEquals("Chair", saved.getName());
        assertEquals("Home", saved.getCategory());
        assertTrue(saved.isActive());
        assertNull(saved.getImageUrl());
    }

    @Test
    void addProductRejectsNegativeStock() {

        Product request = new Product();

        request.setName("Chair");
        request.setCategory("Home");
        request.setPrice(25.5);
        request.setQuantity(-1);

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.addProduct(request)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProductDeactivatesProductAndDeletesCloudinaryImageAfterCommit() {

        String imageUrl =
                "https://res.cloudinary.com/test-cloud/image/upload/product_images/product-7-uuid.png";

        product.setImageUrl(imageUrl);

        TransactionSynchronizationManager.initSynchronization();

        try {

            Boolean removed = productService.deleteProduct(7L);

            assertTrue(removed);
            assertFalse(product.isActive());

            verify(productRepository).save(product);

            // Image must not be deleted before transaction commit.
            verify(storageService, never()).deleteImageByUrl(any());

            // Simulate transaction commit.
            List<TransactionSynchronization> synchronizations =
                    TransactionSynchronizationManager.getSynchronizations();

            for (TransactionSynchronization synchronization : synchronizations) {
                synchronization.afterCommit();
            }

            verify(storageService).deleteImageByUrl(imageUrl);

        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    private MockMultipartFile pngUpload() throws Exception {

        BufferedImage image = new BufferedImage(
                2,
                2,
                BufferedImage.TYPE_INT_RGB
        );

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        ImageIO.write(image, "png", output);

        return new MockMultipartFile(
                "file",
                "client-name.png",
                "image/png",
                output.toByteArray()
        );
    }
}
