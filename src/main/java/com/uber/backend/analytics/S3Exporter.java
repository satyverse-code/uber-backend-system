package com.uber.backend.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

@Component
public class S3Exporter {
    private static final Logger log = LoggerFactory.getLogger(S3Exporter.class);

    private final boolean enabled;
    private final String bucket;
    private final String prefix;
    private final S3Client s3;
    private final ObjectMapper mapper;

    public S3Exporter(
            @Value("${analytics.export.enabled:false}") boolean enabled,
            @Value("${analytics.s3.bucket:}") String bucket,
            @Value("${analytics.s3.prefix:trip-events/}") String prefix,
            @Value("${AWS_REGION:us-east-1}") String region,
            @Value("${AWS_ACCESS_KEY_ID:}") String accessKey,
            @Value("${AWS_SECRET_ACCESS_KEY:}") String secretKey,
            ObjectMapper mapper
    ) {
        this.enabled = enabled;
        this.bucket = bucket;
        this.prefix = prefix.endsWith("/") ? prefix : prefix + "/";
        this.mapper = mapper;

        AwsCredentialsProvider provider = (accessKey != null && !accessKey.isBlank() && secretKey != null && !secretKey.isBlank())
                ? StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
                : DefaultCredentialsProvider.create();

        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(provider)
                .build();
    }

    public boolean isEnabled() {
        return enabled && bucket != null && !bucket.isBlank();
    }

    public void export(Object event) {
        if (!isEnabled()) return;
        try {
            String key = prefix + Instant.now().toString() + "/" + UUID.randomUUID() + ".json";
            byte[] bytes = mapper.writeValueAsBytes(event);
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("application/json")
                    .build();
            s3.putObject(req, RequestBody.fromBytes(bytes));
        } catch (Exception e) {
            log.warn("S3 export failed", e);
        }
    }
}
