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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mx.uv.spp.negocio.AdministradorServicio;
import mx.uv.spp.persistencia.dao.impl.ProfesorDAOImpl;
import mx.uv.spp.util.Navegador;
import mx.uv.spp.util.SesionUsuario;
import mx.uv.spp.util.Validador;

/**
 * Controlador de la ventana de alta de Profesores
 * (PanelAltaProfesores.fxml). Implementa el flujo completo de
 * CU-Admin.-01 "Dar de alta Profesor": valida los datos capturados
 * en el mismo orden que el caso de uso, muestra las ventanas
 * emergentes de cada flujo alterno y delega el registro al servicio
 * correspondiente. No contiene lógica de negocio ni acceso directo
 * a la BD.
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

    private static final String MENSAJE_EX01 =
            "Error: no fue posible conectarse con la base de datos, "
            + "inténtelo de nuevo ahora o en unos minutos";

    // Ancho mínimo de las ventanas emergentes para que mensajes
    // largos (p. ej. el de éxito con la contraseña) no se corten.
    private static final double ANCHO_MINIMO_ALERTA = 480.0;

    private AdministradorServicio administradorServicio;

    /**
     * Inicializa el controlador instanciando el servicio del
     * Administrador con su DAO JDBC. El formulario comienza vacío.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        administradorServicio =
                new AdministradorServicio(new ProfesorDAOImpl());
    }

    /* ── Manejadores del menú lateral ───────────────────────── */

    /**
     * Navega a la pantalla principal del Administrador, previa
     * confirmación (FA-01) para no perder los datos capturados.
     */
    @FXML
    private void onBtnInicio() {
        if (confirmarSalidaSinGuardar()) {
            Navegador.cambiarVista(VISTA_PANEL_ADMIN);
        }
    }

    /**
     * Navega a la ventana de gestión de Profesores, previa
     * confirmación (FA-01) para no perder los datos capturados.
     */
    @FXML
    private void onBtnProfesores() {
        if (confirmarSalidaSinGuardar()) {
            Navegador.cambiarVista(VISTA_PROFESORES);
        }
    }

    /**
     * Navega a la ventana de sección del Coordinador, previa
     * confirmación (FA-01) para no perder los datos capturados.
     */
    @FXML
    private void onBtnCoordinador() {
        if (confirmarSalidaSinGuardar()) {
            Navegador.cambiarVista(VISTA_COORDINADOR);
        }
    }

    /**
     * Carga la vista de historial de personal académico, previa
     * confirmación (FA-01) para no perder los datos capturados.
     */
    @FXML
    private void onBtnHistorial() {
        if (confirmarSalidaSinGuardar()) {
            // TODO: navegar a la vista de historial
        }
    }

    /**
     * Limpia la sesión activa y vuelve a la pantalla de login,
     * previa confirmación (FA-01) para no perder los datos
     * capturados.
     */
    @FXML
    private void onBtnCerrarSesion() {
        if (confirmarSalidaSinGuardar()) {
            SesionUsuario.limpiar();
            Navegador.irALogin();
        }
    }

    /* ── Manejadores del formulario ─────────────────────────── */

    /**
     * Cancela el registro (FA-01). Pide confirmación antes de
     * descartar los datos capturados en el formulario.
     */
    @FXML
    private void onBtnCancelar() {
        if (confirmarSalidaSinGuardar()) {
            Navegador.cambiarVista(VISTA_PROFESORES);
        }
    }

    /**
     * Valida los campos del formulario en el mismo orden del flujo
     * normal de CU-Admin.-01 y, si todo es correcto, pide
     * confirmación y delega el registro del nuevo Profesor al
     * servicio.
     */
    @FXML
    private void onBtnRegistrar() {
        String numPersonal     = txtNumPersonal.getText().trim();
        String nombre          = txtNombre.getText().trim();
        String apellidoPaterno = txtApellidoPaterno.getText().trim();
        String apellidoMaterno = txtApellidoMaterno.getText().trim();
        String correo          = txtCorreo.getText().trim();
        String contrasena      = pwdContrasena.getText();

        // Paso 3 / FA-02: ningún campo vacío, excepto apellido materno.
        if (numPersonal.isEmpty() || nombre.isEmpty()
                || apellidoPaterno.isEmpty() || correo.isEmpty()
                || contrasena.isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Datos faltantes",
                    "Uno o más campos se encuentran vacíos, por "
                    + "favor verifique la información.");
            return;
        }

        // Paso 4 / FA-03: formato de número de personal y correo.
        try {
            Validador.validarNumeroPersonal(numPersonal);
            Validador.validarCorreoInstitucionalProfesor(correo);
        } catch (IllegalArgumentException formatoInvalido) {
            mostrarAlerta(AlertType.WARNING, "Formato inválido",
                    "El número de personal o el correo institucional "
                    + "ingresados no cumplen el formato correcto, "
                    + "por favor verifique la información ingresada.");
            return;
        }

        // Paso 5 / FA-04: no se permiten números en nombre/apellidos.
        try {
            Validador.validarSoloLetras(nombre, "nombre");
            Validador.validarSoloLetras(
                    apellidoPaterno, "apellido paterno");
            if (!apellidoMaterno.isEmpty()) {
                Validador.validarSoloLetras(
                        apellidoMaterno, "apellido materno");
            }
        } catch (IllegalArgumentException ingresoInvalido) {
            mostrarAlerta(AlertType.WARNING, "Formato inválido",
                    "No se permiten números en campos donde no "
                    + "correspondan, verifique la información "
                    + "ingresada");
            return;
        }

        // Validación adicional (SEG-03): toda contraseña creada en
        // el sistema debe cumplir el estándar de seguridad, aunque
        // el CU no describa un flujo alterno específico para esto.
        try {
            Validador.validarContrasena(contrasena);
        } catch (IllegalArgumentException contrasenaInvalida) {
            mostrarAlerta(AlertType.WARNING, "Formato inválido",
                    "La contraseña no cumple el formato de "
                    + "seguridad: mínimo 10 caracteres, con "
                    + "mayúsculas, minúsculas y números.");
            return;
        }

        // Paso 6 / FA-05: confirmación antes de persistir.
        if (!confirmarRegistro()) {
            return;
        }

        // Paso 8 (FA-06) y 9 (EX-01): duplicados y conexión a la BD.
        try {
            administradorServicio.registrarProfesor(
                    numPersonal, nombre, apellidoPaterno,
                    apellidoMaterno, correo, contrasena);
        } catch (IllegalStateException duplicado) {
            mostrarAlerta(AlertType.WARNING, "Profesor duplicado",
                    "Lo sentimos, pero ya existe un profesor "
                    + "registrado con el mismo número de personal o "
                    + "correo institucional, por favor verifique la "
                    + "información ingresada.");
            return;
        } catch (IllegalArgumentException datosInvalidos) {
            mostrarAlerta(AlertType.WARNING, "Formato inválido",
                    datosInvalidos.getMessage());
            return;
        } catch (SQLException errorConexion) {
            System.err.println(
                    "Error al registrar profesor: "
                    + errorConexion.getMessage());
            mostrarAlerta(AlertType.ERROR,
                    "Error de conexión con la base de datos",
                    MENSAJE_EX01);
            return;
        }

        // Paso 10-12: registro exitoso.
        mostrarAlerta(AlertType.INFORMATION, "Registro exitoso",
                "El profesor ha sido dado de alta exitosamente, por "
                + "favor, anote la contraseña ingresada para "
                + "proporcionársela al profesor, ya que esta "
                + "información es privada y no volverá a mostrarse: "
                + contrasena);
        limpiarFormulario();
    }

    /* ── Métodos privados de apoyo ───────────────────────────── */

    /**
     * Pide confirmación antes de abandonar el formulario de alta
     * (FA-01), ya sea por "Cancelar" o por cualquier botón del menú
     * lateral, para no perder los datos capturados sin avisar.
     *
     * @return {@code true} si el Administrador confirmó salir.
     */
    private boolean confirmarSalidaSinGuardar() {
        ButtonType btnSalir = new ButtonType("Salir");
        ButtonType btnSeguirEditando =
                new ButtonType("Seguir editando");

        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar registro");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(
                "¿Estás seguro de que deseas cancelar el registro? "
                + "Los datos ingresados hasta ahora no se guardarán.");
        confirmacion.getButtonTypes().setAll(
                btnSalir, btnSeguirEditando);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        return respuesta.isPresent() && respuesta.get() == btnSalir;
    }

    /**
     * Muestra el diálogo de confirmación previo al registro
     * (GUI-ConfirmacionRegistroProfesor).
     *
     * @return {@code true} si el Administrador confirmó el registro.
     */
    private boolean confirmarRegistro() {
        ButtonType btnConfirmar = new ButtonType("Confirmar");
        ButtonType btnRegresar =
                new ButtonType("Regresar y seguir editando");

        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar registro");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(
                "¿Estás seguro de que deseas registrar a este "
                + "profesor?");
        confirmacion.getButtonTypes().setAll(
                btnConfirmar, btnRegresar);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        return respuesta.isPresent()
                && respuesta.get() == btnConfirmar;
    }

    /**
     * Muestra una ventana emergente con el tipo, título y mensaje
     * indicados, y espera a que el Administrador la cierre.
     *
     * @param tipo    Tipo de alerta (advertencia, error o información).
     * @param titulo  Título de la ventana.
     * @param mensaje Cuerpo del mensaje a mostrar.
     */
    private void mostrarAlerta(AlertType tipo, String titulo,
            String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.setResizable(true);
        alerta.getDialogPane().setMinWidth(ANCHO_MINIMO_ALERTA);
        alerta.getDialogPane().setPrefWidth(ANCHO_MINIMO_ALERTA);
        alerta.showAndWait();
    }

    /**
     * Limpia todos los campos del formulario tras un registro
     * exitoso, dejando la ventana lista para un nuevo alta.
     */
    private void limpiarFormulario() {
        txtNumPersonal.clear();
        txtNombre.clear();
        txtApellidoPaterno.clear();
        txtApellidoMaterno.clear();
        txtCorreo.clear();
        pwdContrasena.clear();
    }

}
