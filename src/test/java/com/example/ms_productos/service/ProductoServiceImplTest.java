package com.example.ms_productos.service;

import com.example.msproductos.model.Producto;       // DTO
import com.example.ms_productos.model.ProductoEntity; // Entidad
import com.example.ms_productos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoServiceImpl service;

    // --- TEST FIND ALL ---
    @Test
    void testFindAll() {
        ProductoEntity entity = new ProductoEntity();
        entity.setId(1L);
        entity.setNombre("Teclado");
        entity.setFechaCreacion(LocalDateTime.now());

        when(repository.findAll()).thenReturn(Flux.just(entity));

        Flux<Producto> resultado = service.findAll();

        StepVerifier.create(resultado)
                .expectNextMatches(dto ->
                        dto.getId() == 1L &&
                                dto.getNombre().equals("Teclado") &&
                                dto.getFechaCreacion() != null
                )
                .verifyComplete();
    }

    // --- TEST FIND BY ID (Success) ---
    @Test
    void testFindById() {
        ProductoEntity entity = new ProductoEntity();
        entity.setId(1L);
        entity.setNombre("Mouse");

        when(repository.findById(1L)).thenReturn(Mono.just(entity));

        Mono<Producto> resultado = service.findById(1L);

        StepVerifier.create(resultado)
                .expectNextMatches(dto -> dto.getNombre().equals("Mouse"))
                .verifyComplete();
    }

    // --- TEST FIND BY ID (Not Found - Para subir cobertura) ---
    @Test
    void testFindById_NotFound() {
        when(repository.findById(99L)).thenReturn(Mono.empty());

        Mono<Producto> resultado = service.findById(99L);

        StepVerifier.create(resultado)
                .verifyComplete(); // Esperamos vacío
    }

    // --- TEST SAVE (Success) ---
    @Test
    void testSave() {
        Producto dtoEntrada = new Producto();
        dtoEntrada.setNombre("Monitor");

        ProductoEntity entityGuardada = new ProductoEntity();
        entityGuardada.setId(10L);
        entityGuardada.setNombre("Monitor");
        entityGuardada.setActivo(true);
        entityGuardada.setFechaCreacion(LocalDateTime.now());

        when(repository.save(any(ProductoEntity.class))).thenReturn(Mono.just(entityGuardada));

        Mono<Producto> resultado = service.save(dtoEntrada);

        StepVerifier.create(resultado)
                .expectNextMatches(dto -> dto.getId() == 10L && dto.getActivo() == true)
                .verifyComplete();
    }

    // --- TEST SAVE (Sin fecha - Para probar esa rama del mapper) ---
    @Test
    void testSave_SinFecha() {
        Producto dtoEntrada = new Producto(); // Sin fecha
        dtoEntrada.setNombre("Cable");

        ProductoEntity entityGuardada = new ProductoEntity();
        entityGuardada.setFechaCreacion(LocalDateTime.now());

        when(repository.save(any(ProductoEntity.class))).thenReturn(Mono.just(entityGuardada));

        Mono<Producto> resultado = service.save(dtoEntrada);

        StepVerifier.create(resultado).expectNextCount(1).verifyComplete();
    }

    // --- TEST UPDATE (Success) ---
    @Test
    void testUpdate() {
        long id = 1L;
        Producto dtoRequest = new Producto();
        dtoRequest.setNombre("Nuevo Nombre");
        dtoRequest.setActivo(false);

        ProductoEntity entityEnBD = new ProductoEntity();
        entityEnBD.setId(id);
        entityEnBD.setNombre("Viejo Nombre");

        ProductoEntity entityActualizada = new ProductoEntity();
        entityActualizada.setId(id);
        entityActualizada.setNombre("Nuevo Nombre");
        entityActualizada.setActivo(false);

        when(repository.findById(id)).thenReturn(Mono.just(entityEnBD));
        when(repository.save(any(ProductoEntity.class))).thenReturn(Mono.just(entityActualizada));

        Mono<Producto> resultado = service.update(id, dtoRequest);

        StepVerifier.create(resultado)
                .expectNextMatches(dto ->
                        dto.getNombre().equals("Nuevo Nombre") &&
                                dto.getActivo() == false
                )
                .verifyComplete();
    }

    // --- TEST UPDATE (Not Found - Para subir cobertura) ---
    @Test
    void testUpdate_NotFound() {
        long id = 99L;
        Producto dtoRequest = new Producto();

        when(repository.findById(id)).thenReturn(Mono.empty());

        Mono<Producto> resultado = service.update(id, dtoRequest);

        StepVerifier.create(resultado).verifyComplete();
        verify(repository, never()).save(any());
    }

    // --- TEST DELETE ---
    @Test
    void testDelete() {
        when(repository.deleteById(1L)).thenReturn(Mono.empty());
        Mono<Void> resultado = service.delete(1L);
        StepVerifier.create(resultado).verifyComplete();
        verify(repository, times(1)).deleteById(1L);
    }

    // --- TEST ACTUALIZAR STOCK ---
    @Test
    void testActualizarStock() {
        when(repository.actualizarStock(1L, 50)).thenReturn(Mono.empty());
        Mono<Void> resultado = service.actualizarStock(1L, 50);
        StepVerifier.create(resultado).verifyComplete();
        verify(repository, times(1)).actualizarStock(1L, 50);
    }

    // --- TEST UPDATE (Con Activo Null - Para probar el if) ---
    @Test
    void testUpdate_ActivoNull() {
        long id = 1L;
        Producto dtoRequest = new Producto();
        dtoRequest.setNombre("Solo Nombre");
        dtoRequest.setActivo(null); // Importante para probar el if(productoDto.getActivo() != null)

        ProductoEntity entityEnBD = new ProductoEntity();
        entityEnBD.setId(id);
        entityEnBD.setActivo(true);

        when(repository.findById(id)).thenReturn(Mono.just(entityEnBD));
        when(repository.save(any(ProductoEntity.class))).thenReturn(Mono.just(entityEnBD));

        Mono<Producto> resultado = service.update(id, dtoRequest);

        StepVerifier.create(resultado).expectNextCount(1).verifyComplete();
    }

    // --- TEST FIND BAJO STOCK ---
    @Test
    void testFindBajoStock() {
        ProductoEntity entity = new ProductoEntity();
        entity.setStock(5);

        when(repository.findByStockLessThan(10)).thenReturn(Flux.just(entity));

        Flux<Producto> resultado = service.findBajoStock(10);

        StepVerifier.create(resultado)
                .expectNextMatches(dto -> dto.getStock() == 5)
                .verifyComplete();
    }
}