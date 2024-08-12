package com.example.runningservice.exception;

import com.example.runningservice.dto.NotValidResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Valid 검증에서 실패했을 때 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<NotValidResponseDto> handleNotValidException(
        MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        NotValidResponseDto response = new NotValidResponseDto();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            response.addErrorMessage(NotValidResponseDto.Message.builder()
                .message(fieldError.getDefaultMessage())
                .field(fieldError.getField())
                .build());
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }
}
