package com.Ecart.service;

import com.Ecart.model.Product;
import com.Ecart.repository.ProductRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceImageTest {

    private ProductRepository productRepository;
    private Product product;
    private ProductService productService;
    private Cloudinary cloudinary;
    private Uploader uploader;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        cloudinary = mock(Cloudinary.class);
        uploader = mock(Uploader.class);
        product = new Product();
        product.setId(7L);
        productService = new ProductService(productRepository, mock(ModelMapper.class), cloudinary);
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));
        when(productRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void uploadStoresSecureCloudinaryUrlOnProduct() throws Exception {
        String secureUrl = "https://res.cloudinary.com/test-cloud/image/upload/products/test.png";
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of("secure_url", secureUrl));

        String imageUrl = productService.uploadProductImage(7L, pngUpload());

        assertEquals(secureUrl, imageUrl);
        assertEquals(imageUrl, product.getImageUrl());
        verify(productRepository).save(product);
        verify(uploader).upload(any(byte[].class), anyMap());
    }

    @Test
    void uploadRejectsNonImageAndDoesNotSaveIt() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "image", "picture.png", "image/png", "not an image".getBytes());

        assertThrows(ResponseStatusException.class,
                () -> productService.uploadProductImage(7L, invalidFile));

        verify(productRepository, never()).save(any(Product.class));
        verify(uploader, never()).upload(any(byte[].class), anyMap());
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

        Product saved = productService.addProduct(request);

        assertEquals(null, saved.getId());
        assertEquals("Chair", saved.getName());
        assertEquals("Home", saved.getCategory());
        assertTrue(saved.isActive());
        assertEquals(null, saved.getImageUrl());
    }

    @Test
    void addProductRejectsNegativeStock() {
        Product request = new Product();
        request.setName("Chair");
        request.setCategory("Home");
        request.setPrice(25.5);
        request.setQuantity(-1);

        assertThrows(ResponseStatusException.class, () -> productService.addProduct(request));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProductDeactivatesItToPreserveOrderHistory() {
        boolean removed = productService.deleteProduct(7L);

        assertTrue(removed);
        assertFalse(product.isActive());
        verify(productRepository).save(product);
    }

    private MockMultipartFile pngUpload() throws Exception {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        return new MockMultipartFile("image", "client-name.png", "image/png", output.toByteArray());
    }
}
