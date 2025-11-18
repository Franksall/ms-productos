package com.example.ms_productos.controller;

// Imports de tus clases
import com.example.ms_productos.model.Producto;
import com.example.ms_productos.service.ProductoService;

// --- Imports de PRUEBA WEB ---
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

// --- Imports de SEGURIDAD ---
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;


// Imports estáticos
import static org.mockito.Mockito.*;

// 1. Le decimos que SÓLO pruebe esta clase controladora
@WebFluxTest(ProductoController.class)
class ProductoControllerTest {

    // 2. Herramienta para hacer peticiones HTTP falsas
    @Autowired
    private WebTestClient webClient;

    // 3. Mockeamos el servicio (ya lo probamos en el otro archivo)
    @MockBean
    private ProductoService productoService;

    // 4. ¡EL TRUCO DE SEGURIDAD!
    // Mockeamos el 'Decoder' para que SecurityConfig pueda arrancar
    @MockBean
    private ReactiveJwtDecoder jwtDecoder;


    // --- Nuestra Primera Prueba de Controlador (GET /) ---
    @Test
    void testGetProductos() {
        // --- 1. Preparación (Arrange) ---

        Producto mockProducto = new Producto();
        mockProducto.setId(1L);
        mockProducto.setNombre("Teclado");

        // Mock: "Cuando el controlador llame a productoService.findAll(),
        //       devuelve nuestro productoMock"
        when(productoService.findAll()).thenReturn(Flux.just(mockProducto));

        // --- 2. Ejecución (Act) ---

        // Hacemos una petición GET falsa a "/api/productos"
        // (¡Asumo que tu endpoint es "/api/productos"!)
        webClient.mutateWith(mockJwt()) // <-- ¡Inyectamos un token falso!
                .get().uri("/api/productos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() // Ejecuta la llamada

                // --- 3. Verificación (Assert) ---

                // Esperamos que el estado sea 200 OK
                .expectStatus().isOk()
                // Verificamos que el JSON devuelto tenga el ID 1
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].nombre").isEqualTo("Teclado");

        // Verificamos que el controlador SÍ llamó al servicio
        verify(productoService, times(1)).findAll();
    }
    @Test
    void testGetProductoById_Success() {
        // --- 1. Preparación (Arrange) ---
        long productoId = 1L;
        Producto mockProducto = new Producto();
        mockProducto.setId(productoId);
        mockProducto.setNombre("Mouse");

        // Mock: "Cuando el servicio busque por ID 1, devuelve el productoMock"
        when(productoService.findById(productoId)).thenReturn(Mono.just(mockProducto));

        // --- 2. Ejecución (Act) ---
        webClient.mutateWith(mockJwt()) // <-- ¡Seguridad!
                .get().uri("/api/productos/{id}", productoId) // <-- Usamos la URI con el ID
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

                // --- 3. Verificación (Assert) ---
                .expectStatus().isOk() // Esperamos un 200 OK
                .expectBody()
                .jsonPath("$.id").isEqualTo(productoId)
                .jsonPath("$.nombre").isEqualTo("Mouse");

        // Verificamos que se llamó al servicio
        verify(productoService, times(1)).findById(productoId);
    }

    // --- AÑADE ESTA PRUEBA (Camino Triste: GET /api/productos/{id} No Encontrado) ---
    @Test
    void testGetProductoById_NotFound() {
        // --- 1. Preparación (Arrange) ---
        long idNoExistente = 99L;

        // Mock: "Cuando el servicio busque por ID 99, devuelve vacío"
        when(productoService.findById(idNoExistente)).thenReturn(Mono.empty());

        // --- 2. Ejecución (Act) ---
        webClient.mutateWith(mockJwt()) // <-- ¡Seguridad!
                .get().uri("/api/productos/{id}", idNoExistente) // <-- ID que no existe
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

                // --- 3. Verificación (Assert) ---
                .expectStatus().isNotFound(); // <-- ¡Esperamos un 404 Not Found!

        // Verificamos que se llamó al servicio
        verify(productoService, times(1)).findById(idNoExistente);
    }
    @Test
    void testCreateProducto_Success() {
        // --- 1. Preparación (Arrange) ---

        // 1a. El producto que "enviamos" en el body (sin ID)
        Producto productoInput = new Producto();
        productoInput.setNombre("Monitor");
        productoInput.setPrecio(250.0);

        // 1b. El producto que "devuelve" el servicio (con ID)
        Producto productoMock = new Producto();
        productoMock.setId(10L);
        productoMock.setNombre("Monitor");
        productoMock.setPrecio(250.0);

        // Mock: "Cuando el servicio guarde (save), devuelve el mock con ID"
        when(productoService.save(any(Producto.class))).thenReturn(Mono.just(productoMock));

        // --- 2. Ejecución (Act) ---
        webClient
                .mutateWith(mockJwt()) // <-- 1. Autentica al usuario
                .mutateWith(csrf())    // <-- 2. ¡Añade el token CSRF para el POST!
                .post().uri("/api/productos") // <-- ¡POST!
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(productoInput), Producto.class) // <-- ¡Enviamos el body!
                .exchange()

                // --- 3. Verificación (Assert) ---
                // Tu controlador tiene @ResponseStatus(HttpStatus.CREATED)
                .expectStatus().isCreated() // <-- ¡Esperamos un 201 CREATED!
                .expectBody()
                .jsonPath("$.id").isEqualTo(10L) // Verificamos el ID devuelto
                .jsonPath("$.nombre").isEqualTo("Monitor");

        // Verificamos que se llamó al servicio
        verify(productoService, times(1)).save(any(Producto.class));
    }
    @Test
    void testUpdateProducto_Success() {
        // --- 1. Preparación (Arrange) ---
        long productoId = 1L;

        // 1a. Los "nuevos datos" que enviamos en el body
        Producto productoInput = new Producto();
        productoInput.setNombre("Monitor Gamer");
        productoInput.setPrecio(300.0);

        // 1b. El producto "actualizado" que devuelve el servicio
        Producto productoMock = new Producto();
        productoMock.setId(productoId);
        productoMock.setNombre("Monitor Gamer");
        productoMock.setPrecio(300.0);

        // Mock: "Cuando el servicio actualice, devuelve el mock"
        when(productoService.update(eq(productoId), any(Producto.class))).thenReturn(Mono.just(productoMock));

        // --- 2. Ejecución (Act) ---
        webClient
                .mutateWith(mockJwt()) // <-- 1. Autentica
                .mutateWith(csrf())    // <-- 2. Añade token CSRF (¡para PUT!)
                .put().uri("/api/productos/{id}", productoId) // <-- ¡PUT!
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(productoInput), Producto.class) // <-- ¡Enviamos el body!
                .exchange()

                // --- 3. Verificación (Assert) ---
                .expectStatus().isOk() // Esperamos un 200 OK
                .expectBody()
                .jsonPath("$.id").isEqualTo(productoId)
                .jsonPath("$.nombre").isEqualTo("Monitor Gamer");

        // Verificamos que se llamó al servicio
        verify(productoService, times(1)).update(eq(productoId), any(Producto.class));
    }

    // --- AÑADE ESTA PRUEBA (Camino Triste: PUT /api/productos/{id} No Encontrado) ---
    @Test
    void testUpdateProducto_NotFound() {
        // --- 1. Preparación (Arrange) ---
        long idNoExistente = 99L;
        Producto productoInput = new Producto(); // El body (aunque no importa)

        // Mock: "Cuando el servicio intente actualizar, devuelve vacío"
        when(productoService.update(eq(idNoExistente), any(Producto.class))).thenReturn(Mono.empty());

        // --- 2. Ejecución (Act) ---
        webClient
                .mutateWith(mockJwt()) // <-- 1. Autentica
                .mutateWith(csrf())    // <-- 2. Añade token CSRF
                .put().uri("/api/productos/{id}", idNoExistente) // <-- ID que no existe
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(productoInput), Producto.class)
                .exchange()

                // --- 3. Verificación (Assert) ---
                .expectStatus().isNotFound(); // <-- ¡Esperamos un 404 Not Found!

        // Verificamos que se llamó al servicio
        verify(productoService, times(1)).update(eq(idNoExistente), any(Producto.class));
    }
    @Test
    void testDeleteProducto_Success() {
        // --- 1. Preparación (Arrange) ---
        long productoId = 1L;

        // Mock: "Cuando el servicio borre (delete), devuelve un Mono vacío"
        when(productoService.delete(productoId)).thenReturn(Mono.empty());

        // --- 2. Ejecución (Act) ---
        webClient
                .mutateWith(mockJwt()) // <-- 1. Autentica
                .mutateWith(csrf())    // <-- 2. Añade token CSRF (¡para DELETE!)
                .delete().uri("/api/productos/{id}", productoId) // <-- ¡DELETE!
                .exchange()

                // --- 3. Verificación (Assert) ---
                // Tu controlador tiene @ResponseStatus(HttpStatus.NO_CONTENT)
                .expectStatus().isNoContent(); // <-- ¡Esperamos un 204 No Content!

        // Verificamos que se llamó al servicio
        verify(productoService, times(1)).delete(productoId);
    }
    @Test
    void testActualizarStock_Success() {
        // --- 1. Preparación (Arrange) ---
        long productoId = 1L;
        int cantidad = 10;

        // Mock: "Cuando el servicio actualice stock, devuelve un Mono vacío"
        // (Asumo que el servicio devuelve Mono<Void> como en el impl)
        when(productoService.actualizarStock(productoId, cantidad)).thenReturn(Mono.empty());

        // --- 2. Ejecución (Act) ---
        webClient
                .mutateWith(mockJwt()) // <-- 1. Autentica
                .mutateWith(csrf())    // <-- 2. Añade token CSRF (¡para PUT!)
                .put().uri("/api/productos/{id}/stock?cantidad={stock}", productoId, cantidad) // <-- ¡PUT con RequestParam!
                .exchange()

                // --- 3. Verificación (Assert) ---
                // Tu controlador devuelve ResponseEntity.ok()
                .expectStatus().isOk(); // <-- ¡Esperamos un 200 OK!

        // Verificamos que se llamó al servicio
        verify(productoService, times(1)).actualizarStock(productoId, cantidad);
    }

    // --- AÑADE ESTA PRUEBA (GET /api/productos/bajo-stock) ---
    @Test
    void testGetProductosBajoStock_Success() {
        // --- 1. Preparación (Arrange) ---
        int stockMinimo = 10;

        // 1a. Un producto que cumple la condición
        Producto mockProducto = new Producto();
        mockProducto.setId(5L);
        mockProducto.setNombre("ProductoBajoStock");
        mockProducto.setStock(5); // Tiene 5 (que es < 10)

        // Mock: "Cuando el servicio busque bajo stock, devuelve el mock"
        when(productoService.findBajoStock(stockMinimo)).thenReturn(Flux.just(mockProducto));

        // --- 2. Ejecución (Act) ---
        webClient
                .mutateWith(mockJwt()) // <-- ¡Seguridad para GET!
                .get().uri("/api/productos/bajo-stock?minimo={stock}", stockMinimo) // <-- ¡GET con RequestParam!
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

                // --- 3. Verificación (Assert) ---
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(5L)
                .jsonPath("$[0].stock").isEqualTo(5);

        // Verificamos que se llamó al servicio
        verify(productoService, times(1)).findBajoStock(stockMinimo);
    }
}