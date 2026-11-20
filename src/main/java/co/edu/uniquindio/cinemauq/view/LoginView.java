package co.edu.uniquindio.cinemauq.view;

import co.edu.uniquindio.cinemauq.App;
import co.edu.uniquindio.cinemauq.controller.LoginController;
import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/** Vista de inicio de sesión y registro de clientes. */
public class LoginView {

    private final LoginController controller;
    private final VBox root = new VBox(12);
    private final TextField txtEmail = new TextField();
    private final PasswordField txtPassword = new PasswordField();
    private final Button btnIngresar = new Button("Ingresar");
    private final Label lblEstado = new Label();

    public LoginView(LoginController controller) {
        this.controller = controller;
        construir();
    }

    private void construir() {
        Label logo = new Label("CinemaUQ");
        logo.getStyleClass().add("brand-title");
        Label sub = new Label("Cartelera, entradas, confitería y puntos");
        sub.getStyleClass().add("brand-subtitle");

        txtEmail.setPromptText("Correo electrónico");
        txtPassword.setPromptText("Contraseña");
        txtEmail.setMaxWidth(300);
        txtPassword.setMaxWidth(300);

        btnIngresar.getStyleClass().add("button-primary");
        btnIngresar.setPrefWidth(300);
        btnIngresar.setMaxWidth(Double.MAX_VALUE);
        btnIngresar.setOnAction(e -> ingresar());
        btnIngresar.setDefaultButton(true);
        txtPassword.setOnAction(e -> ingresar());

        lblEstado.getStyleClass().add("login-status");
        lblEstado.setWrapText(true);
        lblEstado.setVisible(false);
        lblEstado.setManaged(false);

        Hyperlink lnkRegistro = new Hyperlink("¿No tiene cuenta? Regístrese aquí");
        lnkRegistro.setOnAction(e -> mostrarRegistro());

        Label ayuda = new Label("Usuarios de prueba:\nadmin@cinemauq.com / admin123\nana@mail.com / 1234   ·   carlos@mail.com / 1234");
        ayuda.getStyleClass().add("login-help");

        VBox tarjeta = new VBox(14, logo, sub, txtEmail, txtPassword, lblEstado, btnIngresar, lnkRegistro, ayuda);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.getStyleClass().add("login-card");
        root.getChildren().add(tarjeta);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.getStyleClass().add("login-screen");
    }

    private void ingresar() {
        btnIngresar.setDisable(true);
        lblEstado.setText("Verificando acceso…");
        lblEstado.setVisible(true);
        lblEstado.setManaged(true);
        try {
            Usuario u = controller.iniciarSesion(txtEmail.getText(), txtPassword.getText());
            if (u instanceof Administrador a) {
                App.mostrarAdmin(a);
            } else if (u instanceof Cliente c) {
                App.mostrarCliente(c);
            } else {
                throw new IllegalStateException("El usuario no tiene un rol reconocido.");
            }
        } catch (CinemaException e) {
            mostrarError(e.getMessage());
        } catch (RuntimeException e) {
            mostrarError("No se pudo abrir el panel: " + e.getMessage());
            e.printStackTrace();
        } finally {
            btnIngresar.setDisable(false);
        }
    }

    private void mostrarError(String mensaje) {
        lblEstado.setText(mensaje);
        lblEstado.getStyleClass().add("login-status-error");
    }

    private void mostrarRegistro() {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Registro de cliente");
        dialogo.setHeaderText("Cree su cuenta. Se le asignará una tarjeta virtual.");
        TextField nombre = new TextField();
        TextField email = new TextField();
        PasswordField pass = new PasswordField();
        TextField documento = new TextField();
        TextField telefono = new TextField();
        GridPane g = UI.formulario();
        g.addRow(0, new Label("Nombre completo:"), nombre);
        g.addRow(1, new Label("Correo:"), email);
        g.addRow(2, new Label("Contraseña:"), pass);
        g.addRow(3, new Label("Documento:"), documento);
        g.addRow(4, new Label("Teléfono:"), telefono);
        dialogo.getDialogPane().setContent(g);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogo.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                UI.ejecutar(() -> {
                    Cliente c = controller.registrar(nombre.getText(), email.getText(), pass.getText(),
                            documento.getText(), telefono.getText());
                    UI.info("Cuenta creada para " + c.getNombre() + ".\nTarjeta virtual: " + c.getTarjeta().getNumero()
                            + "\nSaldo inicial: $0. Solicite una recarga para comprar.");
                    txtEmail.setText(c.getEmail());
                });
            }
        });
    }

    public Parent getRoot() { return root; }
}
