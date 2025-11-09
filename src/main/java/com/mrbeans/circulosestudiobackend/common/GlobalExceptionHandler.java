package com.mrbeans.circulosestudiobackend.common;

import com.mrbeans.circulosestudiobackend.common.dto.ErrorResponse;
import com.mrbeans.circulosestudiobackend.common.exception.GenericException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //Validaciones para @Valid en los dtos
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest webRequest) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        f -> f.getField(),
                        f -> f.getDefaultMessage()
                ));

        String path = ((ServletWebRequest) webRequest).getRequest().getRequestURI();

        ErrorResponse body = new ErrorResponse();
        body.setTimestamp(LocalDateTime.now());
        body.setStatusCode(status.value());
        body.setError(HttpStatus.valueOf(status.value()).getReasonPhrase());
        body.setMessage("Error de validación de campos");
        body.setPath(path);
        body.setValidationErrors(errors);

        return new ResponseEntity<>(body, headers, status);
    }

    //JSON mal formado o error de lectura HTTP
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest webRequest) {

        String path = ((ServletWebRequest) webRequest).getRequest().getRequestURI();

        ErrorResponse body = new ErrorResponse();
        body.setTimestamp(LocalDateTime.now());
        body.setStatusCode(status.value());
        body.setError(HttpStatus.valueOf(status.value()).getReasonPhrase());
        body.setMessage("JSON inválido: " + ex.getMostSpecificCause().getMessage());
        body.setPath(path);

        return new ResponseEntity<>(body, headers, status);
    }

    // GenericException para errores genericos de los services
    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ErrorResponse> onGenericException(
            GenericException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse body = new ErrorResponse();
        body.setTimestamp(LocalDateTime.now());
        body.setStatusCode(status.value());
        body.setError(status.getReasonPhrase());
        body.setMessage(ex.getMessage());
        body.setPath(request.getRequestURI());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> onAuthorizationDeniedException(
            AuthorizationDeniedException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse body = new ErrorResponse();
        body.setTimestamp(LocalDateTime.now());
        body.setStatusCode(status.value());
        body.setError(status.getReasonPhrase());
        body.setMessage("Acceso denegado: " + ex.getMessage());
        body.setPath(request.getRequestURI());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> onBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse body = new ErrorResponse();
        body.setTimestamp(LocalDateTime.now());
        body.setStatusCode(status.value());
        body.setError(status.getReasonPhrase());
        body.setMessage("Credenciales invalidas");
        body.setPath(request.getRequestURI());

        return new ResponseEntity<>(body, status);
    }

    // Cualquier otra excepción no manejada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> onGenericError(
            Exception ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse body = new ErrorResponse();
        body.setTimestamp(LocalDateTime.now());
        body.setStatusCode(status.value());
        body.setError(status.getReasonPhrase());
        body.setMessage("Ha ocurrido un error inesperado");
        body.setPath(request.getRequestURI());

        return new ResponseEntity<>(body, status);
    }
}
