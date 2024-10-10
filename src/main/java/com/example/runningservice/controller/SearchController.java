package com.example.runningservice.controller;

import com.example.runningservice.dto.post.GetPostSimpleResponseDto;
import com.example.runningservice.dto.search.SearchPostRequestDto;
import com.example.runningservice.enums.SearchType;
import com.example.runningservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
@Slf4j
public class SearchController {

    private final SearchService searchService;

    /**
     * Post 검색(Like 쿼리 사용)
     */
    @GetMapping("/crew/{crewId}/posts")
    public ResponseEntity<Page<GetPostSimpleResponseDto>> searchPosts(@PathVariable Long crewId,
        @RequestParam String keyword,
        @RequestParam(defaultValue = "TITLE_CONTENT") SearchType searchType,
        @PageableDefault(direction = Direction.DESC, sort = "createdAt") Pageable pageable) {

        SearchPostRequestDto searchPostRequestDto = SearchPostRequestDto.builder()
            .crewId(crewId)
            .keyword(keyword)
            .searchType(searchType)
            .build();

        return ResponseEntity.ok(searchService.searchPost(searchPostRequestDto, pageable));
    }
}
