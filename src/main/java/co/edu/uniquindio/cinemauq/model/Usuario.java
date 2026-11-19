package co.edu.uniquindio.cinemauq.model;

/**
 * Clase abstracta para los usuarios del sistema.
 * LSP: Cliente y Administrador pueden usarse donde se espera un Usuario (por ejemplo en el login).
 */
public abstract class Usuario {

    private final String id;
    private String nombre;
    private String email;
    private String password;

    protected Usuario(String id, String nombre, String email, String password) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    public boolean verificarPassword(String password) {
        return this.password.equals(password);
    }

    /** Cada tipo de usuario indica su rol. */
    public abstract String getRol();

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() { return nombre + " (" + email + ")"; }
}
