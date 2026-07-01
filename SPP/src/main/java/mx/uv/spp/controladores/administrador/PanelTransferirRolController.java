/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 30 de junio del 2026
 */
package mx.uv.spp.controladores.administrador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import mx.uv.spp.modelo.Profesor;

/**
 * Controlador de la ventana emergente para transferir el rol de
 * Coordinador a un Profesor activo (PanelTransferirRol.fxml).
 * Se abre como diálogo modal desde PanelSeccionCoordinadorController.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PanelTransferirRolController implements Initializable {

    @FXML private TableView<Profesor>           tblProfesores;
    @FXML private TableColumn<Profesor, String> colNumPersonal;
    @FXML private TableColumn<Profesor, String> colNombre;
    @FXML private TableColumn<Profesor, String> colCorreo;

    private static final String RUTA_FXML =
            "/mx/uv/spp/vistas/administrador/PanelTransferirRol.fxml";
    private static final String TITULO_VENTANA = "SPP — Transferir rol";
    private static final int    ANCHO_DIALOGO  = 760;
    private static final int    ALTO_DIALOGO   = 480;

    private final ObservableList<Profesor> profesores =
            FXCollections.observableArrayList();

    /**
     * Abre la ventana emergente como diálogo modal bloqueante.
     * Debe llamarse desde el hilo de la aplicación JavaFX.
     *
     * @param propietario Ventana padre sobre la que se bloquea el
     *                    foco mientras el diálogo está abierto; no nula.
     */
    public static void mostrar(Window propietario) {
        try {
            FXMLLoader cargador = new FXMLLoader(
                    PanelTransferirRolController.class
                            .getResource(RUTA_FXML));
            Parent raiz = cargador.load();
            Stage dialogo = new Stage();
            dialogo.initModality(Modality.WINDOW_MODAL);
            dialogo.initOwner(propietario);
            dialogo.setTitle(TITULO_VENTANA);
            dialogo.setResizable(false);
            dialogo.setScene(
                    new Scene(raiz, ANCHO_DIALOGO, ALTO_DIALOGO));
            dialogo.showAndWait();
        } catch (IOException excepcion) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "No se pudo abrir la ventana de transferencia:\n"
                    + excepcion.getMessage());
            alerta.showAndWait();
            System.err.println(
                    "IOException en PanelTransferirRolController"
                    + ".mostrar: " + excepcion.getMessage());
        }
    }

    /**
     * Inicializa la tabla enlazando cada columna con los
     * atributos del modelo {@link Profesor}.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        colNumPersonal.setCellValueFactory(
                new PropertyValueFactory<>("numPersonal"));
        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombreCompleto"));
        colCorreo.setCellValueFactory(
                new PropertyValueFactory<>("correo"));

        tblProfesores.setItems(profesores);
        cargarProfesoresActivos();
    }

    /* ── Manejadores de botones ──────────────────────────────── */

    /**
     * Transfiere el rol de Coordinador al Profesor seleccionado
     * en la tabla. Valida que haya una selección activa.
     */
    @FXML
    private void onBtnTransferirRol() {
        Profesor seleccionado = tblProfesores.getSelectionModel()
                .getSelectedItem();
        if (seleccionado == null) {
            Alert aviso = new Alert(Alert.AlertType.WARNING);
            aviso.setTitle("Sin selección");
            aviso.setHeaderText(null);
            aviso.setContentText(
                    "Seleccione un Profesor de la lista "
                    + "para transferirle el rol.");
            aviso.showAndWait();
            return;
        }
        // TODO: invocar CoordinadorServicio.transferirRol(seleccionado)
        //       y cerrar el diálogo si la operación es exitosa
        cerrarVentana();
    }

    /**
     * Cierra el diálogo sin realizar ningún cambio.
     */
    @FXML
    private void onBtnRegresar() {
        cerrarVentana();
    }

    /* ── Métodos auxiliares ──────────────────────────────────── */

    /**
     * Consulta los Profesores con estado activo y los carga en la
     * tabla. Excluye al Coordinador vigente.
     */
    private void cargarProfesoresActivos() {
        // TODO: invocar ProfesorServicio.listarActivos()
        //       filtrar al actual coordinador
        //       y agregar los resultados a 'profesores'
    }

    /**
     * Cierra la ventana emergente actual.
     */
    private void cerrarVentana() {
        Stage escenario = (Stage) tblProfesores.getScene().getWindow();
        escenario.close();
    }

}
