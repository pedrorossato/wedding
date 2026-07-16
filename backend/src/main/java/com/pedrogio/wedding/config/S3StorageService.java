package com.pedrogio.wedding.config;

import com.pedrogio.wedding.config.WeddingProperties.Aws;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final String bucket;
    private final String region;

    public S3StorageService(S3Client s3Client, WeddingProperties properties) {
        this.s3Client = s3Client;
        Aws aws = properties.getAws();
        this.bucket = aws.getBucket();
        this.region = aws.getRegion();
    }

    public ImageUpload upload(String prefix, MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String key = prefix + "/" + UUID.randomUUID() + extension;

        try {
            s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build(), RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException("Falha ao fazer upload da imagem", e);
        }

        String url = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
        return new ImageUpload(key, url);
    }

    public void delete(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build());
    }

    public record ImageUpload(String s3Key, String imageUrl) {}
}
