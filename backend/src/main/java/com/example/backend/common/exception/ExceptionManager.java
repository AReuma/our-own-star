package com.example.backend.common.exception;

import com.example.backend.common.exception.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    public ResponseEntity<ErrorDTO> validExceptionHandler(MethodArgumentNotValidException e){
        /*
         *  BindingResult는 @Valid 어노테이션과 함께 사용되어 검증(Validation) 과정에서 발생한 에러 정보를 수집하고 이를 처리합니다.
         * 검증 과정에서 발생한 오류가 있다면 해당 오류 정보를 BindingResult에 저장하여 컨트롤러에서 사용할 수 있도록 합니다.
         * */
        BindingResult bindingResult = e.getBindingResult();
        String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();

        log.error(errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), errorMessage, HttpStatus.BAD_REQUEST.getReasonPhrase()));
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorDTO> runtimeExceptionHandler(AppException e){
        log.error(e.getErrorCode().name());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(new ErrorDTO(e.getErrorCode().getHttpStatus().value(), e.getMessage(), e.getErrorCode().getHttpStatus().getReasonPhrase()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> runtimeExceptionHandler(RuntimeException e){
        log.error(e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorDTO(HttpStatus.CONFLICT.value(), e.getMessage(), HttpStatus.CONFLICT.getReasonPhrase()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDTO> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage());

        // 예외 메시지나 상황에 따라 적절한 응답을 생성
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(HttpStatus.BAD_REQUEST.value(), "Data integrity violation", HttpStatus.BAD_REQUEST.getReasonPhrase()));
    }
}
