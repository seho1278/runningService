package com.example.runningservice.service;

import com.example.runningservice.dto.post.GetPostSimpleResponseDto;
import com.example.runningservice.dto.post.PostResponseDto;
import com.example.runningservice.dto.search.SearchPostRequestDto;
import com.example.runningservice.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<GetPostSimpleResponseDto> searchPost(SearchPostRequestDto requestDto, Pageable pageable) {

        String keyword = requestDto.getKeyword();
        String author = requestDto.getAuthor();
        Long crewId = requestDto.getCrewId();

        return postRepository.searchPostsByCrewIdAndKeywordAndAuthor(
            crewId, keyword, author, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> searchPostAdvance(SearchPostRequestDto requestDto, Pageable pageable) {

        String keyword = requestDto.getKeyword();
        String author = requestDto.getAuthor();
        Long crewId = requestDto.getCrewId();
        return null;
    }
}
