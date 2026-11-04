package gm.libreria_waos_spring.servicio;

import gm.libreria_waos_spring.LibreriaWaosSpringApplication;
import gm.libreria_waos_spring.exception.ConflictoException;
import gm.libreria_waos_spring.exception.RecursoNoEncontradoException;
import gm.libreria_waos_spring.exception.ValidacionException;
import gm.libreria_waos_spring.modelo.Autor;
import gm.libreria_waos_spring.modelo.Libro;
import gm.libreria_waos_spring.repositorio.AutorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static gm.libreria_waos_spring.util.ConsolaUtil.*;

@Service
public class AutorServicio implements IAutorServicio {
    @Autowired
    private AutorRepositorio autorRepositorio;

    private static final Logger logger = LoggerFactory.getLogger(LibreriaWaosSpringApplication.class);

    private final String NL = System.lineSeparator(); // Salto de línea genérico para cualquier plataforma o SO

    @Override
    @Transactional(readOnly = true)
    public void listarAutores() {
        logger.info(NL + "--- Lista de Autores ---" + NL);

        List<Autor> autores = autorRepositorio.findAllConLibros();
        for (Autor autor : autores) logger.info(autor + NL);
    }

    @Override
    @Transactional(readOnly = true)
    public void buscarAutorPorId() {
        logger.info(NL + "--- Buscar Autor Por ID ---" + NL);

        // 1. Pedir ID del Autor con un DoWhile por si el usuario ingresa un valor inválido.
        Integer idAutor;
        do {
            idAutor = pedirInt(NL + "Ingrese el ID del Autor", false);
            if (idAutor == null) return;
            if (idAutor < 1) loguearIdMenorQue1();
        } while (idAutor < 1);

        // 3. Verificar que el Autor se encuentre en la BD.
        if (!autorRepositorio.existsById(idAutor)) {
            logger.info("Autor no disponible" + NL);
            return;
        }

        // 4. Leer el Autor de la BD usando autorRepositorio.
        Autor autor = autorRepositorio.findById(idAutor).orElseThrow(() -> new RecursoNoEncontradoException(NL + "Autor no disponible." + NL));

        // 5. Imprimir Autor en consola.
        logger.info("Autor encontrado: " + NL + autor + NL);
    }

    @Override
    @Transactional
    public void agregarAutor() {
        logger.info(NL + "--- Agregar Autor ---" + NL);

        String nombre = pedirString("Ingrese el Nombre del Autor", false);
        if (nombre == null) return;
        String nacionalidad = pedirString("Ingrese la Nacionalidad del Autor", false);
        if (nacionalidad == null) return;

        validarAutor(nombre, nacionalidad);

        Autor autor = Autor.builder()
                .nombre(nombre)
                .nacionalidad(nacionalidad)
                .build();

        validarDuplicado(nombre, nacionalidad);

        logger.info(NL + "Está por agregar a la Base de Datos al Autor de nombre '" + autor.getNombre() + "' y de nacionalidad " + autor.getNacionalidad() + "." + NL);
        if (!chequearSeguro()) return;

        autorRepositorio.save(autor);
        logger.info(NL + "Autor agregado a la Base de Datos con éxito!");
    }

    @Override
    @Transactional
    public void modificarAutor() {
        logger.info(NL + "--- Modificar Autor ---" + NL);

        listarAutores();

        Integer idAutor;
        do {
            idAutor = pedirInt(NL + "Ingrese el ID del Autor", false);
            if (idAutor == null) return;
            if (idAutor < 1) loguearIdMenorQue1();
        } while (idAutor < 1);

        Autor autor = autorRepositorio.findById(idAutor).orElseThrow(() -> new RecursoNoEncontradoException("Autor no encontrado en la BD" + NL));

        String nombre = pedirString("Ingrese el Nombre el Autor", true);
        String nacionalidad = pedirString("Ingrese la Nacionalidad del Autor", true);

        if (nombre != null) autor.setNombre(nombre);
        if (nacionalidad != null) autor.setNacionalidad(nacionalidad);

        validarAutor(autor.getNombre(), autor.getNacionalidad());

        logger.info("Está por modificar el Autor:\s" + autor);

        autorRepositorio.save(autor);
        logger.info("Autor modificado con éxito!" + NL + autor);
    }

    @Override
    @Transactional
    public void eliminarAutor() {
        logger.info(NL + "--- Eliminar Autor ---" + NL);

        listarAutores();

        Integer idAutor;
        do {
            idAutor = pedirInt(NL + "Ingrese el ID del Autor", false);
            if (idAutor == null) return;
            if (idAutor < 1) loguearIdMenorQue1();
        } while (idAutor < 1);

        try {
            Autor autor = autorRepositorio.findById(idAutor).orElseThrow(() -> new RecursoNoEncontradoException("Autor no disponible." + NL));

            logger.info("Está por eliminar el Autor '" + autor.getNombre() + "' de Nacionalidad " + autor.getNacionalidad() + "," + NL);
            logger.info("Y sus Libros:" + NL);
            List<Libro> libros = autor.getLibros();
            for (Libro libro : libros) logger.info(libro + NL);

            if (!chequearSeguro()) return;

            autorRepositorio.delete(autor);
            logger.info("Autor y Libros eliminados con éxito.");
        } catch (RecursoNoEncontradoException e) {
            logger.info("Autor no disponible." + NL);
        }
    }

    private void validarDuplicado(String nombre, String nacionalidad) {
        // Validar que el Autor no se encuentre en la BD a la hora de crearlo.
        boolean existe = autorRepositorio.existsByNombreAndNacionalidad(nombre, nacionalidad);
        if (existe) throw new ConflictoException(NL + "Ya existe el autor '" + nombre + "' de nacionalidad " + nacionalidad + " en la Base de Datos.");
    }

    private void validarAutor(String nombre, String nacionalidad) {
        if (nombre == null || nombre.isBlank()) throw new ValidacionException("El Nombre es obligatorio.");
        if (nacionalidad == null || nacionalidad.isBlank()) throw new ValidacionException("La Nacionalidad es obligatoria.");
    }
}
