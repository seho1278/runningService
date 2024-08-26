package com.example.runningservice.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageUtil {

    public static Pageable getSortedPageable(Pageable pageable, String defaultSortBy,
        Sort.Direction sortDirection, int defaultPageNumber, int defaultPageSize) {

        Sort defaultSort = Sort.by(sortDirection, defaultSortBy);
        // Pageable에서 sort 정보를 추출 (sort=정렬기준(ex.createdAt) 이 있으면 isSorted()==true)
        int pageNumber =
            pageable != null ? pageable.getPageNumber() : defaultPageNumber; // 기본 페이지 번호 0
        int pageSize = (pageable != null && pageable.getPageSize() > 0) ? pageable.getPageSize()
            : defaultPageSize; // 기본 페이지 크기 10

        Sort sortOrder =
            pageable == null || pageable.getSort().isUnsorted() ? defaultSort : pageable.getSort();

        // 정렬 순서 설정
        return PageRequest.of(pageNumber, pageSize, sortOrder);
    }
}
