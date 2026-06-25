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
import java.sql.Timestamp;
import mx.uv.spp.modelo.ResultadoAutenticacion;
import mx.uv.spp.modelo.TipoUsuario;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.UsuarioDAO;
import mx.uv.spp.util.CifradoAES;
import mx.uv.spp.util.Constantes;

/**
 * Implementación JDBC de {@link UsuarioDAO}.
 * Cada tipo de usuario reside en su propia tabla (administrador,
 * coordinador, profesor, estudiante). El método de login varía:
 * Estudiante usa matrícula cifrada; los demás usan correo cifrado.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String MENSAJE_CREDENCIALES =
            "Credenciales incorrectas.";

    /**
     * Busca al usuario en su tabla según el tipo, verifica la
     * contraseña comparando el texto plano con el valor descifrado
     * de la BD y carga los campos de seguridad SEG-01.
     *
     * @param identificador Correo o matrícula en texto plano.
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

        String sql = construirSqlAutenticar(tipo);

        try (Connection con =
                     ConexionBD.obtenerInstancia().obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, CifradoAES.cifrar(identificador));

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    resultado.setMensajeError(MENSAJE_CREDENCIALES);
                    return resultado;
                }

                poblarCamposSeguridad(resultado, rs);
                resultado.setNombreCompleto(
                        construirNombreCompleto(rs, tipo));

                String contrasenaBD =
                        CifradoAES.descifrar(
                                rs.getString("contrasena"));
                resultado.setExitoso(
                        contrasena.equals(contrasenaBD));

                if (!resultado.isExitoso()) {
                    resultado.setMensajeError(MENSAJE_CREDENCIALES);
                }
            }
        }
        return resultado;
    }

    /**
     * Incrementa el contador de intentos fallidos y, si alcanza el
     * máximo permitido, registra la marca de tiempo de bloqueo.
     *
     * @param idUsuario PK del usuario en su tabla.
     * @param tipo      Rol del usuario (determina la tabla a actualizar).
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    @Override
    public void incrementarIntentosFallidos(int idUsuario,
            TipoUsuario tipo) throws SQLException {
        String tabla = obtenerTabla(tipo);
        String pk    = obtenerPkColumna(tipo);

        try (Connection con =
                     ConexionBD.obtenerInstancia().obtenerConexion()) {

            String sqlInc = "UPDATE " + tabla
                    + " SET intentos_fallidos ="
                    + " intentos_fallidos + 1"
                    + " WHERE " + pk + " = ?";
            try (PreparedStatement ps =
                         con.prepareStatement(sqlInc)) {
                ps.setInt(1, idUsuario);
                ps.executeUpdate();
            }

            String sqlBlq = "UPDATE " + tabla
                    + " SET fecha_bloqueo = NOW()"
                    + " WHERE " + pk + " = ?"
                    + " AND intentos_fallidos >= ?"
                    + " AND fecha_bloqueo IS NULL";
            try (PreparedStatement ps =
                         con.prepareStatement(sqlBlq)) {
                ps.setInt(1, idUsuario);
                ps.setInt(2, Constantes.MAX_INTENTOS_LOGIN);
                ps.executeUpdate();
            }
        }
    }

    /**
     * Pone a cero el contador de intentos fallidos y elimina la
     * marca de bloqueo del usuario indicado.
     *
     * @param idUsuario PK del usuario en su tabla.
     * @param tipo      Rol del usuario (determina la tabla a actualizar).
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    @Override
    public void reiniciarIntentos(int idUsuario,
            TipoUsuario tipo) throws SQLException {
        String tabla = obtenerTabla(tipo);
        String pk    = obtenerPkColumna(tipo);

        String sql = "UPDATE " + tabla
                + " SET intentos_fallidos = 0,"
                + " fecha_bloqueo = NULL"
                + " WHERE " + pk + " = ?";

        try (Connection con =
                     ConexionBD.obtenerInstancia().obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    /**
     * Reemplaza la contraseña almacenada con el valor ya cifrado.
     *
     * @param idUsuario       PK del usuario en su tabla.
     * @param contrasenaCifrada Nuevo valor cifrado en Base64.
     * @param tipo            Rol del usuario (determina la tabla).
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    @Override
    public void actualizarContrasena(int idUsuario,
            String contrasenaCifrada,
            TipoUsuario tipo) throws SQLException {
        String tabla = obtenerTabla(tipo);
        String pk    = obtenerPkColumna(tipo);

        String sql = "UPDATE " + tabla
                + " SET contrasena = ?"
                + " WHERE " + pk + " = ?";

        try (Connection con =
                     ConexionBD.obtenerInstancia().obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, contrasenaCifrada);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    /* ── Métodos privados ───────────────────────────────────── */

    /**
     * Construye el SELECT adaptado a la tabla y columna de
     * identificador de cada tipo de usuario. La PK se proyecta
     * siempre como {@code id_usuario} para uniformar la lectura.
     *
     * @param tipo Rol del usuario.
     * @return SQL parametrizado con un único {@code ?}.
     * @throws IllegalArgumentException si el tipo no está mapeado.
     */
    private String construirSqlAutenticar(TipoUsuario tipo) {
        switch (tipo) {
            case ADMINISTRADOR:
                return "SELECT id_administrador AS id_usuario,"
                        + " nombre,"
                        + " NULL AS primer_apellido,"
                        + " NULL AS segundo_apellido,"
                        + " contrasena, estado,"
                        + " intentos_fallidos, fecha_bloqueo"
                        + " FROM administrador"
                        + " WHERE correo = ?";
            case COORDINADOR:
                return "SELECT id_coordinador AS id_usuario,"
                        + " nombre,"
                        + " primer_apellido, segundo_apellido,"
                        + " contrasena, estado,"
                        + " intentos_fallidos, fecha_bloqueo"
                        + " FROM coordinador"
                        + " WHERE correo = ?";
            case PROFESOR:
                return "SELECT id_profesor AS id_usuario,"
                        + " nombre,"
                        + " primer_apellido, segundo_apellido,"
                        + " contrasena, estado,"
                        + " intentos_fallidos, fecha_bloqueo"
                        + " FROM profesor"
                        + " WHERE correo = ?";
            case ESTUDIANTE:
                return "SELECT id_estudiante AS id_usuario,"
                        + " nombre,"
                        + " primer_apellido, segundo_apellido,"
                        + " contrasena, estado,"
                        + " intentos_fallidos, fecha_bloqueo"
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
            case ADMINISTRADOR: return "administrador";
            case COORDINADOR:   return "coordinador";
            case PROFESOR:      return "profesor";
            case ESTUDIANTE:    return "estudiante";
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
            case ADMINISTRADOR: return "id_administrador";
            case COORDINADOR:   return "id_coordinador";
            case PROFESOR:      return "id_profesor";
            case ESTUDIANTE:    return "id_estudiante";
            default:
                throw new IllegalArgumentException(
                        "TipoUsuario no mapeado: " + tipo);
        }
    }

    /**
     * Lee los campos de seguridad del ResultSet al ResultadoAutenticacion.
     *
     * @param resultado Objeto a poblar.
     * @param rs        Fila actual del ResultSet.
     * @throws SQLException si alguna columna no existe en el ResultSet.
     */
    private void poblarCamposSeguridad(ResultadoAutenticacion resultado,
            ResultSet rs) throws SQLException {
        resultado.setIdUsuario(rs.getInt("id_usuario"));
        resultado.setEstado(rs.getString("estado"));
        resultado.setIntentosFallidos(rs.getInt("intentos_fallidos"));
        Timestamp tsBloqueo = rs.getTimestamp("fecha_bloqueo");
        if (tsBloqueo != null) {
            resultado.setFechaBloqueo(tsBloqueo.toLocalDateTime());
        }
    }

    /**
     * Construye el nombre completo del usuario descifrando las columnas
     * de nombre y apellidos. El Administrador solo tiene {@code nombre};
     * los demás roles también tienen {@code primer_apellido} y
     * opcionalmente {@code segundo_apellido}.
     *
     * @param rs   Fila actual del ResultSet.
     * @param tipo Rol del usuario (determina qué columnas leer).
     * @return Nombre completo en texto plano.
     * @throws SQLException si alguna columna no existe en el ResultSet.
     */
    private String construirNombreCompleto(ResultSet rs,
            TipoUsuario tipo) throws SQLException {
        String nombre = CifradoAES.descifrar(rs.getString("nombre"));
        if (tipo == TipoUsuario.ADMINISTRADOR) {
            return nombre;
        }
        String apellidoP =
                CifradoAES.descifrar(rs.getString("primer_apellido"));
        String apellidoM = rs.getString("segundo_apellido");
        StringBuilder sb = new StringBuilder(nombre)
                .append(" ").append(apellidoP);
        if (apellidoM != null && !apellidoM.isEmpty()) {
            sb.append(" ")
              .append(CifradoAES.descifrar(apellidoM));
        }
        return sb.toString();
    }

}
