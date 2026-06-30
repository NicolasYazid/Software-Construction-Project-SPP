/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 30 de junio del 2026
 */
package mx.uv.spp.controladores.administrador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * Controlador base del panel del Administrador (PanelAdministrador.fxml).
 * La navegación principal del Administrador se delega a
 * {@link PanelCoordinador2Controller}, que contiene las sub-vistas
 * de gestión de Profesores y Coordinador.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PanelAdministradorController implements Initializable {

    /**
     * Inicializa el controlador. Sin acciones requeridas al arrancar.
     *
     * @param ubicacion URL del FXML (no usado).
     * @param recursos  Paquete de i18n (no usado).
     */
    @Override
    public void initialize(URL ubicacion, ResourceBundle recursos) {
    }

}
