# LibreriaWaos Spring 📚

CRUD de una Librería desarrollado con **Java 21, Spring Boot 3, Spring Data JPA y MySQL**.
Proyecto enfocado en buenas prácticas: separación de capas (Modelo, Repositorio, Servicio), manejo de excepciones personalizadas, validaciones de negocio y una interfaz de consola limpia y centralizada.

![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green) ![Maven](https://img.shields.io/badge/Maven-3.9-blue) ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)

## ✨ Características
- **Arquitectura en Capas**: Separación clara entre Entidades, Repositorios (JPA) y Servicios.
- **Relaciones JPA**: Mapeo `@OneToMany` y `@ManyToOne` entre Autor y Libro.
- **Excepciones de Negocio**: Uso de excepciones personalizadas (`RecursoNoEncontradoException`, `ConflictoException`, etc.).
- **Aplicación de BigDecimal**: Aplicación de la clase BigDecimal para mejorar la precisión del manejo de datos de índole monetaria.
- **Validaciones**: No se puede agregar un libro si el Autor no existe o si los datos son inválidos.
- **Utilidad de Consola**: Clase `ConsolaUtil` centraliza la entrada de datos, validando tipos y permitiendo cancelar o mantener valores.
- **Sin Hardcode**: Las credenciales de la BD están en `application.properties`, el cual está excluido de Git mediante `.gitignore`.

## 🛠️ Stack Tecnológico
| Capa          | Tecnología                  |
|---------------|-----------------------------|
| Lenguaje      | Java 21                     |
| Framework     | Spring Boot 3               |
| Persistencia  | Spring Data JPA / Hibernate |
| Base de Datos | MySQL 8.0                   |
| Build         | Maven 3.9                   |
| Utilidades    | Lombok                      |

## 🗄️ Esquema de Base de Datos
```sql
CREATE DATABASE libreria_waos_v2;
USE libreria_waos_v2;

CREATE TABLE autores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    nacionalidad VARCHAR(255)
);

CREATE TABLE libros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    id_autor INT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    ventas INT DEFAULT 0,
    existencias INT NOT NULL,
    disponible BOOLEAN,
    FOREIGN KEY (id_autor) REFERENCES autores(id)
);
```

## 🚀 Cómo Ejecutar

### 1. Prerrequisitos
Tener instalado: JDK 21, Maven y MySQL Server.

### 2. Configuración
Renombra el archivo application_example.properties (ubicado en src/main/resources/) a application.properties.

Edita el archivo con tus credenciales de MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/libreria_waos_v2
spring.datasource.username=root
spring.datasource.password=tu_password
```

### 3. Ejecución
Puedes ejecutar el proyecto desde tu IDE (IntelliJ IDEA) ejecutando la clase LibreriaWaosSpringApplication, o desde la terminal con:

```bash
mvn spring-boot:run
```

📂 Estructura del Proyecto
```
LibreriaWaosSpring/
├── src/
│   ├── main/
│   │   ├── java/gm/libreria_waos_spring/
│   │   │   ├── exception/    # Excepciones personalizadas
│   │   │   ├── modelo/       # Entidades JPA (Autor, Libro)
│   │   │   ├── repositorio/  # Interfaces JpaRepository
│   │   │   ├── servicio/     # Lógica de negocio (AutorServicio, LibroServicio)
│   │   │   ├── util/         # ConsolaUtil.java
│   │   │   └── LibreriaWaosSpringApplication.java
│   │   └── resources/
│   │       ├── application_example.properties  # Plantilla de configuración
│   │       └── logback-spring.xml              # Configuración de logs
│   └── test/
├── pom.xml                   # Dependencias Maven: Spring Boot, JPA, MySQL
├── .gitignore
└── README.md
```

## 📈 Mejoras Futuras

Migrar la interfaz de consola a una API REST con Spring Web.

Implementar un frontend con Angular o React.

Implementar paginación en los listados.

# Hecho por Nacho Wic (@NachoWicDev) | Proyecto de práctica Spring Boot Jr

