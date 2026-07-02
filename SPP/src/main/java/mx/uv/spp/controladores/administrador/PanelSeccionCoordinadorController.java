/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 30 de junio del 2026
 */
package mx.uv.spp.controladores.administrador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import mx.uv.spp.modelo.Profesor;
import mx.uv.spp.negocio.AdministradorServicio;
import mx.uv.spp.persistencia.dao.impl.ProfesorDAOImpl;
import mx.uv.spp.util.Navegador;
import mx.uv.spp.util.SesionUsuario;

/**
 * Controlador de la ventana de sección del Coordinador
 * (PanelSeccionCoordinador.fxml). Muestra los datos del
 * Coordinador activo y expone la acción de cambio, implementando
 * CU-Admin.-03 "Cambiar Coordinador".
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PanelSeccionCoordinadorController implements Initializable {

    @FXML private Label lblNombre;
    @FXML private Label lblNumeroPersonal;
    @FXML private Label lblCorreo;
    @FXML private Label lblMensaje;
    @FXML private Button btnCambiarCoordinador;

    private static final String VISTA_PANEL_ADMIN =
            "/mx/uv/spp/vistas/administrador/PanelAdministrador.fxml";
    private static final String VISTA_PROFESORES =
            "/mx/uv/spp/vistas/administrador/PanelProfesores.fxml";

    private static final String MENSAJE_ERROR_CONEXION_BD =
            "Error: no fue posible conectarse con la base de datos, "
            + "inténtelo de nuevo ahora o en unos minutos.";

    // Ancho mínimo de las ventanas emergentes para que mensajes
    // largos no se corten con puntos suspensivos.
    private static final double ANCHO_MINIMO_ALERTA = 480.0;

    private AdministradorServicio administradorServicio;

    /** Coordinador vigente, obtenido una sola vez (paso 2 del CU). */
    private Profesor coordinadorActual;

    /**
     * Inicializa la vista cargando los datos del Coordinador activo.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        administradorServicio =
                new AdministradorServicio(new ProfesorDAOImpl());
        cargarDatosCoordinador();
    }

    /* ── Manejadores del menú lateral ───────────────────────── */

    /**
     * Navega a la pantalla principal del Administrador.
     */
    @FXML
    private void onBtnInicio() {
        Navegador.cambiarVista(VISTA_PANEL_ADMIN);
    }

    /**
     * Navega a la ventana de gestión de Profesores.
     */
    @FXML
    private void onBtnProfesores() {
        Navegador.cambiarVista(VISTA_PROFESORES);
    }

    /**
     * Permanece en esta misma ventana; no realiza ninguna acción.
     */
    @FXML
    private void onBtnCoordinador() {
        // Ya estamos en la vista del Coordinador.
    }

    /**
     * Carga la vista de historial de personal académico.
     */
    @FXML
    private void onBtnHistorial() {
        // TODO: navegar a la vista de historial
    }

    /**
     * Limpia la sesión activa y vuelve a la pantalla de login.
     */
    @FXML
    private void onBtnCerrarSesion() {
        SesionUsuario.limpiar();
        Navegador.irALogin();
    }

    /* ── Manejadores del área de contenido ──────────────────── */

    /**
     * Abre la ventana emergente GUI-TransferirRol (paso 4),
     * reutilizando el Coordinador vigente ya obtenido en el paso 2
     * en vez de volver a consultarlo. Al cerrarse, recarga los
     * datos mostrados y, si hubo una transferencia exitosa, muestra
     * el mensaje de éxito (paso 9).
     */
    @FXML
    private void onBtnCambiarCoordinador() {
        boolean transferido = PanelTransferirRolController.mostrar(
                btnCambiarCoordinador.getScene().getWindow(),
                coordinadorActual);

        cargarDatosCoordinador();

        if (transferido) {
            lblMensaje.setText(
                    "El profesor seleccionado ahora es el nuevo "
                    + "Coordinador.");
        }
    }

    /**
     * Regresa a la pantalla principal del Administrador.
     */
    @FXML
    private void onBtnRegresar() {
        Navegador.cambiarVista(VISTA_PANEL_ADMIN);
    }

    /* ── Carga de datos ─────────────────────────────────────── */

    /**
     * Consulta el Coordinador activo a través del servicio y
     * rellena las etiquetas de la tarjeta (paso 2). Muestra "—" en
     * cada campo si ningún Profesor posee el rol actualmente.
     */
    private void cargarDatosCoordinador() {
        lblMensaje.setText("");
        try {
            coordinadorActual =
                    administradorServicio.obtenerCoordinadorActual();
        } catch (SQLException e) {
            System.err.println(
                    "Error al cargar el coordinador actual: "
                    + e.getMessage());
            coordinadorActual = null;
            mostrarErrorConexion();
        }

        if (coordinadorActual == null) {
            lblNombre.setText("Nombre: —");
            lblNumeroPersonal.setText("Número de personal: —");
            lblCorreo.setText("Correo: —");
        } else {
            lblNombre.setText(
                    "Nombre: " + coordinadorActual.getNombreCompleto());
            lblNumeroPersonal.setText(
                    "Número de personal: "
                    + coordinadorActual.getNumeroPersonal());
            lblCorreo.setText(
                    "Correo: " + coordinadorActual.getCorreo());
        }
    }

    /**
     * Muestra la ventana emergente de error de conexión (EX-01).
     */
    private void mostrarErrorConexion() {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Error de conexión con la base de datos");
        alerta.setHeaderText(null);
        alerta.setContentText(MENSAJE_ERROR_CONEXION_BD);
        alerta.setResizable(true);
        alerta.getDialogPane().setMinWidth(ANCHO_MINIMO_ALERTA);
        alerta.getDialogPane().setPrefWidth(ANCHO_MINIMO_ALERTA);
        alerta.showAndWait();
    }

}
