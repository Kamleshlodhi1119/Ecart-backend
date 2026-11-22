package com.Ecart.service;

import com.Ecart.dto.ProductDto;
import com.Ecart.model.Product;
import com.Ecart.repository.ProductRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_IMAGE_PIXELS = 12_000_000;

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final Cloudinary cloudinary;

    public ProductService(
            ProductRepository productRepository,
            ModelMapper modelMapper,
            Cloudinary cloudinary) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.cloudinary = cloudinary;
    }

	public List<ProductDto> getAllActiveProducts() {
        return productRepository.findByActive(true)
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());
    }

    public List<Product> getallProduct() {
        return productRepository.findAll();
    }

    public Product addProduct(Product product) {
        if (product == null
                || product.getName() == null || product.getName().isBlank()
                || product.getCategory() == null || product.getCategory().isBlank()
                || !Double.isFinite(product.getPrice()) || product.getPrice() < 0
                || product.getQuantity() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Product name, category, non-negative price, and non-negative quantity are required");
        }

        Product newProduct = new Product();
        newProduct.setName(product.getName().trim());
        newProduct.setDescription(product.getDescription());
        newProduct.setCategory(product.getCategory().trim());
        newProduct.setPrice(product.getPrice());
        newProduct.setQuantity(product.getQuantity());
        newProduct.setActive(true);
        newProduct.setImageUrl(null);
        return productRepository.save(newProduct);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return modelMapper.map(product, ProductDto.class);
    }

    public List<ProductDto> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndActive(category, true)
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());
    }

    public List<ProductDto> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCaseAndActive(query, true)
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());
    }

    public Optional<Product> getRawProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public String uploadProductImage(Long id, MultipartFile file) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        validateImage(file);

        Map<?, ?> uploadResult;
        try {
            uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "products",
                    "public_id", UUID.randomUUID().toString(),
                    "overwrite", false));
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Could not upload product image to Cloudinary", e);
        }

        Object secureUrl = uploadResult.get("secure_url");
        if (!(secureUrl instanceof String imageUrl) || imageUrl.isBlank()
                || !imageUrl.startsWith("https://")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Cloudinary did not return a secure image URL");
        }

        product.setImageUrl(imageUrl);
        productRepository.save(product);
        return imageUrl;
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An image file is required");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image must be 5 MB or smaller");
        }

        try (ImageInputStream input = ImageIO.createImageInputStream(file.getInputStream())) {
            if (input == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image file");
            }
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported or invalid image file");
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                switch (reader.getFormatName().toUpperCase()) {
                    case "JPEG", "JPG", "PNG", "GIF", "BMP" -> { }
                    default -> throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Only JPEG, PNG, GIF, and BMP images are supported");
                };
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0 || height <= 0 || (long) width * height > MAX_IMAGE_PIXELS) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image dimensions are too large");
                }
                BufferedImage decoded = reader.read(0);
                if (decoded == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image file");
                }
                decoded.flush();
            } finally {
                reader.dispose();
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read image file", e);
        }
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        Optional<Product> product = productRepository.findByIdForUpdate(id);
        if (product.isPresent()) {
            Product existingProduct = product.get();
            existingProduct.setActive(false);
            productRepository.save(existingProduct);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean toggleProductStatus(Long id) {
        Optional<Product> optionalProduct = productRepository.findByIdForUpdate(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setActive(!product.isActive());
            productRepository.save(product);
            return true;
        }
        return false;
    }
}
