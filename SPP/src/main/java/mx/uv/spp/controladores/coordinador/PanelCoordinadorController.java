/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 30 de junio del 2026
 */
package mx.uv.spp.controladores.coordinador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import mx.uv.spp.util.Navegador;
import mx.uv.spp.util.SesionUsuario;

/**
 * Controlador del panel principal del Coordinador.
 * Gestiona la navegación entre las sub-vistas del menú lateral
 * y el cierre de sesión del Coordinador de Prácticas Profesionales.
 * No contiene lógica de negocio ni acceso directo a la BD.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PanelCoordinadorController implements Initializable {

    @FXML private StackPane contenedor;
    @FXML private VBox      panelBienvenida;

    private static final String VISTA_ORGANIZACIONES =
            "/mx/uv/spp/vistas/coordinador/organizaciones.fxml";
    private static final String VISTA_PROYECTOS =
            "/mx/uv/spp/vistas/coordinador/proyectos.fxml";
    private static final String VISTA_ESTUDIANTES =
            "/mx/uv/spp/vistas/coordinador/estudiantes.fxml";
    private static final String VISTA_PERIODOS =
            "/mx/uv/spp/vistas/coordinador/periodos.fxml";
    private static final String VISTA_GRUPOS =
            "/mx/uv/spp/vistas/coordinador/grupos.fxml";
    private static final String VISTA_DOCUMENTOS =
            "/mx/uv/spp/vistas/coordinador/documentos.fxml";

    /**
     * Inicializa el panel mostrando la vista de bienvenida.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
    }

    /* ── Manejadores de botones del menú ────────────────────── */

    /**
     * Carga la sub-vista de gestión de Organizaciones Vinculadas.
     */
    @FXML
    private void onBtnOrganizaciones() {
        cargarVista(VISTA_ORGANIZACIONES);
    }

    /**
     * Carga la sub-vista de gestión de Proyectos.
     */
    @FXML
    private void onBtnProyectos() {
        cargarVista(VISTA_PROYECTOS);
    }

    /**
     * Carga la sub-vista de gestión de Estudiantes.
     */
    @FXML
    private void onBtnEstudiantes() {
        cargarVista(VISTA_ESTUDIANTES);
    }

    /**
     * Carga la sub-vista de gestión de Periodos Escolares.
     */
    @FXML
    private void onBtnPeriodos() {
        cargarVista(VISTA_PERIODOS);
    }

    /**
     * Carga la sub-vista de gestión de Grupos.
     */
    @FXML
    private void onBtnGrupos() {
        cargarVista(VISTA_GRUPOS);
    }

    /**
     * Carga la sub-vista de gestión de Documentos Iniciales.
     */
    @FXML
    private void onBtnDocumentos() {
        cargarVista(VISTA_DOCUMENTOS);
    }

    /**
     * Limpia la sesión activa y vuelve a la pantalla de login.
     */
    @FXML
    private void onBtnCerrarSesion() {
        SesionUsuario.limpiar();
        Navegador.irALogin();
    }

    /* ── Carga de sub-vistas ────────────────────────────────── */

    /**
     * Carga el FXML indicado como contenido central del panel.
     * Oculta el panel de bienvenida y reemplaza el contenedor.
     *
     * @param rutaFxml Ruta del FXML en el classpath; no nula.
     */
    private void cargarVista(String rutaFxml) {
        try {
            FXMLLoader cargador = new FXMLLoader(
                    getClass().getResource(rutaFxml));
            if (cargador.getLocation() == null) {
                throw new IOException(
                        "FXML no encontrado: " + rutaFxml);
            }
            Parent raiz = cargador.load();
            panelBienvenida.setVisible(false);
            panelBienvenida.setManaged(false);
            contenedor.getChildren().setAll(raiz);
        } catch (IOException e) {
            System.err.println(
                    "Error al cargar vista en panel: "
                    + e.getMessage());
        }
    }

}
