package com.example.ms_productos.controller;

import com.example.ms_productos.model.Producto;
import com.example.ms_productos.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/productos") // URL endpoints
public class ProductoController {

    @Autowired
    private ProductoService service;

    // GET /api/productos
    @GetMapping
    public Flux<Producto> getAllProductos() {
        return service.findAll();
    }

    // GET /api/productos/{id}
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Producto>> getProductoById(@PathVariable Long id) {
        return service.findById(id)
                .map(producto -> ResponseEntity.ok(producto)) // Devuelve 200 OK si lo encuentra
                .defaultIfEmpty(ResponseEntity.notFound().build()); // Devuelve 404 si no
    }

    // POST /api/productos
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Devuelve un código 201 Created
    public Mono<Producto> createProducto(@RequestBody Producto producto) {
        return service.save(producto);
    }

    // PUT /api/productos/{id}
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Producto>> updateProducto(@PathVariable Long id, @RequestBody Producto producto) {
        return service.update(id, producto)
                .map(updatedProducto -> ResponseEntity.ok(updatedProducto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // DELETE /api/productos/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Devuelve un código 204 No Content
    public Mono<Void> deleteProducto(@PathVariable Long id) {
        return service.delete(id);
    }

    // - Endpoints de Procedimientos Almacenados ---

    // PUT /api/productos/{id}/stock
    //   RequestParam para recibir la cantidad
    @PutMapping("/{id}/stock")
    public Mono<ResponseEntity<Void>> actualizarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        return service.actualizarStock(id, cantidad)
                .then(Mono.just(ResponseEntity.ok().<Void>build())) // Devuelve 200 OK
                .defaultIfEmpty(ResponseEntity.notFound().build()); // Devuelve 404 si el ID no existe
    }

    // GET /api/productos/bajo-stock
    //  un RequestParam para recibir el mínimo
    @GetMapping("/bajo-stock")
    public Flux<Producto> getProductosBajoStock(@RequestParam Integer minimo) {
        return service.findBajoStock(minimo);
    }
}