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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import mx.uv.spp.negocio.EstudianteServicio;
import mx.uv.spp.persistencia.dao.impl.AutoevaluacionDAOImpl;
import mx.uv.spp.persistencia.dao.impl.DocumentoDAOImpl;
import mx.uv.spp.persistencia.dao.impl.PeriodoInscripcionesDAOImpl;
import mx.uv.spp.persistencia.dao.impl.SeleccionProyectoDAOImpl;
import mx.uv.spp.util.Constantes;
import mx.uv.spp.util.SesionUsuario;

/**
 * Controlador de la vista Autoevaluación (CU-30).
 * Presenta las 10 afirmaciones en escala Likert (1–5) y permite
 * al Estudiante enviar sus respuestas. La entrega es irreversible;
 * si ya la entregó, se deshabilitan los controles.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class AutoevaluacionController implements Initializable {

    @FXML private Label lblEstado;
    @FXML private Label lblInstrucciones;
    @FXML private Spinner<Integer> spnA1;
    @FXML private Spinner<Integer> spnA2;
    @FXML private Spinner<Integer> spnA3;
    @FXML private Spinner<Integer> spnA4;
    @FXML private Spinner<Integer> spnA5;
    @FXML private Spinner<Integer> spnA6;
    @FXML private Spinner<Integer> spnA7;
    @FXML private Spinner<Integer> spnA8;
    @FXML private Spinner<Integer> spnA9;
    @FXML private Spinner<Integer> spnA10;
    @FXML private Button btnEnviar;

    private EstudianteServicio estudianteServicio;

    private static final String ESTILO_ERROR =
            "-fx-text-fill: #c0392b;";
    private static final String ESTILO_OK =
            "-fx-text-fill: #27ae60;";
    private static final String ESTILO_INFO =
            "-fx-text-fill: #1C3A6E;";

    /**
     * Inicializa el controlador: configura los spinners, instancia
     * el servicio y verifica si el Estudiante ya entregó.
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

        configurarSpinners();
        verificarEntregaPrevia();
    }

    // Manejador de evento

    /**
     * Solicita confirmación y envía las respuestas de la
     * autoevaluación. La acción es irreversible.
     */
    @FXML
    private void onBtnEnviar() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar autoevaluación");
        alerta.setHeaderText("Esta acción es irreversible.");
        alerta.setContentText(
                "Una vez enviada, no podrás modificar tu "
                + "autoevaluación. ¿Deseas continuar?");
        Optional<ButtonType> respuesta = alerta.showAndWait();
        if (respuesta.isPresent()
                && respuesta.get() == ButtonType.OK) {
            enviarAutoevaluacion();
        }
    }

    // Métodos privados

    /**
     * Configura cada {@link Spinner} con valores del 1 al 5 y
     * valor inicial 3 (punto medio de la escala Likert).
     */
    private void configurarSpinners() {
        Spinner<Integer>[] spinners = new Spinner[]{
            spnA1, spnA2, spnA3, spnA4, spnA5,
            spnA6, spnA7, spnA8, spnA9, spnA10
        };
        for (Spinner<Integer> spn : spinners) {
            spn.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(
                            Constantes.ESCALA_AUTOEVALUACION_MIN,
                            Constantes.ESCALA_AUTOEVALUACION_MAX,
                            3));
        }
    }

    /**
     * Consulta si el Estudiante ya entregó su autoevaluación.
     * Si es así, deshabilita el botón de envío y muestra un mensaje.
     */
    private void verificarEntregaPrevia() {
        int idInscripcion = SesionUsuario.getIdInscripcion();
        if (idInscripcion == 0) {
            mostrarMensaje(ESTILO_ERROR,
                    "No tienes inscripción activa en este ciclo.");
            btnEnviar.setDisable(true);
            return;
        }
        try {
            AutoevaluacionDAOImpl autoDAO =
                    new AutoevaluacionDAOImpl();
            if (autoDAO.existePorInscripcion(idInscripcion)) {
                mostrarMensaje(ESTILO_OK,
                        "Ya enviaste tu autoevaluación. "
                        + "Esta acción es irreversible.");
                deshabilitarFormulario();
            } else {
                mostrarMensaje(ESTILO_INFO,
                        "Escala: 1 = Totalmente en desacuerdo  "
                        + "| 5 = Totalmente de acuerdo.");
            }
        } catch (SQLException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "Error al verificar estado: " + e.getMessage());
        }
    }

    /**
     * Recopila los valores de los spinners y llama al servicio para
     * persistir la autoevaluación.
     */
    private void enviarAutoevaluacion() {
        int[] respuestas = {
            spnA1.getValue(),  spnA2.getValue(),
            spnA3.getValue(),  spnA4.getValue(),
            spnA5.getValue(),  spnA6.getValue(),
            spnA7.getValue(),  spnA8.getValue(),
            spnA9.getValue(),  spnA10.getValue()
        };
        int idInscripcion = SesionUsuario.getIdInscripcion();
        try {
            estudianteServicio.entregarAutoevaluacion(
                    idInscripcion, respuestas);
            mostrarMensaje(ESTILO_OK,
                    "Autoevaluación enviada correctamente.");
            deshabilitarFormulario();
        } catch (IllegalArgumentException
                | IllegalStateException e) {
            mostrarMensaje(ESTILO_ERROR, e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "Error al guardar autoevaluación: "
                    + e.getMessage());
        }
    }

    /**
     * Deshabilita todos los spinners y el botón de envío.
     * Se usa tras una entrega exitosa o cuando ya existe una entrega.
     */
    private void deshabilitarFormulario() {
        Spinner<Integer>[] spinners = new Spinner[]{
            spnA1, spnA2, spnA3, spnA4, spnA5,
            spnA6, spnA7, spnA8, spnA9, spnA10
        };
        for (Spinner<Integer> spn : spinners) {
            spn.setDisable(true);
        }
        btnEnviar.setDisable(true);
    }

    /**
     * Actualiza el texto y el estilo del label de estado.
     *
     * @param estilo  Estilo CSS en línea.
     * @param mensaje Texto a mostrar.
     */
    private void mostrarMensaje(String estilo, String mensaje) {
        lblEstado.setStyle(estilo);
        lblEstado.setText(mensaje);
    }

}
