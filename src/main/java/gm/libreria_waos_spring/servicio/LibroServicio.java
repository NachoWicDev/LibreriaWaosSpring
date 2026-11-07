package gm.libreria_waos_spring.servicio;

import gm.libreria_waos_spring.LibreriaWaosSpringApplication;
import gm.libreria_waos_spring.exception.ConflictoException;
import gm.libreria_waos_spring.exception.RecursoNoEncontradoException;
import gm.libreria_waos_spring.exception.ValidacionException;
import gm.libreria_waos_spring.modelo.Autor;
import gm.libreria_waos_spring.modelo.Libro;
import gm.libreria_waos_spring.repositorio.AutorRepositorio;
import gm.libreria_waos_spring.repositorio.LibroRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static gm.libreria_waos_spring.util.ConsolaUtil.*;

@Service
public class LibroServicio implements ILibroServicio {
    @Autowired
    private LibroRepositorio libroRepositorio;

    @Autowired
    private AutorRepositorio autorRepositorio;

    private static final Logger logger = LoggerFactory.getLogger(LibreriaWaosSpringApplication.class);

    private final String NL = System.lineSeparator(); // Salto de línea genérico para cualquier plataforma o SO.

    private final int VENTAS_INICIALES = 0; // Cuando creas un libro no tiene ventas.

    @Override
    @Transactional(readOnly = true)
    public void listarLibros() {
        logger.info(NL + "--- Lista de Libros ---" + NL);

        // 1. Leer los libros de la BD y guardarlos en un objeto List<Libro>.
        List<Libro> libros = libroRepositorio.findAllConAutor();

        // 2. Leer los objetos Libro de la lista usando un ForEach e imprimirlos en consola.
        for (Libro libro : libros) logger.info(String.valueOf(libro + NL));
    }

    @Override
    @Transactional(readOnly = true)
    public void listarMasVendidos() {
        logger.info(NL + "--- Libros Más Vendidos ---" + NL);

        // 1. Leer los libros de la BD y guardarlos en un objeto List<Libro>.
        List<Libro> libros = libroRepositorio.findAllByOrderByVentasDesc();

        // 2. Leer los objetos Libro de la lista usando un ForEach e imprimirlos en consola
        for (Libro libro : libros) logger.info(libro + NL);
    }

    @Override
    @Transactional(readOnly = true)
    public void listarPorNombre() {
        logger.info(NL + "--- Lista de Libros por Nombre ---" + NL);

        // 1. Leer los libros de la BD y guardarlos en un objeto List<Libro>.
        List<Libro> libros = libroRepositorio.findAllConAutorByNombre();

        // 2. Leer los objetos Libro de la lista usando un ForEach e imprimirlos en consola
        for (Libro libro : libros) logger.info(libro + NL);
    }

    @Override
    @Transactional(readOnly = true)
    public void buscarLibroPorId() {
        logger.info(NL + "--- Buscar Libro Por ID ---" + NL);

        // 1. Listar Libros disponibles.
        logger.info("Lista de Libros:" + NL);
        List<Libro> libros = libroRepositorio.findAllConAutor();
        for (Libro libroList : libros) logger.info(libroList + NL);

        // 2. Pedir ID del Libro con un DoWhile por si el usuario ingresa un valor inválido.
        Integer idLibro;
        do {
            idLibro = pedirInt(NL + "Ingrese el ID del Libro", false);
            if (idLibro == null) return;
            if (idLibro < 1) loguearIdMenorQue1();
        } while (idLibro < 1);

        // 3. Verificar que el Libro se encuentre en la BD.
        if (!libroRepositorio.existsById(idLibro)) {
            logger.info("Libro no disponible" + NL);
            return;
        }

        // 4. Leer el libro de la BD usando libroRepositorio.
        Libro libro = libroRepositorio.findById(idLibro).orElseThrow(() -> new RecursoNoEncontradoException(NL + "Libro no disponible." + NL));

        // 5. Imprimir Libro en consola.
        logger.info("Libro encontrado: " + NL + libro + NL);
    }

    @Override
    @Transactional
    public void agregarLibro() {
        logger.info(NL + "--- Agregar Libro ---" + NL);

        // 1. Recibir título.
        String titulo = pedirString("Ingrese el Título del Nuevo Libro", false);
        if (titulo == null) return;

        // 2. Mostrar lista de Autores para su posterior elección.
        List<Autor> autores = autorRepositorio.findAllConLibros();
        logger.info(NL + "--- Lista de Autores ---" + NL);
        for (Autor autor : autores) {
            logger.info(NL + autor);
        }

        // 3. Recibir ID del Autor.
        Integer idAutor;
        do {
            idAutor = pedirInt(NL + "Ingrese el ID del Autor", false);
            if (idAutor == null) return;
            if (idAutor < 1) loguearIdMenorQue1();
        } while (idAutor < 1);

        // 4. Validar y asignar Autor.
        if (!autorRepositorio.existsById(idAutor)) {
            logger.info("Autor no disponible." + NL);
            return;
        }
        Autor autor = autorRepositorio.findById(idAutor).orElseThrow(() -> new RecursoNoEncontradoException(NL + "Autor no disponible." + NL));

        // 5. Recibir y validar Precio y Existencias.
        Double precio;
        do {
            precio = pedirDouble("Ingrese el Precio del Libro", false);
            if (precio == null) return;
            if (precio < 0) loguearPrecioMenorQue0();
        } while (precio < 0);

        Integer existencias;
        do {
            existencias = pedirInt("Ingrese las Existencias del Libro", false);
            if (existencias == null) return;
            if (existencias < 0) loguearExistenciasMenorQue0();
        } while (existencias < 0);

        // 6. Validar que el Libro no esté duplicado.
        validarLibroDuplicado(titulo, idAutor);

        // 7. Crear constructor del Libro.
        Libro libro = Libro.builder()
                .titulo(titulo)
                .autor(autor)
                .precio(precio)
                .ventas(VENTAS_INICIALES)
                .existencias(existencias)
                .disponible(existencias > 0)
                .build();

        // 8. Validar confirmación de parte del Cliente.
        logger.info("Está por agregar a la Base de Datos el libro:" + NL + libro + NL);
        if (!chequearSeguro()) return;

        // 9. Validar Libro antes de inyectar en la BD.
        validarLibro(libro.getTitulo(), libro.getAutor().getId(), libro.getPrecio(), libro.getExistencias(), libro.getVentas(), libro.getDisponible());

        // 10. Inyectar y guardar Libro en la BD.
        libroRepositorio.save(libro);
        logger.info(NL + "Libro creado con éxito" + NL);
    }

    @Override
    @Transactional
    public void modificarLibro() {
        logger.info(NL + "--- Modificar Libro ---" + NL);

        // 1. Listar libros para que el usuario elija cuál modificar.
        listarLibros();

        // 2. Recibir ID del libro a modificar.
        Integer id;
        do {
            id = pedirInt(NL + "Ingrese el ID del Libro a modificar", false);
            if (id == null) return;
            if (id < 1) loguearIdMenorQue1();
        } while (id < 1);

        // 3. Validar y asignar Libro en memoria.
        if (!libroRepositorio.existsById(id)) {
            logger.info("Libro no disponible." + NL);
            return;
        }
        Libro libro = libroRepositorio.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("El libro no está disponible." + NL));

        // 4. Pedir título
        String titulo = pedirString("Ingrese el Título del libro", true);

        // 5. Listar autores para que el usuario elija cuál será el autor del libro.
        logger.info(NL + "--- Lista de Autores ---");
        List<Autor> autores = autorRepositorio.findAllConLibros();
        for (Autor autor : autores) logger.info(NL + autor);

        // 6. Pedir ID de nuevo autor con un do while y revisar si es null para no cambiar de autor
        Integer idAutor;
        do {
            idAutor = pedirInt(NL + "Ingrese el ID del Autor", true);
            if (idAutor != null && idAutor < 1) loguearIdMenorQue1();
        } while (idAutor != null && idAutor < 1);

        // 7. Pedir Precio, Ventas y Existencias del libro.
        Double precio = pedirDouble("Ingrese el Precio del libro", true);
        Integer ventas = pedirInt("Ingrese las Ventas del Libro", true);
        Integer existencias = pedirInt("Ingrese las Existencias del Libro", true);

        // 8. Validar campos vacíos (Si es null o String vacío, mantiene el valor que tenía anteriormente).
        if (titulo != null) libro.setTitulo(titulo);
        if (precio != null) libro.setPrecio(precio);
        if (ventas != null) libro.setVentas(ventas);
        if (existencias != null) {
            libro.setExistencias(existencias);
            libro.setDisponible(existencias >= 1);
        }

        // 9. Cambio de Autor (con validación de null).
        if (idAutor != null) {
            Autor autorViejo = libro.getAutor();
            Autor autorNuevo = autorRepositorio.findById(idAutor).orElseThrow(() -> new RecursoNoEncontradoException(NL + "El Autor no está disponible." + NL));

            // 10. Retirar Libro del Autor anterior (si éste es válido).
            if (autorViejo != null && !autorViejo.getId().equals(autorNuevo.getId())) {
                autorViejo.quitarLibro(libro);
            }

            // 11. Agregar Libro al nuevo Autor.
            autorNuevo.agregarLibro(libro);
        }

        // 12. Validar Libro actualizado.
        validarLibro(libro.getTitulo(), libro.getAutor().getId(), libro.getPrecio(), libro.getExistencias(), libro.getVentas(), libro.getDisponible());

        // 13. Validar confirmación por parte del usuario.
        if (!chequearSeguro()) return;

        // 14. Guardar modificación de Libro.
        libroRepositorio.save(libro);
        logger.info(NL + "Libro modificado con éxito." + NL);
    }

    @Override
    @Transactional
    public void eliminarLibro() {
        logger.info(NL + "--- Eliminar Libro ---" + NL);

        List<Libro> libros = libroRepositorio.findAllConAutor();
        logger.info("Lista de Libros:" + NL);
        for (Libro libro : libros) logger.info(libro + NL);

        // 1. Pedir ID del Libro.
        Integer idLibro;
        do {
            idLibro = pedirInt(NL + "Ingrese el ID del Libro", false);
            if (idLibro == null) return;
            if (idLibro < 1) loguearIdMenorQue1();
        } while (idLibro < 1);

        // 2. Buscar Libro en la BD.
        Libro libro = libroRepositorio.findById(idLibro).orElseThrow(() -> new RecursoNoEncontradoException(NL + "Libro no disponible." + NL));

        // 3. Chequear validación por parte del usuario.
        logger.info("Está por eliminar el libro:" + NL + libro + NL);
        if (!chequearSeguro()) return;

        // 4. Retirar Libro de la BD de su Autor.
        Autor autor = libro.getAutor();
        autor.quitarLibro(libro);

        // 5. Eliminar Libro de la BD.
        libroRepositorio.delete(libro);
        logger.info(NL + "Libro eliminado con éxito." + NL);
    }

    @Override
    @Transactional
    public void comprarLibro() {
        logger.info(NL + "--- Comprar Libro ---" + NL);

        // 1. Listar libros para que el usuario decida cual comprar.
        listarLibros();

        // 2. Pedir ID de Libro elegido.
        Integer idLibro;
        do {
            idLibro = pedirInt(NL + "Ingrese el ID del Libro", false);
            if (idLibro == null) return;
            if (idLibro < 1) loguearIdMenorQue1();
        } while (idLibro < 1);

        // 3. Buscar Libro en la BD
        Libro libro = libroRepositorio.findById(idLibro).orElseThrow(() -> new RecursoNoEncontradoException(NL + "Libro no disponible." + NL));

        // 4. Validar datos correctos del Libro.
        validarLibro(libro.getTitulo(), libro.getAutor().getId(), libro.getPrecio(), libro.getExistencias(), libro.getVentas(), libro.getDisponible());

        // 5. Validar Stock del Libro.
        if (libro.getExistencias() < 1) {
            logger.info("El libro '" + libro.getTitulo() + "' no tiene Stock, vuelva pronto." + NL);
            return;
        }

        // 6. Notificar al usuario la cantidad de copias en Stock del Libro.
        logger.info("El Libro '" + libro.getTitulo() + "' tiene " + libro.getExistencias() + " copias en stock y cuesta $" + libro.getPrecio() + NL);

        // 7. Consultar y validar cantidad de copias que desea comprar.
        Integer cantidad;
        do {
            cantidad = pedirInt("Ingrese cuantas copias del Libro desea comprar", false);
            if (cantidad == null) return;
            if (cantidad > libro.getExistencias()) {
                logger.info("El Libro tiene " + libro.getExistencias() + " copias en Stock, no le podemos vender más que eso." + NL);
            } else if (cantidad < 1) {
                logger.info("Debe ingresar un numero mayor o igual a 1");
            }
        } while (cantidad > libro.getExistencias() || cantidad < 1);

        // 8. Validación por parte del usuario.
        logger.info("Está por comprar " + cantidad + " copias del libro:" + NL + libro + NL);
        logger.info("Sería un total de $" + libro.getPrecio() * cantidad + NL);
        if (!chequearSeguro()) return;

        // 9. Modificación de valores existencias ventas y disponible.
        libro.setExistencias(libro.getExistencias() - cantidad);
        libro.setVentas(libro.getVentas() + cantidad);
        libro.setDisponible(libro.getExistencias() > 0);

        // 10. Guardar transacción en la BD.
        libroRepositorio.save(libro);
        logger.info(NL + "Compra realizada con éxito." + NL);
    }

    @Override
    @Transactional
    public void recargarStock() {
        logger.info(NL + "--- Recargar Stock ---");

        // 1. Listar libros para que el cliente elija cuál reestockear.
        List<Libro> libros = libroRepositorio.findAllConAutor();
        for (Libro libro : libros) logger.info(NL + libro);

        // 2. Pedir ID del Libro elegido.
        Integer idLibro;
        do {
            idLibro = pedirInt(NL + "Ingrese el ID del Libro", false);
            if (idLibro == null) return;
            if (idLibro < 1) loguearIdMenorQue1();
        } while (idLibro < 1);

        // 3. Buscar Libro elegido en la BD.
        Libro libro = libroRepositorio.findById(idLibro).orElseThrow(() -> new RecursoNoEncontradoException(NL + "Libro no disponible." + NL));

        // 4. Validar datos correctos del Libro.
        validarLibro(libro.getTitulo(), libro.getAutor().getId(), libro.getPrecio(), libro.getExistencias(), libro.getVentas(), libro.getDisponible());

        // 5. Notificar al usuario de la cantidad de existencias en Stock.
        logger.info(NL + "El libro '" + libro.getTitulo() + "' tiene " + libro.getExistencias() + " en Stock.");

        // 6. Pedir al usuario cantidad de copias a reestockear y validar que el número sea válido.
        Integer cantidad;
        do {
            cantidad = pedirInt("Ingrese la cantidad de copias a reestockear", false);
            if (cantidad == null) return;
            if (cantidad < 1) loguearCopiasMenorQue1();
        } while (cantidad < 1);

        // 7. Validación por parte del usuario.
        logger.info("Está por reestockear " + cantidad + " copias del Libro '" + libro.getTitulo() + "'");
        if (!chequearSeguro()) return;

        // 8. Asignar nuevo valor de existencias y disponible según corresponda.
        libro.setExistencias(libro.getExistencias() + cantidad);
        libro.setDisponible(libro.getExistencias() > 0);

        // 9. Guardar transacción en la BD.
        libroRepositorio.save(libro);
        logger.info(NL + "Libro reestockeado con éxito!" + NL);
    }

    @Transactional(readOnly = true)
    private void validarLibroDuplicado(String titulo, Integer idAutor) {
        // Validar que el libro no se encuentre en la BD a la hora de crearlo.
        boolean existe = libroRepositorio.existsByTituloAndAutor_Id(titulo.trim(), idAutor);
        if (existe) throw new ConflictoException(NL + "Ya existe '" + titulo + "' para este Autor." + NL);
    }

    private void validarLibro(String titulo, Integer idAutor, Double precio, Integer existencias, Integer ventas, Boolean disponible) {
        // Validaciones de conlfictos con valores del libro.
        if (titulo == null || titulo.isBlank()) throw new ValidacionException("El título es obligatorio.");
        if (titulo.trim().length() > 100) throw new ValidacionException("El título no puede pasar de 100 caracteres.");
        if (idAutor == null || idAutor <= 0) throw new ValidacionException("El ID del Autor debe ser mayor a 0." + NL);
        if (precio == null || precio < 0) throw new ValidacionException(NL + "El precio debe ser mayor o igual a 0." + NL);
        if (existencias == null || existencias < 0) throw new ValidacionException(NL + "El stock debe ser mayor o igual a 0." + NL);
        if (ventas == null || ventas < 0) throw new ValidacionException(NL + "Las ventas deben ser igual o mayores a 0." + NL);
        if (disponible == null) throw new ValidacionException(NL + "El valor Disponible no puede ser Nulo." + NL);

        boolean debeEstarDisponible = existencias > 0;
        if (!disponible.equals(debeEstarDisponible)) {
            throw new ValidacionException(NL + "El valor Disponible y la cantidad de Existencias no coinciden." + NL);
        }
    }
}
