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
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import mx.uv.spp.modelo.Documento;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.DocumentoDAO;
import mx.uv.spp.util.Constantes;

/**
 * Implementación JDBC de {@link DocumentoDAO} para spp_db.
 * En spp_db la tabla {@code documento} no existe; las entregas se
 * almacenan en la tabla {@code entrega} junto con el catálogo
 * {@code entregable}. Este DAO traduce entre el POJO {@link Documento}
 * (que usa IDs enteros para el estado) y los ENUM string de spp_db.
 *
 * <p>El centinela -1.0 en {@code calificacion} se mapea a {@code NULL}
 * en la BD y viceversa.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class DocumentoDAOImpl implements DocumentoDAO {

    /**
     * {@inheritDoc}
     */
    @Override
    public Documento obtenerPorId(int idDocumento)
            throws SQLException {
        String sql = "SELECT id, estudiante_id, entregable_id,"
                + " estado, archivo_adjunto,"
                + " fecha_entrega, calificacion"
                + " FROM entrega"
                + " WHERE id = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idDocumento);
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
     * <p>Convierte {@code idInscripcion} a {@code estudiante_id}
     * antes de consultar {@code entrega}.
     */
    @Override
    public List<Documento> obtenerPorInscripcionYTipo(
            int idInscripcion, int idTipoEvidencia)
            throws SQLException {
        int estudianteId = obtenerEstudianteId(idInscripcion);
        String sql = "SELECT id, estudiante_id, entregable_id,"
                + " estado, archivo_adjunto,"
                + " fecha_entrega, calificacion"
                + " FROM entrega"
                + " WHERE estudiante_id = ?"
                + " AND entregable_id = ?";
        List<Documento> lista = new ArrayList<>();
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, estudianteId);
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
     * <p>Convierte {@code idInscripcion} a {@code estudiante_id} y
     * agrega {@code LIMIT 1} para eficiencia.
     */
    @Override
    public Documento obtenerPorInscripcionYTipoUnico(
            int idInscripcion, int idTipoEvidencia)
            throws SQLException {
        int estudianteId = obtenerEstudianteId(idInscripcion);
        String sql = "SELECT id, estudiante_id, entregable_id,"
                + " estado, archivo_adjunto,"
                + " fecha_entrega, calificacion"
                + " FROM entrega"
                + " WHERE estudiante_id = ?"
                + " AND entregable_id = ?"
                + " LIMIT 1";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, estudianteId);
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
     * <p>Inserta en {@code entrega}. Convierte el estado entero del
     * POJO al string ENUM de la BD.
     */
    @Override
    public int insertar(Documento documento) throws SQLException {
        int estudianteId = obtenerEstudianteId(
                documento.getIdInscripcion());
        String sql = "INSERT INTO entrega"
                + " (estudiante_id, entregable_id, estado,"
                + " archivo_adjunto, fecha_entrega, calificacion)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, estudianteId);
            ps.setInt(2, documento.getIdTipoEvidencia());
            ps.setString(3, estadoIntAString(
                    documento.getIdEstadoDocumento()));
            ps.setString(4, documento.getRutaArchivo());
            establecerTimestamp(ps, 5, documento.getFechaEntrega());
            establecerCalificacion(ps, 6,
                    documento.getCalificacion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException(
                "No se generó clave primaria al insertar entrega.");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Actualiza el estado y la ruta del archivo en {@code entrega}.
     */
    @Override
    public void actualizarEntrega(Documento documento)
            throws SQLException {
        String sql = "UPDATE entrega"
                + " SET archivo_adjunto = ?,"
                + " fecha_entrega = ?, estado = ?"
                + " WHERE id = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, documento.getRutaArchivo());
            establecerTimestamp(ps, 2, documento.getFechaEntrega());
            ps.setString(3, estadoIntAString(
                    documento.getIdEstadoDocumento()));
            ps.setInt(4, documento.getIdDocumento());
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
        String sql = "UPDATE entrega"
                + " SET calificacion = ?, estado = ?"
                + " WHERE id = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, calificacion);
            ps.setString(2, estadoIntAString(idEstadoDocumento));
            ps.setInt(3, idDocumento);
            ps.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>spp_db maneja la prórroga en la tabla {@code prorroga}
     * separada. Este método no realiza ninguna acción.
     */
    @Override
    public void actualizarProrroga(int idDocumento,
            LocalDate fechaProrroga) throws SQLException {
        // La prórroga se gestiona en la tabla prorroga de spp_db.
    }

    /**
     * {@inheritDoc}
     *
     * <p>Busca entregas con {@code estado = 'entregada'} y sin
     * calificación, para grupos del profesor dado.
     */
    @Override
    public List<Documento> obtenerEntregadosSinCalificarPorProfesor(
            int idProfesor) throws SQLException {
        String sql = "SELECT e.id, e.estudiante_id,"
                + " e.entregable_id, e.estado,"
                + " e.archivo_adjunto, e.fecha_entrega,"
                + " e.calificacion"
                + " FROM entrega e"
                + " JOIN inscripcion i"
                + " ON i.estudiante_id = e.estudiante_id"
                + " JOIN grupo g"
                + " ON g.id = i.grupo_id"
                + " WHERE g.profesor_id = ?"
                + " AND e.estado = 'entregada'"
                + " AND e.calificacion IS NULL";
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
     * {@inheritDoc}
     *
     * <p>Filtra solo los tipos EvaluacionOV (ids 13 y 14).
     */
    @Override
    public List<Documento> obtenerOVSinCalificarPorProfesor(
            int idProfesor) throws SQLException {
        String sql = "SELECT e.id, e.estudiante_id,"
                + " e.entregable_id, e.estado,"
                + " e.archivo_adjunto, e.fecha_entrega,"
                + " e.calificacion"
                + " FROM entrega e"
                + " JOIN inscripcion i"
                + " ON i.estudiante_id = e.estudiante_id"
                + " JOIN grupo g ON g.id = i.grupo_id"
                + " WHERE g.profesor_id = ?"
                + " AND e.entregable_id IN ("
                + Constantes.TIPO_EVIDENCIA_EVALUACION_OV + ", "
                + Constantes.TIPO_EVIDENCIA_EVALUACION_OV_2 + ")"
                + " AND e.estado = 'entregada'"
                + " AND e.calificacion IS NULL";
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

    /* ── Métodos privados ───────────────────────────────────── */

    /**
     * Construye un {@link Documento} desde la fila actual del
     * {@code ResultSet}. Convierte el string ENUM de {@code estado}
     * al entero que espera el POJO.
     *
     * @param rs ResultSet posicionado en la fila a mapear.
     * @return instancia de {@link Documento} con los campos poblados.
     * @throws SQLException si alguna columna no existe en el RS.
     */
    private Documento mapearResultSet(ResultSet rs)
            throws SQLException {
        Documento doc = new Documento();
        doc.setIdDocumento(rs.getInt("id"));
        doc.setIdInscripcion(rs.getInt("estudiante_id"));
        doc.setIdTipoEvidencia(rs.getInt("entregable_id"));
        doc.setIdEstadoDocumento(
                estadoStringAInt(rs.getString("estado")));
        String ruta = rs.getString("archivo_adjunto");
        doc.setRutaArchivo(ruta);
        doc.setNombreArchivo(
                ruta != null
                ? new java.io.File(ruta).getName()
                : null);

        Timestamp ts = rs.getTimestamp("fecha_entrega");
        doc.setFechaEntrega(ts != null
                ? ts.toLocalDateTime() : null);

        double cal = rs.getDouble("calificacion");
        doc.setCalificacion(rs.wasNull()
                ? Constantes.CENTINELA_SIN_CALIFICACION : cal);

        return doc;
    }

    /**
     * Convierte el entero interno de estado al string ENUM de spp_db.
     *
     * @param estadoInt Constante {@code ESTADO_DOCUMENTO_*}.
     * @return String ENUM para la columna {@code entrega.estado}.
     */
    private String estadoIntAString(int estadoInt) {
        switch (estadoInt) {
            case Constantes.ESTADO_DOCUMENTO_ENTREGADO:
                return Constantes.ESTADO_ENTREGA_ENTREGADA;
            case Constantes.ESTADO_DOCUMENTO_EVALUADO:
                return Constantes.ESTADO_ENTREGA_EVALUADA;
            case Constantes.ESTADO_DOCUMENTO_APROBADO:
                return Constantes.ESTADO_ENTREGA_EVALUADA;
            case Constantes.ESTADO_DOCUMENTO_RECHAZADO:
                return Constantes.ESTADO_ENTREGA_CON_RETARDO;
            default:
                return Constantes.ESTADO_ENTREGA_NO_ENTREGADA;
        }
    }

    /**
     * Convierte el string ENUM de spp_db al entero interno de estado.
     *
     * @param estadoStr String de la columna {@code entrega.estado}.
     * @return Constante {@code ESTADO_DOCUMENTO_*}.
     */
    private int estadoStringAInt(String estadoStr) {
        if (estadoStr == null) {
            return Constantes.ESTADO_DOCUMENTO_PENDIENTE;
        }
        switch (estadoStr) {
            case "entregada":
                return Constantes.ESTADO_DOCUMENTO_ENTREGADO;
            case "evaluada":
                return Constantes.ESTADO_DOCUMENTO_EVALUADO;
            case "evaluadaConRetardo":
                return Constantes.ESTADO_DOCUMENTO_RECHAZADO;
            default:
                return Constantes.ESTADO_DOCUMENTO_PENDIENTE;
        }
    }

    /**
     * Obtiene el {@code estudiante_id} a partir de un id de inscripción.
     *
     * @param idInscripcion PK de la inscripción.
     * @return {@code estudiante_id} o {@code 0} si no existe.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    private int obtenerEstudianteId(int idInscripcion)
            throws SQLException {
        String sql = "SELECT estudiante_id FROM inscripcion"
                + " WHERE id = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("estudiante_id");
                }
            }
        }
        return 0;
    }

    /**
     * Establece un parámetro {@code TIMESTAMP}, o {@code NULL}.
     *
     * @param ps     PreparedStatement destino.
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
     * Establece el parámetro de calificación. Si el valor es negativo
     * (centinela -1.0), inserta {@code NULL}.
     *
     * @param ps     PreparedStatement destino.
     * @param indice Posición del parámetro (1-based).
     * @param valor  Calificación real ≥ 0.0, o -1.0 para NULL.
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
