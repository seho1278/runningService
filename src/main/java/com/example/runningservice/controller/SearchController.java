package com.example.runningservice.controller;

import com.example.runningservice.dto.post.GetPostSimpleResponseDto;
import com.example.runningservice.dto.search.SearchPostRequestDto;
import com.example.runningservice.enums.SearchType;
import com.example.runningservice.service.SearchService;
import lombok.RequiredArgsConstructor;
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
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/crew/{crewId}/posts/like")
    public ResponseEntity<Page<GetPostSimpleResponseDto>> searchPosts(@PathVariable Long crewId,
        @RequestParam String keyword,
        @RequestParam(defaultValue = "TITLE_CONTENT") SearchType searchType,
        @PageableDefault(page = 0, size = 10, direction = Direction.DESC, sort = "createdAt") Pageable pageable) {

        SearchPostRequestDto searchPostRequestDto = SearchPostRequestDto.builder()
            .crewId(crewId)
            .keyword(keyword)
            .searchType(searchType)
            .build();

        return ResponseEntity.ok(searchService.searchPost(searchPostRequestDto, pageable));
    }

// Like 쿼리가 아닌 다른 방식으로 검색 기능 구현 예정
//    @GetMapping("/search/crew/{crewId}/posts")
//    public ResponseEntity<Page<PostResponseDto>> searchPostAdvance(@PathVariable Long crewId,
//        @RequestParam String keyword, @RequestParam(required = false) String author,
//        @PageableDefault(page = 0, size = 10, direction = Direction.DESC, sort = "createdAt") Pageable pageable) {
//
//        SearchPostRequestDto searchPostRequestDto = SearchPostRequestDto.builder()
//            .crewId(crewId)
//            .keyword(keyword)
//            .author(author)
//            .build();
//
//        return ResponseEntity.ok(searchService.searchPostAdvance(searchPostRequestDto, pageable));
//    }
}
