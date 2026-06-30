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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mx.uv.spp.util.Navegador;
import mx.uv.spp.util.SesionUsuario;

/**
 * Controlador de la ventana de alta de Profesores
 * (PanelAltaProfesores.fxml). Recoge los datos del formulario
 * y delega el registro al servicio correspondiente.
 * No contiene lógica de negocio ni acceso directo a la BD.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PanelAltaProfesoresController implements Initializable {

    @FXML private TextField     txtNumPersonal;
    @FXML private TextField     txtNombre;
    @FXML private TextField     txtApellidoPaterno;
    @FXML private TextField     txtApellidoMaterno;
    @FXML private TextField     txtCorreo;
    @FXML private PasswordField pwdContrasena;

    private static final String VISTA_PANEL_ADMIN =
            "/mx/uv/spp/vistas/administrador/PanelAdministrador.fxml";
    private static final String VISTA_PROFESORES =
            "/mx/uv/spp/vistas/administrador/PanelProfesores.fxml";
    private static final String VISTA_COORDINADOR =
            "/mx/uv/spp/vistas/administrador/PanelSeccionCoordinador.fxml";

    /**
     * Inicializa el controlador. No requiere acciones adicionales
     * porque el formulario comienza vacío.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        // Formulario vacío al abrirse; no se requiere inicialización.
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
     * Navega a la ventana de sección del Coordinador.
     */
    @FXML
    private void onBtnCoordinador() {
        Navegador.cambiarVista(VISTA_COORDINADOR);
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

    /* ── Manejadores del formulario ─────────────────────────── */

    /**
     * Cancela el registro y regresa a la lista de Profesores
     * sin guardar ningún dato.
     */
    @FXML
    private void onBtnCancelar() {
        Navegador.cambiarVista(VISTA_PROFESORES);
    }

    /**
     * Valida los campos del formulario y, si son correctos,
     * delega el registro del nuevo Profesor al servicio.
     */
    @FXML
    private void onBtnRegistrar() {
        String numPersonal      = txtNumPersonal.getText().trim();
        String nombre           = txtNombre.getText().trim();
        String apellidoPaterno  = txtApellidoPaterno.getText().trim();
        String apellidoMaterno  = txtApellidoMaterno.getText().trim();
        String correo           = txtCorreo.getText().trim();
        String contrasena       = pwdContrasena.getText();

        if (numPersonal.isEmpty() || nombre.isEmpty()
                || apellidoPaterno.isEmpty()
                || apellidoMaterno.isEmpty()
                || correo.isEmpty()
                || contrasena.isEmpty()) {
            return;
        }

        // TODO: invocar ProfesorServicio.registrar(...)
        //       y navegar a PanelProfesores al concluir
    }

}
