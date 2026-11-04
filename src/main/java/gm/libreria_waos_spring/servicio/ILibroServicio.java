package gm.libreria_waos_spring.servicio;

public interface ILibroServicio {
    void listarLibros();
    void listarMasVendidos();
    void listarPorNombre();
    void buscarLibroPorId();
    void comprarLibro();
    void recargarStock();
    void agregarLibro();
    void modificarLibro();
    void eliminarLibro();
}
