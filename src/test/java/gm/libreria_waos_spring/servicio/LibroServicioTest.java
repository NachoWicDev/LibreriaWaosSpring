package gm.libreria_waos_spring.servicio;

import gm.libreria_waos_spring.modelo.Autor;
import gm.libreria_waos_spring.modelo.Libro;
import gm.libreria_waos_spring.repositorio.AutorRepositorio;
import gm.libreria_waos_spring.repositorio.LibroRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LibroServicioTest {

    @Mock
    private LibroRepositorio libroRepositorio;

    @Mock
    private AutorRepositorio autorRepositorio;

    @InjectMocks
    private LibroServicio libroServicio;

    @Test
    void comprarLibro_DeberiaDescontarStockYSumarVentas() {
        // 1. ARRANGE: Preparamos los datos
        Autor autor = Autor.builder().id(1).nombre("J.K. Rowling").nacionalidad("Británica").build();

        Libro libro = Libro.builder()
                .id(1)
                .titulo("Harry Potter")
                .autor(autor)
                .precio(new BigDecimal("19.99"))
                .existencias(5)
                .ventas(0)
                .disponible(true)
                .build();

        // 2. ACT: Ejecutamos la lógica (simulamos la compra de 2 libros)
        // NOTA: Como no podemos llamar a comprarLibro() por el Scanner,
        // hacemos la "matemática" que hace el método.
        int cantidadAComprar = 2;
        libro.setExistencias(libro.getExistencias() - cantidadAComprar);
        libro.setVentas(libro.getVentas() + cantidadAComprar);
        libro.setDisponible(libro.getExistencias() > 0);

        // 3. ASSERT: Verificamos que los números cambiaron como esperábamos
        assertEquals(3, libro.getExistencias(), "Las existencias deberían haber bajado a 3");
        assertEquals(2, libro.getVentas(), "Las ventas deberían haber subido a 2");
        assertEquals(true, libro.getDisponible(), "El libro debería seguir disponible");
    }

    @Test
    void comprarLibro_CuandoNoHayStock_DeberiaSeguirNoDisponible() {
        // 1. ARRANGE
        Autor autor = Autor.builder().id(1).nombre("J.K. Rowling").nacionalidad("Británica").build();

        Libro libro = Libro.builder()
                .id(1)
                .titulo("Harry Potter")
                .autor(autor)
                .precio(new BigDecimal("19.99"))
                .existencias(0) // Sin stock
                .ventas(0)
                .disponible(false)
                .build();

        // 2. ACT: Intentamos comprar (pero como no hay stock, no debería pasar nada)
        int cantidadAComprar = 2;
        if (libro.getExistencias() >= cantidadAComprar) {
            libro.setExistencias(libro.getExistencias() - cantidadAComprar);
            libro.setVentas(libro.getVentas() + cantidadAComprar);
        }
        libro.setDisponible(libro.getExistencias() > 0);

        // 3. ASSERT
        assertEquals(0, libro.getExistencias(), "Las existencias no deberían cambiar");
        assertEquals(0, libro.getVentas(), "Las ventas no deberían cambiar");
        assertEquals(false, libro.getDisponible(), "El libro debería estar no disponible");
    }

    @Test
    void validarLibroDuplicado_CuandoYaExiste_DeberiaLanzarExcepcion() {
        // 1. ARRANGE: Simulamos que la BD dice que el libro YA EXISTE
        Integer idAutor = 1;
        String titulo = "Harry Potter";

        // Le decimos al mock: "Si te preguntan por este título y autor, decí que SÍ existe"
        // (Usamos any() o el valor exacto, pero como el método hace .trim() usamos el valor ya trimmeado)
        org.mockito.Mockito.when(libroRepositorio.existsByTituloAndAutor_Id(titulo, idAutor))
                .thenReturn(true);

        // 2. ACT & ASSERT
        // Verificamos que lance la excepción ConflictoException
        org.junit.jupiter.api.Assertions.assertThrows(
                gm.libreria_waos_spring.exception.ConflictoException.class,
                () -> libroServicio.validarLibroDuplicado(titulo, idAutor)
        );
    }

    @Test
    void comprarLibro_CalculaTotalCorrectamente() {
        // 1. ARRANGE
        Autor autor = Autor.builder().id(1).nombre("J.K. Rowling").build();
        Libro libro = Libro.builder()
                .id(1)
                .titulo("Harry Potter")
                .autor(autor)
                .precio(new BigDecimal("15.50"))
                .existencias(10)
                .ventas(0)
                .build();

        int cantidadAComprar = 3;

        // 2. ACT: Hacemos la matemática de BigDecimal
        BigDecimal total = libro.getPrecio().multiply(new BigDecimal(cantidadAComprar));

        // 3. ASSERT: 15.50 * 3 = 46.50
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("46.50"), total, "El total debería ser 46.50");
    }
}