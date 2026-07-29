package com.example.ems.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> resourceNotFoundExceptionHandler(ResourceNotFoundException exp, WebRequest req) {
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), exp.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorDetails> duplicateResourceExceptionHandler(DuplicateResourceException exp, WebRequest req) {
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), exp.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(err, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> validationExceptionHandler(MethodArgumentNotValidException exp, WebRequest req) {
        Map<String, String> validationErrors = new HashMap<>();
        exp.getBindingResult().getFieldErrors()
                .forEach(fieldError -> validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), "Validation failed", req.getDescription(false), validationErrors);
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> illegalArgumentExceptionHandler(IllegalArgumentException exp, WebRequest req) {
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), exp.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorDetails> fileStorageExceptionHandler(FileStorageException exp, WebRequest req) {
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), exp.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorDetails> maxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException exp, WebRequest req) {
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), "Uploaded file exceeds the maximum allowed size", req.getDescription(false));
        return new ResponseEntity<>(err, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> globalExceptionHandler(Exception exp, WebRequest req) {
        ErrorDetails err = new ErrorDetails(LocalDateTime.now(), exp.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
