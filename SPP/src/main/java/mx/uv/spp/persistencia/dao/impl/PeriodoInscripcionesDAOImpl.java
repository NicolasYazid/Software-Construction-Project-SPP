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
import mx.uv.spp.modelo.PeriodoInscripciones;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.PeriodoInscripcionesDAO;

/**
 * Implementación JDBC de {@link PeriodoInscripcionesDAO} para spp_db.
 * spp_db no tiene tabla {@code periodo_inscripciones} separada; el
 * estado del periodo se consulta directamente desde
 * {@code periodo_escolar}. El estado {@code 'iniciado'} equivale
 * a un periodo de inscripciones abierto.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PeriodoInscripcionesDAOImpl
        implements PeriodoInscripcionesDAO {

    /**
     * {@inheritDoc}
     *
     * <p>Consulta {@code periodo_escolar} con estado {@code 'iniciado'}.
     * Mapea {@code id} y las fechas al POJO {@link PeriodoInscripciones}.
     */
    @Override
    public PeriodoInscripciones obtenerDelCicloActivo()
            throws SQLException {
        String sql = "SELECT id, fecha_inicio, fecha_fin"
                + " FROM periodo_escolar"
                + " WHERE estado = 'iniciado'"
                + " LIMIT 1";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
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
     */
    @Override
    public boolean existeEnCicloActivo() throws SQLException {
        String sql = "SELECT COUNT(*)"
                + " FROM periodo_escolar"
                + " WHERE estado = 'iniciado'";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>spp_db no tiene tabla {@code periodo_inscripciones}; este
     * método no realiza ninguna acción en la BD.
     */
    @Override
    public void insertar(PeriodoInscripciones periodo)
            throws SQLException {
        // No aplica en spp_db; el periodo escolar se gestiona
        // directamente en periodo_escolar.
    }

    /**
     * Construye un {@link PeriodoInscripciones} desde la fila actual
     * del {@code ResultSet}. Las columnas {@code fecha_inicio} y
     * {@code fecha_fin} son NOT NULL en el esquema.
     *
     * @param rs ResultSet posicionado en la fila a mapear.
     * @return instancia con todos los campos poblados.
     * @throws SQLException si alguna columna no existe en el RS.
     */
    private PeriodoInscripciones mapearResultSet(ResultSet rs)
            throws SQLException {
        PeriodoInscripciones pi = new PeriodoInscripciones();
        pi.setIdPeriodoInscripciones(rs.getInt("id"));
        pi.setIdCicloEscolar(rs.getInt("id"));
        Date sqlInicio = rs.getDate("fecha_inicio");
        if (sqlInicio != null) {
            pi.setFechaInicio(sqlInicio.toLocalDate());
        }
        Date sqlFin = rs.getDate("fecha_fin");
        if (sqlFin != null) {
            pi.setFechaCierre(sqlFin.toLocalDate());
        }
        return pi;
    }

}
