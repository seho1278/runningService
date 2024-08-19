package com.example.runningservice.dto;

import com.example.runningservice.enums.JoinStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class GetJoinApplicationsDto {
    private JoinStatus status;
    private Pageable pageable;
}
