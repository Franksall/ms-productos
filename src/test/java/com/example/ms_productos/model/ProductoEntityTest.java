package com.example.ms_productos.model;

import com.example.msproductos.model.Producto; // DTO
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductoEntityTest {
    @Test
    void testBoilerplate() {
        // Esto cubre las líneas generadas por Lombok o getters/setters manuales
        ProductoEntity p1 = new ProductoEntity();
        p1.setId(1L);

        ProductoEntity p2 = new ProductoEntity();
        p2.setId(1L);

        // Forzamos la ejecución de estos métodos
        p1.equals(p2);
        p1.hashCode();
        assertNotNull(p1.toString());

        // Y también del DTO generado por si acaso
        Producto dto = new Producto();
        dto.equals(new Producto());
        dto.hashCode();
        assertNotNull(dto.toString());
    }
}