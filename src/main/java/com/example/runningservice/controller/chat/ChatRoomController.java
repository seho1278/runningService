package com.example.runningservice.controller.chat;

import com.example.runningservice.dto.chat.ChatRoomDetailsDto;
import com.example.runningservice.dto.chat.ChatRoomRequestDto;
import com.example.runningservice.entity.chat.ChatRoomEntity;
import com.example.runningservice.service.chat.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/crew/{crew_id}")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 크루 전체 채팅방 조회
    @GetMapping("/{member_id}/chatrooms")
    public ResponseEntity<Map<ChatRoomEntity, ChatRoomDetailsDto>> getCrewChatRoomList(@PathVariable("crew_id") Long crewId,
                                                                                       @PathVariable("member_id") Long memberId) {
        return ResponseEntity.ok(chatRoomService.getCrewChatRoomListForMember(crewId, memberId));
    }

    @PostMapping("/chatroom/personal")
    public ResponseEntity<Void> createPersonalChatRoom(@PathVariable("crew_id") Long crewId,
                                                       @RequestParam("memberA_id") Long memberAId,
                                                       @RequestParam("memberB_id") Long memberBId) {
        chatRoomService.createPersonalChatRoom(crewId, memberAId, memberBId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/chatroom/{chatroom_id}/join/{join_member_id}")
    public ResponseEntity<Void> joinChatRoom(@PathVariable("crew_id") Long crewId,
                                             @PathVariable("chatroom_id") Long roomId,
                                             @PathVariable("join_member_id") Long memberId) {
        chatRoomService.joinChatRoom(crewId, roomId, memberId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/chatroom/{chatroom_id}/enter/{enter_member_id}")
    public ResponseEntity<Void> enterChatRoom(@PathVariable("crew_id") Long crewId,
                                              @PathVariable("chatroom_id") Long roomId,
                                              @PathVariable("enter_member_id") Long memberId) {
        chatRoomService.enterChatRoom(crewId, roomId, memberId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/chatroom/{chatroom_id}/leave/{leave_member_id}")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable("crew_id") Long crewId,
                                              @PathVariable("chatroom_id") Long roomId,
                                              @PathVariable("leave_member_id") Long memberId) {
        chatRoomService.leaveChatRoom(crewId, roomId, memberId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/chatroom/{chatroom_id}/ejection/{ejection_member_id}")
    public ResponseEntity<Void> ejectionChatRoom(@PathVariable("crew_id") Long crewId,
                                                 @PathVariable("chatroom_id") Long roomId,
                                                 @PathVariable("ejection_member_id") Long memberId,
                                                 @RequestParam("admin_id") Long adminId) {
        chatRoomService.ejectionChatRoom(crewId, roomId, memberId, adminId);
        return ResponseEntity.ok().build();
    }

}
