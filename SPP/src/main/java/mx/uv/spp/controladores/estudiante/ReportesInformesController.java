/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de junio del 2026
 */
package mx.uv.spp.controladores.estudiante;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import mx.uv.spp.modelo.Documento;
import mx.uv.spp.negocio.EstudianteServicio;
import mx.uv.spp.persistencia.dao.impl.AutoevaluacionDAOImpl;
import mx.uv.spp.persistencia.dao.impl.DocumentoDAOImpl;
import mx.uv.spp.persistencia.dao.impl.PeriodoInscripcionesDAOImpl;
import mx.uv.spp.persistencia.dao.impl.SeleccionProyectoDAOImpl;
import mx.uv.spp.util.Constantes;
import mx.uv.spp.util.SesionUsuario;
import mx.uv.spp.util.Validador;

/**
 * Controlador de la vista Reportes e Informes.
 * Permite al Estudiante entregar el Reporte Mensual, el Informe
 * Parcial, el Informe Final y la Presentación. También gestiona
 * la entrega de la Evaluación a la OV (irreversible). Acepta
 * archivos PDF y PPTX con un máximo de 5 MB (sección 8).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class ReportesInformesController implements Initializable {

    @FXML private Label                    lblEstado;
    @FXML private TableView<FilaEvidencia> tblEvidencias;
    @FXML private TableColumn<FilaEvidencia,
            String>                        colEvidencia;
    @FXML private TableColumn<FilaEvidencia,
            String>                        colEstado;
    @FXML private TableColumn<FilaEvidencia,
            String>                        colArchivo;
    @FXML private TableColumn<FilaEvidencia,
            Void>                          colAccion;

    private final ObservableList<FilaEvidencia> filas =
            FXCollections.observableArrayList();
    private EstudianteServicio estudianteServicio;
    private DocumentoDAOImpl   documentoDAOImpl;

    private static final String ESTILO_ERROR =
            "-fx-text-fill: #c0392b;";
    private static final String ESTILO_INFO  =
            "-fx-text-fill: #1C3A6E;";
    private static final String ESTILO_OK    =
            "-fx-text-fill: #27ae60;";

    /**
     * Inicializa el controlador: configura columnas, instancia el
     * servicio y carga las evidencias del Estudiante.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        documentoDAOImpl = new DocumentoDAOImpl();
        estudianteServicio = new EstudianteServicio(
                new PeriodoInscripcionesDAOImpl(),
                new SeleccionProyectoDAOImpl(),
                documentoDAOImpl,
                new AutoevaluacionDAOImpl());

        configurarColumnas();
        tblEvidencias.setItems(filas);
        cargarFilas();
    }

    /* ── Métodos privados ───────────────────────────────────── */

    /**
     * Configura columnas de texto y la columna de acción con
     * un botón "Subir" por fila.
     */
    private void configurarColumnas() {
        colEvidencia.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().nombre));
        colEstado.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().estado));
        colArchivo.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().archivo));

        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btnSubir = new Button("Subir");

            {
                btnSubir.setOnAction(e -> {
                    FilaEvidencia fila = getTableView()
                            .getItems().get(getIndex());
                    subirArchivo(fila);
                });
            }

            @Override
            protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : btnSubir);
            }
        });
    }

    /**
     * Carga o refresca las filas con el estado actual de cada
     * evidencia desde la BD.
     */
    private void cargarFilas() {
        int idInscripcion = SesionUsuario.getIdInscripcion();
        if (idInscripcion == 0) {
            mostrarMensaje(ESTILO_ERROR,
                    "No tienes inscripción activa en este ciclo.");
            return;
        }
        filas.clear();
        int[] tipos = {
            Constantes.TIPO_EVIDENCIA_REPORTE_MENSUAL,
            Constantes.TIPO_EVIDENCIA_INFORME_PARCIAL,
            Constantes.TIPO_EVIDENCIA_INFORME_FINAL,
            Constantes.TIPO_EVIDENCIA_PRESENTACION,
            Constantes.TIPO_EVIDENCIA_EVALUACION_OV
        };
        String[] nombres = {
            "Reporte Mensual",
            "Informe Parcial (210 hrs)",
            "Informe Final (420 hrs)",
            "Presentación (.pdf/.pptx)",
            "Evaluación a la OV (irreversible)"
        };
        try {
            for (int i = 0; i < tipos.length; i++) {
                Documento doc =
                        documentoDAOImpl
                        .obtenerPorInscripcionYTipoUnico(
                                idInscripcion, tipos[i]);
                FilaEvidencia fila = new FilaEvidencia();
                fila.nombre  = nombres[i];
                fila.idTipo  = tipos[i];
                if (doc != null) {
                    fila.estado  = obtenerNombreEstado(
                            doc.getIdEstadoDocumento());
                    fila.archivo = doc.getNombreArchivo() != null
                            ? doc.getNombreArchivo() : "";
                } else {
                    fila.estado  = "Pendiente";
                    fila.archivo = "";
                }
                filas.add(fila);
            }
            mostrarMensaje(ESTILO_INFO,
                    "Acepta PDF (todos) o PPTX (presentación), "
                    + "máx. 5 MB.");
        } catch (SQLException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "Error al cargar evidencias: " + e.getMessage());
        }
    }

    /**
     * Abre un {@link FileChooser} para seleccionar el archivo y lo
     * entrega mediante el servicio. Valida extensión y tamaño.
     *
     * @param fila Fila de la tabla que corresponde a la evidencia.
     */
    private void subirArchivo(FilaEvidencia fila) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar archivo — " + fila.nombre);

        if (fila.idTipo
                == Constantes.TIPO_EVIDENCIA_PRESENTACION) {
            selector.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(
                            "PDF o PPTX", "*.pdf", "*.pptx"));
        } else {
            selector.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Archivos PDF", "*.pdf"));
        }

        File archivo = selector.showOpenDialog(
                tblEvidencias.getScene().getWindow());
        if (archivo == null) {
            return;
        }
        try {
            Validador.validarTamanoArchivo(
                    archivo.length(), archivo.getName());
        } catch (IllegalArgumentException e) {
            mostrarMensaje(ESTILO_ERROR, e.getMessage());
            return;
        }

        int idInscripcion = SesionUsuario.getIdInscripcion();
        try {
            estudianteServicio.entregarDocumento(
                    idInscripcion,
                    fila.idTipo,
                    archivo.getAbsolutePath(),
                    archivo.getName());
            mostrarMensaje(ESTILO_OK,
                    "\"" + fila.nombre
                    + "\" entregado correctamente.");
            cargarFilas();
        } catch (IllegalArgumentException e) {
            mostrarMensaje(ESTILO_ERROR, e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "Error al guardar evidencia: "
                    + e.getMessage());
        }
    }

    /**
     * Convierte el id de estado numérico al nombre legible.
     *
     * @param idEstado Identificador de estado en BD.
     * @return texto del estado.
     */
    private String obtenerNombreEstado(int idEstado) {
        switch (idEstado) {
            case Constantes.ESTADO_DOCUMENTO_PENDIENTE:
                return "Pendiente";
            case Constantes.ESTADO_DOCUMENTO_ENTREGADO:
                return "Entregado";
            case Constantes.ESTADO_DOCUMENTO_APROBADO:
                return "Aprobado";
            case Constantes.ESTADO_DOCUMENTO_RECHAZADO:
                return "Rechazado";
            case Constantes.ESTADO_DOCUMENTO_CON_PRORROGA:
                return "Con prórroga";
            case Constantes.ESTADO_DOCUMENTO_EVALUADO:
                return "Evaluado";
            default:
                return "Desconocido";
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
     * Datos de una fila de la tabla de evidencias del Estudiante.
     */
    static class FilaEvidencia {
        String nombre  = "";
        int    idTipo;
        String estado  = "Pendiente";
        String archivo = "";
    }

}
