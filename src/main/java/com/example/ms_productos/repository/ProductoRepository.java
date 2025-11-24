package com.example.ms_productos.repository;

import com.example.ms_productos.model.ProductoEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository; // <-- 1. No es JpaRepository
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductoRepository extends R2dbcRepository<ProductoEntity, Long> { // <-- 2. Es R2dbcRepository

    /**
     * Tarea 2.5: Llamada al procedimiento almacenado "actualizar_stock".
     * Como @Procedure no existe en R2DBC, usamos @Query
     * Usamos @Modifying porque la función ejecuta un UPDATE
     * Retorna Mono<Void> porque es reactivo y la función no retorna nada
     */
    @Modifying
    @Query("SELECT * FROM actualizar_stock(:productoId, :cantidad)")
    Mono<Void> actualizarStock(Long productoId, Integer cantidad);

    /**
     * Tarea 2.5: Llamada al procedimiento almacenado "productos_bajo_stock".
     *  List<Object[]>, pero en reactivo, podemos mapear
     * la tabla que retorna la función (id, nombre, stock)
     */
    Flux<ProductoEntity> findByStockLessThan(Integer minimo);
}