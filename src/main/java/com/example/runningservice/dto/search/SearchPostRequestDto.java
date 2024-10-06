package com.example.runningservice.dto.search;

import com.example.runningservice.enums.SearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchPostRequestDto {
    private Long crewId;
    private String keyword;
    private String author;
    private SearchType searchType;

}
