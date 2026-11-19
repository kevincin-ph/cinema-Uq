package co.edu.uniquindio.cinemauq.view;

import co.edu.uniquindio.cinemauq.controller.ClienteController;
import co.edu.uniquindio.cinemauq.exceptions.CinemaException;
import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.model.entradas.EntradaPreferencial;
import co.edu.uniquindio.cinemauq.model.enums.MetodoPago;
import co.edu.uniquindio.cinemauq.model.enums.TipoAsiento;
import co.edu.uniquindio.cinemauq.util.Formato;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/** Ventana de compra: selección gráfica de asientos, confitería, promoción y pago. */
public class CompraView {

    private static final String VERDE = "#dbeafe";
    private static final String DORADO = "#fef3c7";
    private static final String GRIS = "#c7c7cc";
    private static final String AZUL = "#0071e3";

    private final ClienteController controller;
    private final Funcion funcion;
    private final Runnable alTerminar;
    private final Set<Asiento> seleccionados = new LinkedHashSet<>();
    private final Map<ItemConfiteria, Spinner<Integer>> cantidades = new LinkedHashMap<>();
    private final ComboBox<Promocion> cbPromo = new ComboBox<>();
    private final ComboBox<MetodoPago> cbMetodo = new ComboBox<>();
    private final TextArea resumen = new TextArea();
    private Stage stage;

    public CompraView(ClienteController controller, Funcion funcion, Runnable alTerminar) {
        this.controller = controller;
        this.funcion = funcion;
        this.alTerminar = alTerminar;
    }

    public void mostrar() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Comprar entradas - " + funcion.getPelicula().getTitulo());

        Label info = UI.titulo(funcion.getPelicula().getTitulo() + "  |  " + Formato.fechaHora(funcion.getFechaHora())
                + "  |  " + funcion.getSala().getNombre() + "  |  General " + Formato.moneda(funcion.getPrecioBase())
                + "  ·  Preferencial " + Formato.moneda(funcion.getPrecioBase() * (1 + EntradaPreferencial.RECARGO)));

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setTop(info);
        HBox centro = new HBox(14, mapaAsientos(), panelConfiteria());
        centro.setPadding(new Insets(10, 0, 0, 0));
        root.setCenter(centro);
        root.setRight(panelResumen());

        Scene scene = new Scene(root, 1260, 640);
        scene.getStylesheets().add(CompraView.class.getResource(
                "/co/edu/uniquindio/cinemauq/view/cinema.css").toExternalForm());
        stage.setScene(scene);
        actualizarResumen();
        stage.showAndWait();
    }

    private Node mapaAsientos() {
        Label pantalla = new Label("PANTALLA");
        pantalla.setMaxWidth(Double.MAX_VALUE);
        pantalla.setAlignment(Pos.CENTER);
        pantalla.getStyleClass().add("seat-screen");

        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        for (Asiento a : funcion.getSala().getAsientos()) {
            ToggleButton b = new ToggleButton(a.getCodigo());
            b.setPrefSize(44, 30);
            String colorLibre = a.getTipo() == TipoAsiento.PREFERENCIAL ? DORADO : VERDE;
            if (!funcion.isAsientoDisponible(a)) {
                b.setDisable(true);
                colorear(b, GRIS);
            } else {
                colorear(b, colorLibre);
                b.setOnAction(e -> {
                    if (b.isSelected()) {
                        seleccionados.add(a);
                        colorear(b, AZUL);
                    } else {
                        seleccionados.remove(a);
                        colorear(b, colorLibre);
                    }
                    actualizarResumen();
                });
            }
            grid.add(b, a.getNumero(), a.getFila() - 'A');
        }
        for (int i = 0; i < funcion.getSala().getFilas(); i++) {
            grid.add(new Label(String.valueOf((char) ('A' + i))), 0, i);
        }
        HBox leyenda = new HBox(10, muestra(VERDE, "General"), muestra(DORADO, "Preferencial"),
                muestra(AZUL, "Seleccionado"), muestra(GRIS, "Ocupado"));
        VBox v = new VBox(8, UI.titulo("1. Elija sus asientos"), pantalla, grid, leyenda,
                new Label("Disponibles: " + (funcion.getSala().getCapacidad() - funcion.getCantidadOcupados())
                        + " de " + funcion.getSala().getCapacidad()));
        ScrollPane sp = new ScrollPane(v);
        sp.setFitToWidth(true);
        sp.setPrefWidth(560);
        return sp;
    }

    private Node muestra(String color, String texto) {
        Label cuadro = new Label("   ");
        cuadro.setStyle("-fx-background-color: " + color + ";");
        return new HBox(4, cuadro, new Label(texto));
    }

    private void colorear(ButtonBase b, String color) {
        String textColor = AZUL.equals(color) ? "white" : "#1d1d1f";
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: " + textColor
                + "; -fx-font-size: 10px; -fx-background-radius: 8; -fx-border-radius: 8; -fx-opacity: 1;");
    }

    private Node panelConfiteria() {
        VBox lista = new VBox(6);
        for (ItemConfiteria item : controller.getConfiteria()) {
            Spinner<Integer> sp = new Spinner<>(0, 10, 0);
            sp.setPrefWidth(70);
            sp.valueProperty().addListener((obs, viejo, nuevo) -> actualizarResumen());
            cantidades.put(item, sp);
            Label l = new Label(item.getDescripcion() + "\n" + Formato.moneda(item.getPrecio()));
            l.setWrapText(true);
            l.setPrefWidth(200);
            lista.getChildren().add(new HBox(8, sp, l));
        }
        ScrollPane sp = new ScrollPane(lista);
        sp.setPrefWidth(300);
        VBox v = new VBox(8, UI.titulo("2. Confitería (opcional)"), sp);
        VBox.setVgrow(sp, Priority.ALWAYS);
        return v;
    }

    private Node panelResumen() {
        cbPromo.setItems(FXCollections.observableArrayList(controller.getPromociones()));
        cbPromo.setPromptText("Sin promoción");
        cbPromo.setOnAction(e -> actualizarResumen());
        Button quitarPromo = new Button("Quitar");
        quitarPromo.setOnAction(e -> {
            cbPromo.getSelectionModel().clearSelection();
            actualizarResumen();
        });

        cbMetodo.setItems(FXCollections.observableArrayList(MetodoPago.values()));
        cbMetodo.setValue(MetodoPago.TARJETA_VIRTUAL);
        cbMetodo.setOnAction(e -> actualizarResumen());

        resumen.setEditable(false);
        resumen.setPrefWidth(340);
        resumen.getStyleClass().add("purchase-summary");
        VBox.setVgrow(resumen, Priority.ALWAYS);

        Button confirmar = new Button("Confirmar y pagar");
        confirmar.setStyle(UI.estiloBoton());
        confirmar.setMaxWidth(Double.MAX_VALUE);
        confirmar.setOnAction(e -> confirmar());
        Button cancelar = new Button("Cancelar");
        cancelar.setMaxWidth(Double.MAX_VALUE);
        cancelar.setOnAction(e -> stage.close());

        VBox v = new VBox(8, UI.titulo("3. Resumen y pago"), new Label("Promoción:"), new HBox(6, cbPromo, quitarPromo),
                new Label("Método de pago:"), cbMetodo, resumen, confirmar, cancelar);
        v.setPadding(new Insets(10, 0, 0, 10));
        return v;
    }

    private Compra armar() throws CinemaException {
        Map<ItemConfiteria, Integer> items = new LinkedHashMap<>();
        cantidades.forEach((item, sp) -> {
            if (sp.getValue() > 0) {
                items.put(item, sp.getValue());
            }
        });
        return controller.armarCompra(funcion, new ArrayList<>(seleccionados), items, cbPromo.getValue());
    }

    private void actualizarResumen() {
        try {
            Compra c = armar();
            String texto = c.getDetalle();
            if (cbMetodo.getValue() == MetodoPago.PUNTOS) {
                texto += "\n\nPuntos necesarios: " + controller.puntosNecesarios(c.getTotal())
                        + "\nSus puntos vigentes: " + controller.getPuntosVigentes();
            } else {
                texto += "\n\nSaldo de su tarjeta: " + Formato.moneda(controller.getCliente().getTarjeta().getSaldo());
            }
            resumen.setText(texto);
        } catch (CinemaException e) {
            resumen.setText(e.getMessage());
        }
    }

    private void confirmar() {
        UI.ejecutar(() -> {
            Compra c = armar();
            String forma = cbMetodo.getValue() == MetodoPago.PUNTOS
                    ? controller.puntosNecesarios(c.getTotal()) + " puntos" : Formato.moneda(c.getTotal());
            if (!UI.confirmar("¿Confirmar la compra por " + forma + " con " + cbMetodo.getValue() + "?")) {
                return;
            }
            controller.confirmarCompra(c, cbMetodo.getValue());
            UI.texto("Compra exitosa", c.getDetalle());
            stage.close();
            alTerminar.run();
        });
    }
}
