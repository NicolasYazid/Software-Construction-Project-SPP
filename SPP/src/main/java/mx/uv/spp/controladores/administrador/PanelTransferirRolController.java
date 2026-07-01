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
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import mx.uv.spp.modelo.Profesor;
import mx.uv.spp.negocio.AdministradorServicio;
import mx.uv.spp.persistencia.dao.impl.ProfesorDAOImpl;

/**
 * Controlador de la ventana emergente para transferir el rol de
 * Coordinador a un Profesor activo (PanelTransferirRol.fxml).
 * Se abre como diálogo modal desde
 * {@link PanelSeccionCoordinadorController}, implementando el flujo
 * completo de CU-Admin.-03 "Cambiar Coordinador".
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PanelTransferirRolController implements Initializable {

    @FXML private TableView<Profesor>           tblProfesores;
    @FXML private TableColumn<Profesor, String> colNumPersonal;
    @FXML private TableColumn<Profesor, String> colNombre;
    @FXML private TableColumn<Profesor, String> colCorreo;
    @FXML private Button                        btnTransferirRol;
    @FXML private Label                         lblMensaje;

    private static final String RUTA_FXML =
            "/mx/uv/spp/vistas/administrador/PanelTransferirRol.fxml";
    private static final String TITULO_VENTANA = "SPP — Transferir rol";
    private static final int    ANCHO_DIALOGO  = 760;
    private static final int    ALTO_DIALOGO   = 480;

    private static final String MENSAJE_EX01 =
            "Error: no fue posible conectarse con la base de datos, "
            + "inténtelo de nuevo ahora o en unos minutos.";

    // Ancho mínimo de las ventanas emergentes para que mensajes
    // largos no se corten con puntos suspensivos.
    private static final double ANCHO_MINIMO_ALERTA = 480.0;

    private final ObservableList<Profesor> profesores =
            FXCollections.observableArrayList();

    private AdministradorServicio administradorServicio;

    /** Coordinador vigente obtenido una sola vez por el llamador. */
    private Profesor coordinadorActual;

    /** Indica si el diálogo terminó con una transferencia exitosa. */
    private boolean transferenciaExitosa;

    /**
     * Abre la ventana emergente como diálogo modal bloqueante.
     * Debe llamarse desde el hilo de la aplicación JavaFX.
     *
     * @param propietario       Ventana padre sobre la que se bloquea
     *                          el foco mientras el diálogo está
     *                          abierto; no nula.
     * @param coordinadorActual Coordinador vigente obtenido en el
     *                          paso 2 del FN (CU-Admin.-03); nulo si
     *                          ningún Profesor posee el rol
     *                          actualmente. Se reutiliza tal cual,
     *                          sin volver a consultar la BD.
     * @return {@code true} si el rol de Coordinador fue transferido
     *         exitosamente durante esta apertura del diálogo.
     */
    public static boolean mostrar(Window propietario,
            Profesor coordinadorActual) {
        try {
            FXMLLoader cargador = new FXMLLoader(
                    PanelTransferirRolController.class
                            .getResource(RUTA_FXML));
            Parent raiz = cargador.load();

            PanelTransferirRolController controlador =
                    cargador.getController();
            controlador.coordinadorActual = coordinadorActual;

            Stage dialogo = new Stage();
            dialogo.initModality(Modality.WINDOW_MODAL);
            dialogo.initOwner(propietario);
            dialogo.setTitle(TITULO_VENTANA);
            dialogo.setResizable(false);
            dialogo.setScene(
                    new Scene(raiz, ANCHO_DIALOGO, ALTO_DIALOGO));
            dialogo.showAndWait();

            return controlador.transferenciaExitosa;
        } catch (IOException excepcion) {
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "No se pudo abrir la ventana de transferencia:\n"
                    + excepcion.getMessage());
            ensancharAlerta(alerta);
            alerta.showAndWait();
            System.err.println(
                    "IOException en PanelTransferirRolController"
                    + ".mostrar: " + excepcion.getMessage());
            return false;
        }
    }

    /**
     * Inicializa la tabla enlazando cada columna con los
     * atributos del modelo {@link Profesor} y carga los Profesores
     * candidatos desde la BD.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        administradorServicio =
                new AdministradorServicio(new ProfesorDAOImpl());

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
     * en la tabla. Valida que haya una selección activa (FA-02),
     * pide confirmación con un mensaje distinto según si ya existía
     * un Coordinador (paso 6), y delega la actualización
     * transaccional al servicio (paso 8).
     */
    @FXML
    private void onBtnTransferirRol() {
        lblMensaje.setText("");

        Profesor seleccionado = tblProfesores.getSelectionModel()
                .getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText(
                    "Debes seleccionar un profesor de la lista "
                    + "para continuar.");
            return;
        }

        if (!confirmarTransferencia()) {
            return;
        }

        try {
            administradorServicio.transferirRolCoordinador(
                    seleccionado.getId(),
                    coordinadorActual != null
                            ? coordinadorActual.getId() : null);
        } catch (IllegalArgumentException datosInvalidos) {
            lblMensaje.setText(datosInvalidos.getMessage());
            return;
        } catch (SQLException errorConexion) {
            System.err.println(
                    "Error al transferir el rol de Coordinador: "
                    + errorConexion.getMessage());
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error de conexión con la base de datos");
            alerta.setHeaderText(null);
            alerta.setContentText(MENSAJE_EX01);
            ensancharAlerta(alerta);
            alerta.showAndWait();
            cerrarVentana();
            return;
        }

        transferenciaExitosa = true;
        cerrarVentana();
    }

    /**
     * Cierra el diálogo sin realizar ningún cambio (FA-01).
     */
    @FXML
    private void onBtnRegresar() {
        cerrarVentana();
    }

    /* ── Métodos auxiliares ──────────────────────────────────── */

    /**
     * Muestra la ventana de confirmación previa a la transferencia
     * (paso 6). El mensaje cambia según si ya existía un Coordinador
     * vigente que perderá el rol.
     *
     * @return {@code true} si el Administrador confirmó la operación.
     */
    private boolean confirmarTransferencia() {
        String mensaje = coordinadorActual != null
                ? "¿Estás seguro de que deseas transferir el rol de "
                  + "Coordinador a este Profesor? El Coordinador "
                  + "actual perderá el rol y sus privilegios."
                : "¿Estás seguro de que deseas asignar el rol de "
                  + "Coordinador a este Profesor?";

        ButtonType btnConfirmar = new ButtonType("Confirmar");
        ButtonType btnCancelar = new ButtonType("Cancelar");

        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar transferencia");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(mensaje);
        confirmacion.getButtonTypes().setAll(
                btnConfirmar, btnCancelar);
        ensancharAlerta(confirmacion);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        return respuesta.isPresent()
                && respuesta.get() == btnConfirmar;
    }

    /**
     * Consulta los Profesores activos elegibles para el rol de
     * Coordinador y los carga en la tabla (ya excluye al Coordinador
     * vigente, pues nunca tiene {@code coordinador = FALSE}).
     * Deshabilita el botón "Transferir rol" si no hay candidatos
     * (FA-04).
     */
    private void cargarProfesoresActivos() {
        profesores.clear();
        try {
            profesores.addAll(administradorServicio
                    .listarCandidatosACoordinador());
        } catch (SQLException e) {
            System.err.println(
                    "Error al cargar profesores activos: "
                    + e.getMessage());
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error de conexión con la base de datos");
            alerta.setHeaderText(null);
            alerta.setContentText(MENSAJE_EX01);
            ensancharAlerta(alerta);
            alerta.showAndWait();
        }
        btnTransferirRol.setDisable(profesores.isEmpty());
    }

    /**
     * Cierra la ventana emergente actual.
     */
    private void cerrarVentana() {
        Stage escenario = (Stage) tblProfesores.getScene().getWindow();
        escenario.close();
    }

    /**
     * Ensancha una ventana emergente para que los mensajes largos
     * (p. ej. la confirmación de transferencia) no se corten con
     * puntos suspensivos.
     *
     * @param alerta Ventana emergente a redimensionar; no nula.
     */
    private static void ensancharAlerta(Alert alerta) {
        alerta.setResizable(true);
        alerta.getDialogPane().setMinWidth(ANCHO_MINIMO_ALERTA);
        alerta.getDialogPane().setPrefWidth(ANCHO_MINIMO_ALERTA);
    }

}
