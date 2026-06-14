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
import mx.uv.spp.persistencia.dao.impl.UsuarioDAOImpl;

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
    private static final String ETIQUETA_PRACTICANTE   =
            "Practicante";

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
                ETIQUETA_PRACTICANTE));
        cmbTipo.getSelectionModel().selectFirst();

        cmbTipo.valueProperty().addListener(
                (obs, anterior, nuevo) ->
                        actualizarEtiquetaIdentificador(nuevo));
    }

    /**
     * Maneja el clic en el botón "Ingresar".
     * Valida los campos en la UI, invoca el servicio de autenticación
     * y actualiza la pantalla según el resultado: navega a la pantalla
     * principal, redirige al cambio de contraseña (SEG-02), o muestra
     * el mensaje de error recibido del servicio.
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
                if (resultado.isContrasenaTemporal()) {
                    navegarACambioContrasena(resultado);
                } else {
                    navegarAPantallaPrincipal(resultado);
                }
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
            boolean esPracticante = ETIQUETA_PRACTICANTE
                    .equals(cmbTipo.getValue());
            mostrarMensaje(ESTILO_ERROR,
                    esPracticante
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
     *         {@code PRACTICANTE} como valor por defecto.
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
            return TipoUsuario.PRACTICANTE;
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
        if (ETIQUETA_PRACTICANTE.equals(rolSeleccionado)) {
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
     * Navega a la pantalla de cambio de contraseña obligatorio
     * (SEG-02). El resultado contiene el ID y tipo de usuario para
     * que el controlador destino pueda persistir el cambio.
     *
     * @param resultado Resultado del login con {@code contrasenaTemporal = true}.
     */
    private void navegarACambioContrasena(
            ResultadoAutenticacion resultado) {
        mostrarMensaje(ESTILO_INFO,
                "Bienvenido/a. Debe establecer una nueva contraseña"
                + " antes de continuar (SEG-02).");
        // Pantalla de cambio de contraseña pendiente de implementar.
        // App.cambiarVista("/mx/uv/spp/vistas/comun/cambio_contrasena.fxml");
    }

    /**
     * Navega a la pantalla principal del rol autenticado.
     * Cada rol tiene su propia pantalla de inicio definida en los
     * controladores del paquete correspondiente.
     *
     * @param resultado Resultado con el tipo de usuario y nombre completo.
     */
    private void navegarAPantallaPrincipal(
            ResultadoAutenticacion resultado) {
        mostrarMensaje(ESTILO_INFO,
                "¡Bienvenido/a, "
                + resultado.getNombreCompleto() + "!");
        // Navegar según el rol una vez que las pantallas existan:
        // switch (resultado.getTipo()) {
        //   case ADMINISTRADOR →
        //     App.cambiarVista(".../administrador/principal.fxml");
        //   case COORDINADOR   →
        //     App.cambiarVista(".../coordinador/principal.fxml");
        //   case PROFESOR      →
        //     App.cambiarVista(".../profesor/principal.fxml");
        //   case PRACTICANTE   →
        //     App.cambiarVista(".../practicante/principal.fxml");
        // }
    }

}
