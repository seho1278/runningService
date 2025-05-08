package com.example.runningservice.dto.post.reply;

import com.example.runningservice.entity.post.ReplyEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyResponseDto {
    private Long replyId;
    private Long postId;
    private String memberNickName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReplyResponseDto of(ReplyEntity replyEntity) {
        return ReplyResponseDto.builder()
            .replyId(replyEntity.getId())
            .postId(replyEntity.getPost().getId())
            .memberNickName(replyEntity.getMember().getNickName())
            .content(replyEntity.getContent())
            .createdAt(replyEntity.getCreatedAt())
            .updatedAt(replyEntity.getUpdatedAt())
            .build();
    }
}
