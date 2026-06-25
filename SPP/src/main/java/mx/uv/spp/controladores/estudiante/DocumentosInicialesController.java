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
 * Controlador de la vista Documentos Iniciales.
 * Permite al Estudiante entregar los documentos iniciales requeridos:
 * Oficio de Aceptación, Horario EE, Horario Laboral y Cronograma.
 * La re-entrega de un documento sobrescribe el anterior (sección 8).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class DocumentosInicialesController implements Initializable {

    @FXML private Label                        lblEstado;
    @FXML private TableView<FilaDocumento>     tblDocumentos;
    @FXML private TableColumn<FilaDocumento,
            String>                            colDocumento;
    @FXML private TableColumn<FilaDocumento,
            String>                            colEstado;
    @FXML private TableColumn<FilaDocumento,
            String>                            colArchivo;
    @FXML private TableColumn<FilaDocumento,
            Void>                              colAccion;

    private final ObservableList<FilaDocumento> filas =
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
     * Inicializa el controlador: configura la tabla, instancia el
     * servicio y carga el estado actual de cada documento.
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
        tblDocumentos.setItems(filas);
        cargarFilas();
    }

    /* ── Métodos privados ───────────────────────────────────── */

    /**
     * Configura las columnas de texto y la columna de acción con
     * un botón "Subir" por fila.
     */
    private void configurarColumnas() {
        colDocumento.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().nombre));
        colEstado.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().estado));
        colArchivo.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().archivo));

        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btnSubir =
                    new Button("Subir PDF");

            {
                btnSubir.setOnAction(e -> {
                    FilaDocumento fila = getTableView()
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
     * Carga o refresca el estado de cada documento desde la BD.
     * Define las cuatro filas fijas que corresponden a los documentos
     * iniciales que el Estudiante debe entregar.
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
            Constantes.TIPO_EVIDENCIA_OFICIO_ACEPTACION,
            Constantes.TIPO_EVIDENCIA_HORARIO_CLASES,
            Constantes.TIPO_EVIDENCIA_HORARIO_LABORAL,
            Constantes.TIPO_EVIDENCIA_CRONOGRAMA
        };
        String[] nombres = {
            "Oficio de Aceptación",
            "Horario de la EE",
            "Horario Laboral (OV)",
            "Cronograma de Actividades"
        };
        try {
            for (int i = 0; i < tipos.length; i++) {
                Documento doc =
                        documentoDAOImpl
                        .obtenerPorInscripcionYTipoUnico(
                                idInscripcion, tipos[i]);
                FilaDocumento fila = new FilaDocumento();
                fila.nombre    = nombres[i];
                fila.idTipo    = tipos[i];
                if (doc != null) {
                    fila.estado   = obtenerNombreEstado(
                            doc.getIdEstadoDocumento());
                    fila.archivo  = doc.getNombreArchivo() != null
                            ? doc.getNombreArchivo() : "";
                } else {
                    fila.estado  = "Pendiente";
                    fila.archivo = "";
                }
                filas.add(fila);
            }
            mostrarMensaje(ESTILO_INFO,
                    "Sube cada documento en formato PDF (máx. 5 MB)."
                    + " La re-entrega reemplaza el anterior.");
        } catch (SQLException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "Error al cargar documentos: " + e.getMessage());
        }
    }

    /**
     * Abre un {@link FileChooser} para seleccionar un PDF y lo
     * entrega mediante el servicio. Valida extensión y tamaño.
     *
     * @param fila Fila de la tabla que corresponde al documento
     *             que se está subiendo.
     */
    private void subirArchivo(FilaDocumento fila) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar PDF — " + fila.nombre);
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Archivos PDF", "*.pdf"));
        File archivo = selector.showOpenDialog(
                tblDocumentos.getScene().getWindow());
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
                    "Documento \"" + fila.nombre
                    + "\" entregado correctamente.");
            cargarFilas();
        } catch (IllegalArgumentException e) {
            mostrarMensaje(ESTILO_ERROR, e.getMessage());
        } catch (SQLException e) {
            mostrarMensaje(ESTILO_ERROR,
                    "Error al guardar el documento: "
                    + e.getMessage());
        }
    }

    /**
     * Convierte el id numérico del estado de un documento al texto
     * legible para el usuario.
     *
     * @param idEstado Identificador del estado en BD.
     * @return nombre del estado o "Desconocido" si no se reconoce.
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
     * Actualiza el texto y estilo del label de estado.
     *
     * @param estilo  Estilo CSS en línea.
     * @param mensaje Texto a mostrar.
     */
    private void mostrarMensaje(String estilo, String mensaje) {
        lblEstado.setStyle(estilo);
        lblEstado.setText(mensaje);
    }

    /* ── Clase interna de fila de la tabla ──────────────────── */

    /**
     * Datos de una fila de la tabla de documentos iniciales.
     * Cada fila representa un tipo de documento requerido.
     */
    static class FilaDocumento {
        String nombre  = "";
        int    idTipo;
        String estado  = "Pendiente";
        String archivo = "";
    }

}
