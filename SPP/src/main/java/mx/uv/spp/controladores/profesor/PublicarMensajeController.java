/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de junio del 2026
 */
package mx.uv.spp.controladores.profesor;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import mx.uv.spp.modelo.Grupo;
import mx.uv.spp.negocio.ProfesorServicio;
import mx.uv.spp.persistencia.dao.impl.DocumentoDAOImpl;
import mx.uv.spp.persistencia.dao.impl.GrupoDAOImpl;
import mx.uv.spp.persistencia.dao.impl.MensajeGrupoDAOImpl;
import mx.uv.spp.util.SesionUsuario;

/**
 * Controlador de la vista Publicar Mensaje (CU-31).
 * Permite al Profesor publicar un mensaje a todos los Estudiantes
 * de un Grupo. El mensaje puede contener texto, un PDF adjunto o
 * ambos; al menos uno es obligatorio.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PublicarMensajeController implements Initializable {

    @FXML private Label lblEstado;
    @FXML private ComboBox<Grupo> cmbGrupo;
    @FXML private TextField txtAsunto;
    @FXML private TextArea txaTexto;
    @FXML private Label lblArchivo;

    private ProfesorServicio profesorServicio;
    private GrupoDAOImpl grupoDAOImpl;
    private File archivoPdf;

    private static final String ESTILO_ERROR =
            "-fx-text-fill: #c0392b;";
    private static final String ESTILO_INFO =
            "-fx-text-fill: #1C3A6E;";
    private static final String ESTILO_OK =
            "-fx-text-fill: #27ae60;";
    private static final String MENSAJE_ERROR_CONEXION_BD =
            "No fue posible conectarse con la base de datos. "
            + "Inténtelo de nuevo en unos minutos.";
    private static final String TEXTO_SIN_ADJUNTO =
            "Sin adjunto.";

    /**
     * Inicializa el controlador: instancia los DAOs y servicios,
     * configura el ComboBox de grupos y carga los grupos del Profesor.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        profesorServicio = new ProfesorServicio(
                new DocumentoDAOImpl(),
                new MensajeGrupoDAOImpl());
        grupoDAOImpl = new GrupoDAOImpl();
        configurarComboGrupo();
        cargarGrupos();
    }

    // Manejadores de eventos

    /**
     * Abre un {@link FileChooser} para adjuntar un PDF al mensaje.
     */
    @FXML
    private void onBtnAdjuntarPdf() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Adjuntar PDF al mensaje");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Archivos PDF", "*.pdf"));
        File archivo = selector.showOpenDialog(
                cmbGrupo.getScene().getWindow());
        if (archivo != null) {
            archivoPdf = archivo;
            lblArchivo.setText("Adjunto: " + archivo.getName());
        }
    }

    /**
     * Valida el formulario y publica el mensaje al grupo seleccionado.
     * Limpia el formulario tras un envío exitoso.
     */
    @FXML
    private void onBtnPublicar() {
        Grupo grupoSeleccionado = cmbGrupo.getValue();
        if (grupoSeleccionado == null) {
            mostrarMensaje(ESTILO_ERROR,
                    "Seleccione el grupo receptor del mensaje.");
            return;
        }
        String asunto = txtAsunto.getText().trim();
        String texto = txaTexto.getText();
        String ruta = archivoPdf != null
                ? archivoPdf.getAbsolutePath() : null;
        String nombre = archivoPdf != null
                ? archivoPdf.getName() : null;
        int idProfesor = SesionUsuario.getIdUsuario();

        try {
            profesorServicio.publicarMensaje(
                    grupoSeleccionado.getIdGrupo(),
                    idProfesor,
                    asunto.isEmpty() ? null : asunto,
                    texto,
                    ruta,
                    nombre);
            mostrarMensaje(ESTILO_OK,
                    "Mensaje publicado correctamente.");
            txtAsunto.clear();
            txaTexto.clear();
            archivoPdf = null;
            lblArchivo.setText(TEXTO_SIN_ADJUNTO);
        } catch (IllegalArgumentException e) {
            mostrarMensaje(ESTILO_ERROR, e.getMessage());
        } catch (SQLException e) {
            System.err.println(
                    "Error al publicar mensaje: " + e.getMessage());
            mostrarMensaje(ESTILO_ERROR, MENSAJE_ERROR_CONEXION_BD);
        }
    }

    // Métodos privados de apoyo

    /**
     * Configura las celdas del ComboBox para mostrar nombre y NRC
     * de cada Grupo.
     */
    private void configurarComboGrupo() {
        cmbGrupo.setCellFactory(listaGrupos -> new ListCell<Grupo>() {
            @Override
            protected void updateItem(Grupo item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(vacio || item == null ? null
                        : item.getNombre()
                        + " (" + item.getNrc() + ")");
            }
        });
        cmbGrupo.setButtonCell(new ListCell<Grupo>() {
            @Override
            protected void updateItem(Grupo item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(vacio || item == null ? null
                        : item.getNombre()
                        + " (" + item.getNrc() + ")");
            }
        });
    }

    /**
     * Carga los grupos asignados al Profesor autenticado y los pone
     * en el ComboBox. Selecciona el primero automáticamente.
     */
    private void cargarGrupos() {
        int idProfesor = SesionUsuario.getIdUsuario();
        try {
            List<Grupo> grupos =
                    grupoDAOImpl.obtenerPorProfesor(idProfesor);
            ObservableList<Grupo> items =
                    FXCollections.observableArrayList(grupos);
            cmbGrupo.setItems(items);
            if (grupos.isEmpty()) {
                mostrarMensaje(ESTILO_ERROR,
                        "No tiene grupos asignados en este ciclo.");
            } else {
                cmbGrupo.getSelectionModel().selectFirst();
                mostrarMensaje(ESTILO_INFO,
                        "Redacte el mensaje. El texto o el PDF "
                        + "adjunto son obligatorios.");
            }
        } catch (SQLException e) {
            System.err.println(
                    "Error al cargar grupos del Profesor: "
                    + e.getMessage());
            mostrarMensaje(ESTILO_ERROR, MENSAJE_ERROR_CONEXION_BD);
        }
    }

    /**
     * Actualiza el texto y el estilo del label de estado.
     *
     * @param estilo Estilo CSS en línea.
     * @param mensaje Texto a mostrar.
     */
    private void mostrarMensaje(String estilo, String mensaje) {
        lblEstado.setStyle(estilo);
        lblEstado.setText(mensaje);
    }

}
