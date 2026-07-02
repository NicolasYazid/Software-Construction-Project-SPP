/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 30 de junio del 2026
 */
package mx.uv.spp.controladores.administrador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.spp.modelo.Profesor;
import mx.uv.spp.negocio.AdministradorServicio;
import mx.uv.spp.persistencia.dao.impl.ProfesorDAOImpl;
import mx.uv.spp.util.Navegador;
import mx.uv.spp.util.SesionUsuario;

/**
 * Controlador de la ventana de gestión de Profesores
 * (PanelProfesores.fxml). Muestra el listado de profesores
 * registrados y expone las acciones de alta y baja.
 * No contiene lógica de negocio ni acceso directo a la BD.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PanelProfesoresController implements Initializable {

    @FXML private TableView<Profesor> tblProfesores;
    @FXML private TableColumn<Profesor, String> colNumeroPersonal;
    @FXML private TableColumn<Profesor, String> colNombre;
    @FXML private TableColumn<Profesor, String> colCorreo;
    @FXML private TableColumn<Profesor, String> colEstado;

    private static final String VISTA_PANEL_ADMIN =
            "/mx/uv/spp/vistas/administrador/PanelAdministrador.fxml";
    private static final String VISTA_COORDINADOR =
            "/mx/uv/spp/vistas/administrador/PanelSeccionCoordinador.fxml";
    private static final String VISTA_ALTA_PROFESOR =
            "/mx/uv/spp/vistas/administrador/PanelAltaProfesores.fxml";

    private final ObservableList<Profesor> profesores =
            FXCollections.observableArrayList();

    private AdministradorServicio administradorServicio;

    /**
     * Inicializa la tabla enlazando cada columna con los
     * atributos del modelo {@link Profesor} y carga los Profesores
     * registrados desde la BD.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        administradorServicio =
                new AdministradorServicio(new ProfesorDAOImpl());

        colNumeroPersonal.setCellValueFactory(
                new PropertyValueFactory<>("numeroPersonal"));
        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombreCompleto"));
        colCorreo.setCellValueFactory(
                new PropertyValueFactory<>("correo"));
        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado"));

        tblProfesores.setItems(profesores);
        cargarProfesores();
    }

    /* ── Manejadores del menú lateral ───────────────────────── */

    /**
     * Navega a la pantalla principal del Administrador.
     */
    @FXML
    private void onBtnInicio() {
        Navegador.cambiarVista(VISTA_PANEL_ADMIN);
    }

    /**
     * Permanece en esta misma ventana; no realiza ninguna acción.
     */
    @FXML
    private void onBtnProfesores() {
        // Ya estamos en la vista de Profesores.
    }

    /**
     * Navega a la ventana de gestión del Coordinador.
     */
    @FXML
    private void onBtnCoordinador() {
        Navegador.cambiarVista(VISTA_COORDINADOR);
    }

    /**
     * Carga la vista de historial de personal académico.
     */
    @FXML
    private void onBtnHistorial() {
        // TODO: navegar a la vista de historial
    }

    /**
     * Limpia la sesión activa y vuelve a la pantalla de login.
     */
    @FXML
    private void onBtnCerrarSesion() {
        SesionUsuario.limpiar();
        Navegador.irALogin();
    }

    /* ── Manejadores del área de contenido ──────────────────── */

    /**
     * Regresa a la pantalla principal del Administrador.
     */
    @FXML
    private void onBtnRegresar() {
        Navegador.cambiarVista(VISTA_PANEL_ADMIN);
    }

    /**
     * Navega a la ventana de registro de un nuevo Profesor.
     */
    @FXML
    private void onBtnAltaProfesor() {
        Navegador.cambiarVista(VISTA_ALTA_PROFESOR);
    }

    /**
     * Inactiva al Profesor seleccionado en la tabla.
     * Requiere que el usuario haya seleccionado una fila.
     */
    @FXML
    private void onBtnBajaProfesor() {
        Profesor seleccionado = tblProfesores.getSelectionModel()
                .getSelectedItem();
        if (seleccionado == null) {
            return;
        }
        // TODO: confirmar y delegar al servicio de baja
    }

    /* ── Carga de datos ─────────────────────────────────────── */

    /**
     * Consulta el listado de profesores a través del servicio
     * y lo carga en la tabla. Se invoca cada vez que se abre esta
     * ventana, por lo que la tabla siempre refleja el estado más
     * reciente de la BD (p. ej. tras un alta reciente).
     */
    private void cargarProfesores() {
        profesores.clear();
        try {
            profesores.addAll(
                    administradorServicio.listarProfesores());
        } catch (SQLException e) {
            System.err.println(
                    "Error al cargar profesores: " + e.getMessage());
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error de conexión con la base de datos");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Error: no fue posible conectarse con la base "
                    + "de datos, inténtelo de nuevo ahora o en unos "
                    + "minutos");
            alerta.showAndWait();
        }
    }

}
