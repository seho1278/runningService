package com.example.runningservice.repository.chat;

public interface ChatJoinRepositoryCustom {
    void deleteAllByMemberIdAndCrewId(Long memberId, Long crewId);
}
