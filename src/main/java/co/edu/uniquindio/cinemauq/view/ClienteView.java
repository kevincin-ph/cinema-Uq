package co.edu.uniquindio.cinemauq.view;

import co.edu.uniquindio.cinemauq.App;
import co.edu.uniquindio.cinemauq.controller.ClienteController;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.LotePuntos;
import co.edu.uniquindio.cinemauq.model.Movimiento;
import co.edu.uniquindio.cinemauq.model.MovimientoPuntos;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Reembolso;
import co.edu.uniquindio.cinemauq.util.Formato;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Vista principal del cliente. */
public class ClienteView {

    private final ClienteController controller;
    private final Cliente cliente;
    private final BorderPane root = new BorderPane();

    private final Label lblSaldo = new Label();
    private final Label lblPuntos = new Label();
    private final ListView<Pelicula> listaPeliculas = new ListView<>();
    private final ListView<Funcion> listaFunciones = new ListView<>();
    private final Label lblDetalle = new Label("Seleccione una película");
    private final TableView<Compra> tablaCompras = UI.tabla();
    private final TableView<Movimiento> tablaMovimientos = UI.tabla();
    private final Label lblTarjeta = new Label();
    private final TableView<LotePuntos> tablaLotes = UI.tabla();
    private final TableView<MovimientoPuntos> tablaMovPuntos = UI.tabla();
    private final Label lblResumenPuntos = new Label();
    private final ListView<String> listaNotificaciones = new ListView<>();

    public ClienteView(ClienteController controller) {
        this.controller = controller;
        this.cliente = controller.getCliente();
        root.setTop(encabezado());
        TabPane tabs = new TabPane(
                new Tab("Cartelera", tabCartelera()),
                new Tab("Mis compras", tabCompras()),
                new Tab("Mi tarjeta", tabTarjeta()),
                new Tab("Mis puntos", tabPuntos()),
                new Tab("Notificaciones", UI.caja(UI.titulo("Notificaciones"), listaNotificaciones)));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> refrescar());
        root.setCenter(tabs);
        refrescar();
    }

    private HBox encabezado() {
        Label titulo = new Label("CinemaUQ  ·  Hola, " + cliente.getNombre());
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        lblSaldo.setStyle("-fx-text-fill: #ffca28; -fx-font-weight: bold;");
        lblPuntos.setStyle("-fx-text-fill: #80deea; -fx-font-weight: bold;");
        Button salir = new Button("Cerrar sesión");
        salir.setOnAction(e -> App.mostrarLogin());
        HBox h = new HBox(20, titulo, UI.espacio(), lblSaldo, lblPuntos, salir);
        h.setPadding(new Insets(12));
        h.setStyle("-fx-background-color: " + UI.COLOR_PRIMARIO + "; -fx-alignment: center-left;");
        return h;
    }

    private Parent tabCartelera() {
        listaPeliculas.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pelicula p, boolean vacio) {
                super.updateItem(p, vacio);
                setText(vacio || p == null ? null : p.getTitulo() + "\n" + p.getGenero() + " · "
                        + p.getDuracionMinutos() + " min · " + p.getClasificacion());
            }
        });
        listaPeliculas.getSelectionModel().selectedItemProperty().addListener((o, a, p) -> mostrarPelicula(p));
        listaPeliculas.setPrefWidth(300);

        listaFunciones.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Funcion f, boolean vacio) {
                super.updateItem(f, vacio);
                setText(vacio || f == null ? null : Formato.fechaHora(f.getFechaHora()) + "   " + f.getSala().getNombre()
                        + "   desde " + Formato.moneda(f.getPrecioBase()) + "   ("
                        + (f.getSala().getCapacidad() - f.getCantidadOcupados()) + " sillas libres)");
            }
        });
        lblDetalle.setWrapText(true);
        Button comprar = new Button("Comprar entradas para la función seleccionada");
        comprar.setStyle(UI.estiloBoton());
        comprar.setOnAction(e -> {
            Funcion f = listaFunciones.getSelectionModel().getSelectedItem();
            if (f == null) {
                UI.error("Seleccione una función.");
                return;
            }
            new CompraView(controller, f, this::refrescar).mostrar();
            mostrarPelicula(listaPeliculas.getSelectionModel().getSelectedItem());
        });
        VBox derecha = UI.caja(UI.titulo("Funciones disponibles"), lblDetalle, listaFunciones, comprar);
        VBox.setVgrow(listaFunciones, Priority.ALWAYS);
        HBox.setHgrow(derecha, Priority.ALWAYS);
        VBox izquierda = UI.caja(UI.titulo("Películas en cartelera"), listaPeliculas);
        VBox.setVgrow(listaPeliculas, Priority.ALWAYS);
        return new HBox(izquierda, derecha);
    }

    private void mostrarPelicula(Pelicula p) {
        if (p == null) {
            listaFunciones.getItems().clear();
            return;
        }
        lblDetalle.setText(p.getTitulo() + " (" + p.getClasificacion() + ")\n" + p.getSinopsis());
        listaFunciones.setItems(FXCollections.observableArrayList(controller.getFunciones(p)));
    }

    private Parent tabCompras() {
        UI.columnas(tablaCompras,
                UI.col("Código", Compra::getCodigo),
                UI.col("Película", c -> c.getFuncion().getPelicula().getTitulo()),
                UI.col("Función", c -> Formato.fechaHora(c.getFuncion().getFechaHora())),
                UI.col("Sala", c -> c.getFuncion().getSala().getNombre()),
                UI.col("Asientos", Compra::getAsientosTexto),
                UI.col("Total", c -> Formato.moneda(c.getTotal())),
                UI.col("Pago", c -> String.valueOf(c.getMetodoPago())),
                UI.col("Estado", c -> c.getEstado().getNombre()),
                UI.col("Reembolso", c -> c.getReembolso() == null ? "" : Formato.moneda(c.getReembolso().getMonto())));
        Button detalle = new Button("Ver detalle");
        detalle.setOnAction(e -> {
            Compra c = tablaCompras.getSelectionModel().getSelectedItem();
            if (c != null) {
                UI.texto("Detalle de la compra", c.getDetalle());
            }
        });
        Button cancelar = new Button("Solicitar cancelación");
        cancelar.setOnAction(e -> cancelarCompra());
        Label reglas = new Label("Cancelación: más de 24 h antes → 100 %  ·  entre 2 y 24 h → 80 %  ·  menos de 2 h → no se permite.");
        return UI.caja(UI.titulo("Historial de compras"), reglas, tablaCompras, UI.fila(detalle, cancelar));
    }

    private void cancelarCompra() {
        Compra c = tablaCompras.getSelectionModel().getSelectedItem();
        if (c == null) {
            UI.error("Seleccione una compra.");
            return;
        }
        UI.ejecutar(() -> {
            if (!UI.confirmar("Política aplicable: " + controller.politicaCancelacion(c) + "\n\n¿Desea cancelar la compra "
                    + c.getCodigo() + "?")) {
                return;
            }
            Reembolso r = controller.cancelar(c);
            UI.info("Compra cancelada. Se devolvieron " + Formato.moneda(r.getMonto()) + " (" + (int) r.getPorcentaje()
                    + " %) a su " + c.getMetodoPago() + ".");
            refrescar();
        });
    }

    private Parent tabTarjeta() {
        UI.columnas(tablaMovimientos,
                UI.col("Fecha", m -> Formato.fechaHora(m.getFecha())),
                UI.col("Tipo", m -> m.getTipo().name()),
                UI.col("Descripción", Movimiento::getDescripcion),
                UI.col("Monto", m -> Formato.moneda(m.getMonto())),
                UI.col("Saldo", m -> Formato.moneda(m.getSaldoResultante())));
        TextField monto = new TextField();
        monto.setPromptText("Monto, ej: 50000");
        Button solicitar = new Button("Solicitar recarga");
        solicitar.setStyle(UI.estiloBoton());
        solicitar.setOnAction(e -> UI.ejecutar(() -> {
            controller.solicitarRecarga(monto.getText());
            UI.info("Solicitud enviada. El administrador debe aprobar la recarga.");
            monto.clear();
        }));
        lblTarjeta.setStyle("-fx-font-size: 14px;");
        return UI.caja(UI.titulo("Mi tarjeta virtual"), lblTarjeta,
                UI.fila(new Label("Solicitar recarga:"), monto, solicitar), tablaMovimientos);
    }

    private Parent tabPuntos() {
        UI.columnas(tablaLotes,
                UI.col("Puntos obtenidos", l -> String.valueOf(l.getPuntosObtenidos())),
                UI.col("Disponibles", l -> String.valueOf(l.getPuntosDisponibles())),
                UI.col("Fecha de obtención", l -> Formato.fecha(l.getFechaObtencion())),
                UI.col("Fecha de vencimiento", l -> Formato.fecha(l.getFechaVencimiento())),
                UI.col("Estado", l -> l.isVigente(LocalDate.now()) ? "Vigente" : "Vencido"));
        UI.columnas(tablaMovPuntos,
                UI.col("Fecha", m -> Formato.fechaHora(m.getFecha())),
                UI.col("Tipo", m -> m.getTipo().name()),
                UI.col("Puntos", m -> String.valueOf(m.getPuntos())),
                UI.col("Descripción", MovimientoPuntos::getDescripcion));
        Label regla = new Label(controller.getReglaPuntos()
                + "\nPara redimir: en la compra elija el método de pago 'Puntos CinemaUQ'.");
        regla.setWrapText(true);
        lblResumenPuntos.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        return UI.caja(UI.titulo("Mis puntos CinemaUQ"), lblResumenPuntos, regla, new Label("Lotes de puntos:"), tablaLotes,
                new Label("Movimientos:"), tablaMovPuntos);
    }

    private void refrescar() {
        lblSaldo.setText("Saldo: " + Formato.moneda(cliente.getTarjeta().getSaldo()));
        lblPuntos.setText("Puntos: " + controller.getPuntosVigentes());

        Pelicula sel = listaPeliculas.getSelectionModel().getSelectedItem();
        listaPeliculas.setItems(FXCollections.observableArrayList(controller.getCartelera()));
        if (sel != null) {
            listaPeliculas.getSelectionModel().select(sel);
        }

        tablaCompras.setItems(FXCollections.observableArrayList(controller.getCompras()));

        lblTarjeta.setText("Número: " + cliente.getTarjeta().getNumero() + "     Estado: "
                + (cliente.getTarjeta().isActiva() ? "ACTIVA" : "INACTIVA") + "     Saldo: "
                + Formato.moneda(cliente.getTarjeta().getSaldo()));
        List<Movimiento> movs = new ArrayList<>(cliente.getTarjeta().getMovimientos());
        Collections.reverse(movs);
        tablaMovimientos.setItems(FXCollections.observableArrayList(movs));

        lblResumenPuntos.setText("Puntos vigentes: " + controller.getPuntosVigentes() + "     Puntos vencidos: "
                + cliente.getCuentaPuntos().getPuntosVencidos(LocalDate.now()));
        tablaLotes.setItems(FXCollections.observableArrayList(cliente.getCuentaPuntos().getLotes()));
        List<MovimientoPuntos> mp = new ArrayList<>(cliente.getCuentaPuntos().getMovimientos());
        Collections.reverse(mp);
        tablaMovPuntos.setItems(FXCollections.observableArrayList(mp));

        listaNotificaciones.setItems(FXCollections.observableArrayList(cliente.getNotificaciones()));
    }

    public Parent getRoot() { return root; }
}
