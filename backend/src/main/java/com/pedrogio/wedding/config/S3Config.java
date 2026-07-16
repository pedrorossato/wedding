package com.pedrogio.wedding.config;

import com.pedrogio.wedding.config.WeddingProperties.Aws;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    private final Aws aws;

    public S3Config(WeddingProperties properties) {
        this.aws = properties.getAws();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .region(Region.of(aws.getRegion()))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(aws.getAccessKey(), aws.getSecretKey())))
            .build();
    }
}
