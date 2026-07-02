/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.persistencia.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import mx.uv.spp.modelo.ResultadoAutenticacion;
import mx.uv.spp.modelo.TipoUsuario;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.UsuarioDAO;
import mx.uv.spp.util.CifradoAES;
import mx.uv.spp.util.Constantes;

/**
 * Implementación JDBC de {@link UsuarioDAO} para la base de datos
 * spp_db. Las contraseñas se cifran con AES-128-CBC (SEG-04) antes
 * de compararse o persistirse; ver {@link CifradoAES}.
 * El rol Coordinador se identifica por la columna {@code coordinador=TRUE}
 * en la tabla {@code profesor}. No existen columnas
 * {@code intentos_fallidos} ni {@code fecha_bloqueo} en spp_db;
 * el control SEG-01 se gestiona en memoria dentro de
 * {@code LoginServicio}.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String MENSAJE_CREDENCIALES =
            "Credenciales incorrectas.";

    /**
     * Busca al usuario en su tabla según el tipo, descifra la
     * contraseña almacenada con AES-128-CBC y la compara contra la
     * ingresada en texto plano.
     *
     * @param identificador Correo, matrícula o usuario en texto plano.
     * @param contrasena    Contraseña en texto plano.
     * @param tipo          Rol del usuario.
     * @return Resultado nunca nulo; {@code idUsuario == 0} indica
     *         que el identificador no existe en la BD.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    @Override
    public ResultadoAutenticacion autenticar(String identificador,
            String contrasena, TipoUsuario tipo) throws SQLException {

        ResultadoAutenticacion resultado = new ResultadoAutenticacion();
        resultado.setTipo(tipo);

        String sqlAutenticar = construirSqlAutenticar(tipo);

        try (Connection con =
                     ConexionBD.obtenerInstancia().obtenerConexion();
             PreparedStatement psAutenticar =
                     con.prepareStatement(sqlAutenticar)) {

            psAutenticar.setString(1, identificador);

            try (ResultSet rsUsuario = psAutenticar.executeQuery()) {
                if (!rsUsuario.next()) {
                    resultado.setMensajeError(MENSAJE_CREDENCIALES);
                    return resultado;
                }

                resultado.setIdUsuario(
                        rsUsuario.getInt("id_usuario"));
                resultado.setEstado(rsUsuario.getString("estado"));
                resultado.setIntentosFallidos(
                        rsUsuario.getInt("intentos_fallidos"));
                java.sql.Timestamp fechaBloqueoBD =
                        rsUsuario.getTimestamp("fecha_bloqueo");
                resultado.setFechaBloqueo(
                        fechaBloqueoBD != null
                        ? fechaBloqueoBD.toLocalDateTime() : null);
                resultado.setNombreCompleto(
                        construirNombreCompleto(rsUsuario, tipo));

                String contrasenaBD =
                        rsUsuario.getString("contrasenia");
                String contrasenaBDDescifrada =
                        CifradoAES.descifrar(contrasenaBD);
                resultado.setExitoso(
                        contrasena.equals(contrasenaBDDescifrada));

                if (!resultado.isExitoso()) {
                    resultado.setMensajeError(MENSAJE_CREDENCIALES);
                }
            }
        }
        return resultado;
    }

    /**
     * Incrementa {@code intentos_fallidos} en 1 y registra
     * {@code fecha_bloqueo} cuando se alcanza el máximo de intentos.
     *
     * @param idUsuario PK del usuario en su tabla.
     * @param tipo      Rol del usuario (determina la tabla y PK).
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    @Override
    public void incrementarIntentosFallidos(int idUsuario,
            TipoUsuario tipo) throws SQLException {
        String tabla = obtenerTabla(tipo);
        String columnaPk = obtenerPkColumna(tipo);
        String sqlIncrementarIntentos = "UPDATE " + tabla
                + " SET intentos_fallidos = intentos_fallidos + 1,"
                + " fecha_bloqueo = CASE"
                + " WHEN intentos_fallidos + 1 >= ?"
                + " THEN NOW() ELSE NULL END"
                + " WHERE " + columnaPk + " = ?";
        try (Connection con =
                     ConexionBD.obtenerInstancia().obtenerConexion();
             PreparedStatement psIncrementarIntentos =
                     con.prepareStatement(sqlIncrementarIntentos)) {
            psIncrementarIntentos.setInt(
                    1, Constantes.MAX_INTENTOS_LOGIN);
            psIncrementarIntentos.setInt(2, idUsuario);
            psIncrementarIntentos.executeUpdate();
        }
    }

    /**
     * Reinicia {@code intentos_fallidos} a 0 y limpia
     * {@code fecha_bloqueo} tras un login exitoso o bloqueo expirado.
     *
     * @param idUsuario PK del usuario en su tabla.
     * @param tipo      Rol del usuario (determina la tabla y PK).
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    @Override
    public void reiniciarIntentos(int idUsuario,
            TipoUsuario tipo) throws SQLException {
        String tabla = obtenerTabla(tipo);
        String columnaPk = obtenerPkColumna(tipo);
        String sqlReiniciarIntentos = "UPDATE " + tabla
                + " SET intentos_fallidos = 0,"
                + " fecha_bloqueo = NULL"
                + " WHERE " + columnaPk + " = ?";
        try (Connection con =
                     ConexionBD.obtenerInstancia().obtenerConexion();
             PreparedStatement psReiniciarIntentos =
                     con.prepareStatement(sqlReiniciarIntentos)) {
            psReiniciarIntentos.setInt(1, idUsuario);
            psReiniciarIntentos.executeUpdate();
        }
    }

    /**
     * Actualiza la contraseña del usuario en su tabla.
     *
     * @param idUsuario         PK del usuario en su tabla.
     * @param contrasenaCifrada Nuevo valor de contraseña.
     * @param tipo              Rol del usuario (determina la tabla).
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    @Override
    public void actualizarContrasena(int idUsuario,
            String contrasenaCifrada,
            TipoUsuario tipo) throws SQLException {
        String tabla = obtenerTabla(tipo);
        String columnaPk = obtenerPkColumna(tipo);

        String sqlActualizarContrasena = "UPDATE " + tabla
                + " SET contrasenia = ?"
                + " WHERE " + columnaPk + " = ?";

        try (Connection con =
                     ConexionBD.obtenerInstancia().obtenerConexion();
             PreparedStatement psActualizarContrasena =
                     con.prepareStatement(sqlActualizarContrasena)) {
            psActualizarContrasena.setString(1, contrasenaCifrada);
            psActualizarContrasena.setInt(2, idUsuario);
            psActualizarContrasena.executeUpdate();
        }
    }

    // Métodos privados

    /**
     * Construye el SELECT adaptado a la tabla y columna identificadora
     * de cada tipo de usuario. El Administrador usa {@code usuario};
     * Coordinador y Profesor usan {@code correo_institucional};
     * Estudiante usa {@code matricula}.
     *
     * @param tipo Rol del usuario.
     * @return SQL parametrizado con un único {@code ?}.
     * @throws IllegalArgumentException si el tipo no está mapeado.
     */
    private String construirSqlAutenticar(TipoUsuario tipo) {
        switch (tipo) {
            case ADMINISTRADOR:
                return "SELECT id AS id_usuario,"
                        + " usuario AS nombre,"
                        + " NULL AS apellido_paterno,"
                        + " NULL AS apellido_materno,"
                        + " contrasenia,"
                        + " 'activo' AS estado,"
                        + " intentos_fallidos,"
                        + " fecha_bloqueo"
                        + " FROM administrador"
                        + " WHERE usuario = ?";
            case COORDINADOR:
                return "SELECT id AS id_usuario,"
                        + " nombre,"
                        + " apellido_paterno,"
                        + " apellido_materno,"
                        + " contrasenia, estado,"
                        + " intentos_fallidos,"
                        + " fecha_bloqueo"
                        + " FROM profesor"
                        + " WHERE correo_institucional = ?"
                        + " AND coordinador = TRUE";
            case PROFESOR:
                return "SELECT id AS id_usuario,"
                        + " nombre,"
                        + " apellido_paterno,"
                        + " apellido_materno,"
                        + " contrasenia, estado,"
                        + " intentos_fallidos,"
                        + " fecha_bloqueo"
                        + " FROM profesor"
                        + " WHERE correo_institucional = ?"
                        + " AND coordinador = FALSE";
            case ESTUDIANTE:
                return "SELECT id AS id_usuario,"
                        + " nombre,"
                        + " apellido_paterno,"
                        + " apellido_materno,"
                        + " contrasenia, estado,"
                        + " intentos_fallidos,"
                        + " fecha_bloqueo"
                        + " FROM estudiante"
                        + " WHERE matricula = ?";
            default:
                throw new IllegalArgumentException(
                        "TipoUsuario no mapeado: " + tipo);
        }
    }

    /**
     * Retorna el nombre de la tabla que corresponde al tipo de usuario.
     *
     * @param tipo Rol del usuario.
     * @return Nombre literal de la tabla en la BD.
     * @throws IllegalArgumentException si el tipo no está mapeado.
     */
    private String obtenerTabla(TipoUsuario tipo) {
        switch (tipo) {
            case ADMINISTRADOR:
                return "administrador";
            case COORDINADOR:
            case PROFESOR:
                return "profesor";
            case ESTUDIANTE:
                return "estudiante";
            default:
                throw new IllegalArgumentException(
                        "TipoUsuario no mapeado: " + tipo);
        }
    }

    /**
     * Retorna el nombre de la columna PK de la tabla del tipo dado.
     *
     * @param tipo Rol del usuario.
     * @return Nombre de la columna clave primaria.
     * @throws IllegalArgumentException si el tipo no está mapeado.
     */
    private String obtenerPkColumna(TipoUsuario tipo) {
        switch (tipo) {
            case ADMINISTRADOR:
            case COORDINADOR:
            case PROFESOR:
            case ESTUDIANTE:
                return "id";
            default:
                throw new IllegalArgumentException(
                        "TipoUsuario no mapeado: " + tipo);
        }
    }

    /**
     * Construye el nombre completo leyendo las columnas {@code nombre},
     * {@code apellido_paterno} y {@code apellido_materno}. El
     * Administrador solo tiene la columna {@code nombre} (proyectada
     * como alias).
     *
     * @param rsUsuario Fila actual del ResultSet.
     * @param tipo Rol del usuario.
     * @return Nombre completo en texto plano.
     * @throws SQLException si alguna columna no existe en el ResultSet.
     */
    private String construirNombreCompleto(ResultSet rsUsuario,
            TipoUsuario tipo) throws SQLException {
        String nombre = rsUsuario.getString("nombre");
        if (nombre == null) {
            nombre = "";
        }
        if (tipo == TipoUsuario.ADMINISTRADOR) {
            return nombre;
        }
        String apellidoPaterno =
                rsUsuario.getString("apellido_paterno");
        String apellidoMaterno =
                rsUsuario.getString("apellido_materno");
        StringBuilder sb = new StringBuilder(nombre);
        if (apellidoPaterno != null && !apellidoPaterno.isEmpty()) {
            sb.append(" ").append(apellidoPaterno);
        }
        if (apellidoMaterno != null && !apellidoMaterno.isEmpty()) {
            sb.append(" ").append(apellidoMaterno);
        }
        return sb.toString();
    }

}
