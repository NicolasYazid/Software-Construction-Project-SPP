/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.uv.spp.util.Navegador;

/**
 * Punto de entrada de la aplicación SPP.
 * Carga el FXML inicial (pantalla de login) y expone
 * {@link #cambiarVista(String)} para que los controladores
 * puedan navegar entre pantallas sin conocer el Stage.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class App extends Application {

    private static final String TITULO_APP =
            "Sistema de Prácticas Profesionales — SPP";
    private static final String FXML_LOGIN =
            "/mx/uv/spp/vistas/login.fxml";
    private static final int ANCHO_INICIAL = 960;
    private static final int ALTO_INICIAL = 600;

    private static Scene escena;

    /**
     * Inicializa y muestra la ventana principal con la pantalla
     * de login como vista inicial.
     *
     * @param escenario Stage primario provisto por JavaFX.
     * @throws IOException si el archivo FXML no puede cargarse.
     */
    @Override
    public void start(Stage escenario) throws IOException {
        Parent raiz = cargarFXML(FXML_LOGIN);
        escena = new Scene(raiz, ANCHO_INICIAL, ALTO_INICIAL);
        escenario.setTitle(TITULO_APP);
        escenario.setResizable(false);
        escenario.setScene(escena);
        Navegador.inicializar(escenario);
        escenario.show();
    }

    /**
     * Reemplaza la raíz de la escena para navegar entre pantallas.
     * Los controladores llaman a este método pasando la ruta absoluta
     * del FXML destino.
     *
     * @param rutaFxml Ruta absoluta del FXML en el classpath,
     * comenzando con {@code /mx/uv/spp/vistas/...}.
     * @throws IOException si el archivo FXML no se encuentra o
     * no puede parsearse.
     */
    public static void cambiarVista(String rutaFxml)
            throws IOException {
        escena.setRoot(cargarFXML(rutaFxml));
    }

    /**
     * Carga y retorna el nodo raíz del FXML indicado.
     *
     * @param rutaFxml Ruta absoluta desde la raíz del classpath.
     * @return nodo raíz listo para usarse como Scene root.
     * @throws IOException si el recurso no existe o tiene errores
     * de sintaxis FXML.
     */
    private static Parent cargarFXML(String rutaFxml)
            throws IOException {
        FXMLLoader cargador = new FXMLLoader(
                App.class.getResource(rutaFxml));
        if (cargador.getLocation() == null) {
            throw new IOException(
                    "FXML no encontrado en el classpath: "
                    + rutaFxml);
        }
        return cargador.load();
    }

    /**
     * Método principal; JavaFX llama a {@link #start(Stage)}
     * a través de {@code launch()}.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        launch();
    }

}
