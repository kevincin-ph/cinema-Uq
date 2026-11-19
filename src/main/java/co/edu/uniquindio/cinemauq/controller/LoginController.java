package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.exceptions.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.facade.OperacionesAcceso;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Usuario;

/** Controlador de la pantalla de inicio de sesión (MVC). Depende solo de OperacionesAcceso (ISP). */
public class LoginController {

    private final OperacionesAcceso acceso;

    public LoginController(OperacionesAcceso acceso) {
        this.acceso = acceso;
    }

    public Usuario iniciarSesion(String email, String password) throws CinemaException {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new DatoInvalidoException("Ingrese el correo y la contraseña.");
        }
        return acceso.iniciarSesion(email, password);
    }

    public Cliente registrar(String nombre, String email, String password, String documento, String telefono)
            throws CinemaException {
        return acceso.registrarCliente(nombre, email, password, documento, telefono);
    }
}
