package com.example.runningservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다."),
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
    ENCRYPTION_ERROR(HttpStatus.BAD_REQUEST, ""),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NOT_FOUND_CREW(HttpStatus.BAD_REQUEST, "크루를 찾을 수 없습니다."),
    NOT_FOUND_APPLY(HttpStatus.BAD_REQUEST, "해당 신청내역을 찾을 수 없습니다."),

    //크루가입
    ALREADY_EXIST_JOIN_APPLY(HttpStatus.BAD_REQUEST, "이미 가입신청 기록이 있습니다."),
    ALREADY_CREWMEMBER(HttpStatus.BAD_REQUEST, "이미 크루에 가입된 회원입니다."),
    RECORD_OPEN_REQUIRED(HttpStatus.FORBIDDEN, "러닝 기록을 공개해야 합니다."),
    GENDER_RESTRICTION_NOT_MET(HttpStatus.FORBIDDEN, "성별 제한을 충족하지 못했습니다."),
    GENDER_REQUIRED(HttpStatus.FORBIDDEN, "가입을 위해 성별 정보가 필요합니다."),
    AGE_RESTRICTION_NOT_MET(HttpStatus.FORBIDDEN, "나이 제한을 충족하지 못했습니다."),
    AGE_REQUIRED(HttpStatus.FORBIDDEN, "가입을 위해 연령 정보가 필요합니다."),
    //권한
    UNAUTHORIZED_MY_APPLY_ACCESS(HttpStatus.FORBIDDEN, "잘못된 접근입니다. 자신의 가입 신청 내역만 조회할 수 있습니다.");


    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String s) {
        this.httpStatus = httpStatus;
        this.message = s;
    }
}
