package com.example.projectManagement.exceptions;

import com.example.projectManagement.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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


}
