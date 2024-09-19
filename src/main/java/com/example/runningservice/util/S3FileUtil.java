package com.example.runningservice.util;

import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;


@Component
@RequiredArgsConstructor
@Slf4j
public class S3FileUtil {

    private final S3Client amazonS3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private final Map<String, PresignedUrl> signedUrlCache = new HashMap<>();

    private final Duration duration = Duration.ofMinutes(10);

    @AllArgsConstructor
    private static class PresignedUrl {

        private String url;
        private LocalDateTime expiration;
    }

    /**
     * fileName을 이용해 이미지 url을 조회한다.
     */
    public String getImgUrl(String fileName) {
        return fileName;
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

    /**
     * 인증된 파일 URL 조회
     */
    public String createPresignedUrl(String keyName) {
        if (verifyUrl(keyName)) {
            return signedUrlCache.get(keyName).url;
        }

        try (S3Presigner presigner = S3Presigner.create()) {

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(duration) // 10분 간 해당 링크로 이미지 조회가 가능
                .getObjectRequest(objectRequest)
                .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

            LocalDateTime expirationDate = LocalDateTime.now().plus(duration);

            String presignedUrl = presignedRequest.url().toExternalForm();
            signedUrlCache.put(keyName, new PresignedUrl(presignedUrl, expirationDate));

            return presignedUrl;
        }
    }

    private boolean verifyUrl(String keyName) {
        PresignedUrl presignedUrl = signedUrlCache.get(keyName);

        return presignedUrl != null &&
            presignedUrl.expiration.isAfter(LocalDateTime.now().plusMinutes(1));
    }

    public String uploadFileAndReturnFileName(String prefix, Long id, MultipartFile image) {

        String defaultImageName = prefix + "-default";

        if (image != null && !image.isEmpty()) {
            String fileName = prefix + "-" + id;
            putObject(fileName, image);

            return getImgUrl(fileName);
        } else { // 크루 이미지가 없으면 기본 이미지로 사용
            return getImgUrl(defaultImageName);
        }
    }

    public List<String> uploadFilesAndReturnFileNames(String prefix, Long id, List<MultipartFile> images) {

        List<String> fileUrls = new ArrayList<>();
        if (!images.isEmpty()) {
            for (MultipartFile image : images) {
                String fileName = prefix + "-" + id + "-" + images.indexOf(image);
                putObject(fileName, image);
                fileUrls.add(getImgUrl(fileName));
            }
        }
        return fileUrls;
    }


}
