package gm.libreria_waos_spring.repositorio;

import gm.libreria_waos_spring.modelo.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface AutorRepositorio extends JpaRepository<Autor, Integer> {
    @Query("SELECT DISTINCT a FROM Autor a LEFT JOIN FETCH a.libros")
    List<Autor> findAllConLibros();

    boolean existsByNombreAndNacionalidad(String nombre, String nacionalidad);
}
