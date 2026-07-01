/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de junio del 2026
 */
package mx.uv.spp.controladores.profesor;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import mx.uv.spp.modelo.Documento;
import mx.uv.spp.negocio.ProfesorServicio;
import mx.uv.spp.persistencia.dao.impl.DocumentoDAOImpl;
import mx.uv.spp.persistencia.dao.impl.MensajeGrupoDAOImpl;
import mx.uv.spp.util.Constantes;
import mx.uv.spp.util.SesionUsuario;

/**
 * Controlador de la vista Calificación OV (CU-Est.-27).
 * Muestra al Profesor los documentos de EvaluaciónOV entregados
 * por sus Estudiantes que aún no tienen calificación. El Profesor
 * puede ver el documento y registrar la calificación entera (1–10)
 * que la Organización Vinculada otorgó al Estudiante.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class CalificacionOVController implements Initializable {

    @FXML private Label                    lblEstado;
    @FXML private TableView<FilaOV>        tblOV;
    @FXML private TableColumn<FilaOV,
            String>                        colEstudiante;
    @FXML private TableColumn<FilaOV,
            String>                        colTipo;
    @FXML private TableColumn<FilaOV,
            String>                        colArchivo;
    @FXML private TableColumn<FilaOV,
            Void>                          colAccion;

    private final ObservableList<FilaOV> filas =
            FXCollections.observableArrayList();
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
     * Inicializa el controlador: instancia el servicio, configura
     * las columnas de la tabla y carga las evaluaciones OV pendientes.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        profesorServicio = new ProfesorServicio(
                new DocumentoDAOImpl(),
                new MensajeGrupoDAOImpl());
        configurarColumnas();
        tblOV.setItems(filas);
        cargarFilas();
    }

    /* ── Métodos privados ───────────────────────────────────── */

    /**
     * Configura las columnas de texto y la columna de acción con
     * los botones "Ver documento" y "Calificar" por fila.
     */
    private void configurarColumnas() {
        colEstudiante.setCellValueFactory(
                c -> new SimpleStringProperty(
                        String.valueOf(
                                c.getValue().idEstudiante)));
        colTipo.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().nombreTipo));
        colArchivo.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().nombreArchivo));

        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btnVer =
                    new Button("Ver documento");
            private final Button btnCalificar =
                    new Button("Calificar");
            private final HBox celdaAcciones =
                    new HBox(4, btnVer, btnCalificar);

            {
                btnVer.setOnAction(e -> {
                    FilaOV fila = getTableView()
                            .getItems().get(getIndex());
                    verDocumento(fila);
                });
                btnCalificar.setOnAction(e -> {
                    FilaOV fila = getTableView()
                            .getItems().get(getIndex());
                    calificarOV(fila);
                });
            }

            @Override
            protected void updateItem(
                    Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : celdaAcciones);
            }
        });
    }

    /**
     * Carga los documentos de EvaluaciónOV entregados y sin
     * calificación del grupo del Profesor autenticado.
     */
    private void cargarFilas() {
        int idProfesor = SesionUsuario.getIdUsuario();
        filas.clear();
        try {
            List<Documento> pendientes =
                    new DocumentoDAOImpl()
                    .obtenerOVSinCalificarPorProfesor(idProfesor);
            for (Documento doc : pendientes) {
                FilaOV fila  = new FilaOV();
                fila.idDocumento   = doc.getIdDocumento();
                fila.idEstudiante  = doc.getIdInscripcion();
                fila.nombreTipo    = doc.getIdTipoEvidencia()
                        == Constantes.TIPO_EVIDENCIA_EVALUACION_OV
                        ? "Evaluación OV 1"
                        : "Evaluación OV 2";
                fila.rutaArchivo   =
                        doc.getRutaArchivo() != null
                        ? doc.getRutaArchivo() : "";
                fila.nombreArchivo =
                        doc.getNombreArchivo() != null
                        ? doc.getNombreArchivo() : "";
                filas.add(fila);
            }
            if (filas.isEmpty()) {
                mostrarMensaje(ESTILO_INFO,
                        "No hay evaluaciones OV pendientes de "
                        + "calificación.");
            } else {
                mostrarMensaje(ESTILO_INFO,
                        filas.size()
                        + " evaluación(es) OV por calificar.");
            }
        } catch (SQLException e) {
            System.err.println(
                    "Error al cargar evaluaciones OV: "
                    + e.getMessage());
            mostrarMensaje(ESTILO_ERROR, MENSAJE_EX01);
        }
    }

    /**
     * Abre el documento OV con la aplicación predeterminada del
     * sistema operativo para que el Profesor lo revise.
     *
     * @param fila Fila que contiene la ruta del documento.
     */
    private void verDocumento(FilaOV fila) {
        if (fila.rutaArchivo == null
                || fila.rutaArchivo.isEmpty()) {
            mostrarMensaje(ESTILO_ERROR,
                    "No hay documento adjunto para esta "
                    + "evaluación.");
            return;
        }
        File archivo = new File(fila.rutaArchivo);
        if (!archivo.exists()) {
            mostrarMensaje(ESTILO_ERROR,
                    "El archivo no se encontró en la ruta "
                    + "registrada.");
            return;
        }
        try {
            Desktop.getDesktop().open(archivo);
        } catch (IOException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "No fue posible abrir el documento.");
        }
    }

    /**
     * Muestra un diálogo para ingresar la calificación entera (1–10)
     * otorgada por la OV y la registra tras confirmación.
     *
     * @param fila Fila con los datos del documento OV.
     */
    private void calificarOV(FilaOV fila) {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Calificación de la OV");
        dialogo.setHeaderText(
                "Estudiante " + fila.idEstudiante
                + " — " + fila.nombreTipo);
        dialogo.setContentText(
                "Calificación OV (número entero del 1 al 10):");

        Optional<String> resultado = dialogo.showAndWait();
        if (!resultado.isPresent()
                || resultado.get().trim().isEmpty()) {
            return;
        }
        double calificacion;
        try {
            calificacion =
                    Double.parseDouble(resultado.get().trim());
        } catch (NumberFormatException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "La calificación debe ser un número "
                    + "entero entre 1 y 10.");
            return;
        }
        if (calificacion % 1.0 != 0.0
                || calificacion < Constantes.CALIFICACION_MIN
                || calificacion > Constantes.CALIFICACION_MAX) {
            mostrarMensaje(ESTILO_ERROR,
                    "La calificación debe ser un número "
                    + "entero entre 1 y 10.");
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar calificación OV");
        confirmacion.setHeaderText(
                "¿Registrar calificación "
                + (int) calificacion + " para este Estudiante?");
        confirmacion.setContentText(
                "Esta acción no puede deshacerse.");
        Optional<ButtonType> confirm =
                confirmacion.showAndWait();
        if (!confirm.isPresent()
                || confirm.get() != ButtonType.OK) {
            return;
        }

        try {
            profesorServicio.calificarEvidencia(
                    fila.idDocumento, calificacion);
            mostrarMensaje(ESTILO_OK,
                    "Calificación de la OV registrada "
                    + "exitosamente.");
            cargarFilas();
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

    /* ── Clase interna de fila ──────────────────────────────── */

    /**
     * Datos de una fila de la tabla de evaluaciones OV pendientes.
     */
    static class FilaOV {
        int    idDocumento;
        int    idEstudiante;
        String nombreTipo     = "";
        String rutaArchivo    = "";
        String nombreArchivo  = "";
    }

}
