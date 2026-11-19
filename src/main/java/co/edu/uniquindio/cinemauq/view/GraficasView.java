package co.edu.uniquindio.cinemauq.view;

import co.edu.uniquindio.cinemauq.service.EstadisticasService;
import co.edu.uniquindio.cinemauq.util.Formato;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeSet;

/** Reportes visuales con los gráficos de JavaFX: barras, pastel y líneas. */
public class GraficasView {

    private final EstadisticasService est;
    private final DatePicker desde = new DatePicker(LocalDate.now().minusDays(30));
    private final DatePicker hasta = new DatePicker(LocalDate.now().plusDays(7));
    private final BarChart<String, Number> barrasEntradas = new BarChart<>(new CategoryAxis(), new NumberAxis());
    private final PieChart pastelCategorias = new PieChart();
    private final LineChart<String, Number> lineasVentas = new LineChart<>(new CategoryAxis(), new NumberAxis());
    private final BarChart<String, Number> barrasOcupacion = new BarChart<>(new CategoryAxis(), new NumberAxis());
    private final VBox root;

    public GraficasView(EstadisticasService est) {
        this.est = est;
        barrasEntradas.setTitle("Entradas vendidas por película");
        barrasEntradas.setLegendVisible(false);
        pastelCategorias.setTitle("Distribución de ventas por categoría");
        lineasVentas.setTitle("Ventas y recargas por día");
        barrasOcupacion.setTitle("Ocupación de salas (%)");
        barrasOcupacion.setLegendVisible(false);
        for (var c : new javafx.scene.chart.Chart[]{barrasEntradas, pastelCategorias, lineasVentas, barrasOcupacion}) {
            c.setAnimated(false);
        }
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(barrasEntradas, 0, 0);
        grid.add(pastelCategorias, 1, 0);
        grid.add(lineasVentas, 0, 1);
        grid.add(barrasOcupacion, 1, 1);
        for (var c : new javafx.scene.chart.Chart[]{barrasEntradas, pastelCategorias, lineasVentas, barrasOcupacion}) {
            c.setPrefSize(600, 300);
        }
        VBox.setVgrow(grid, Priority.ALWAYS);
        Button actualizar = new Button("Actualizar gráficas");
        actualizar.setStyle(UI.estiloBoton());
        actualizar.setOnAction(e -> actualizar());
        root = UI.caja(UI.titulo("Reportes visuales"),
                UI.fila(new Label("Desde:"), desde, new Label("Hasta:"), hasta, actualizar), grid);
    }

    public void actualizar() {
        LocalDate d = desde.getValue();
        LocalDate h = hasta.getValue();

        XYChart.Series<String, Number> entradas = new XYChart.Series<>();
        est.entradasPorPelicula(d, h).forEach((k, v) -> entradas.getData().add(new XYChart.Data<>(k, v)));
        barrasEntradas.getData().clear();
        barrasEntradas.getData().add(entradas);

        pastelCategorias.getData().clear();
        est.ventasPorCategoria(d, h).forEach((k, v) ->
                pastelCategorias.getData().add(new PieChart.Data(k + " " + Formato.moneda(v), v)));

        Map<LocalDate, Double> ventas = est.ventasPorDia(d, h);
        Map<LocalDate, Double> recargas = est.recargasPorDia(d, h);
        TreeSet<LocalDate> dias = new TreeSet<>(ventas.keySet());
        dias.addAll(recargas.keySet());
        XYChart.Series<String, Number> sVentas = new XYChart.Series<>();
        sVentas.setName("Ventas");
        XYChart.Series<String, Number> sRecargas = new XYChart.Series<>();
        sRecargas.setName("Recargas");
        for (LocalDate dia : dias) {
            String etiqueta = dia.getDayOfMonth() + "/" + dia.getMonthValue();
            sVentas.getData().add(new XYChart.Data<>(etiqueta, ventas.getOrDefault(dia, 0.0)));
            sRecargas.getData().add(new XYChart.Data<>(etiqueta, recargas.getOrDefault(dia, 0.0)));
        }
        lineasVentas.getData().clear();
        lineasVentas.getData().add(sVentas);
        lineasVentas.getData().add(sRecargas);

        XYChart.Series<String, Number> ocupacion = new XYChart.Series<>();
        est.ocupacionPorSala(d, h).forEach((k, v) -> ocupacion.getData().add(new XYChart.Data<>(k, v * 100)));
        barrasOcupacion.getData().clear();
        barrasOcupacion.getData().add(ocupacion);
    }

    public Parent getRoot() { return root; }
}
