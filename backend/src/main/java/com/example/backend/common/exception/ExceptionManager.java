package com.example.backend.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> validExceptionHandler(MethodArgumentNotValidException e){
        /*
         *  BindingResult는 @Valid 어노테이션과 함께 사용되어 검증(Validation) 과정에서 발생한 에러 정보를 수집하고 이를 처리합니다.
         * 검증 과정에서 발생한 오류가 있다면 해당 오류 정보를 BindingResult에 저장하여 컨트롤러에서 사용할 수 있도록 합니다.
         * */
        BindingResult bindingResult = e.getBindingResult();
        String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();

        log.error(errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> runtimeExceptionHandler(AppException e){
        log.error(e.getErrorCode().name());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(e.getErrorCode().name() + " " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(RuntimeException e){
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }
}
