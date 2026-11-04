package gm.libreria_waos_spring.modelo;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor // Crea constructor vacío
@AllArgsConstructor // Crea constructor completo (no se puede elegir dejar fuera algunos argumentos del constructor), necesario para usar @Builder
@Builder // Método para crear objetos sin usar AllArgs que obliga a meter ID y causa bugs
@ToString(exclude = "libros") // Crea un toString y excluye los libros para evitar un bucle infinito
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Crea métodos equals y hashCode
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Es la propiedad JPA para un auto increment
    @EqualsAndHashCode.Include // Añade el ID en el método Equals y HashCode
    private Integer id;
    private String nombre;
    private String nacionalidad;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Libro> libros = new ArrayList<>(); // Crea el JOIN entre tablas y permite mostrar los libros de dicho autor

    public void agregarLibro(Libro libro) {
        this.libros.add(libro);
        libro.setAutor(this);
    }

    public void quitarLibro(Libro libro) {
        libros.remove(libro);
        libro.setAutor(null);
    }

    @Override
    public String toString() {
        String titulos = libros.isEmpty()
                ? "Ninguno"
                : libros.stream()
                    .map(Libro::getTitulo)
                .collect(Collectors.joining(", "));
        String nombreCorto = nombre.length() > 30 ? nombre.substring(0, 27) + "..." : nombre;
        nombreCorto = String.format("%-30s", nombreCorto);

        return String.format(
                "ID: %-3d | Nombre: %s | Nacionalidad: %-20s | Libros: %s",
                id,
                nombreCorto,
                nacionalidad,
                titulos.length() > 40 ? titulos.substring(0, 37) + "..." : titulos
        );
    }
}
