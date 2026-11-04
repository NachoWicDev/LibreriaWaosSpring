package gm.libreria_waos_spring;

import gm.libreria_waos_spring.servicio.IAutorServicio;
import gm.libreria_waos_spring.servicio.ILibroServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class LibreriaWaosSpringApplication implements CommandLineRunner {
	// Creación de objetos para la conexión entre el usuario y la BD.
	@Autowired
	private ILibroServicio libroServicio; // Inyectar dependencia de servicio a la capa de presentación

	@Autowired
	private IAutorServicio autorServicio; // Inyectar dependencia de servicio a la capa de presentación

	private static final Logger logger = LoggerFactory.getLogger(LibreriaWaosSpringApplication.class); // Logger para enviar mensajes por consola

	private final String NL = System.lineSeparator(); // Salto de línea genérico para cualquier plataforma o SO

	// Inicio de la aplicación.
	public static void main(String[] args) {
		logger.info("\nIniciando la aplicacion\n");
		// Inicializar fábrica de Spring y correr aplicación
		SpringApplication.run(LibreriaWaosSpringApplication.class, args);
		logger.info("\nAplicación finalizada\n");
	}

	// Script que corre el código.
	@Override
	public void run(String... args) throws Exception {
		libreriaWaosApp();
	}

	private void libreriaWaosApp() {
		logger.info(NL + "*** Aplicación Librería Waos ***" + NL);
		var salir = false;
		var consola = new Scanner(System.in);
		while (!salir) {
			int[] opciones = mostrarMenu(consola);
			salir = ejecutarOpciones(consola, opciones);
			logger.info(NL);
		}
	}

	private int[] mostrarMenu(Scanner consola) {
		// Creación de Array para la recolección de opciones vía consola.
		int[] opciones = new int[2];

		// Muestra de menú y ejecución del menú principal donde el usuario puede elegir entrar en uno de los 3 submenus o cerrar el programa.
		opciones[0] = menuGeneral(consola);

		// Ejecución de la opción seleccionada por el usuario en la consola.
		switch (opciones[0]) {
			case 1 -> opciones[1] = menuRevisionLibros(consola);
			case 2 -> opciones[1] = menuAutores(consola);
			case 3 -> opciones[1] = menuGestionLibros(consola);
			case 4 -> logger.info("¡Regresa pronto!" + NL);
		}
		return opciones;
	}

	private int menuGeneral(Scanner consola) {
		while (true) {
			try {
				// Muestra de menú y ejecución del menú principal donde el usuario puede elegir entrar en uno de los 3 submenus o cerrar el programa.
				logger.info("""
					\nOpciones:
					\t1. Revisión de Libros
					\t2. Revisión y Gestión de Autores
					\t3. Gestión de Libros
					\t4. Salir
					Elija una opción:\s""");
				int opcion = Integer.parseInt(consola.nextLine());
				if (opcion >= 1 && opcion <= 4) {
					return opcion;
				} else {
					loguearOpcionInvalida(4);
				}
			} catch (NumberFormatException e) {
				loguearOpcionInvalida(4);
			}
		}
	}

	private int menuRevisionLibros(Scanner consola) {
		while (true) {
			try {
				// Muestra del submenú elegido y recolección de segunda opción para la próxima ejecución de la misma.
				logger.info("""
					\n--- Opciones de Revisión de Libros ---
					\t1. Listar Libros
					\t2. Ordenar Más vendidos
					\t3. Buscar Libro por ID
					\t4. Listar Libros por Nombre
					\t5. Regresar
					Elija una opción:\s""");
				int opcion = Integer.parseInt(consola.nextLine());

				if (opcion >= 1 && opcion <= 5) {
					return opcion;
				} else {
					loguearOpcionInvalida(5);
				}
			} catch (NumberFormatException e) {
				loguearOpcionInvalida(5);
			}
		}
	}

	private int menuAutores(Scanner consola) {
		while (true) {
			try {
				// Muestra del submenú elegido y recolección de segunda opción para la próxima ejecución de la misma.
				logger.info("""
						\n--- Opciones de Autores ---
						\t1. Listar Autores
						\t2. Buscar Autor por ID
						\t3. Agregar Autor
						\t4. Modificar Autor
						\t5. Eliminar Autor
						\t6. Salir
						Elija una opción:\s""");
				int opcion = Integer.parseInt(consola.nextLine());

				if (opcion >= 1 && opcion <= 6) {
					return opcion;
				} else {
					loguearOpcionInvalida(6);
				}
			} catch (NumberFormatException e) {
				loguearOpcionInvalida(6);
			}
		}
	}

	private int menuGestionLibros(Scanner consola) {
		while (true) {
			try {
				// Muestra del submenú elegido y recolección de segunda opción para la próxima ejecución de la misma.
				logger.info("""
						\n--- Opciones de Gestión de Libros ---
						\t1. Agregar Libro
						\t2. Modificar Libro
						\t3. Comprar Libro
						\t4. Recargar Stock Libro
						\t5. Eliminar Libro
						\t6. Regresar
						Elija una opción:\s""");
				int opcion = Integer.parseInt(consola.nextLine());

				if (opcion >= 1 && opcion <= 6) {
					return opcion;
				} else {
					loguearOpcionInvalida(6);
				}
			} catch (NumberFormatException e) {
				loguearOpcionInvalida(6);
			}
		}
	}

	private boolean ejecutarOpciones(Scanner consola, int[] opciones) {
		boolean salir = false;
		switch (opciones[0]) {
			case 1 -> { // Opciones de Revisión de Libros.
				switch (opciones[1]) {
					case 1 -> libroServicio.listarLibros();
					case 2 -> libroServicio.listarMasVendidos();
					case 3 -> libroServicio.buscarLibroPorId();
					case 4 -> libroServicio.listarPorNombre();
					case 5 -> logger.info(NL);
				}
			}
			case 2 -> { // Opciones de Revisión y Gestión de Autores.
				switch (opciones[1]) {
					case 1 -> autorServicio.listarAutores();
					case 2 -> autorServicio.buscarAutorPorId();
					case 3 -> autorServicio.agregarAutor();
					case 4 -> autorServicio.modificarAutor();
					case 5 -> autorServicio.eliminarAutor();
					case 6 -> logger.info(NL);
				}
			}
			case 3 -> { // Opciones de Gestión de Libros.
				switch (opciones[1]) {
					case 1 -> libroServicio.agregarLibro();
					case 2 -> libroServicio.modificarLibro();
					case 3 -> libroServicio.comprarLibro();
					case 4 -> libroServicio.recargarStock();
					case 5 -> libroServicio.eliminarLibro();
					case 6 -> logger.info(NL);
				}
			}
			case 4 -> salir = true;
		}
		return salir;
	}

	private void loguearOpcionInvalida(int maxOpciones) {
		logger.info("Opción inválida, debe ingresar un número entre 1 y " + maxOpciones + "." + NL);
	}
}