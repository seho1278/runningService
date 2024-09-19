package com.example.runningservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "사용 불가한 토큰입니다."),
    UNABLE_TO_GET_TOKEN(HttpStatus.BAD_REQUEST, "발급된 토큰이 없습니다."),
    CHECK_HEADER_BEARER(HttpStatus.BAD_REQUEST, "토큰의 접두사를 확인하세요."),
    INVALID_LOGIN(HttpStatus.BAD_REQUEST, "사용자의 비밀번호가 일치하지 않습니다."),
    ALREADY_EXIST_LOGINID(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "해당 이메일로 가입한 내역이 있습니다."),
    ALREADY_EXIST_PHONE(HttpStatus.BAD_REQUEST, "해당 전화번호로 가입한 내역이 있습니다."),
    NO_VALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효한 리프레시 토큰이 없습니다."),
    FAILED_UPLOAD_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "잘못된 인증코드입니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "인증되지 않은 이메일 입니다."),
    ENCRYPTION_ERROR(HttpStatus.BAD_REQUEST, "암호화 과정에 문제가 발생했습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NOT_FOUND_CREW(HttpStatus.BAD_REQUEST, "크루를 찾을 수 없습니다."),
    NOT_FOUND_APPLY(HttpStatus.BAD_REQUEST, "해당 신청내역을 찾을 수 없습니다."),
    NOT_FOUND_REGULAR_RUN(HttpStatus.BAD_REQUEST, "정기러닝이 존재하지 않습니다."),
    UNAUTHORIZED_CREW_ACCESS(HttpStatus.FORBIDDEN, "크루 접근 권한이 없습니다."),
    DECRYPTION_ERROR(HttpStatus.BAD_REQUEST, "복호화 과정 중 에러가 발생하였습니다."),
    NOT_FOUND_CREW_MEMBER(HttpStatus.BAD_REQUEST, "해당 크루원을 찾을 수 없습니다."),
    ROLE_NOT_CHANGED(HttpStatus.BAD_REQUEST, "새로운 역할이 현재 역할과 동일합니다."),
    UNAUTHORIZED_REGULAR_ACCESS(HttpStatus.FORBIDDEN, "정기러닝 접근 권한이 없습니다."),
    NOT_FOUND_ACTIVITY(HttpStatus.BAD_REQUEST, "활동이 존재하지 않습니다."),
    UNAUTHORIZED_ACTIVITY(HttpStatus.FORBIDDEN, "활동 접근 권한이 없습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "시작 날짜가 종료 날짜보다 빨라야 합니다."),
    NOT_FOUND_CHATROOM(HttpStatus.BAD_REQUEST, "채팅방을 찾을 수 없습니다."),
    INVALID_SORT(HttpStatus.BAD_REQUEST, "유효한 정렬기준이 아닙니다."),
    GOOGLE_LOGIN_FAILED(HttpStatus.BAD_REQUEST, "Google 로그인에 실패했습니다."),
    ALREADY_EXIST_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "접근 권한이 없습니다."),
    MISSING_REQUIRED_INFORMATION(HttpStatus.BAD_REQUEST, "필수 정보가 누락되었습니다."),
    NOT_FOUND_USER_NOTIFICATION(HttpStatus.BAD_REQUEST, "사용자에게 전송된 알림을 찾을 수 없습니다."),
    REJECT_SUBSCRIBE_NOTIFICATION(HttpStatus.BAD_REQUEST, "사용자가 알림 수신을 거부하여 구독할 수 없습니다."),
    NOT_FOUND_CHAT_MESSAGE(HttpStatus.BAD_REQUEST, "채팅 메시지를 찾을 수 없습니다."),
    NOT_FOUND_RUN_RECORD(HttpStatus.NOT_FOUND, "러닝 기록을 찾을 수 없습니다."),
    NOT_FOUND_RUN_GOAL(HttpStatus.NOT_FOUND, "러닝 목표를 찾을 수 없습니다."),
    INVALID_RUN_ARGUMENT(HttpStatus.BAD_REQUEST, "러닝 기록이 없거나 유저정보가 없습니다."),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "해당 게시물을 찾을 수 없습니다."),

    //크루가입
    ALREADY_EXIST_PENDING_JOIN_APPLY(HttpStatus.BAD_REQUEST, "이미 거압 숭안 대기중입니다."),
    ALREADY_CREWMEMBER(HttpStatus.BAD_REQUEST, "이미 크루에 가입된 회원입니다."),
    RECORD_OPEN_REQUIRED(HttpStatus.FORBIDDEN, "러닝 기록을 공개해야 합니다."),
    GENDER_RESTRICTION_NOT_MET(HttpStatus.FORBIDDEN, "성별 제한을 충족하지 못했습니다."),
    GENDER_REQUIRED(HttpStatus.FORBIDDEN, "가입을 위해 성별 정보가 필요합니다."),
    AGE_RESTRICTION_NOT_MET(HttpStatus.FORBIDDEN, "나이 제한을 충족하지 못했습니다."),
    AGE_REQUIRED(HttpStatus.FORBIDDEN, "가입을 위해 연령 정보가 필요합니다."),
    JOIN_NOT_ALLOWED_FOR_FORCE_WITHDRAWN(HttpStatus.FORBIDDEN, "강제퇴장된 멤버는 재가입할 수 없습니다."),

    //권한
    UNAUTHORIZED_MY_APPLY_ACCESS(HttpStatus.FORBIDDEN, "잘못된 접근입니다. 자신의 가입 신청 내역만 조회할 수 있습니다.");


    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String s) {
        this.httpStatus = httpStatus;
        this.message = s;
    }
}
