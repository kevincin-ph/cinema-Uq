package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.controller.AdminController;
import co.edu.uniquindio.cinemauq.controller.ClienteController;
import co.edu.uniquindio.cinemauq.controller.LoginController;
import co.edu.uniquindio.cinemauq.facade.CineFacade;
import co.edu.uniquindio.cinemauq.facade.CineFacadeFactory;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.view.AdminView;
import co.edu.uniquindio.cinemauq.view.ClienteView;
import co.edu.uniquindio.cinemauq.view.LoginView;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Punto de entrada de la aplicación JavaFX. Maneja el cambio entre pantallas. */
public class App extends Application {

    private static Stage stage;
    private static CineFacade facade;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        facade = CineFacadeFactory.crear(true);
        stage.setTitle("CinemaUQ - Sistema integral de gestión de cine");
        mostrarLogin();
        stage.show();
    }

    public static void mostrarLogin() {
        LoginView vista = new LoginView(new LoginController(facade));
        stage.setScene(escena(vista.getRoot(), 500, 520));
        stage.centerOnScreen();
    }

    public static void mostrarCliente(Cliente cliente) {
        ClienteView vista = new ClienteView(new ClienteController(facade, cliente));
        stage.setScene(escena(vista.getRoot(), 1150, 720));
        stage.centerOnScreen();
    }

    public static void mostrarAdmin(Administrador admin) {
        AdminView vista = new AdminView(new AdminController(facade, admin));
        stage.setScene(escena(vista.getRoot(), 1280, 780));
        stage.centerOnScreen();
    }

    private static Scene escena(Parent root, double ancho, double alto) {
        Scene scene = new Scene(root, ancho, alto);
        scene.getStylesheets().add(App.class.getResource(
                "/co/edu/uniquindio/cinemauq/view/cinema.css").toExternalForm());
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
