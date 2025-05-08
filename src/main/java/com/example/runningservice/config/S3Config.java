package com.example.runningservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

@Configuration
public class S3Config {

    @Value("${aws.s3.roleArn}")
    private String roleARN;

    @Value("${aws.s3.region}")
    private Region region;

    @Bean
    public StsClient stsClient() {
        return StsClient.create();
    }

    @Bean
    public AwsCredentialsProvider credentialsProvider(StsClient stsClient) {
        return StsAssumeRoleCredentialsProvider.builder()
            .stsClient(stsClient)
            .refreshRequest(AssumeRoleRequest.builder()
                .roleArn(roleARN)
                .roleSessionName("tempSession")
                .build())
            .build();
    }

    @Bean
    public S3Client amazonS3Client(AwsCredentialsProvider credentialsProvider) {
        return S3Client.builder()
            .credentialsProvider(credentialsProvider)
            .region(region)
            .build();
    }
}
