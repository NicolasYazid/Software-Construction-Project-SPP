/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.controladores.comun;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mx.uv.spp.modelo.ResultadoAutenticacion;
import mx.uv.spp.modelo.TipoUsuario;
import mx.uv.spp.negocio.LoginServicio;
import mx.uv.spp.persistencia.dao.impl.EstudianteInscritoDAOImpl;
import mx.uv.spp.persistencia.dao.impl.UsuarioDAOImpl;
import mx.uv.spp.util.Navegador;
import mx.uv.spp.util.SesionUsuario;

/**
 * Controlador de la pantalla de autenticación (login.fxml).
 * Responsabilidades: capturar entradas, validarlas en la UI y
 * delegar la autenticación a {@link LoginServicio}. No contiene
 * lógica de negocio ni accede directamente a la base de datos.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class LoginController implements Initializable {

    /* ── Controles inyectados desde FXML ─────────────────────── */

    @FXML private ComboBox<String> cmbTipo;
    @FXML private Label            lblIdentificador;
    @FXML private TextField        txtIdentificador;
    @FXML private PasswordField    pwdContrasena;
    @FXML private Button           btnIniciarSesion;
    @FXML private Label            lblMensaje;

    /* ── Servicio de negocio ─────────────────────────────────── */

    private LoginServicio loginServicio;

    /* ── Constantes de etiquetas de UI ──────────────────────── */

    private static final String ETIQUETA_ADMINISTRADOR =
            "Administrador";
    private static final String ETIQUETA_COORDINADOR   =
            "Coordinador";
    private static final String ETIQUETA_PROFESOR      =
            "Profesor(a)";
    private static final String ETIQUETA_ESTUDIANTE    =
            "Estudiante";

    private static final String LABEL_CORREO     =
            "Correo electrónico:";
    private static final String LABEL_MATRICULA  =
            "Matrícula:";
    private static final String PROMPT_CORREO    =
            "Ingrese su correo electrónico";
    private static final String PROMPT_MATRICULA =
            "Ingrese su matrícula (ej. S21013417)";

    private static final String ESTILO_OCULTO =
            "lbl-mensaje-oculto";
    private static final String ESTILO_ERROR  =
            "lbl-mensaje-error";
    private static final String ESTILO_INFO   =
            "lbl-mensaje-info";

    /**
     * Inicializa el controlador al cargar el FXML.
     * Configura el ComboBox con los roles disponibles y registra
     * el listener que actualiza la etiqueta del identificador cuando
     * el usuario cambia de rol.
     *
     * @param ubicacion  URL del FXML cargado (no se usa directamente).
     * @param recursos   Paquete de internacionalización (no aplicado aún).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        loginServicio = new LoginServicio(new UsuarioDAOImpl());

        cmbTipo.setItems(FXCollections.observableArrayList(
                ETIQUETA_ADMINISTRADOR,
                ETIQUETA_COORDINADOR,
                ETIQUETA_PROFESOR,
                ETIQUETA_ESTUDIANTE));
        cmbTipo.getSelectionModel().selectFirst();

        cmbTipo.valueProperty().addListener(
                (obs, anterior, nuevo) ->
                        actualizarEtiquetaIdentificador(nuevo));
    }

    /**
     * Maneja el clic en el botón "Ingresar".
     * Valida los campos en la UI, invoca el servicio de autenticación
     * y actualiza la pantalla según el resultado: navega a la pantalla
     * principal o muestra el mensaje de error recibido del servicio.
     */
    @FXML
    private void onBtnIniciarSesion() {
        String identificador = txtIdentificador.getText();
        String contrasena    = pwdContrasena.getText();

        if (!validarCamposUI(identificador, contrasena)) {
            return;
        }

        TipoUsuario tipo = obtenerTipoSeleccionado();
        btnIniciarSesion.setDisable(true);

        try {
            ResultadoAutenticacion resultado =
                    loginServicio.autenticarUsuario(
                            identificador, contrasena, tipo);

            if (resultado.isExitoso()) {
                pwdContrasena.clear();
                navegarAPantallaPrincipal(resultado);
            } else {
                mostrarMensaje(ESTILO_ERROR,
                        resultado.getMensajeError());
                pwdContrasena.clear();
                pwdContrasena.requestFocus();
            }
        } catch (IllegalArgumentException e) {
            mostrarMensaje(ESTILO_ERROR, e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "Error de conexión con la base de datos. "
                    + "Verifique la configuración.");
            System.err.println(
                    "SQLException en LoginController: "
                    + e.getMessage());
        } finally {
            btnIniciarSesion.setDisable(false);
        }
    }

    /* ── Métodos privados de apoyo ──────────────────────────── */

    /**
     * Valida que los campos de la UI no estén vacíos antes de
     * llamar al servicio. Muestra mensajes de error específicos
     * y posiciona el foco en el campo incorrecto.
     *
     * @param identificador Texto del campo de identificador.
     * @param contrasena    Texto del campo de contraseña.
     * @return {@code true} si ambos campos tienen contenido válido.
     */
    private boolean validarCamposUI(String identificador,
            String contrasena) {
        if (cmbTipo.getValue() == null) {
            mostrarMensaje(ESTILO_ERROR,
                    "Seleccione un tipo de usuario.");
            cmbTipo.requestFocus();
            return false;
        }
        if (identificador.trim().isEmpty()) {
            boolean esEstudiante = ETIQUETA_ESTUDIANTE
                    .equals(cmbTipo.getValue());
            mostrarMensaje(ESTILO_ERROR,
                    esEstudiante
                    ? "Ingrese su matrícula."
                    : "Ingrese su correo electrónico.");
            txtIdentificador.requestFocus();
            return false;
        }
        if (contrasena.isEmpty()) {
            mostrarMensaje(ESTILO_ERROR,
                    "Ingrese su contraseña.");
            pwdContrasena.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Mapea la etiqueta seleccionada en el ComboBox al enum
     * {@link TipoUsuario} que espera la capa de negocio.
     *
     * @return {@link TipoUsuario} correspondiente, o
     *         {@code ESTUDIANTE} como valor por defecto.
     */
    private TipoUsuario obtenerTipoSeleccionado() {
        String seleccion = cmbTipo.getValue();
        if (ETIQUETA_ADMINISTRADOR.equals(seleccion)) {
            return TipoUsuario.ADMINISTRADOR;
        } else if (ETIQUETA_COORDINADOR.equals(seleccion)) {
            return TipoUsuario.COORDINADOR;
        } else if (ETIQUETA_PROFESOR.equals(seleccion)) {
            return TipoUsuario.PROFESOR;
        } else {
            return TipoUsuario.ESTUDIANTE;
        }
    }

    /**
     * Actualiza la etiqueta y el prompt del campo de identificador
     * según el rol seleccionado, y limpia los campos del formulario.
     * Se llama automáticamente cuando el usuario cambia el ComboBox.
     *
     * @param rolSeleccionado Etiqueta del rol recién elegido.
     */
    private void actualizarEtiquetaIdentificador(
            String rolSeleccionado) {
        if (ETIQUETA_ESTUDIANTE.equals(rolSeleccionado)) {
            lblIdentificador.setText(LABEL_MATRICULA);
            txtIdentificador.setPromptText(PROMPT_MATRICULA);
        } else {
            lblIdentificador.setText(LABEL_CORREO);
            txtIdentificador.setPromptText(PROMPT_CORREO);
        }
        txtIdentificador.clear();
        pwdContrasena.clear();
        lblMensaje.setText("");
        lblMensaje.getStyleClass().setAll(ESTILO_OCULTO);
    }

    /**
     * Cambia el estilo y el texto de la etiqueta de mensajes.
     *
     * @param estilo  Clase CSS a aplicar (error, info u oculto).
     * @param mensaje Texto a mostrar al usuario.
     */
    private void mostrarMensaje(String estilo, String mensaje) {
        lblMensaje.getStyleClass().setAll(estilo);
        lblMensaje.setText(mensaje);
    }

    /**
     * Navega a la pantalla principal del rol autenticado.
     * Antes de navegar, deposita el contexto del usuario en
     * {@link SesionUsuario}. Para el Estudiante, también consulta
     * el {@code id_inscripcion} del ciclo activo.
     *
     * @param resultado Resultado con el tipo de usuario y nombre completo.
     */
    private void navegarAPantallaPrincipal(
            ResultadoAutenticacion resultado) {
        SesionUsuario.inicializar(
                resultado.getIdUsuario(),
                resultado.getNombreCompleto(),
                resultado.getTipo());

        if (resultado.getTipo() == TipoUsuario.ESTUDIANTE) {
            try {
                int idInscripcion =
                        new EstudianteInscritoDAOImpl()
                        .obtenerIdInscripcionActivo(
                                resultado.getIdUsuario());
                SesionUsuario.setIdInscripcion(idInscripcion);
            } catch (java.sql.SQLException e) {
                System.err.println(
                        "No se pudo obtener idInscripcion: "
                        + e.getMessage());
            }
        }

        switch (resultado.getTipo()) {
            case ESTUDIANTE:
                Navegador.irAPanelEstudiante();
                break;
            case PROFESOR:
                Navegador.irAPanelProfesor();
                break;
            case COORDINADOR:
                Navegador.irAPanelCoordinador();
                break;
            case ADMINISTRADOR:
                Navegador.irAPanelAdministrador();
                break;
            default:
                mostrarMensaje(ESTILO_ERROR,
                        "Tipo de usuario no reconocido.");
                break;
        }
    }

}
