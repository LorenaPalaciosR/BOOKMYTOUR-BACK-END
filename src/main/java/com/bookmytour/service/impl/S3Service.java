package com.bookmytour.service.impl;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public String uploadFile(String fileName, Path filePath) {
        // Crear una instancia de S3Client con los valores de configuración
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))  // Utilizar la región especificada en application.properties
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();

        // Configurar la solicitud de carga
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        // Subir el archivo al bucket S3
        s3Client.putObject(putRequest, filePath);

        // Retornar la URL del archivo
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toExternalForm();
    }
}
