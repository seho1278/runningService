package com.example.runningservice.dto.post;

import com.example.runningservice.entity.post.PostEntity;
import com.example.runningservice.entity.post.ReplyEntity;
import com.example.runningservice.enums.PostCategory;
import com.example.runningservice.util.S3FileUtil;
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
public class PostResponseDto {

    private Long postId;
    private Long memberId;
    private Long crewId;
    private Long activityId;
    private String title;
    private PostCategory postCategory;
    private String content;
    private List<String> imageUrls;
    private Boolean isNotice;
    private List<ReplyEntity> comment;

    public static PostResponseDto of(PostEntity postEntity, S3FileUtil s3FileUtil) {
        return PostResponseDto.builder()
            .postId(postEntity.getId())
            .memberId(postEntity.getMember().getId())
            .crewId(postEntity.getCrewId())
            .activityId(postEntity.getActivityId())
            .title(postEntity.getTitle())
            .postCategory(postEntity.getPostCategory())
            .content(postEntity.getContent())
            .imageUrls(postEntity.getImages().stream().map(s3FileUtil::createPresignedUrl).collect(
                Collectors.toList()))
            .isNotice(postEntity.getIsNotice())
            .comment(postEntity.getComment())
            .build();
    }
}
