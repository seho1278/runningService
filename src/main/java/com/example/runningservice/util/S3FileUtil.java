package com.example.runningservice.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
@RequiredArgsConstructor
public class S3FileUtil {

    private final AmazonS3 amazonS3Client;

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
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        PutObjectRequest putObjectRequest;
        try {
            putObjectRequest = new PutObjectRequest(
                bucketName,
                fileName,
                multipartFile.getInputStream(),
                objectMetadata);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAILED_UPLOAD_IMAGE);
        }

        amazonS3Client.putObject(putObjectRequest);
    }

    /**
     * 버킷에서 파일명의 데이터를 삭제한다.
     */
    public void deleteObject(String fileName) {

        amazonS3Client.deleteObject(bucketName, fileName);
    }
}
