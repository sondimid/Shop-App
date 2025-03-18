package com.example.ShopApp_BE.ControllerAdvice;

import com.example.ShopApp_BE.ControllerAdvice.Exceptions.NotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler  {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception exception){
        if(exception instanceof NotFoundException){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }

        if(exception instanceof ExpiredJwtException){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
        }

        if(exception instanceof UsernameNotFoundException){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
        }

        if(exception instanceof MethodArgumentNotValidException ex){

            String errorMessage = ex.getBindingResult().getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

}
