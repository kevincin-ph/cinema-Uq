package co.edu.uniquindio.cinemauq.model;

public class Administrador extends Usuario {

    public Administrador(String id, String nombre, String email, String password) {
        super(id, nombre, email, password);
    }

    @Override
    public String getRol() { return "ADMINISTRADOR"; }
}
