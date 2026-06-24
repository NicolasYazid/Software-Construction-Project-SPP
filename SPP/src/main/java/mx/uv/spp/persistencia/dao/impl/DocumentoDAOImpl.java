/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.persistencia.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import mx.uv.spp.modelo.Documento;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.DocumentoDAO;

/**
 * Implementación JDBC de {@link DocumentoDAO}.
 * Accede a la tabla {@code documento} con {@code PreparedStatement}.
 * El centinela -1.0 en {@code calificacion} se mapea a {@code NULL}
 * en la BD y viceversa; permite distinguir "sin calificar" de una
 * calificación real de 0.0.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class DocumentoDAOImpl implements DocumentoDAO {

    private static final String SQL_SELECT_CAMPOS =
            "SELECT id_documento, id_inscripcion,"
            + " id_tipo_evidencia, id_estado_documento,"
            + " ruta_archivo, nombre_archivo,"
            + " fecha_entrega, fecha_limite,"
            + " fecha_prorroga, observaciones, calificacion";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Documento> obtenerPorInscripcionYTipo(
            int idInscripcion, int idTipoEvidencia)
            throws SQLException {
        String sql = SQL_SELECT_CAMPOS
                + " FROM documento"
                + " WHERE id_inscripcion = ?"
                + " AND id_tipo_evidencia = ?";
        List<Documento> lista = new ArrayList<>();
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            ps.setInt(2, idTipoEvidencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Agrega {@code LIMIT 1} para eficiencia, dado que el CU
     * garantiza al más un registro por tipo e inscripción.
     */
    @Override
    public Documento obtenerPorInscripcionYTipoUnico(
            int idInscripcion, int idTipoEvidencia)
            throws SQLException {
        String sql = SQL_SELECT_CAMPOS
                + " FROM documento"
                + " WHERE id_inscripcion = ?"
                + " AND id_tipo_evidencia = ?"
                + " LIMIT 1";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            ps.setInt(2, idTipoEvidencia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Si {@code calificacion} en el POJO vale -1.0, se inserta
     * {@code NULL} en la BD (sentinela de "sin calificar").
     */
    @Override
    public int insertar(Documento documento) throws SQLException {
        String sql = "INSERT INTO documento"
                + " (id_inscripcion, id_tipo_evidencia,"
                + " id_estado_documento, ruta_archivo,"
                + " nombre_archivo, fecha_entrega,"
                + " fecha_limite, fecha_prorroga,"
                + " observaciones, calificacion)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, documento.getIdInscripcion());
            ps.setInt(2, documento.getIdTipoEvidencia());
            ps.setInt(3, documento.getIdEstadoDocumento());
            ps.setString(4, documento.getRutaArchivo());
            ps.setString(5, documento.getNombreArchivo());
            establecerTimestamp(ps, 6, documento.getFechaEntrega());
            establecerDate(ps, 7, documento.getFechaLimite());
            establecerDate(ps, 8, documento.getFechaProrroga());
            ps.setString(9, documento.getObservaciones());
            establecerCalificacion(ps, 10,
                    documento.getCalificacion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException(
                "No se generó clave primaria al insertar documento.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actualizarEntrega(Documento documento)
            throws SQLException {
        String sql = "UPDATE documento"
                + " SET ruta_archivo = ?, nombre_archivo = ?,"
                + " fecha_entrega = ?, id_estado_documento = ?"
                + " WHERE id_documento = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, documento.getRutaArchivo());
            ps.setString(2, documento.getNombreArchivo());
            establecerTimestamp(ps, 3, documento.getFechaEntrega());
            ps.setInt(4, documento.getIdEstadoDocumento());
            ps.setInt(5, documento.getIdDocumento());
            ps.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actualizarCalificacion(int idDocumento,
            double calificacion, int idEstadoDocumento)
            throws SQLException {
        String sql = "UPDATE documento"
                + " SET calificacion = ?,"
                + " id_estado_documento = ?"
                + " WHERE id_documento = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, calificacion);
            ps.setInt(2, idEstadoDocumento);
            ps.setInt(3, idDocumento);
            ps.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actualizarProrroga(int idDocumento,
            LocalDate fechaProrroga) throws SQLException {
        String sql = "UPDATE documento"
                + " SET fecha_prorroga = ?"
                + " WHERE id_documento = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            establecerDate(ps, 1, fechaProrroga);
            ps.setInt(2, idDocumento);
            ps.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Filtra por estado 'Entregado' usando JOIN con
     * {@code estado_documento} para evitar valores mágicos de ID.
     * Considera sin calificar los registros con
     * {@code calificacion IS NULL}.
     */
    @Override
    public List<Documento> obtenerEntregadosSinCalificarPorProfesor(
            int idProfesor) throws SQLException {
        String sql = "SELECT d.id_documento, d.id_inscripcion,"
                + " d.id_tipo_evidencia, d.id_estado_documento,"
                + " d.ruta_archivo, d.nombre_archivo,"
                + " d.fecha_entrega, d.fecha_limite,"
                + " d.fecha_prorroga, d.observaciones,"
                + " d.calificacion"
                + " FROM documento d"
                + " JOIN estudiante_inscrito ei"
                + " ON d.id_inscripcion = ei.id_inscripcion"
                + " JOIN estado_documento ed"
                + " ON d.id_estado_documento"
                + "  = ed.id_estado_documento"
                + " WHERE ei.id_profesor = ?"
                + " AND ed.nombre = 'Entregado'"
                + " AND d.calificacion IS NULL";
        List<Documento> lista = new ArrayList<>();
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProfesor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Construye un {@link Documento} desde la fila actual del
     * {@code ResultSet}. Convierte {@code NULL} en la columna
     * {@code calificacion} al centinela -1.0 del POJO.
     *
     * @param rs ResultSet posicionado en la fila a mapear.
     * @return instancia de {@link Documento} con todos los campos.
     * @throws SQLException si alguna columna no existe en el RS.
     */
    private Documento mapearResultSet(ResultSet rs)
            throws SQLException {
        Documento doc = new Documento();
        doc.setIdDocumento(rs.getInt("id_documento"));
        doc.setIdInscripcion(rs.getInt("id_inscripcion"));
        doc.setIdTipoEvidencia(rs.getInt("id_tipo_evidencia"));
        doc.setIdEstadoDocumento(rs.getInt("id_estado_documento"));
        doc.setRutaArchivo(rs.getString("ruta_archivo"));
        doc.setNombreArchivo(rs.getString("nombre_archivo"));

        Timestamp tsEntrega = rs.getTimestamp("fecha_entrega");
        doc.setFechaEntrega(tsEntrega != null
                ? tsEntrega.toLocalDateTime() : null);

        Date sqlLimite = rs.getDate("fecha_limite");
        doc.setFechaLimite(sqlLimite != null
                ? sqlLimite.toLocalDate() : null);

        Date sqlProrroga = rs.getDate("fecha_prorroga");
        doc.setFechaProrroga(sqlProrroga != null
                ? sqlProrroga.toLocalDate() : null);

        doc.setObservaciones(rs.getString("observaciones"));

        double cal = rs.getDouble("calificacion");
        doc.setCalificacion(rs.wasNull() ? -1.0 : cal);

        return doc;
    }

    /**
     * Establece un parámetro {@code TIMESTAMP} en el
     * {@code PreparedStatement}, o {@code NULL} si el valor es nulo.
     *
     * @param ps    PreparedStatement destino.
     * @param indice Posición del parámetro (1-based).
     * @param valor  Valor {@link LocalDateTime} o {@code null}.
     * @throws SQLException si ocurre un error al establecer el param.
     */
    private void establecerTimestamp(PreparedStatement ps,
            int indice, LocalDateTime valor) throws SQLException {
        if (valor != null) {
            ps.setTimestamp(indice, Timestamp.valueOf(valor));
        } else {
            ps.setNull(indice, Types.TIMESTAMP);
        }
    }

    /**
     * Establece un parámetro {@code DATE} en el
     * {@code PreparedStatement}, o {@code NULL} si el valor es nulo.
     *
     * @param ps    PreparedStatement destino.
     * @param indice Posición del parámetro (1-based).
     * @param valor  Valor {@link LocalDate} o {@code null}.
     * @throws SQLException si ocurre un error al establecer el param.
     */
    private void establecerDate(PreparedStatement ps,
            int indice, LocalDate valor) throws SQLException {
        if (valor != null) {
            ps.setDate(indice, Date.valueOf(valor));
        } else {
            ps.setNull(indice, Types.DATE);
        }
    }

    /**
     * Establece el parámetro de calificación. Si el valor es
     * negativo (centinela -1.0), inserta {@code NULL}.
     *
     * @param ps      PreparedStatement destino.
     * @param indice  Posición del parámetro (1-based).
     * @param valor   Calificación real ≥ 0.0, o -1.0 para NULL.
     * @throws SQLException si ocurre un error al establecer el param.
     */
    private void establecerCalificacion(PreparedStatement ps,
            int indice, double valor) throws SQLException {
        if (valor < 0) {
            ps.setNull(indice, Types.DECIMAL);
        } else {
            ps.setDouble(indice, valor);
        }
    }

}
