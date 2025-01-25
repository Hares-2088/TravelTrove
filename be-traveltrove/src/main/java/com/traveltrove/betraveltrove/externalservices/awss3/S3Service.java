package com.traveltrove.betraveltrove.externalservices.awss3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
public class S3Service {

    private final String bucketName;
    private final S3Presigner presigner;

    public S3Service(
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.region}") String region,
            @Value("${aws.s3.access-key}") String accessKey,
            @Value("${aws.s3.secret-access-key}") String secretKey) {

        this.bucketName = bucketName;

        log.info("Initializing S3 Presigner with region: {}", region);

        this.presigner = S3Presigner.builder()
                .region(Region.of(region)) // ✅ No more NullPointerException
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }

    public S3Presigner getPresigner() {
        return presigner;
    }


    public String generatePresignedUrl(String userId, String fileName, String contentType) {
        log.info("Generating pre-signed URL for user: {} and file: {}", userId, fileName);

        String uniqueFileName = userId.replace("|", "-") + "_" + UUID.randomUUID() + "_" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

        log.info("Pre-signed URL generated successfully: {}", presignedRequest.url());

        return presignedRequest.url().toString();
    }


}
