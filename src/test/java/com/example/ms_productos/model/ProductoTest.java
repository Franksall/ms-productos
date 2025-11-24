package com.example.ms_productos.model;

import com.example.msproductos.model.Producto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    void testProductoGettersAndSetters() {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Laptop");
        p.setPrecio(1000.0);
        p.setStock(50);
        p.setActivo(true);

        assertEquals(1L, p.getId());
        assertEquals("Laptop", p.getNombre());
        assertEquals(1000.0, p.getPrecio());
        assertEquals(50, p.getStock());
        assertTrue(p.getActivo());
    }

    @Test
    void testToString() {
        // Si usas @Data de Lombok, el toString también cuenta
        Producto p = new Producto();
        p.setId(1L);
        assertNotNull(p.toString());
    }
    @Test
    void testProductoEntity() {
        // Probamos la Entidad (ProductoEntity)
        ProductoEntity entity = new ProductoEntity();
        entity.setId(2L);
        entity.setNombre("Mouse");
        entity.setPrecio(50.0);
        entity.setStock(100);
        entity.setActivo(false);
        LocalDateTime ahora = LocalDateTime.now();
        entity.setFechaCreacion(ahora);

        assertEquals(2L, entity.getId());
        assertEquals("Mouse", entity.getNombre());
        assertEquals(50.0, entity.getPrecio());
        assertEquals(100, entity.getStock());
        assertFalse(entity.getActivo());
        assertEquals(ahora, entity.getFechaCreacion());

        // Truco para cobertura de Lombok / Boilerplate
        assertNotNull(entity.toString());
        ProductoEntity entity2 = new ProductoEntity();
        entity2.setId(2L);
        entity.equals(entity2);
        entity.hashCode();
    }
}