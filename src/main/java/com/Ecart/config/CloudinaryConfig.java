
package com.Ecart.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName = "rkomctj9";

    @Value("${cloudinary.api-key}")
    private String apiKey = "798581775852262";

    @Value("${cloudinary.api-secret:b8bj4R0Wzwrf51XrrlYTN3f2HSY}")
    private String apiSecret;

    
   

    @Bean
    public Cloudinary cloudinary() {

        return new Cloudinary(
            ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
            )
        );
    }
}
