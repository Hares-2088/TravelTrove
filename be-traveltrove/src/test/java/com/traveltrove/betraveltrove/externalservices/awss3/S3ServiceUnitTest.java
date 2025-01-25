package com.traveltrove.betraveltrove.externalservices.awss3;

import com.traveltrove.betraveltrove.externalservices.awss3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.lang.reflect.Field;
import java.net.URI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceUnitTest {
    @Mock
    private S3Presigner presigner;

    private S3Service s3Service;

    private final String bucketName = "traveltrove-images";
    private final String region = "us-east-2";
    private final String accessKey = "fake-access-key";
    private final String secretKey = "fake-secret-key";

    @BeforeEach
    void setUp() throws Exception {
        s3Service = new S3Service(bucketName, region, accessKey, secretKey);

        // Use reflection to inject the mock presigner
        Field presignerField = S3Service.class.getDeclaredField("presigner");
        presignerField.setAccessible(true);
        presignerField.set(s3Service, presigner);
    }

    @Test
    void whenGeneratePresignedUrl_thenReturnValidUrl() throws Exception {
        String userId = "auth0-12345";
        String fileName = "test-image.jpg";
        String contentType = "image/jpeg";
        String expectedUrl = "https://s3.amazonaws.com/" + bucketName + "/" + userId + "_UUID_" + fileName;

        // Mocking AWS PutObjectRequest
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(userId + "_UUID_" + fileName)
                .contentType(contentType)
                .build();

        // Mocking the PreSigned URL generation
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = mock(PresignedPutObjectRequest.class);
        URI presignedUrlUri = URI.create(expectedUrl);
        when(presignedPutObjectRequest.url()).thenReturn(presignedUrlUri.toURL());

        when(presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(presignedPutObjectRequest);

        // Execute method
        String presignedUrl = s3Service.generatePresignedUrl(userId, fileName, contentType);

        // Verify behavior and validate result
        assertNotNull(presignedUrl);
        assertEquals(expectedUrl, presignedUrl);
        verify(presigner, times(1)).presignPutObject(any(PutObjectPresignRequest.class));
    }
}