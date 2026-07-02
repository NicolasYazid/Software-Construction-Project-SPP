/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestiona la conexión JDBC a la base de datos MySQL del sistema SPP.
 * Implementa el patrón Singleton para reutilizar una sola instancia
 * durante la sesión de la aplicación. Los parámetros de conexión se
 * leen de un archivo externo para no exponer credenciales en el código.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public final class ConexionBD {

    private static final String RUTA_CONFIG = "/bd.properties";

    private static ConexionBD instancia;

    private Connection conexion;
    private String url;
    private String usuario;
    private String contrasena;

    /**
     * Constructor privado que carga los parámetros desde el archivo
     * de propiedades. Es fatal si el archivo no existe o está incompleto,
     * ya que sin base de datos el sistema no puede operar.
     *
     * @throws RuntimeException si el archivo de configuración no se
     * encuentra en el classpath o le faltan propiedades requeridas.
     */
    private ConexionBD() {
        try {
            cargarPropiedades();
        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo cargar la configuración de la BD: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Devuelve la única instancia de {@code ConexionBD}.
     * Usa doble verificación para garantizar hilo-seguridad en el
     * arranque de la aplicación.
     *
     * @return instancia única de esta clase.
     */
    public static synchronized ConexionBD obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Entrega una conexión activa a la base de datos.
     * Si la conexión previa fue cerrada o nunca se creó, la establece.
     *
     * @return objeto {@link Connection} listo para ejecutar sentencias SQL.
     * @throws SQLException si el driver no puede establecer la conexión
     * con los parámetros provistos en el archivo de configuración.
     */
    public Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, contrasena);
        }
        return conexion;
    }

    /**
     * Cierra la conexión activa con la base de datos.
     * Debe invocarse al terminar la sesión o antes de apagar la aplicación.
     */
    public void cerrarConexion() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                System.err.println(
                        "Error al cerrar la conexión con la BD: "
                        + e.getMessage());
            }
        }
    }

    /**
     * Lee los parámetros de conexión desde el archivo de propiedades
     * ubicado en la raíz del classpath.
     *
     * @throws IOException si el archivo no se encuentra o no puede leerse,
     * o si le falta alguna de las tres propiedades requeridas.
     */
    private void cargarPropiedades() throws IOException {
        Properties propiedades = new Properties();

        try (InputStream flujo =
                ConexionBD.class.getResourceAsStream(RUTA_CONFIG)) {

            if (flujo == null) {
                throw new IOException(
                        "Archivo no encontrado en el classpath: "
                        + RUTA_CONFIG);
            }
            propiedades.load(flujo);
        }

        url = propiedades.getProperty("bd.url");
        usuario = propiedades.getProperty("bd.usuario");
        contrasena = propiedades.getProperty("bd.contrasena");

        if (url == null || usuario == null || contrasena == null) {
            throw new IOException(
                    "Configuración incompleta: se requieren bd.url, "
                    + "bd.usuario y bd.contrasena en " + RUTA_CONFIG);
        }
    }

}
