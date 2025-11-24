package com.example.ms_productos.service;


import com.example.msproductos.model.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Define la lógica de negocio para el ms-productos.
 * ( Tarea 2.6 para ser reactivo)
 */
public interface ProductoService {

    Flux<Producto> findAll(); // GET /api/productos

    Mono<Producto> findById(Long id); // GET /api/productos/{id}

    Mono<Producto> save(Producto producto); // POST /api/productos

    Mono<Producto> update(Long id, Producto producto); // PUT /api/productos/{id}

    Mono<Void> delete(Long id); // DELETE /api/productos/{id}

    /**
     * Tareas 2.5 y 2.6 Llama a los procedimientos almacenados
     */
    Mono<Void> actualizarStock(Long productoId, Integer cantidad); // PUT /api/productos/{id}/stock

    Flux<Producto> findBajoStock(Integer minimo); // GET /api/productos/bajo-stock
}