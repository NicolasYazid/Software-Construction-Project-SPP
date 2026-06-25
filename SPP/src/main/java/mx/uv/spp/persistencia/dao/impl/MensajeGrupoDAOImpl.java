/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.persistencia.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import mx.uv.spp.modelo.MensajeGrupo;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.MensajeGrupoDAO;

/**
 * Implementación JDBC de {@link MensajeGrupoDAO}.
 * Accede a la tabla {@code mensaje_grupo}. El campo
 * {@code fecha_publicacion} tiene DEFAULT CURRENT_TIMESTAMP en la
 * BD, pero se persiste explícitamente desde Java para permitir
 * consistencia en pruebas. Los campos {@code asunto}, {@code texto},
 * {@code ruta_archivo} y {@code nombre_archivo} son opcionales
 * (aceptan NULL).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class MensajeGrupoDAOImpl implements MensajeGrupoDAO {

    /**
     * {@inheritDoc}
     *
     * <p>Si {@code fechaPublicacion} en el POJO es {@code null},
     * se usa {@code LocalDateTime.now()} para mantener control
     * desde la capa Java.
     */
    @Override
    public int insertar(MensajeGrupo mensaje) throws SQLException {
        String sql = "INSERT INTO mensaje_grupo"
                + " (id_grupo, id_profesor, asunto, texto,"
                + " ruta_archivo, nombre_archivo,"
                + " fecha_publicacion)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, mensaje.getIdGrupo());
            ps.setInt(2, mensaje.getIdProfesor());
            ps.setString(3, mensaje.getAsunto());
            ps.setString(4, mensaje.getTexto());
            ps.setString(5, mensaje.getRutaArchivo());
            ps.setString(6, mensaje.getNombreArchivo());
            LocalDateTime fp = mensaje.getFechaPublicacion();
            if (fp != null) {
                ps.setTimestamp(7, Timestamp.valueOf(fp));
            } else {
                ps.setTimestamp(7,
                        Timestamp.valueOf(LocalDateTime.now()));
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException(
                "No se generó clave primaria al insertar"
                + " mensaje_grupo.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MensajeGrupo> obtenerPorGrupo(
            int idGrupo) throws SQLException {
        String sql = "SELECT id_mensaje_grupo, id_grupo,"
                + " id_profesor, asunto, texto,"
                + " ruta_archivo, nombre_archivo,"
                + " fecha_publicacion"
                + " FROM mensaje_grupo"
                + " WHERE id_grupo = ?"
                + " ORDER BY fecha_publicacion DESC";
        List<MensajeGrupo> lista = new ArrayList<>();
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Construye un {@link MensajeGrupo} desde la fila actual del
     * {@code ResultSet}. Los campos {@code asunto}, {@code texto},
     * {@code ruta_archivo} y {@code nombre_archivo} pueden ser
     * {@code null}.
     *
     * @param rs ResultSet posicionado en la fila a mapear.
     * @return instancia de {@link MensajeGrupo} con todos los campos.
     * @throws SQLException si alguna columna no existe en el RS.
     */
    private MensajeGrupo mapearResultSet(ResultSet rs)
            throws SQLException {
        MensajeGrupo mg = new MensajeGrupo();
        mg.setIdMensajeGrupo(rs.getInt("id_mensaje_grupo"));
        mg.setIdGrupo(rs.getInt("id_grupo"));
        mg.setIdProfesor(rs.getInt("id_profesor"));
        mg.setAsunto(rs.getString("asunto"));
        mg.setTexto(rs.getString("texto"));
        mg.setRutaArchivo(rs.getString("ruta_archivo"));
        mg.setNombreArchivo(rs.getString("nombre_archivo"));
        Timestamp ts = rs.getTimestamp("fecha_publicacion");
        mg.setFechaPublicacion(ts != null
                ? ts.toLocalDateTime() : null);
        return mg;
    }

}
