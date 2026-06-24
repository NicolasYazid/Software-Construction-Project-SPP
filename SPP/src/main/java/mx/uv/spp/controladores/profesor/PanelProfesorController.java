/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.controladores.profesor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import mx.uv.spp.util.Navegador;

/**
 * Controlador del panel principal del Profesor Asesor
 * (panel_profesor.fxml). Gestiona la navegación entre
 * las sub-vistas de su menú lateral y el cierre de sesión.
 * No contiene lógica de negocio ni acceso directo a la BD.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PanelProfesorController implements Initializable {

    @FXML private StackPane contenedor;

    /**
     * Inicializa el controlador tras cargar el FXML.
     * Los botones del menú se habilitarán individualmente
     * conforme se implementen sus sub-vistas.
     *
     * @param ubicacion URL del FXML cargado (no se usa).
     * @param recursos  Paquete de i18n (no aplicado aún).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
    }

    /**
     * Carga el FXML indicado como contenido central del panel.
     * Reemplaza cualquier vista previa que hubiera en el contenedor.
     *
     * @param rutaFxml Ruta absoluta del FXML en el classpath;
     *                 no puede ser nula ni vacía.
     * @throws IllegalArgumentException si {@code rutaFxml} es nula
     *         o vacía.
     */
    public void cargarVista(String rutaFxml) {
        if (rutaFxml == null || rutaFxml.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La ruta del FXML no puede ser nula ni vacía.");
        }
        try {
            FXMLLoader cargador = new FXMLLoader(
                    getClass().getResource(rutaFxml));
            if (cargador.getLocation() == null) {
                throw new IOException(
                        "FXML no encontrado: " + rutaFxml);
            }
            Parent raiz = cargador.load();
            contenedor.getChildren().setAll(raiz);
        } catch (IOException e) {
            System.err.println(
                    "Error al cargar vista en panel profesor: "
                    + e.getMessage());
        }
    }

    /**
     * Maneja el clic en el botón "Cerrar sesión".
     * Vuelve a la pantalla de inicio de sesión a través del
     * centralizador de navegación.
     */
    @FXML
    private void onBtnCerrarSesion() {
        Navegador.irALogin();
    }

}
