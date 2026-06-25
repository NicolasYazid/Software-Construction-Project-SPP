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
 * Adaptado a la arquitectura de herencia (Opción 1) que unifica a todos
 * los actores en la tabla base 'usuario' del esquema spp_bd.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String MENSAJE_CREDENCIALES = "Credenciales incorrectas.";

    @Override
    public ResultadoAutenticacion autenticar(String identificador,
                                             String contrasena, TipoUsuario tipo) throws SQLException {

        // La consulta busca en la tabla unificada.
        // Estudiantes usan matrícula, el resto correo.
        String sql = "SELECT id_usuario, nombre, apellido_paterno, apellido_materno, "
                + "contrasenia, activo, intentos_fallidos, fecha_bloqueo "
                + "FROM usuario WHERE rol = ? AND ";

        if (tipo == TipoUsuario.ESTUDIANTE) {
            sql += "matricula = ?";
        } else {
            sql += "correo = ?";
        }

        ResultadoAutenticacion resultado = new ResultadoAutenticacion();
        resultado.setTipo(tipo);

        try (Connection con = ConexionBD.obtenerInstancia().obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, obtenerRolString(tipo));
            ps.setString(2, CifradoAES.cifrar(identificador));

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    resultado.setMensajeError(MENSAJE_CREDENCIALES);
                    return resultado;
                }

                poblarCamposSeguridad(resultado, rs);
                resultado.setNombreCompleto(construirNombreCompleto(rs));

                // Nota: La BD actual usa 'contrasenia', no 'contrasena'
                String contrasenaBD = CifradoAES.descifrar(rs.getString("contrasenia"));
                resultado.setExitoso(contrasena.equals(contrasenaBD));

                if (!resultado.isExitoso()) {
                    resultado.setMensajeError(MENSAJE_CREDENCIALES);
                }
            }
        }
        return resultado;
    }

    @Override
    public void incrementarIntentosFallidos(int idUsuario, TipoUsuario tipo) throws SQLException {
        try (Connection con = ConexionBD.obtenerInstancia().obtenerConexion()) {
            String sqlIncrementar = "UPDATE usuario SET intentos_fallidos = intentos_fallidos + 1 WHERE id_usuario = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlIncrementar)) {
                ps.setInt(1, idUsuario);
                ps.executeUpdate();
            }

            String sqlBloqueo = "UPDATE usuario SET fecha_bloqueo = NOW() "
                    + "WHERE id_usuario = ? AND intentos_fallidos >= ? AND fecha_bloqueo IS NULL";
            try (PreparedStatement ps = con.prepareStatement(sqlBloqueo)) {
                ps.setInt(1, idUsuario);
                ps.setInt(2, Constantes.MAX_INTENTOS_LOGIN);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void reiniciarIntentos(int idUsuario, TipoUsuario tipo) throws SQLException {
        String sql = "UPDATE usuario SET intentos_fallidos = 0, fecha_bloqueo = NULL WHERE id_usuario = ?";
        try (Connection con = ConexionBD.obtenerInstancia().obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizarContrasena(int idUsuario, String contrasenaCifrada, TipoUsuario tipo) throws SQLException {
        String sql = "UPDATE usuario SET contrasenia = ? WHERE id_usuario = ?";
        try (Connection con = ConexionBD.obtenerInstancia().obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, contrasenaCifrada);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    /* ── Utilidades privadas ─────────────────────────────────── */

    private void poblarCamposSeguridad(ResultadoAutenticacion resultado, ResultSet rs) throws SQLException {
        resultado.setIdUsuario(rs.getInt("id_usuario"));

        boolean activo = rs.getBoolean("activo");
        resultado.setEstado(activo ? "Activo" : "Inactivo");

        resultado.setIntentosFallidos(rs.getInt("intentos_fallidos"));
        Timestamp tsBloqueo = rs.getTimestamp("fecha_bloqueo");
        if (tsBloqueo != null) {
            resultado.setFechaBloqueo(tsBloqueo.toLocalDateTime());
        }
    }


    private String construirNombreCompleto(ResultSet rs) throws SQLException {
        String nombre = CifradoAES.descifrar(rs.getString("nombre"));
        String apellidoP = CifradoAES.descifrar(rs.getString("apellido_paterno"));
        String apellidoM = rs.getString("apellido_materno"); // Puede ser nulo

        StringBuilder sb = new StringBuilder(nombre).append(" ").append(apellidoP);
        if (apellidoM != null && !apellidoM.isEmpty()) {
            sb.append(" ").append(CifradoAES.descifrar(apellidoM));
        }
        return sb.toString();
    }

    private String obtenerRolString(TipoUsuario tipo) {
        switch (tipo) {
            case ADMINISTRADOR: return "administrador";
            case COORDINADOR:   return "coordinador";
            case PROFESOR:      return "profesor";
            case ESTUDIANTE:    return "estudiante";
            default:
                throw new IllegalArgumentException("TipoUsuario no mapeado: " + tipo);
        }
    }
}