/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 30 de junio del 2026
 */
package mx.uv.spp.controladores.administrador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import mx.uv.spp.util.Navegador;
import mx.uv.spp.util.SesionUsuario;

/**
 * Controlador de la ventana de sección del Coordinador
 * (PanelSeccionCoordinador.fxml). Muestra los datos del
 * Coordinador activo y expone la acción de cambio.
 * No contiene lógica de negocio ni acceso directo a la BD.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PanelSeccionCoordinadorController implements Initializable {

    @FXML private Label  lblNombre;
    @FXML private Label  lblNumPersonal;
    @FXML private Label  lblCorreo;
    @FXML private Button btnCambiarCoordinador;

    private static final String VISTA_PANEL_ADMIN =
            "/mx/uv/spp/vistas/administrador/PanelAdministrador.fxml";
    private static final String VISTA_PROFESORES =
            "/mx/uv/spp/vistas/administrador/PanelProfesores.fxml";

    /**
     * Inicializa la vista cargando los datos del Coordinador activo.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
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
     * Abre el flujo para registrar un nuevo Coordinador,
     * reemplazando al activo.
     */
    @FXML
    private void onBtnCambiarCoordinador() {
        PanelTransferirRolController.mostrar(
                btnCambiarCoordinador.getScene().getWindow());
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
     * Consulta el Coordinador activo a través del DAO y
     * rellena las etiquetas de la tarjeta.
     */
    private void cargarDatosCoordinador() {
        // TODO: invocar CoordinadorServicio.obtenerActivo()
        //       y asignar los valores a lblNombre, lblNumPersonal
        //       y lblCorreo con el formato "Campo: valor"
        lblNombre.setText("Nombre: —");
        lblNumPersonal.setText("No. Personal: —");
        lblCorreo.setText("Correo: —");
    }

}
