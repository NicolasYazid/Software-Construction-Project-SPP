/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de junio del 2026
 */
package mx.uv.spp.controladores.profesor;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import mx.uv.spp.negocio.ProfesorServicio;
import mx.uv.spp.persistencia.dao.impl.DocumentoDAOImpl;
import mx.uv.spp.persistencia.dao.impl.MensajeGrupoDAOImpl;

/**
 * Controlador de la vista Calificación OV (CU-26).
 * Permite al Profesor registrar la calificación de la Organización
 * Vinculada para un Estudiante dado su identificador de inscripción.
 * La calificación debe estar en el rango 1.0–10.0.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class CalificacionOVController implements Initializable {

    @FXML private Label     lblEstado;
    @FXML private TextField txtIdInscripcion;
    @FXML private TextField txtCalificacion;

    private ProfesorServicio profesorServicio;

    private static final String ESTILO_ERROR =
            "-fx-text-fill: #c0392b;";
    private static final String ESTILO_INFO  =
            "-fx-text-fill: #1C3A6E;";
    private static final String ESTILO_OK    =
            "-fx-text-fill: #27ae60;";
    private static final String MENSAJE_EX01 =
            "No fue posible conectarse con la base de datos. "
            + "Inténtelo de nuevo en unos minutos.";

    /**
     * Inicializa el controlador: instancia el servicio y muestra
     * instrucciones en la etiqueta de estado.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        profesorServicio = new ProfesorServicio(
                new DocumentoDAOImpl(),
                new MensajeGrupoDAOImpl());
        mostrarMensaje(ESTILO_INFO,
                "Ingrese el ID de inscripción del Estudiante "
                + "y la calificación de la OV (1.0–10.0).");
    }

    /* ── Manejador de evento ─────────────────────────────────── */

    /**
     * Valida los campos del formulario y registra la calificación de
     * la OV para la inscripción indicada.
     */
    @FXML
    private void onBtnRegistrar() {
        String txtId  = txtIdInscripcion.getText().trim();
        String txtCal = txtCalificacion.getText().trim();

        if (txtId.isEmpty() || txtCal.isEmpty()) {
            mostrarMensaje(ESTILO_ERROR,
                    "Todos los campos son obligatorios.");
            return;
        }
        int idInscripcion;
        try {
            idInscripcion = Integer.parseInt(txtId);
        } catch (NumberFormatException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "El ID de inscripción debe ser un "
                    + "número entero.");
            return;
        }
        double calificacion;
        try {
            calificacion = Double.parseDouble(txtCal);
        } catch (NumberFormatException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "La calificación debe ser un número válido.");
            return;
        }
        try {
            profesorServicio.registrarCalificacionOV(
                    idInscripcion, calificacion);
            mostrarMensaje(ESTILO_OK,
                    "Calificación de la OV registrada.");
            txtIdInscripcion.clear();
            txtCalificacion.clear();
        } catch (IllegalArgumentException
                | IllegalStateException e) {
            mostrarMensaje(ESTILO_ERROR, e.getMessage());
        } catch (SQLException e) {
            System.err.println(
                    "Error al registrar calificación OV: "
                    + e.getMessage());
            mostrarMensaje(ESTILO_ERROR, MENSAJE_EX01);
        }
    }

    /* ── Método privado de apoyo ─────────────────────────────── */

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
