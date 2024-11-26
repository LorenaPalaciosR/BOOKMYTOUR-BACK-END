package com.bookmytour.service.impl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.tika.Tika;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
      try {
          s3Client = S3Client.builder()
                  .region(Region.of(region))
                  .credentialsProvider(StaticCredentialsProvider.create(
                          AwsBasicCredentials.create(accessKey, secretKey)))
                  .build();
      } catch  (RuntimeException e){
          throw new RuntimeException("No se pudo conectar al bucket");

      }
    }
    public String uploadFileToS3(MultipartFile file, String bucketName) {
        try {
            String key = "tours/" + file.getOriginalFilename().replaceAll(" ", "_");

            // Detectar el tipo MIME usando Apache Tika
            Tika tika = new Tika();
            String contentType = tika.detect(file.getInputStream());
            System.out.println("Tipo MIME detectado: " + contentType);

            // Crear la solicitud con el tipo MIME detectado
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket("arn:aws:s3:us-east-1:767397661704:accesspoint/imagenesbucketsback")
                    .key(key)
                    .contentType(contentType)
                    .build();

            // Subir el archivo a S3
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // Retornar la URL pública del archivo
            return "https://" + "imagesbucketsback.s3.us-east-1.amazonaws.com/" + key;
        } catch (IOException e) {
            throw new RuntimeException("Error al subir archivo a S3", e);
        }
    }
    public void deleteFileFromS3(String fileUrl) {
        String bucketName = "imagesbucketsback";
        String key = fileUrl.substring(fileUrl.indexOf("tours/")); // Extrae la clave desde la URL
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    /*
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
 */
    @PreDestroy
    public void closeS3Client() {
        // Liberar recursos si es necesario (opcional)
        if (s3Client != null) {
            s3Client.close();
        }
    }



}
