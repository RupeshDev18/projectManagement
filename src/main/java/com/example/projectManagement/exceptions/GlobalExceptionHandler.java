package com.example.projectManagement.exceptions;

import com.example.projectManagement.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistException(EmailAlreadyExistException ex){
        ErrorResponse er=new ErrorResponse(ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(er);

    }

    @ExceptionHandler(EmailNotFoundException.class)
    public  ResponseEntity<ErrorResponse> handleEmailNotFoundException(EmailNotFoundException ex){
        ErrorResponse er=new ErrorResponse(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(er);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public  ResponseEntity<ErrorResponse> handleInvalidCredentialException(EmailNotFoundException ex){
        ErrorResponse er=new ErrorResponse(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(er);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidationExceptions(
//            MethodArgumentNotValidException ex) {
//
//        Map<String, String> errors = new HashMap<>();
//
//        ex.getBindingResult()
//                .getFieldErrors()
//                .forEach(error ->
//                        errors.put(error.getField(),
//                                error.getDefaultMessage()));
//
//        return ResponseEntity.badRequest().body(errors);
//    }
}
