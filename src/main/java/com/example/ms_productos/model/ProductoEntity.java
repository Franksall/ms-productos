package com.example.ms_productos.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data //  Anotación  Lombok
@NoArgsConstructor //  Lombok
@AllArgsConstructor //  Lombok
@Table("productos") //   R2DBC (reemplaza a @Entity)
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}