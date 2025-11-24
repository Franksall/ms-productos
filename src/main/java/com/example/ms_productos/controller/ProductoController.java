package com.example.ms_productos.controller;

import com.example.msproductos.api.ProductoControllerApi;
import com.example.msproductos.model.Producto;
import com.example.ms_productos.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
// No se a;ade @RequestMapping a nivel de clase, lo forzamos en los métodos
public class ProductoController implements ProductoControllerApi {

    @Autowired
    private ProductoService service;

    //  RUTAS

    @Override
    @GetMapping("/api/productos")
    public Mono<ResponseEntity<Flux<Producto>>> getAllProductos(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(service.findAll()));
    }

    @Override
    @GetMapping("/api/productos/{id}")
    public Mono<ResponseEntity<Producto>> getProductoById(@PathVariable("id") Long id, ServerWebExchange exchange) {
        return service.findById(id)
                .map(producto -> ResponseEntity.ok(producto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    @PostMapping("/api/productos")
    public Mono<ResponseEntity<Producto>> createProducto(@Valid @RequestBody Mono<Producto> productoMono, ServerWebExchange exchange) {
        return productoMono
                .flatMap(producto -> service.save(producto))
                .map(nuevo -> ResponseEntity
                        .created(URI.create("/api/productos/" + nuevo.getId()))
                        .body(nuevo)
                );
    }

    @Override
    @PutMapping("/api/productos/{id}")
    public Mono<ResponseEntity<Producto>> updateProducto(@PathVariable("id") Long id, @Valid @RequestBody Mono<Producto> productoMono, ServerWebExchange exchange) {
        return productoMono
                .flatMap(producto -> service.update(id, producto))
                .map(actualizado -> ResponseEntity.ok(actualizado))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    @DeleteMapping("/api/productos/{id}")
    public Mono<ResponseEntity<Void>> deleteProducto(@PathVariable("id") Long id, ServerWebExchange exchange) {
        return service.delete(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    @PutMapping("/api/productos/{id}/stock")
    public Mono<ResponseEntity<Void>> actualizarStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad, ServerWebExchange exchange) {
        return service.actualizarStock(id, cantidad)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    @GetMapping("/api/productos/bajo-stock")
    public Mono<ResponseEntity<Flux<Producto>>> getProductosBajoStock(@RequestParam("minimo") Integer minimo, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(service.findBajoStock(minimo)));
    }
}