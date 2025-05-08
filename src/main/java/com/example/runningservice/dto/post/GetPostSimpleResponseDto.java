package com.example.runningservice.dto.post;

import com.example.runningservice.entity.post.PostEntity;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class GetPostSimpleResponseDto {

    private Long postId;
    private String postTitle;
    private String writerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GetPostSimpleResponseDto of(PostEntity postEntity) {
        return GetPostSimpleResponseDto.builder()
            .postId(postEntity.getId())
            .postTitle(postEntity.getTitle())
            .writerName(postEntity.getMember().getNickName())
            .createdAt(postEntity.getCreatedAt())
            .updatedAt(postEntity.getUpdatedAt())
            .build();
    }

    @QueryProjection
    public GetPostSimpleResponseDto(Long postId, String postTitle, String writerName,
        LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.postId = postId;
        this.postTitle = postTitle;
        this.writerName = writerName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
