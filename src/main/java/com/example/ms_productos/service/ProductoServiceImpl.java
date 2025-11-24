package com.example.ms_productos.service;

import com.example.msproductos.model.Producto;       // DTO (Robot Generado)
import com.example.ms_productos.model.ProductoEntity; // Entidad (Base de Datos)

import com.example.ms_productos.repository.ProductoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository repository;


    //  MÉTODOS TRADUCTORES (Mappers)


    // Convierte de la Base de Datos (Entity)  a  API (DTO)
    private Producto mapToDto(ProductoEntity entity) {
        Producto dto = new Producto();
        BeanUtils.copyProperties(entity, dto); // Copia nombre, precio, stock...

        // Conversión especial de fecha (LocalDateTime -> OffsetDateTime)
        if (entity.getFechaCreacion() != null) {
            dto.setFechaCreacion(entity.getFechaCreacion().atOffset(ZoneOffset.UTC));
        }
        return dto;
    }

    // Convierte de la API (DTO)  a Base de Datos (Entity)
    private ProductoEntity mapToEntity(Producto dto) {
        ProductoEntity entity = new ProductoEntity();
        BeanUtils.copyProperties(dto, entity);

        // Si es nuevo y no tiene fecha, le ponemos la actual
        if (entity.getFechaCreacion() == null) {
            entity.setFechaCreacion(LocalDateTime.now());
        }
        return entity;
    }


    //  LÓGICA DE NEGOCIO


    @Override
    public Flux<Producto> findAll() {
        return repository.findAll()
                .map(this::mapToDto); // Traduc cada resultado de Entity a DTO
    }

    @Override
    public Mono<Producto> findById(Long id) {
        return repository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    public Mono<Producto> save(Producto productoDto) {
        // Convert el DTO que llega a Entidad para poder guardarlo
        ProductoEntity entity = mapToEntity(productoDto);

        //  reglas de negocio (Tarea 2.3)
        entity.setActivo(true);

        //  Guarda y convert el resultado de vuelta a DTO
        return repository.save(entity)
                .map(this::mapToDto);
    }

    @Override
    public Mono<Producto> update(Long id, Producto productoDto) {
        return repository.findById(id)
                .flatMap(existingEntity -> {
                    // Actualizamos solo los campos editables
                    existingEntity.setNombre(productoDto.getNombre());
                    existingEntity.setDescripcion(productoDto.getDescripcion());
                    existingEntity.setPrecio(productoDto.getPrecio());
                    existingEntity.setStock(productoDto.getStock());

                    // Si el DTO trae estado activo, lo actualizamos, si no, lo dejamos como estaba
                    if(productoDto.getActivo() != null) {
                        existingEntity.setActivo(productoDto.getActivo());
                    }

                    // No tocamos ID ni FechaCreacion
                    return repository.save(existingEntity);
                })
                .map(this::mapToDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }

    // Procedimientos Almacenados (Tareas 2.5 / 2.6)

    @Override
    public Mono<Void> actualizarStock(Long id, Integer cantidad) {
        // Llamamos a query personalizada del repositorio
        return repository.actualizarStock(id, cantidad);
    }

    @Override
    public Flux<Producto> findBajoStock(Integer minimo) {
        //  repositorio devuelve Entities, convertimos a DTOs
        return repository.findByStockLessThan(minimo)
                .map(this::mapToDto);
    }
}