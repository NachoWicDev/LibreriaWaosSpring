package gm.libreria_waos_spring.repositorio;

import gm.libreria_waos_spring.modelo.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepositorio extends JpaRepository<Libro, Integer> {
    @Query("SELECT l FROM Libro l JOIN FETCH l.autor ORDER BY ventas DESC")
    List<Libro> findAllByOrderByVentasDesc();

    @Query("SELECT l FROM Libro l JOIN FETCH l.autor ORDER BY l.id")
    List<Libro> findAllConAutor();

    @Query("SELECT l FROM Libro l JOIN FETCH l.autor ORDER BY l.titulo")
    List<Libro> findAllConAutorByNombre();

    boolean existsByTituloAndAutor_Id(String titulo, Integer idAutor);
}
