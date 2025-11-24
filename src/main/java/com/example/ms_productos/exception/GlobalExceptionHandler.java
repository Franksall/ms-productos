package com.example.ms_productos.exception;

import com.example.msproductos.model.ErrorDetail; // Generado por OpenAPI
import com.example.msproductos.model.ErrorModel;  // Generado por OpenAPI
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException; // Para errores de validación en WebFlux

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Capturar Errores de Validación (@Valid falló)
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorModel> handleValidationErrors(WebExchangeBindException ex) {

        // Convertimos los errores de Spring a tu ErrorDetail de OpenAPI
        List<ErrorDetail> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    ErrorDetail detalle = new ErrorDetail();
                    detalle.setCode("E400_INVALID_FIELD");
                    detalle.setMessage(error.getDefaultMessage());
                    detalle.setField(error.getField());
                    return detalle;
                })
                .collect(Collectors.toList());

        ErrorModel errorModel = new ErrorModel();
        errorModel.setSuccess(false);
        errorModel.setErrors(detalles);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorModel);
    }

    // 2. Capturar Errores Generales (NullPointer, Base de datos, etc.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorModel> handleGeneralErrors(Exception ex) {

        ErrorDetail detalle = new ErrorDetail();
        detalle.setCode("E500_INTERNAL_ERROR");
        detalle.setMessage(ex.getMessage()); // O un mensaje genérico "Error del servidor"
        detalle.setField(null);

        ErrorModel errorModel = new ErrorModel();
        errorModel.setSuccess(false);
        errorModel.setErrors(List.of(detalle));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorModel);
    }
}