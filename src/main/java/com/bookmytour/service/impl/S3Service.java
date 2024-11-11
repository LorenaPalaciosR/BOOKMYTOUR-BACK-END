package com.bookmytour.service.impl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Path;

@Service
public class S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;
    private S3Client s3Client;

    @PostConstruct
    public void initializeS3Client() {
        // Crear una única instancia de S3Client al inicio del servicio
        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String uploadFile(String fileName, Path filePath) {
        try {
            // Configurar la solicitud de carga
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            // Subir el archivo al bucket S3
            PutObjectResponse response = s3Client.putObject(putRequest, filePath);

            // Verificar el éxito de la carga
            if (response.sdkHttpResponse().isSuccessful()) {
                // Retornar la URL del archivo
                return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toExternalForm();
            } else {
                throw new RuntimeException("Error al subir el archivo a S3: " + response.sdkHttpResponse().statusText().orElse("Desconocido"));
            }
        } catch (S3Exception e) {
            throw new RuntimeException("Error al interactuar con S3: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al subir el archivo: " + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void closeS3Client() {
        // Liberar recursos si es necesario (opcional)
        if (s3Client != null) {
            s3Client.close();
        }
    }
}
