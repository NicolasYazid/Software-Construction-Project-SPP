/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de junio del 2026
 */
package mx.uv.spp.controladores.estudiante;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.spp.modelo.Proyecto;
import mx.uv.spp.modelo.SeleccionProyecto;
import mx.uv.spp.negocio.EstudianteServicio;
import mx.uv.spp.persistencia.dao.impl.AutoevaluacionDAOImpl;
import mx.uv.spp.persistencia.dao.impl.DocumentoDAOImpl;
import mx.uv.spp.persistencia.dao.impl.PeriodoInscripcionesDAOImpl;
import mx.uv.spp.persistencia.dao.impl.SeleccionProyectoDAOImpl;
import mx.uv.spp.util.SesionUsuario;

/**
 * Controlador de la vista Elegir Proyecto (CU-21).
 * Muestra la lista de proyectos disponibles, permite al Estudiante
 * ordenarlos por prioridad y confirma la selección irreversible
 * (RN-11). Si la selección ya fue registrada, muestra la vista
 * en modo solo lectura.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class ElegirProyectoController implements Initializable {

    @FXML private Label                     lblEstado;
    @FXML private TableView<Proyecto>       tblProyectos;
    @FXML private TableColumn<Proyecto, Integer> colPrioridad;
    @FXML private TableColumn<Proyecto, String>  colNombre;
    @FXML private TableColumn<Proyecto, String>  colDescripcion;
    @FXML private TableColumn<Proyecto, Integer> colCupo;
    @FXML private Button                    btnSubir;
    @FXML private Button                    btnBajar;
    @FXML private Button                    btnConfirmar;

    private final ObservableList<Proyecto> proyectos =
            FXCollections.observableArrayList();
    private EstudianteServicio estudianteServicio;

    private static final String ESTILO_ERROR =
            "-fx-text-fill: #c0392b;";
    private static final String ESTILO_INFO  =
            "-fx-text-fill: #1C3A6E;";
    private static final String ESTILO_OK    =
            "-fx-text-fill: #27ae60;";

    /**
     * Inicializa el controlador: configura las columnas de la tabla,
     * crea el servicio y carga los proyectos disponibles.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        estudianteServicio = new EstudianteServicio(
                new PeriodoInscripcionesDAOImpl(),
                new SeleccionProyectoDAOImpl(),
                new DocumentoDAOImpl(),
                new AutoevaluacionDAOImpl());

        configurarColumnas();
        tblProyectos.setItems(proyectos);
        cargarProyectos();
    }

    /* ── Manejadores de eventos ─────────────────────────────── */

    /**
     * Mueve el proyecto seleccionado una posición hacia arriba en la
     * lista (mayor prioridad). No hace nada si es el primero.
     */
    @FXML
    private void onBtnSubir() {
        int indice = tblProyectos.getSelectionModel()
                .getSelectedIndex();
        if (indice > 0) {
            Proyecto temp = proyectos.remove(indice);
            proyectos.add(indice - 1, temp);
            tblProyectos.getSelectionModel().select(indice - 1);
        }
    }

    /**
     * Mueve el proyecto seleccionado una posición hacia abajo en la
     * lista (menor prioridad). No hace nada si es el último.
     */
    @FXML
    private void onBtnBajar() {
        int indice = tblProyectos.getSelectionModel()
                .getSelectedIndex();
        if (indice >= 0 && indice < proyectos.size() - 1) {
            Proyecto temp = proyectos.remove(indice);
            proyectos.add(indice + 1, temp);
            tblProyectos.getSelectionModel().select(indice + 1);
        }
    }

    /**
     * Solicita confirmación y registra la lista de prioridades.
     * Muestra una alerta de advertencia antes de proceder, ya que
     * la selección es irreversible (RN-11).
     */
    @FXML
    private void onBtnConfirmar() {
        if (proyectos.isEmpty()) {
            mostrarMensaje(ESTILO_ERROR,
                    "No hay proyectos para confirmar.");
            return;
        }
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar selección");
        alerta.setHeaderText("Esta acción es irreversible (RN-11).");
        alerta.setContentText(
                "Una vez confirmada tu lista de prioridades no "
                + "podrás modificarla. ¿Deseas continuar?");
        Optional<ButtonType> respuesta = alerta.showAndWait();
        if (respuesta.isPresent()
                && respuesta.get() == ButtonType.OK) {
            registrarSeleccion();
        }
    }

    /* ── Métodos privados ───────────────────────────────────── */

    /**
     * Configura las columnas de la tabla vinculándolas a los
     * atributos del modelo {@link Proyecto}.
     */
    private void configurarColumnas() {
        colPrioridad.setCellValueFactory(
                celda -> new javafx.beans.property
                        .SimpleIntegerProperty(
                        proyectos.indexOf(celda.getValue()) + 1)
                        .asObject());
        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombreProyecto"));
        colDescripcion.setCellValueFactory(
                new PropertyValueFactory<>("descripcion"));
        colCupo.setCellValueFactory(
                new PropertyValueFactory<>("cupoDisponible"));
    }

    /**
     * Carga los proyectos disponibles desde el servicio de negocio.
     * Si el Estudiante ya registró su lista, deshabilita los controles
     * y muestra un mensaje informativo.
     */
    private void cargarProyectos() {
        int idInscripcion = SesionUsuario.getIdInscripcion();
        if (idInscripcion == 0) {
            mostrarMensaje(ESTILO_ERROR,
                    "No tienes una inscripción activa en este ciclo.");
            deshabilitarControles();
            return;
        }
        try {
            List<Proyecto> disponibles =
                    estudianteServicio.obtenerProyectosParaOrdenar(
                            idInscripcion);
            proyectos.setAll(disponibles);
            mostrarMensaje(ESTILO_INFO,
                    "Ordena los proyectos arrastrando con ↑/↓. "
                    + "El primero es tu primera opción.");
        } catch (IllegalStateException e) {
            mostrarMensaje(ESTILO_OK, e.getMessage());
            deshabilitarControles();
        } catch (SQLException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "Error al obtener los proyectos: "
                    + e.getMessage());
            deshabilitarControles();
        }
    }

    /**
     * Construye la lista de {@link SeleccionProyecto} según el orden
     * actual de la tabla y llama al servicio para persistirla.
     */
    private void registrarSeleccion() {
        int idInscripcion = SesionUsuario.getIdInscripcion();
        List<SeleccionProyecto> selecciones = new ArrayList<>();
        for (int i = 0; i < proyectos.size(); i++) {
            SeleccionProyecto sel = new SeleccionProyecto();
            sel.setIdProyecto(proyectos.get(i).getIdProyecto());
            sel.setPrioridad(i + 1);
            selecciones.add(sel);
        }
        try {
            estudianteServicio.registrarPrioridades(
                    idInscripcion, selecciones);
            mostrarMensaje(ESTILO_OK,
                    "Lista de prioridades registrada correctamente.");
            deshabilitarControles();
        } catch (IllegalArgumentException
                | IllegalStateException e) {
            mostrarMensaje(ESTILO_ERROR, e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "Error al guardar las prioridades: "
                    + e.getMessage());
        }
    }

    /**
     * Deshabilita los botones de reordenamiento y confirmación.
     * Se usa cuando la selección ya fue registrada o no hay
     * inscripción activa.
     */
    private void deshabilitarControles() {
        btnSubir.setDisable(true);
        btnBajar.setDisable(true);
        btnConfirmar.setDisable(true);
    }

    /**
     * Actualiza el texto y el estilo del label de estado.
     *
     * @param estilo  Estilo CSS en línea a aplicar.
     * @param mensaje Texto a mostrar al usuario.
     */
    private void mostrarMensaje(String estilo, String mensaje) {
        lblEstado.setStyle(estilo);
        lblEstado.setText(mensaje);
    }

}
