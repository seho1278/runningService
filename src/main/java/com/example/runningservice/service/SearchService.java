package com.example.runningservice.service;

import com.example.runningservice.dto.post.GetPostSimpleResponseDto;
import com.example.runningservice.dto.search.SearchPostRequestDto;
import com.example.runningservice.enums.SearchType;
import com.example.runningservice.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<GetPostSimpleResponseDto> searchPost(SearchPostRequestDto requestDto,
        Pageable pageable) {

        String keyword = requestDto.getKeyword();
        SearchType searchType = requestDto.getSearchType();
        Long crewId = requestDto.getCrewId();

        return postRepository.searchPostsByCrewIdAndKeyword(
            crewId, keyword, searchType, pageable);
    }

    @Transactional(readOnly = true)
    public Page<GetPostSimpleResponseDto> searchByFullText(SearchPostRequestDto requestDto,
        Pageable pageable) {

        String keyword = requestDto.getKeyword();
        String searchType = requestDto.getSearchType().name();
        Long crewId = requestDto.getCrewId();

        return postRepository.searchPostByFullText(crewId, searchType, keyword, pageable);
    }
}
