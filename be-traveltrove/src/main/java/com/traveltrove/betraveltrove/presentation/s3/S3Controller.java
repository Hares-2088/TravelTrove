package com.traveltrove.betraveltrove.presentation.s3;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.traveltrove.betraveltrove.externalservices.awss3.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/s3")
public class S3Controller {

    private final S3Service s3Service;

    @Autowired
    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/generate-url")
    public Map<String, String> generatePresignedUrl(@RequestHeader("Authorization") String authToken,
                                                    @RequestBody Map<String, String> request) {
        String fileName = request.get("fileName");
        String contentType = request.get("contentType");

        String userId = extractUserIdFromToken(authToken);
        log.info("Received request from user: {} to generate pre-signed URL for file: {}", userId, fileName);

        String presignedUrl = s3Service.generatePresignedUrl(userId, fileName, contentType);

        return Map.of(
                "presignedUrl", presignedUrl,
                "contentType", contentType  // ✅ Ensure `contentType` is included in the response
        );
    }


    private String extractUserIdFromToken(String token) {
        try {
            return JWT.decode(token.replace("Bearer ", "")).getSubject();
        } catch (JWTDecodeException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}
