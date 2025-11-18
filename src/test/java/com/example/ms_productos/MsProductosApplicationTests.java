package com.example.ms_productos;

import com.example.ms_productos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

// ¡Ya NO necesitamos las propiedades de 'exclude' o 'properties' aquí!
// El 'application.yml' de test se encargará de apagar todo.
@SpringBootTest
class MsProductosApplicationTests {

    // Solo necesitamos el mock del repo,
    // porque la auto-configuración de la BD (que apagaremos en el YML)
    // lo requiere para arrancar.
    @MockBean
    private ProductoRepository productoRepository;

    @Test
    void contextLoads() {
    }
}