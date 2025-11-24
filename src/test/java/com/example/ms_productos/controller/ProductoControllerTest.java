package com.example.ms_productos.controller;

import com.example.msproductos.model.Producto;
import com.example.ms_productos.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Seguridad
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    // --- 1. GET ALL ---
    @Test
    void testGetAllProductos() {
        Producto dto = new Producto();
        dto.setId(1L);
        dto.setNombre("Laptop");

        // Mock devuelve un Flux con 1 producto
        when(productoService.findAll()).thenReturn(Flux.just(dto));

        webClient.mutateWith(mockJwt())
                .get().uri("/api/productos")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Producto.class)
                .hasSize(1)
                .consumeWith(response -> {
                    Producto p = response.getResponseBody().get(0);
                    assert p.getNombre().equals("Laptop");
                });
    }

    // --- 2. GET BY ID (Success) ---
    @Test
    void testGetProductoById_Success() {
        Producto dto = new Producto();
        dto.setId(1L);
        dto.setNombre("Mouse");

        when(productoService.findById(1L)).thenReturn(Mono.just(dto));

        webClient.mutateWith(mockJwt())
                .get().uri("/api/productos/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Producto.class)
                .consumeWith(response -> {
                    Producto p = response.getResponseBody();
                    assert p.getId() == 1L;
                    assert p.getNombre().equals("Mouse");
                });
    }

    // --- 3. GET BY ID (Not Found) ---
    @Test
    void testGetProductoById_NotFound() {
        when(productoService.findById(99L)).thenReturn(Mono.empty());

        webClient.mutateWith(mockJwt())
                .get().uri("/api/productos/{id}", 99L)
                .exchange()
                .expectStatus().isNotFound();
    }

    // --- 4. CREATE (POST) ---
    @Test
    void testCreateProducto() {
        // Input
        Producto dtoInput = new Producto();
        dtoInput.setNombre("Teclado");
        dtoInput.setPrecio(100.0);
        dtoInput.setStock(10);

        // Output esperado del servicio
        Producto dtoOutput = new Producto();
        dtoOutput.setId(5L);
        dtoOutput.setNombre("Teclado");
        dtoOutput.setPrecio(100.0);
        dtoOutput.setStock(10);
        dtoOutput.setActivo(true);

        // Configuración del Mock: Asegúrate de que coincida cualquier Producto
        when(productoService.save(any(Producto.class))).thenReturn(Mono.just(dtoOutput));

        webClient.mutateWith(mockJwt()).mutateWith(csrf())
                .post().uri("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dtoInput)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Producto.class)
                .consumeWith(res -> {
                    Producto p = res.getResponseBody();
                    assert p != null;
                    assert p.getId() == 5L;
                    assert p.getNombre().equals("Teclado");
                });
    }

    // --- 5. UPDATE (Success) ---
    @Test
    void testUpdateProducto_Success() {
        Producto dtoOutput = new Producto();
        dtoOutput.setId(1L);
        dtoOutput.setNombre("Actualizado");

        Producto dtoInput = new Producto();
        dtoInput.setNombre("Nuevo");

        when(productoService.update(eq(1L), any(Producto.class))).thenReturn(Mono.just(dtoOutput));

        webClient.mutateWith(mockJwt()).mutateWith(csrf())
                .put().uri("/api/productos/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dtoInput)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Producto.class)
                .consumeWith(res -> {
                    Producto p = res.getResponseBody();
                    assert p != null;
                    assert p.getNombre().equals("Actualizado");
                });
    }

    // --- 6. UPDATE (Not Found) ---
    @Test
    void testUpdateProducto_NotFound() {
        Producto dtoInput = new Producto();
        dtoInput.setNombre("Nuevo");

        when(productoService.update(eq(99L), any(Producto.class))).thenReturn(Mono.empty());

        webClient.mutateWith(mockJwt()).mutateWith(csrf())
                .put().uri("/api/productos/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dtoInput)
                .exchange()
                .expectStatus().isNotFound();
    }

    // --- 7. DELETE (Success) ---
    @Test
    void testDeleteProducto_Success() {
        when(productoService.delete(1L)).thenReturn(Mono.empty());

        webClient.mutateWith(mockJwt()).mutateWith(csrf())
                .delete().uri("/api/productos/{id}", 1L)
                .exchange()
                .expectStatus().isNoContent();
    }

    // --- 8. STOCK UPDATE (Success) ---
    @Test
    void testActualizarStock_Success() {
        when(productoService.actualizarStock(1L, 50)).thenReturn(Mono.empty());

        webClient.mutateWith(mockJwt()).mutateWith(csrf())
                .put().uri(uriBuilder -> uriBuilder
                        .path("/api/productos/{id}/stock")
                        .queryParam("cantidad", 50)
                        .build(1L))
                .exchange()
                .expectStatus().isOk();
    }

    // --- 9. BAJO STOCK (Success) ---
    @Test
    void testGetProductosBajoStock() {
        Producto dto = new Producto();
        dto.setId(2L);
        dto.setStock(5);

        when(productoService.findBajoStock(10)).thenReturn(Flux.just(dto));

        webClient.mutateWith(mockJwt())
                .get().uri(uri -> uri.path("/api/productos/bajo-stock")
                        .queryParam("minimo", 10).build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Producto.class)
                .hasSize(1)
                .consumeWith(res -> {
                    Producto p = res.getResponseBody().get(0);
                    assert p.getStock() == 5;
                });
    }
}