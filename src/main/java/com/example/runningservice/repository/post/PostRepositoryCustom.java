package com.example.runningservice.repository.post;

import com.example.runningservice.dto.post.GetPostRequestDto;
import com.example.runningservice.dto.post.GetPostSimpleResponseDto;
import com.example.runningservice.dto.post.PostResponseDto;
import com.example.runningservice.entity.post.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<PostEntity> findAllNotNoticeByCrewIdAndFilter(Long crewId, GetPostRequestDto.Filter filter,
        Pageable pageable);

    Page<GetPostSimpleResponseDto> searchPostsByCrewIdAndKeywordAndAuthor(Long crewId, String keyword, String author, Pageable pageable);
}
