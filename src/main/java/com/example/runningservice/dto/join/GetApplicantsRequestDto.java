package com.example.runningservice.dto.join;

import com.example.runningservice.enums.JoinStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class GetApplicantsRequestDto {
    private JoinStatus status;
    private Pageable pageable;
}
