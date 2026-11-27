package com.Ecart.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class CloudinaryStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/webp"
    );

    private final Cloudinary cloudinary;

    public CloudinaryStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadProductImage(Long productId, MultipartFile file) {

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                "Image size must not exceed 5 MB"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                "Only JPG, PNG and WEBP images are allowed"
            );
        }

        try {
            String publicId =
                "product-" + productId + "-" + UUID.randomUUID();

            Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "product_images",
                    "public_id", publicId,
                    "resource_type", "image"
                )
            );

            Object secureUrl = result.get("secure_url");

            if (secureUrl == null) {
                throw new RuntimeException(
                    "Cloudinary did not return an image URL"
                );
            }

            return secureUrl.toString();

        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to read image file",
                e
            );
        } catch (Exception e) {
            throw new RuntimeException(
                "Cloudinary image upload failed",
                e
            );
        }
    }
    
    public void deleteImageByUrl(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {
            String publicId = imageUrl.substring(
                    imageUrl.indexOf("/upload/") + 8
            );

            // Remove version prefix, if present.
            publicId = publicId.replaceFirst("^v\\d+/", "");

            // Remove file extension.
            int extensionIndex = publicId.lastIndexOf(".");
            if (extensionIndex != -1) {
                publicId = publicId.substring(0, extensionIndex);
            }

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "image")
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete Cloudinary image", e);
        }
    }
}