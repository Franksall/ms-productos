package com.example.ms_productos.service;

// Imports de tus clases (¡puede que necesites ajustar esto!)
import com.example.ms_productos.model.Producto;
import com.example.ms_productos.repository.ProductoRepository;

// Imports de Pruebas
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Imports Reactivos
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

// Imports estáticos de Mockito y AssertJ
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class) // ¡Importante! Activa Mockito
class ProductoServiceImplTest {

    // 1. Creamos un "Mock" (simulacro) del Repositorio
    @Mock
    private ProductoRepository productoRepository;

    // 2. Inyectamos ese Mock en la clase que queremos probar
    @InjectMocks
    private ProductoServiceImpl productoService;


    // --- ¡Nuestra Primera Prueba Unitaria! ---

    @Test
    void testFindAll() {
        // --- 1. Preparación (Arrange) ---
        // (Estoy asumiendo que tu servicio tiene un método 'findAll()')

        // Creamos un producto falso
        Producto mockProducto = new Producto();
        mockProducto.setId(1L);
        mockProducto.setNombre("Teclado");
        mockProducto.setStock(10);

        // Le decimos a Mockito:
        // "CUANDO (when) alguien llame a productoRepository.findAll(),
        // ENTONCES DEVUELVE (thenReturn) un Flux que contenga nuestro producto falso"
        when(productoRepository.findAll()).thenReturn(Flux.just(mockProducto));

        // --- 2. Ejecución (Act) ---

        // Llamamos al método real de tu servicio
        // (Si tu método no se llama 'findAll', tendrás que cambiarlo aquí)
        Flux<Producto> resultadoFlux = productoService.findAll();

        // --- 3. Verificación (Assert) ---

        // Usamos StepVerifier para probar el Flux (el stream reactivo)
        StepVerifier.create(resultadoFlux)
                // Esperamos que emita un elemento
                .expectNextMatches(producto -> {
                    // Verificamos que el producto sea el nuestro
                    assertEquals(1L, producto.getId());
                    assertEquals("Teclado", producto.getNombre());
                    return true;
                })
                // Verificamos que el stream se complete
                .verifyComplete();

        // Verificación extra: Aseguramos que el repositorio fue llamado 1 sola vez
        verify(productoRepository, times(1)).findAll();
    }
    @Test
    void testFindById_Success() {
        // --- 1. Preparación (Arrange) ---
        // (Asumo que tu servicio tiene un método 'findById(Long id)')
        long productoId = 1L;

        Producto mockProducto = new Producto();
        mockProducto.setId(productoId);
        mockProducto.setNombre("Mouse");

        // "CUANDO llamen a repository.findById(1L), DEVUELVE nuestro producto falso"
        when(productoRepository.findById(productoId)).thenReturn(Mono.just(mockProducto));

        // --- 2. Ejecución (Act) ---

        // (Si tu método se llama 'buscarPorId', cámbialo aquí)
        Mono<Producto> resultadoMono = productoService.findById(productoId);

        // --- 3. Verificación (Assert) ---

        StepVerifier.create(resultadoMono)
                .expectNextMatches(producto -> {
                    assertEquals(1L, producto.getId());
                    assertEquals("Mouse", producto.getNombre());
                    return true;
                })
                .verifyComplete();

        verify(productoRepository, times(1)).findById(productoId);
    }
    // ... aquí está tu prueba testFindById_Success ...


    // --- AÑADE ESTA NUEVA PRUEBA (Camino Feliz: save) ---
    @Test
    void testSaveProducto() {
        // --- 1. Preparación (Arrange) ---
        // (Asumo que tu servicio tiene un método 'save(Producto producto)')

        // 1a. El producto que "enviamos" para guardar (no tiene ID)
        Producto productoSinGuardar = new Producto();
        productoSinGuardar.setNombre("Monitor");
        productoSinGuardar.setPrecio(200.0);
        productoSinGuardar.setStock(15);

        // 1b. El producto como "devuelto" por la BD (ahora SÍ tiene ID)
        Producto productoGuardado = new Producto();
        productoGuardado.setId(10L); // ID generado por la BD
        productoGuardado.setNombre("Monitor");
        productoGuardado.setPrecio(200.0);
        productoGuardado.setStock(15);

        // "CUANDO llamen a repository.save(CUALQUIER objeto Producto),
        // DEVUELVE nuestro 'productoGuardado' (con ID)"
        when(productoRepository.save(any(Producto.class))).thenReturn(Mono.just(productoGuardado));

        // --- 2. Ejecución (Act) ---

        // (Si tu método se llama 'crear', cámbialo aquí)
        Mono<Producto> resultadoMono = productoService.save(productoSinGuardar);

        // --- 3. Verificación (Assert) ---

        StepVerifier.create(resultadoMono)
                .expectNextMatches(producto -> {
                    // Verificamos que el producto devuelto SÍ tenga el ID
                    assertEquals(10L, producto.getId());
                    assertEquals("Monitor", producto.getNombre());
                    return true;
                })
                .verifyComplete();

        // Verificamos que el repositorio fue llamado 1 vez
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
    @Test
    void testActualizarStock_Success() {
        // --- 1. Preparación (Arrange) ---
        long productoId = 1L;
        int cantidadVendida = 5;

        // Mock: "CUANDO llamen a repository.actualizarStock(1L, 5),
        //       DEVUELVE un Mono<Void> (es decir, Mono.empty())"
        when(productoRepository.actualizarStock(productoId, cantidadVendida)).thenReturn(Mono.empty());

        // --- 2. Ejecución (Act) ---

        // El método del servicio devuelve Mono<Void>
        Mono<Void> resultadoMono = productoService.actualizarStock(productoId, cantidadVendida);

        // --- 3. Verificación (Assert) ---

        // Para un Mono<Void>, solo verificamos que se complete exitosamente
        StepVerifier.create(resultadoMono)
                .verifyComplete();

        // Verificamos que se llamó al repositorio 1 vez con los argumentos correctos
        verify(productoRepository, times(1)).actualizarStock(productoId, cantidadVendida);
    }
    @Test
    void testUpdate_Success() {
        // --- 1. Preparación (Arrange) ---
        long productoId = 1L;

        // 1a. El producto que "existe" en la BD
        Producto productoEnBD = new Producto();
        productoEnBD.setId(productoId);
        productoEnBD.setNombre("Teclado Viejo");
        productoEnBD.setStock(10);

        // 1b. Los "nuevos datos" que enviamos en el request
        Producto productoRequest = new Producto();
        productoRequest.setNombre("Teclado Nuevo");
        productoRequest.setStock(50);
        productoRequest.setActivo(true);
        // (El DTO puede no tener todos los campos, pero el mock sí)

        // 1c. El producto "final" que esperamos
        Producto productoActualizado = new Producto();
        productoActualizado.setId(productoId);
        productoActualizado.setNombre("Teclado Nuevo");
        productoActualizado.setStock(50);

        // Mock 1: "CUANDO busquen el producto por ID, devuelve el de la BD"
        when(productoRepository.findById(productoId)).thenReturn(Mono.just(productoEnBD));

        // Mock 2: "CUANDO guarden el producto actualizado, devuelve el producto final"
        when(productoRepository.save(any(Producto.class))).thenReturn(Mono.just(productoActualizado));

        // --- 2. Ejecución (Act) ---

        // Llamamos al método 'update' real
        Mono<Producto> resultadoMono = productoService.update(productoId, productoRequest);

        // --- 3. Verificación (Assert) ---

        StepVerifier.create(resultadoMono)
                .expectNextMatches(producto -> {
                    // Verificamos que el stock se haya actualizado
                    assertEquals(50, producto.getStock());
                    assertEquals("Teclado Nuevo", producto.getNombre());
                    return true;
                })
                .verifyComplete();

        // Verificamos que se llamó a AMBOS métodos del repositorio
        verify(productoRepository, times(1)).findById(productoId);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
    @Test
    void testDelete_Success() {
        // --- 1. Preparación (Arrange) ---
        long productoId = 1L;

        // Mock: "CUANDO llamen a repository.deleteById(1L),
        //       DEVUELVE un Mono<Void> (es decir, Mono.empty())"
        when(productoRepository.deleteById(productoId)).thenReturn(Mono.empty());

        // --- 2. Ejecución (Act) ---

        // El método del servicio devuelve Mono<Void>
        Mono<Void> resultadoMono = productoService.delete(productoId);

        // --- 3. Verificación (Assert) ---

        // Para un Mono<Void>, solo verificamos que se complete exitosamente
        StepVerifier.create(resultadoMono)
                .verifyComplete();

        // Verificamos que se llamó al repositorio 1 vez con el ID correcto
        verify(productoRepository, times(1)).deleteById(productoId);
    }

    // --- AÑADE ESTA PRUEBA (Para el método findBajoStock) ---
    @Test
    void testFindBajoStock_Success() {
        // --- 1. Preparación (Arrange) ---
        int stockMinimo = 10;

        // Creamos un producto falso que cumple la condición
        Producto mockProducto = new Producto();
        mockProducto.setId(5L);
        mockProducto.setNombre("ProductoBajoStock");
        mockProducto.setStock(5); // Tiene 5, que es < 10

        // Mock: "CUANDO llamen a repository.findByStockLessThan(10),
        //       DEVUELVE nuestro producto falso"
        when(productoRepository.findByStockLessThan(stockMinimo)).thenReturn(Flux.just(mockProducto));

        // --- 2. Ejecución (Act) ---

        Flux<Producto> resultadoFlux = productoService.findBajoStock(stockMinimo);

        // --- 3. Verificación (Assert) ---

        StepVerifier.create(resultadoFlux)
                .expectNextMatches(producto -> {
                    // Verificamos que el producto devuelto SÍ tenga 5 de stock
                    assertEquals(5, producto.getStock());
                    assertEquals(5L, producto.getId());
                    return true;
                })
                .verifyComplete();

        // Verificamos que se llamó al repositorio 1 vez
        verify(productoRepository, times(1)).findByStockLessThan(stockMinimo);
    }

    // ¡Aquí añadiremos más @Test para los otros métodos (findById, save, etc.)!
}