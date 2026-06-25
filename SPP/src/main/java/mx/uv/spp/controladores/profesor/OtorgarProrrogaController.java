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
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import mx.uv.spp.negocio.ProfesorServicio;
import mx.uv.spp.persistencia.dao.impl.DocumentoDAOImpl;
import mx.uv.spp.persistencia.dao.impl.MensajeGrupoDAOImpl;

/**
 * Controlador de la vista Otorgar Prórroga (CU-30).
 * Permite al Profesor extender la fecha límite de entrega de una
 * Evidencia específica. La nueva fecha debe ser posterior a la fecha
 * actual y no puede reemplazar una prórroga activa vigente.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class OtorgarProrrogaController implements Initializable {

    @FXML private Label      lblEstado;
    @FXML private TextField  txtIdDocumento;
    @FXML private DatePicker dtpFechaProrroga;

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
                "Ingrese el ID del documento y seleccione la "
                + "nueva fecha límite de entrega.");
    }

    /* ── Manejador de evento ─────────────────────────────────── */

    /**
     * Valida los campos del formulario y registra la prórroga para
     * el documento indicado.
     */
    @FXML
    private void onBtnOtorgar() {
        String txtId     = txtIdDocumento.getText().trim();
        LocalDate nuevaFecha = dtpFechaProrroga.getValue();

        if (txtId.isEmpty()) {
            mostrarMensaje(ESTILO_ERROR,
                    "El ID del documento es obligatorio.");
            return;
        }
        if (nuevaFecha == null) {
            mostrarMensaje(ESTILO_ERROR,
                    "Seleccione la nueva fecha límite.");
            return;
        }
        int idDocumento;
        try {
            idDocumento = Integer.parseInt(txtId);
        } catch (NumberFormatException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "El ID del documento debe ser un "
                    + "número entero.");
            return;
        }
        try {
            profesorServicio.otorgarProrroga(
                    idDocumento, nuevaFecha);
            mostrarMensaje(ESTILO_OK,
                    "Prórroga otorgada hasta " + nuevaFecha + ".");
            txtIdDocumento.clear();
            dtpFechaProrroga.setValue(null);
        } catch (IllegalArgumentException
                | IllegalStateException e) {
            mostrarMensaje(ESTILO_ERROR, e.getMessage());
        } catch (SQLException e) {
            System.err.println(
                    "Error al otorgar prórroga: " + e.getMessage());
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
