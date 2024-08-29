package com.example.runningservice.util;

import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@Component
@RequiredArgsConstructor
@Slf4j
public class S3FileUtil {

    private final S3Client amazonS3Client;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    /**
     * fileName을 이용해 이미지 url을 조회한다.
     */
    public String getImgUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }

    /**
     * s3 이미지 업로드 :: s3 버킷에 multipartFile을 fileName으로 저장
     */
    @Async
    public void putObject(String fileName, MultipartFile multipartFile) {
        PutObjectRequest putObjectRequest;
        try {
            putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(multipartFile.getContentType())
                .contentLength(multipartFile.getSize())
                .build();

            amazonS3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(multipartFile.getInputStream(),
                    multipartFile.getSize()));
        } catch (IOException e) {
            log.error(ErrorCode.FAILED_UPLOAD_IMAGE.name(), e);
            throw new CustomException(ErrorCode.FAILED_UPLOAD_IMAGE);
        }
    }

    /**
     * 버킷에서 파일명의 데이터를 삭제한다.
     */
    public void deleteObject(String fileName) {
        amazonS3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .build());
    }
}
