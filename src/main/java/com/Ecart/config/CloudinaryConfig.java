package com.Ecart.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {

        if (isBlank(cloudName)
                || isBlank(apiKey)
                || isBlank(apiSecret)) {
            throw new IllegalStateException(
                "Cloudinary credentials are missing. Configure "
                + "CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, "
                + "and CLOUDINARY_API_SECRET."
            );
        }

        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName.trim(),
            "api_key", apiKey.trim(),
            "api_secret", apiSecret.trim(),
            "secure", true
        ));
    }

    private boolean isBlank(String value) {
        return value == null
            || value.isBlank()
            || value.startsWith("REPLACE_WITH_")
            || value.startsWith("XXXXXXXX");
    }
    
    
    
}