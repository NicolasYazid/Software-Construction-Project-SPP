/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 30 de junio del 2026
 */
package mx.uv.spp.controladores.administrador;

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
 * Controlador del panel principal del Administrador
 * (PanelAdministrador.fxml). Gestiona la navegación entre las
 * sub-vistas del menú lateral y el cierre de sesión.
 * No contiene lógica de negocio ni acceso directo a la BD.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PanelAdministradorController implements Initializable {

    @FXML private StackPane contenedor;
    @FXML private VBox      panelBienvenida;

    private static final String VISTA_PROFESORES =
            "/mx/uv/spp/vistas/administrador/PanelProfesores.fxml";
    private static final String VISTA_COORDINADOR =
            "/mx/uv/spp/vistas/administrador/PanelSeccionCoordinador.fxml";

    /**
     * Inicializa el panel mostrando la vista de bienvenida.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
        // La vista de bienvenida ya está declarada en el FXML;
        // no se requiere acción adicional en la inicialización.
    }

    /* ── Manejadores de botones del menú ────────────────────── */

    /**
     * Restaura la vista de bienvenida del panel del Administrador.
     * Si ya se muestra la bienvenida, no realiza ninguna acción.
     */
    @FXML
    private void onBtnInicio() {
        contenedor.getChildren().clear();
        panelBienvenida.setVisible(true);
        panelBienvenida.setManaged(true);
    }

    /**
     * Carga la sub-vista de historial de personal académico.
     */
    @FXML
    private void onBtnHistorial() {
        // TODO: cargar vista de historial de Profesores y Coordinadores
    }

    /**
     * Navega a la ventana completa de gestión de Profesores.
     */
    @FXML
    private void onBtnProfesores() {
        Navegador.cambiarVista(VISTA_PROFESORES);
    }

    /**
     * Navega a la ventana completa de sección del Coordinador.
     */
    @FXML
    private void onBtnCoordinador() {
        Navegador.cambiarVista(VISTA_COORDINADOR);
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
