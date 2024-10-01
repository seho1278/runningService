package com.example.runningservice.dto.post;

import com.example.runningservice.entity.post.ReplyEntity;
import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.enums.PostCategory;
import com.example.runningservice.util.S3FileUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetPostDetailResponseDto {

    private Long id;
    private String memberNickName;
    private Long crewId;
    private String title;
    private PostCategory postCategory;
    private Long activityId;
    private String content;
    private List<String> images = new ArrayList<>();
    private Boolean isNotice;
    private List<ReplyEntity> comment = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GetPostDetailResponseDto of(PostEntity postEntity, S3FileUtil s3FileUtil) {
        return GetPostDetailResponseDto.builder()
            .id(postEntity.getId())
            .crewId(postEntity.getCrewId())
            .memberNickName(postEntity.getMember().getNickName())
            .title(postEntity.getTitle())
            .postCategory(postEntity.getPostCategory())
            .activityId(postEntity.getActivityId())
            .content(postEntity.getContent())
            .images(postEntity.getImages().stream().map(s3FileUtil::createPresignedUrl).collect(
                Collectors.toList()))
            .isNotice(postEntity.getIsNotice())
            .comment(postEntity.getComment())
            .createdAt(postEntity.getCreatedAt())
            .updatedAt(postEntity.getUpdatedAt())
            .build();
    }
}
