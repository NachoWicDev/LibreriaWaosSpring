package gm.libreria_waos_spring.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor // Crea constructor vacío
@AllArgsConstructor // Crea constructor completo (no se puede elegir dejar fuera algunos argumentos del constructor), necesario para usar @Builder
@Builder // Método para crear objetos sin usar AllArgs que obliga a meter ID y causa bugs
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Crea métodos equals y HashCode
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Es la propiedad JPA para un auto increment
    @EqualsAndHashCode.Include // Añade el ID en el método Equals y HashCode
    private Integer id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El título es obligatorio.")
    private String titulo;

    @ManyToOne(fetch = FetchType.LAZY) // Varios libros para 1 autor
    @JoinColumn(name = "id_autor") // Nombre de la columna FK en la tabla libro
    private Autor autor;

    private Boolean disponible;

    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    private BigDecimal precio;
    private Integer ventas;

    @Min(value = 0, message = "Las existencias no pueden ser negativas")
    private Integer existencias;

    @Override
    public String toString() {
        String disponible = this.getDisponible() ? "Sí" : "No";
        String tituloCorto = titulo.length() > 30 ? titulo.substring(0, 27) + "..." : titulo;
        tituloCorto = String.format("%-30s", tituloCorto);
        String autorCorto = autor.getNombre().length() > 30 ? autor.getNombre().substring(0, 27) + "..." : autor.getNombre();
        autorCorto = String.format("%-30s", autorCorto);

        return String.format(
                "ID: %-3d | Título: %s | Autor: %s | Disponible: %-5s | Precio: $%8.2f | Ventas: %6d | Existencias: %3d",
                id,
                tituloCorto, // usamos el truncado
                autor != null ? autorCorto : "N/A",
                disponible,
                precio != null ? precio.doubleValue() : 0.0,
                ventas,
                existencias
        );
    }
}
