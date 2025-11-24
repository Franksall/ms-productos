package com.example.ms_productos.exception;

import com.example.msproductos.model.ErrorModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    //  TEST 1: Error General (500)
    @Test
    void handleGeneralErrors_ShouldReturn500() {
        //  Simulam excepción cualquiera (ej: NullPointer)
        Exception ex = new RuntimeException("Error inesperado de prueba");

        //  metodo del handler
        ResponseEntity<ErrorModel> response = handler.handleGeneralErrors(ex);

        //  Verifica devuelve 500 INTERNAL_SERVER_ERROR
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getSuccess()); // success debe ser false
        assertEquals("E500_INTERNAL_ERROR", response.getBody().getErrors().get(0).getCode());
    }

    //  TEST 2: Error de Validación (400)
    @Test
    void handleValidationErrors_ShouldReturn400() {
        // Mockear el error de Spring
        // Cre un BindingResult falso que simula un error en el campo "precio"
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("producto", "precio", "El precio no puede ser negativo");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Crea la excepción WebExchangeBindException con ese BindingResult
        // (El MethodParameter puede ser null para el mock)
        WebExchangeBindException ex = new WebExchangeBindException(mock(MethodParameter.class), bindingResult);

        //  EJECUCIÓN
        ResponseEntity<ErrorModel> response = handler.handleValidationErrors(ex);

        // VERIFICACIÓN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Debe ser 400
        assertNotNull(response.getBody());

        // Verificamos que mapeó correctamente el mensaje
        assertEquals("E400_INVALID_FIELD", response.getBody().getErrors().get(0).getCode());
        assertEquals("precio", response.getBody().getErrors().get(0).getField());
    }
}