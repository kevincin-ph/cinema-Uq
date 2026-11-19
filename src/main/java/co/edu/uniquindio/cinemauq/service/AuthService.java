package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.data.BaseDatos;
import co.edu.uniquindio.cinemauq.exceptions.CredencialesInvalidasException;
import co.edu.uniquindio.cinemauq.exceptions.DatoInvalidoException;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.model.Usuario;
import co.edu.uniquindio.cinemauq.util.GeneradorCodigo;

/** SRP: solo se encarga del inicio de sesión y del registro de clientes. */
public class AuthService {

    private final BaseDatos db;

    public AuthService(BaseDatos db) {
        this.db = db;
    }

    public Usuario iniciarSesion(String email, String password) throws CredencialesInvalidasException {
        return db.getUsuarios().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email.trim()) && u.verificarPassword(password))
                .findFirst()
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos."));
    }

    public Cliente registrarCliente(String nombre, String email, String password, String documento, String telefono)
            throws DatoInvalidoException {
        if (vacio(nombre) || vacio(email) || vacio(password) || vacio(documento)) {
            throw new DatoInvalidoException("Nombre, correo, contraseña y documento son obligatorios.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new DatoInvalidoException("El correo no es válido.");
        }
        if (password.length() < 4) {
            throw new DatoInvalidoException("La contraseña debe tener mínimo 4 caracteres.");
        }
        boolean existe = db.getUsuarios().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email.trim()));
        if (existe) {
            throw new DatoInvalidoException("Ya existe un usuario con ese correo.");
        }
        TarjetaVirtual tarjeta = new TarjetaVirtual(generarNumeroTarjeta());
        Cliente cliente = new Cliente(GeneradorCodigo.siguiente("CLI"), nombre.trim(), email.trim(), password,
                documento.trim(), telefono == null ? "" : telefono.trim(), tarjeta);
        db.getUsuarios().add(cliente);
        return cliente;
    }

    private String generarNumeroTarjeta() {
        return "4580-" + (1000 + db.getUsuarios().size()) + "-" + (int) (1000 + Math.random() * 8999);
    }

    private boolean vacio(String s) { return s == null || s.isBlank(); }
}
