package com.example.ms_productos.service;

import com.example.ms_productos.model.Producto;
import com.example.ms_productos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository repository;

    @Override
    public Flux<Producto> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Producto> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        // Asignamos los valores por defecto  Tarea 2.3
        producto.setActivo(true);
        producto.setFechaCreacion(LocalDateTime.now());
        return repository.save(producto);
    }

    @Override
    public Mono<Producto> update(Long id, Producto productoRequest) {
        return repository.findById(id)
                .flatMap(existingProducto -> {
                    // Actualizamos los campos
                    existingProducto.setNombre(productoRequest.getNombre());
                    existingProducto.setDescripcion(productoRequest.getDescripcion());
                    existingProducto.setPrecio(productoRequest.getPrecio());
                    existingProducto.setStock(productoRequest.getStock());
                    existingProducto.setActivo(productoRequest.getActivo());
                    return repository.save(existingProducto);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }

    // -- Métodos de Procedimientos Almacenados ---

    @Override
    public Mono<Void> actualizarStock(Long productoId, Integer cantidad) {
        // Llama al metodo que definimos en el repositorio
        return repository.actualizarStock(productoId, cantidad);
    }

    @Override
    public Flux<Producto> findBajoStock(Integer minimo) {
        // Llama al metodo que definimos en el repositorio
        return repository.findByStockLessThan(minimo);
    }
}