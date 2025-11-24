# Microservicio: Productos (ms-productos)

## 🎯 Propósito

Este servicio gestiona toda la lógica de negocio relacionada con el **catálogo de productos y el inventario**.

Es una API reactiva (construida con Spring WebFlux) que expone endpoints para:
* Crear, leer, actualizar y eliminar productos.
* Consultar y actualizar el stock.

Este servicio es "consumido" (llamado) por `ms-pedidos` cada vez que se crea un nuevo pedido, para verificar el stock y obtener el precio.

## 🛠️ Configuración Clave

* **Puerto de Servicio:** `8081`
* **Tecnología de Datos:** `spring-boot-starter-data-r2dbc` (Reactivo).
* **Base de Datos:** Se conecta a la base de datos `sistema_pedidos_db` en el contenedor `postgres-db`.
* **Tablas/Funciones que utiliza:**
    * `productos` (tabla)
    * `actualizar_stock` (función)
    * `productos_bajo_stock` (función)

## 🐳 Docker

* **Dependencias de Arranque:** En `docker-compose.yml`, este servicio espera a que `ms-config-server`, `postgres-db` y `registry-service` estén en estado `healthy` (saludable) antes de arrancar.
* **Registro de Servicios:** Al arrancar, se conecta a Eureka (en `registry-service:8099`) y se registra con el nombre `MS-PRODUCTOS`.