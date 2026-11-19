package co.edu.uniquindio.cinemauq.view;

import co.edu.uniquindio.cinemauq.App;
import co.edu.uniquindio.cinemauq.controller.AdminController;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Reembolso;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.confiteria.ItemConfiteria;
import co.edu.uniquindio.cinemauq.model.enums.AplicaPromocion;
import co.edu.uniquindio.cinemauq.model.enums.CategoriaProducto;
import co.edu.uniquindio.cinemauq.model.enums.Genero;
import co.edu.uniquindio.cinemauq.model.enums.TipoReporte;
import co.edu.uniquindio.cinemauq.service.EstadisticasService;
import co.edu.uniquindio.cinemauq.util.Formato;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Vista principal del administrador. */
public class AdminView {

    private final AdminController controller;
    private final BorderPane root = new BorderPane();

    private final TableView<Pelicula> tablaPeliculas = UI.tabla();
    private final TableView<Sala> tablaSalas = UI.tabla();
    private final TableView<Funcion> tablaFunciones = UI.tabla();
    private final TableView<ItemConfiteria> tablaConfiteria = UI.tabla();
    private final TableView<Promocion> tablaPromociones = UI.tabla();
    private final TableView<Cliente> tablaClientes = UI.tabla();
    private final TableView<SolicitudRecarga> tablaSolicitudes = UI.tabla();
    private final TableView<Compra> tablaCompras = UI.tabla();
    private final ListView<String> listaBitacora = new ListView<>();
    private final ComboBox<Pelicula> cbPeliculaFuncion = new ComboBox<>();
    private final ComboBox<Sala> cbSalaFuncion = new ComboBox<>();
    private final ListView<ItemConfiteria> listaItemsCombo = new ListView<>();
    private final GridPane panelEstadisticas = new GridPane();
    private final GraficasView graficas;

    public AdminView(AdminController controller) {
        this.controller = controller;
        this.graficas = new GraficasView(controller.getEstadisticas());
        root.setTop(encabezado());
        TabPane tabs = new TabPane(
                new Tab("Películas", tabPeliculas()),
                new Tab("Salas", tabSalas()),
                new Tab("Funciones", tabFunciones()),
                new Tab("Confitería", tabConfiteria()),
                new Tab("Promociones", tabPromociones()),
                new Tab("Clientes y recargas", tabClientes()),
                new Tab("Compras", tabCompras()),
                new Tab("Estadísticas", tabEstadisticas()),
                new Tab("Reportes", tabReportes()),
                new Tab("Gráficas", graficas.getRoot()),
                new Tab("Bitácora", UI.caja(UI.titulo("Eventos del sistema (observador de funciones)"), listaBitacora)));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> refrescar());
        root.setCenter(tabs);
        refrescar();
    }

    private HBox encabezado() {
        Label titulo = new Label("CinemaUQ  ·  Panel de administración  ·  " + controller.getAdmin().getNombre());
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        Button salir = new Button("Cerrar sesión");
        salir.setOnAction(e -> App.mostrarLogin());
        HBox h = new HBox(20, titulo, UI.espacio(), salir);
        h.setPadding(new Insets(12));
        h.setStyle("-fx-background-color: " + UI.COLOR_PRIMARIO + "; -fx-alignment: center-left;");
        return h;
    }

    // ===================== Películas =====================
    private Parent tabPeliculas() {
        UI.columnas(tablaPeliculas,
                UI.col("Código", Pelicula::getCodigo),
                UI.col("Título", Pelicula::getTitulo),
                UI.col("Género", p -> p.getGenero().name()),
                UI.col("Duración", p -> p.getDuracionMinutos() + " min"),
                UI.col("Clasificación", Pelicula::getClasificacion),
                UI.col("En cartelera", p -> p.isEnCartelera() ? "Sí" : "No"));
        TextField titulo = new TextField();
        ComboBox<Genero> genero = new ComboBox<>(FXCollections.observableArrayList(Genero.values()));
        TextField duracion = new TextField();
        duracion.setPromptText("minutos");
        ComboBox<String> clasif = new ComboBox<>(FXCollections.observableArrayList("Todos", "+7", "+12", "+15", "+18"));
        clasif.setValue("Todos");
        TextField sinopsis = new TextField();
        Button agregar = new Button("Agregar película");
        agregar.setStyle(UI.estiloBoton());
        agregar.setOnAction(e -> UI.ejecutar(() -> {
            controller.agregarPelicula(titulo.getText(), genero.getValue(), duracion.getText(), clasif.getValue(), sinopsis.getText());
            titulo.clear();
            duracion.clear();
            sinopsis.clear();
            refrescar();
        }));
        GridPane f = UI.formulario();
        f.addRow(0, new Label("Título:"), titulo, new Label("Género:"), genero, new Label("Duración:"), duracion);
        f.addRow(1, new Label("Clasificación:"), clasif, new Label("Sinopsis:"), sinopsis, agregar);

        Button cartelera = new Button("Poner / quitar de cartelera");
        cartelera.setOnAction(e -> UI.ejecutar(() -> {
            controller.cambiarCartelera(tablaPeliculas.getSelectionModel().getSelectedItem());
            refrescar();
        }));
        Button eliminar = new Button("Eliminar");
        eliminar.setOnAction(e -> UI.ejecutar(() -> {
            controller.eliminarPelicula(tablaPeliculas.getSelectionModel().getSelectedItem());
            refrescar();
        }));
        return UI.caja(UI.titulo("Gestión de películas"), f, tablaPeliculas, UI.fila(cartelera, eliminar));
    }

    // ===================== Salas =====================
    private Parent tabSalas() {
        UI.columnas(tablaSalas,
                UI.col("Código", Sala::getCodigo),
                UI.col("Nombre", Sala::getNombre),
                UI.col("Filas", s -> String.valueOf(s.getFilas())),
                UI.col("Asientos por fila", s -> String.valueOf(s.getColumnas())),
                UI.col("Filas preferenciales", s -> String.valueOf(s.getFilasPreferenciales())),
                UI.col("Capacidad", s -> String.valueOf(s.getCapacidad())));
        TextField nombre = new TextField();
        TextField filas = new TextField();
        TextField columnas = new TextField();
        TextField pref = new TextField("1");
        Button agregar = new Button("Agregar sala");
        agregar.setStyle(UI.estiloBoton());
        agregar.setOnAction(e -> UI.ejecutar(() -> {
            controller.agregarSala(nombre.getText(), filas.getText(), columnas.getText(), pref.getText());
            nombre.clear();
            refrescar();
        }));
        GridPane f = UI.formulario();
        f.addRow(0, new Label("Nombre:"), nombre, new Label("Filas:"), filas, new Label("Asientos por fila:"), columnas,
                new Label("Filas preferenciales (las últimas):"), pref, agregar);
        Button eliminar = new Button("Eliminar sala");
        eliminar.setOnAction(e -> UI.ejecutar(() -> {
            controller.eliminarSala(tablaSalas.getSelectionModel().getSelectedItem());
            refrescar();
        }));
        return UI.caja(UI.titulo("Gestión de salas y asientos"),
                new Label("Los asientos se generan automáticamente (A1, A2, ...). Las sillas preferenciales cuestan 30 % más."),
                f, tablaSalas, eliminar);
    }

    // ===================== Funciones =====================
    private Parent tabFunciones() {
        UI.columnas(tablaFunciones,
                UI.col("Código", Funcion::getCodigo),
                UI.col("Película", f -> f.getPelicula().getTitulo()),
                UI.col("Sala", f -> f.getSala().getNombre()),
                UI.col("Fecha y hora", f -> Formato.fechaHora(f.getFechaHora())),
                UI.col("Precio base", f -> Formato.moneda(f.getPrecioBase())),
                UI.col("Estado", f -> f.getEstado().name()),
                UI.col("Ocupación", f -> f.getCantidadOcupados() + "/" + f.getSala().getCapacidad()
                        + " (" + Formato.porcentaje(f.getOcupacion()) + ")"));
        DatePicker fecha = new DatePicker(LocalDate.now().plusDays(1));
        TextField hora = new TextField("18:00");
        hora.setPrefWidth(70);
        TextField precio = new TextField("12000");
        Button programar = new Button("Programar función");
        programar.setStyle(UI.estiloBoton());
        programar.setOnAction(e -> UI.ejecutar(() -> {
            controller.programarFuncion(cbPeliculaFuncion.getValue(), cbSalaFuncion.getValue(), fecha.getValue(),
                    hora.getText(), precio.getText());
            UI.info("Función programada.");
            refrescar();
        }));
        Button duplicar = new Button("Duplicar seleccionada a la fecha/hora del formulario");
        duplicar.setOnAction(e -> UI.ejecutar(() -> {
            controller.duplicarFuncion(tablaFunciones.getSelectionModel().getSelectedItem(), fecha.getValue(), hora.getText());
            UI.info("Función duplicada (patrón Prototype).");
            refrescar();
        }));
        Button cancelar = new Button("Cancelar función (reembolso 100 %)");
        cancelar.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        cancelar.setOnAction(e -> UI.ejecutar(() -> {
            Funcion f = tablaFunciones.getSelectionModel().getSelectedItem();
            if (f != null && !UI.confirmar("¿Cancelar la función " + f + "?\nSe devolverá el 100 % a todos los compradores.")) {
                return;
            }
            List<Reembolso> r = controller.cancelarFuncion(f);
            UI.info("Función cancelada. Reembolsos realizados: " + r.size() + " por "
                    + Formato.moneda(r.stream().mapToDouble(Reembolso::getMonto).sum()));
            refrescar();
        }));
        GridPane form = UI.formulario();
        form.addRow(0, new Label("Película:"), cbPeliculaFuncion, new Label("Sala:"), cbSalaFuncion,
                new Label("Fecha:"), fecha, new Label("Hora (HH:mm):"), hora, new Label("Precio:"), precio, programar);
        return UI.caja(UI.titulo("Gestión de funciones"),
                new Label("Regla: no se permiten cruces de horario en la misma sala (se dejan 15 min de limpieza)."),
                form, tablaFunciones, UI.fila(duplicar, cancelar));
    }

    // ===================== Confitería =====================
    private Parent tabConfiteria() {
        UI.columnas(tablaConfiteria,
                UI.col("Código", ItemConfiteria::getCodigo),
                UI.col("Nombre", ItemConfiteria::getNombre),
                UI.col("Categoría", i -> i.getCategoria().name()),
                UI.col("Precio", i -> Formato.moneda(i.getPrecio())),
                UI.col("Contenido", ItemConfiteria::getDescripcion));
        TextField nombre = new TextField();
        TextField precio = new TextField();
        ComboBox<CategoriaProducto> cat = new ComboBox<>(FXCollections.observableArrayList(
                CategoriaProducto.BEBIDA, CategoriaProducto.COMIDA, CategoriaProducto.DULCE));
        Button agregar = new Button("Agregar producto");
        agregar.setStyle(UI.estiloBoton());
        agregar.setOnAction(e -> UI.ejecutar(() -> {
            controller.agregarProducto(nombre.getText(), precio.getText(), cat.getValue());
            nombre.clear();
            precio.clear();
            refrescar();
        }));
        GridPane fp = UI.formulario();
        fp.addRow(0, new Label("Producto:"), nombre, new Label("Precio:"), precio, new Label("Categoría:"), cat, agregar);

        listaItemsCombo.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaItemsCombo.setPrefHeight(130);
        TextField nombreCombo = new TextField();
        TextField descuento = new TextField("10");
        Button crearCombo = new Button("Crear combo con los seleccionados");
        crearCombo.setStyle(UI.estiloBoton());
        crearCombo.setOnAction(e -> UI.ejecutar(() -> {
            controller.crearCombo(nombreCombo.getText(), listaItemsCombo.getSelectionModel().getSelectedItems(), descuento.getText());
            nombreCombo.clear();
            refrescar();
        }));
        GridPane fc = UI.formulario();
        fc.addRow(0, new Label("Nombre del combo:"), nombreCombo, new Label("Descuento %:"), descuento, crearCombo);
        Label ayuda = new Label("Seleccione 2 o más productos (Ctrl + clic). Un combo puede contener otros combos (patrón Composite).");

        Button eliminar = new Button("Eliminar seleccionado");
        eliminar.setOnAction(e -> UI.ejecutar(() -> {
            controller.eliminarItem(tablaConfiteria.getSelectionModel().getSelectedItem());
            refrescar();
        }));
        return UI.caja(UI.titulo("Productos y combos"), fp, tablaConfiteria, eliminar,
                UI.titulo("Nuevo combo"), ayuda, listaItemsCombo, fc);
    }

    // ===================== Promociones =====================
    private Parent tabPromociones() {
        UI.columnas(tablaPromociones,
                UI.col("Código", Promocion::getCodigo),
                UI.col("Nombre", Promocion::getNombre),
                UI.col("Descuento", p -> (int) p.getPorcentaje() + " %"),
                UI.col("Aplica a", p -> p.getAplicaA().name()),
                UI.col("Activa", p -> p.isActiva() ? "Sí" : "No"));
        TextField nombre = new TextField();
        TextField pct = new TextField();
        ComboBox<AplicaPromocion> aplica = new ComboBox<>(FXCollections.observableArrayList(AplicaPromocion.values()));
        Button agregar = new Button("Agregar promoción");
        agregar.setStyle(UI.estiloBoton());
        agregar.setOnAction(e -> UI.ejecutar(() -> {
            controller.agregarPromocion(nombre.getText(), pct.getText(), aplica.getValue());
            nombre.clear();
            pct.clear();
            refrescar();
        }));
        GridPane f = UI.formulario();
        f.addRow(0, new Label("Nombre:"), nombre, new Label("Descuento %:"), pct, new Label("Aplica a:"), aplica, agregar);
        Button estado = new Button("Activar / desactivar");
        estado.setOnAction(e -> UI.ejecutar(() -> {
            controller.cambiarEstadoPromocion(tablaPromociones.getSelectionModel().getSelectedItem());
            refrescar();
        }));
        return UI.caja(UI.titulo("Promociones"),
                new Label("La promoción se aplica con el patrón Decorator sobre las entradas y/o la confitería de la compra."),
                f, tablaPromociones, estado);
    }

    // ===================== Clientes y recargas =====================
    private Parent tabClientes() {
        UI.columnas(tablaClientes,
                UI.col("Nombre", Cliente::getNombre),
                UI.col("Correo", Cliente::getEmail),
                UI.col("Documento", Cliente::getDocumento),
                UI.col("Tarjeta", c -> c.getTarjeta().getNumero()),
                UI.col("Estado tarjeta", c -> c.getTarjeta().isActiva() ? "ACTIVA" : "INACTIVA"),
                UI.col("Saldo", c -> Formato.moneda(c.getTarjeta().getSaldo())),
                UI.col("Puntos vigentes", c -> String.valueOf(c.getCuentaPuntos().getPuntosVigentes(LocalDate.now()))),
                UI.col("Compras", c -> String.valueOf(c.getCompras().size())));
        TextField monto = new TextField();
        monto.setPromptText("Monto");
        Button recargar = new Button("Recargar tarjeta");
        recargar.setStyle(UI.estiloBoton());
        recargar.setOnAction(e -> UI.ejecutar(() -> {
            controller.recargar(tablaClientes.getSelectionModel().getSelectedItem(), monto.getText());
            UI.info("Recarga realizada y registrada en el historial de movimientos.");
            monto.clear();
            refrescar();
        }));
        Button bloquear = new Button("Activar / bloquear tarjeta");
        bloquear.setOnAction(e -> UI.ejecutar(() -> {
            controller.cambiarEstadoTarjeta(tablaClientes.getSelectionModel().getSelectedItem());
            refrescar();
        }));

        UI.columnas(tablaSolicitudes,
                UI.col("Código", SolicitudRecarga::getCodigo),
                UI.col("Cliente", s -> s.getCliente().getNombre()),
                UI.col("Monto", s -> Formato.moneda(s.getMonto())),
                UI.col("Fecha", s -> Formato.fechaHora(s.getFecha())),
                UI.col("Estado", s -> s.getEstado().name()));
        tablaSolicitudes.setPrefHeight(170);
        Button aprobar = new Button("Aprobar solicitud");
        aprobar.setOnAction(e -> UI.ejecutar(() -> {
            controller.aprobar(tablaSolicitudes.getSelectionModel().getSelectedItem());
            refrescar();
        }));
        Button rechazar = new Button("Rechazar solicitud");
        rechazar.setOnAction(e -> UI.ejecutar(() -> {
            controller.rechazar(tablaSolicitudes.getSelectionModel().getSelectedItem());
            refrescar();
        }));
        return UI.caja(UI.titulo("Clientes"), tablaClientes, UI.fila(new Label("Monto:"), monto, recargar, bloquear),
                UI.titulo("Solicitudes de recarga"), tablaSolicitudes, UI.fila(aprobar, rechazar));
    }

    // ===================== Compras =====================
    private Parent tabCompras() {
        UI.columnas(tablaCompras,
                UI.col("Código", Compra::getCodigo),
                UI.col("Fecha", c -> Formato.fechaHora(c.getFecha())),
                UI.col("Cliente", c -> c.getCliente().getNombre()),
                UI.col("Película", c -> c.getFuncion().getPelicula().getTitulo()),
                UI.col("Función", c -> Formato.fechaHora(c.getFuncion().getFechaHora())),
                UI.col("Asientos", Compra::getAsientosTexto),
                UI.col("Total", c -> Formato.moneda(c.getTotal())),
                UI.col("Pago", c -> String.valueOf(c.getMetodoPago())),
                UI.col("Estado", c -> c.getEstado().getNombre()));
        Button detalle = new Button("Ver detalle");
        detalle.setOnAction(e -> {
            Compra c = tablaCompras.getSelectionModel().getSelectedItem();
            if (c != null) {
                UI.texto("Detalle de la compra", c.getDetalle());
            }
        });
        return UI.caja(UI.titulo("Compras de todos los clientes"), tablaCompras, detalle);
    }

    // ===================== Estadísticas =====================
    private Parent tabEstadisticas() {
        panelEstadisticas.setHgap(16);
        panelEstadisticas.setVgap(16);
        Button actualizar = new Button("Actualizar");
        actualizar.setOnAction(e -> refrescar());
        return UI.caja(UI.titulo("Estadísticas generales (todo el historial)"), panelEstadisticas, actualizar);
    }

    private void llenarEstadisticas() {
        EstadisticasService est = controller.getEstadisticas();
        panelEstadisticas.getChildren().clear();
        double ventas = est.totalVentas(null, null);
        double reembolsos = est.totalReembolsos(null, null);
        tarjeta(0, 0, "Ventas realizadas", Formato.moneda(ventas) + "\n" + est.comprasEnPeriodo(null, null).size() + " compras");
        tarjeta(1, 0, "Ventas netas", Formato.moneda(ventas - reembolsos));
        tarjeta(2, 0, "Reembolsos", est.cantidadReembolsos(null, null) + " reembolsos\n" + Formato.moneda(reembolsos));
        tarjeta(0, 1, "Ocupación promedio por función", Formato.porcentaje(est.ocupacionPromedio(null, null)));
        tarjeta(1, 1, "Ocupación por sala", est.ocupacionPorSala(null, null).entrySet().stream()
                .map(e -> e.getKey() + ": " + Formato.porcentaje(e.getValue())).collect(Collectors.joining("\n")));
        tarjeta(2, 1, "Recargas", Formato.moneda(est.movimientosRecarga(null, null).stream().mapToDouble(m -> m.getMonto()).sum()));
        tarjeta(0, 2, "Películas con más entradas", top(est.entradasPorPelicula(null, null)));
        tarjeta(1, 2, "Productos más vendidos", top(est.productosVendidos(null, null)));
        tarjeta(2, 2, "Clientes registrados", String.valueOf(est.clientes().size()));
    }

    private String top(Map<String, Integer> mapa) {
        return mapa.entrySet().stream().limit(3).map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining("\n"));
    }

    private void tarjeta(int col, int fila, String titulo, String valor) {
        Label t = new Label(titulo);
        t.setStyle("-fx-text-fill: #5c6bc0; -fx-font-weight: bold;");
        Label v = new Label(valor);
        v.setStyle("-fx-font-size: 15px;");
        VBox box = new VBox(6, t, v);
        box.setPadding(new Insets(12));
        box.setPrefSize(330, 110);
        box.setStyle("-fx-background-color: white; -fx-border-color: #c5cae9; -fx-border-radius: 6; -fx-background-radius: 6;");
        panelEstadisticas.add(box, col, fila);
    }

    // ===================== Reportes exportables =====================
    private Parent tabReportes() {
        ComboBox<TipoReporte> tipo = new ComboBox<>(FXCollections.observableArrayList(TipoReporte.values()));
        tipo.setValue(TipoReporte.VENTAS);
        DatePicker desde = new DatePicker(LocalDate.now().minusDays(30));
        DatePicker hasta = new DatePicker(LocalDate.now());
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setStyle("-fx-font-family: 'monospaced'; -fx-font-size: 12px;");
        VBox.setVgrow(area, Priority.ALWAYS);
        Button generar = new Button("Generar");
        generar.setStyle(UI.estiloBoton());
        generar.setOnAction(e -> UI.ejecutar(() ->
                area.setText(controller.generarReporte(tipo.getValue(), desde.getValue(), hasta.getValue()))));
        Button exportar = new Button("Exportar a TXT");
        exportar.setOnAction(e -> UI.ejecutar(() -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Guardar reporte");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo de texto", "*.txt"));
            fc.setInitialFileName("reporte_" + tipo.getValue().name().toLowerCase() + "_" + LocalDate.now() + ".txt");
            File destino = fc.showSaveDialog(area.getScene().getWindow());
            if (destino != null) {
                File f = controller.exportarReporte(area.getText(), destino);
                UI.info("Reporte guardado en:\n" + f.getAbsolutePath());
            }
        }));
        HBox filtros = UI.fila(new Label("Tipo:"), tipo, new Label("Desde:"), desde, new Label("Hasta:"), hasta, generar, exportar);
        return UI.caja(UI.titulo("Reportes exportables"), filtros, area);
    }

    // ===================== Refrescar =====================
    private void refrescar() {
        tablaPeliculas.setItems(FXCollections.observableArrayList(controller.getPeliculas()));
        tablaSalas.setItems(FXCollections.observableArrayList(controller.getSalas()));
        tablaFunciones.setItems(FXCollections.observableArrayList(controller.getFunciones()));
        tablaConfiteria.setItems(FXCollections.observableArrayList(controller.getConfiteria()));
        listaItemsCombo.setItems(FXCollections.observableArrayList(controller.getConfiteria()));
        tablaPromociones.setItems(FXCollections.observableArrayList(controller.getPromociones()));
        tablaClientes.setItems(FXCollections.observableArrayList(controller.getClientes()));
        tablaSolicitudes.setItems(FXCollections.observableArrayList(controller.getSolicitudes()));
        tablaCompras.setItems(FXCollections.observableArrayList(controller.getCompras()));
        listaBitacora.setItems(FXCollections.observableArrayList(controller.getBitacora()));
        cbPeliculaFuncion.setItems(FXCollections.observableArrayList(
                controller.getPeliculas().stream().filter(Pelicula::isEnCartelera).toList()));
        cbSalaFuncion.setItems(FXCollections.observableArrayList(controller.getSalas()));
        llenarEstadisticas();
        graficas.actualizar();
    }

    public Parent getRoot() { return root; }
}
