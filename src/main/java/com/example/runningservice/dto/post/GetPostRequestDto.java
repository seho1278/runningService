package com.example.runningservice.dto.post;

import com.example.runningservice.enums.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetPostRequestDto {

    @Getter
    @Builder
    public static class Filter {
        private PostCategory postCategory;
    }
}
