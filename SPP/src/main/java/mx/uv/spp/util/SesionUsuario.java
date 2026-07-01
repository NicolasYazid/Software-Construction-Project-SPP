/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de junio del 2026
 */
package mx.uv.spp.util;

import mx.uv.spp.modelo.TipoUsuario;

/**
 * Almacena los datos del usuario autenticado durante la sesión activa.
 * Es el único punto donde el controlador de login deposita el contexto
 * de usuario para que los controladores de panel lo lean sin acoplarse
 * entre sí. Al cerrar sesión, {@link #limpiar()} restablece todos los
 * campos a sus valores vacíos.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public final class SesionUsuario {

    private static int idUsuario;
    private static String nombreCompleto = "";
    private static TipoUsuario tipo;
    private static int idInscripcion;

    private SesionUsuario() {
    }

    /**
     * Inicializa la sesión con los datos básicos del usuario
     * autenticado. Debe llamarse inmediatamente después de un
     * login exitoso y antes de navegar al panel principal.
     *
     * @param idUsuario      Clave primaria en la tabla del tipo.
     * @param nombreCompleto Nombre descifrado para mostrar en la UI.
     * @param tipo           Rol del usuario autenticado.
     */
    public static void inicializar(int idUsuario,
            String nombreCompleto, TipoUsuario tipo) {
        SesionUsuario.idUsuario = idUsuario;
        SesionUsuario.nombreCompleto =
                nombreCompleto != null ? nombreCompleto : "";
        SesionUsuario.tipo = tipo;
        SesionUsuario.idInscripcion = 0;
    }

    /**
     * Retorna el identificador primario del usuario en su tabla de BD.
     *
     * @return id del usuario autenticado; {@code 0} si no hay sesión.
     */
    public static int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Retorna el nombre completo descifrado del usuario.
     *
     * @return nombre para mostrar en encabezados; cadena vacía si no
     *         hay sesión activa.
     */
    public static String getNombreCompleto() {
        return nombreCompleto;
    }

    /**
     * Retorna el rol del usuario autenticado.
     *
     * @return {@link TipoUsuario} del usuario activo, o {@code null}
     *         si no hay sesión.
     */
    public static TipoUsuario getTipo() {
        return tipo;
    }

    /**
     * Retorna el id de inscripción del Estudiante en el ciclo activo.
     * Solo es válido cuando {@code tipo == TipoUsuario.ESTUDIANTE} y
     * después de que {@link #setIdInscripcion(int)} fue invocado.
     *
     * @return id_inscripcion en la tabla {@code estudiante_inscrito};
     *         {@code 0} si no aplica o aún no se ha establecido.
     */
    public static int getIdInscripcion() {
        return idInscripcion;
    }

    /**
     * Establece el id de inscripción del Estudiante para el ciclo
     * activo. Debe llamarse tras {@link #inicializar(int, String,
     * TipoUsuario)} cuando el tipo es ESTUDIANTE.
     *
     * @param idInscripcion Clave primaria en {@code estudiante_inscrito}.
     */
    public static void setIdInscripcion(int idInscripcion) {
        SesionUsuario.idInscripcion = idInscripcion;
    }

    /**
     * Borra todos los datos de la sesión activa.
     * Debe llamarse al cerrar sesión para evitar que datos del usuario
     * anterior persistan si se inicia otra sesión en la misma ejecución.
     */
    public static void limpiar() {
        idUsuario = 0;
        nombreCompleto = "";
        tipo = null;
        idInscripcion = 0;
    }

}
