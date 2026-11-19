package co.edu.uniquindio.cinemauq.view;

import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.function.Function;

/** Utilidades de la vista: tablas, alertas y estilos comunes. */
public final class UI {

    private UI() { }

    /** Acción de la vista que puede lanzar excepciones del dominio. */
    @FunctionalInterface
    public interface Accion {
        void ejecutar() throws Exception;
    }

    /** Ejecuta una acción mostrando las excepciones del dominio como mensajes al usuario. */
    public static void ejecutar(Accion accion) {
        try {
            accion.ejecutar();
        } catch (CinemaException e) {
            alerta(Alert.AlertType.WARNING, "No fue posible realizar la operación", e.getMessage());
        } catch (Exception e) {
            alerta(Alert.AlertType.ERROR, "Error inesperado", e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public static <T> TableView<T> tabla() {
        TableView<T> t = new TableView<>();
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        t.setPlaceholder(new Label("Sin registros"));
        VBox.setVgrow(t, Priority.ALWAYS);
        return t;
    }

    public static <T> TableColumn<T, String> col(String titulo, Function<T, String> valor) {
        TableColumn<T, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(cd -> new SimpleStringProperty(valor.apply(cd.getValue())));
        return c;
    }

    @SafeVarargs
    public static <T> void columnas(TableView<T> tabla, TableColumn<T, String>... cols) {
        tabla.getColumns().addAll(cols);
    }

    public static void info(String mensaje) {
        alerta(Alert.AlertType.INFORMATION, "CinemaUQ", mensaje);
    }

    public static void error(String mensaje) {
        alerta(Alert.AlertType.WARNING, "Atención", mensaje);
    }

    public static boolean confirmar(String mensaje) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        a.setTitle("Confirmar");
        Optional<ButtonType> r = a.showAndWait();
        return r.isPresent() && r.get() == ButtonType.YES;
    }

    /** Muestra un texto largo (detalle de compra) en un área desplazable. */
    public static void texto(String titulo, String contenido) {
        TextArea area = new TextArea(contenido);
        area.setEditable(false);
        area.setPrefSize(560, 360);
        area.setStyle("-fx-font-family: 'monospaced';");
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.getDialogPane().setContent(area);
        a.setResizable(true);
        a.showAndWait();
    }

    private static void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo, mensaje, ButtonType.OK);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    public static Label titulo(String texto) {
        Label l = new Label(texto);
        l.getStyleClass().add("section-title");
        return l;
    }

    public static VBox caja(Node... nodos) {
        VBox v = new VBox(10, nodos);
        v.setPadding(new Insets(12));
        v.getStyleClass().add("card");
        return v;
    }

    public static HBox fila(Node... nodos) {
        HBox h = new HBox(8, nodos);
        h.getStyleClass().add("form-row");
        h.setStyle("-fx-alignment: center-left;");
        return h;
    }

    public static GridPane formulario() {
        GridPane g = new GridPane();
        g.setHgap(8);
        g.setVgap(8);
        g.setPadding(new Insets(10));
        g.getStyleClass().add("form-card");
        return g;
    }

    public static Region espacio() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    public static String estiloBoton() {
        return "-fx-background-color: #0071e3; -fx-text-fill: white; -fx-font-weight: bold;";
    }
}
