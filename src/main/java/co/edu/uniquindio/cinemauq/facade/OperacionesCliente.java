package co.edu.uniquindio.cinemauq.facade;

import co.edu.uniquindio.cinemauq.builder.CompraBuilder;
import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Reembolso;
import co.edu.uniquindio.cinemauq.model.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.model.enums.MetodoPago;
import co.edu.uniquindio.cinemauq.reembolso.PoliticaReembolso;

import java.util.List;

/** ISP: operaciones que necesita el cliente (no ve las del administrador). */
public interface OperacionesCliente {
    List<Pelicula> getCartelera();
    List<Funcion> getFuncionesDisponibles(Pelicula pelicula);
    List<ItemConfiteria> getConfiteria();
    List<Promocion> getPromocionesActivas();
    CompraBuilder nuevaCompra(Cliente cliente, Funcion funcion);
    Compra confirmarCompra(Compra compra, MetodoPago metodo) throws CinemaException;
    int puntosNecesarios(double valor);
    PoliticaReembolso consultarPoliticaCancelacion(Compra compra);
    Reembolso cancelarCompra(Compra compra) throws CinemaException;
    void solicitarRecarga(Cliente cliente, double monto) throws CinemaException;
    int getPuntosVigentes(Cliente cliente);
    String getReglaPuntos();
}
