package com.example.runningservice.dto.post.reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReplyRequestDto {
    private Long postId;
    private String content;
}
