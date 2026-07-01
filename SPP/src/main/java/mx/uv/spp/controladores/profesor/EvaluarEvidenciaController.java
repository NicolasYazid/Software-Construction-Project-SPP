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
 * Controlador de la vista Evaluar Evidencia (CU-29).
 * Muestra al Profesor las evidencias entregadas por sus Estudiantes
 * que aún no tienen calificación asignada y permite calificarlas en
 * rango 1.0–10.0.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class EvaluarEvidenciaController implements Initializable {

    @FXML private Label lblEstado;
    @FXML private TableView<FilaEvidencia> tblEvidencias;
    @FXML private TableColumn<FilaEvidencia, String> colInscripcion;
    @FXML private TableColumn<FilaEvidencia, String> colTipo;
    @FXML private TableColumn<FilaEvidencia, String> colArchivo;
    @FXML private TableColumn<FilaEvidencia, Void> colAccion;

    private final ObservableList<FilaEvidencia> filas =
            FXCollections.observableArrayList();
    private ProfesorServicio profesorServicio;

    private static final String ESTILO_ERROR =
            "-fx-text-fill: #c0392b;";
    private static final String ESTILO_INFO =
            "-fx-text-fill: #1C3A6E;";
    private static final String ESTILO_OK =
            "-fx-text-fill: #27ae60;";
    private static final String MENSAJE_EX01 =
            "No fue posible conectarse con la base de datos. "
            + "Inténtelo de nuevo en unos minutos.";

    /**
     * Inicializa el controlador: instancia el servicio, configura
     * las columnas de la tabla y carga las evidencias pendientes.
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
        tblEvidencias.setItems(filas);
        cargarFilas();
    }

    // Métodos privados

    /**
     * Configura las columnas de texto y la columna de acción con
     * un botón "Calificar" por fila.
     */
    private void configurarColumnas() {
        colInscripcion.setCellValueFactory(
                celda -> new SimpleStringProperty(
                        String.valueOf(
                                celda.getValue().idInscripcion)));
        colTipo.setCellValueFactory(
                celda -> new SimpleStringProperty(
                        celda.getValue().nombreTipo));
        colArchivo.setCellValueFactory(
                celda -> new SimpleStringProperty(
                        celda.getValue().nombreArchivo));

        colAccion.setCellFactory(columna -> new TableCell<>() {
            private final Button btnVer =
                    new Button("Ver");
            private final Button btnCalificar =
                    new Button("Calificar");
            private final HBox celdaAcciones =
                    new HBox(4, btnVer, btnCalificar);

            {
                btnVer.setOnAction(e -> {
                    FilaEvidencia fila = getTableView()
                            .getItems().get(getIndex());
                    verArchivo(fila);
                });
                btnCalificar.setOnAction(e -> {
                    FilaEvidencia fila = getTableView()
                            .getItems().get(getIndex());
                    calificarEvidencia(fila);
                });
            }

            @Override
            protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : celdaAcciones);
            }
        });
    }

    /**
     * Carga o refresca las evidencias pendientes de calificación
     * para el Profesor autenticado.
     */
    private void cargarFilas() {
        int idProfesor = SesionUsuario.getIdUsuario();
        filas.clear();
        try {
            List<Documento> pendientes =
                    profesorServicio.obtenerEvidenciasPendientes(
                            idProfesor);
            for (Documento doc : pendientes) {
                FilaEvidencia fila = new FilaEvidencia();
                fila.idDocumento    = doc.getIdDocumento();
                fila.idInscripcion  = doc.getIdInscripcion();
                fila.nombreTipo     =
                        obtenerNombreTipo(doc.getIdTipoEvidencia());
                fila.rutaArchivo    =
                        doc.getRutaArchivo() != null
                        ? doc.getRutaArchivo() : "";
                fila.nombreArchivo  =
                        doc.getNombreArchivo() != null
                        ? doc.getNombreArchivo() : "";
                filas.add(fila);
            }
            if (filas.isEmpty()) {
                mostrarMensaje(ESTILO_INFO,
                        "No hay evidencias pendientes de "
                        + "calificación.");
            } else {
                mostrarMensaje(ESTILO_INFO,
                        filas.size() + " evidencia(s) por calificar.");
            }
        } catch (SQLException e) {
            System.err.println(
                    "Error al cargar evidencias pendientes: "
                    + e.getMessage());
            mostrarMensaje(ESTILO_ERROR, MENSAJE_EX01);
        }
    }

    /**
     * Abre el archivo adjunto de la evidencia con la aplicación
     * predeterminada del sistema operativo.
     *
     * @param fila Fila con la ruta del archivo a abrir.
     */
    private void verArchivo(FilaEvidencia fila) {
        if (fila.rutaArchivo == null
                || fila.rutaArchivo.isEmpty()) {
            mostrarMensaje(ESTILO_ERROR,
                    "No hay archivo adjunto para esta evidencia.");
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
                    "No fue posible abrir el archivo.");
        }
    }

    /**
     * Abre un diálogo para que el Profesor ingrese la calificación
     * (entero del 1 al 10) y confirme antes de registrarla en la BD.
     *
     * @param fila Fila de la tabla con los datos de la evidencia.
     */
    private void calificarEvidencia(FilaEvidencia fila) {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Calificar evidencia");
        dialogo.setHeaderText(
                "Estudiante " + fila.idInscripcion
                + " — " + fila.nombreTipo);
        dialogo.setContentText(
                "Calificación (número entero del 1 al 10):");

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
                    + "entero del 1 al 10.");
            return;
        }
        if (calificacion % 1.0 != 0.0
                || calificacion < Constantes.CALIFICACION_MIN
                || calificacion > Constantes.CALIFICACION_MAX) {
            mostrarMensaje(ESTILO_ERROR,
                    "La calificación debe ser un número "
                    + "entero del 1 al 10.");
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar calificación");
        confirmacion.setHeaderText(
                "¿Está seguro de registrar la calificación "
                + (int) calificacion + "?");
        confirmacion.setContentText(
                "Esta acción no puede deshacerse.");
        Optional<ButtonType> respuestaConfirmacion =
                confirmacion.showAndWait();
        if (!respuestaConfirmacion.isPresent()
                || respuestaConfirmacion.get() != ButtonType.OK) {
            return;
        }

        try {
            profesorServicio.calificarEvidencia(
                    fila.idDocumento, calificacion);
            mostrarMensaje(ESTILO_OK,
                    "Evidencia calificada correctamente.");
            cargarFilas();
        } catch (IllegalArgumentException e) {
            mostrarMensaje(ESTILO_ERROR, e.getMessage());
        } catch (SQLException e) {
            System.err.println(
                    "Error al calificar evidencia: "
                    + e.getMessage());
            mostrarMensaje(ESTILO_ERROR, MENSAJE_EX01);
        }
    }

    /**
     * Convierte el identificador numérico del tipo de evidencia al
     * nombre legible para el Profesor.
     *
     * @param idTipo FK de {@code tipo_evidencia}.
     * @return nombre del tipo o "Tipo N" si no se reconoce.
     */
    private String obtenerNombreTipo(int idTipo) {
        switch (idTipo) {
            case Constantes.TIPO_EVIDENCIA_OFICIO_ACEPTACION:
                return "Oficio de Aceptación";
            case Constantes.TIPO_EVIDENCIA_OFICIO_ASIGNACION:
                return "Oficio de Asignación";
            case Constantes.TIPO_EVIDENCIA_HORARIO_CLASES:
                return "Horario EE";
            case Constantes.TIPO_EVIDENCIA_CRONOGRAMA:
                return "Cronograma / Horario Laboral";
            case Constantes.TIPO_EVIDENCIA_REPORTE_MENSUAL:
                return "Reporte Mensual";
            case Constantes.TIPO_EVIDENCIA_INFORME_PARCIAL:
                return "Informe Parcial";
            case Constantes.TIPO_EVIDENCIA_INFORME_FINAL:
                return "Informe Final";
            case Constantes.TIPO_EVIDENCIA_PRESENTACION:
                return "Presentación";
            case Constantes.TIPO_EVIDENCIA_EVALUACION_OV:
                return "Evaluación OV";
            case Constantes.TIPO_EVIDENCIA_AUTOEVALUACION:
                return "Autoevaluación";
            default:
                return "Tipo " + idTipo;
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

    // Clase interna de fila

    /**
     * Datos de una fila de la tabla de evidencias pendientes.
     */
    static class FilaEvidencia {
        int idDocumento;
        int idInscripcion;
        String nombreTipo = "";
        String rutaArchivo = "";
        String nombreArchivo = "";
    }

}
