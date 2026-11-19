package co.edu.uniquindio.cinemauq.facade;

import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Usuario;

/** ISP: el login solo necesita estas dos operaciones. */
public interface OperacionesAcceso {
    Usuario iniciarSesion(String email, String password) throws CinemaException;
    Cliente registrarCliente(String nombre, String email, String password, String documento, String telefono) throws CinemaException;
}
