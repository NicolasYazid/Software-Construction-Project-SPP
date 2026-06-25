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
 * Implementación JDBC de {@link PeriodoInscripcionesDAO}.
 * Accede a la tabla {@code periodo_inscripciones} con JOIN a
 * {@code ciclo_escolar} para filtrar por ciclo en estado
 * {@code 'Iniciado'}. Las fechas se convierten entre
 * {@code java.sql.Date} y {@code java.time.LocalDate}.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PeriodoInscripcionesDAOImpl
        implements PeriodoInscripcionesDAO {

    /**
     * {@inheritDoc}
     *
     * <p>Hace JOIN con {@code ciclo_escolar} para identificar
     * el ciclo activo sin depender de un id específico.
     */
    @Override
    public PeriodoInscripciones obtenerDelCicloActivo()
            throws SQLException {
        String sql = "SELECT pi.id_periodo_inscripciones,"
                + " pi.id_ciclo_escolar,"
                + " pi.fecha_inicio, pi.fecha_cierre"
                + " FROM periodo_inscripciones pi"
                + " JOIN ciclo_escolar ce"
                + " ON pi.id_ciclo_escolar = ce.id_ciclo_escolar"
                + " WHERE ce.estado = 'Iniciado'"
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
                + " FROM periodo_inscripciones pi"
                + " JOIN ciclo_escolar ce"
                + " ON pi.id_ciclo_escolar = ce.id_ciclo_escolar"
                + " WHERE ce.estado = 'Iniciado'";
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
     */
    @Override
    public void insertar(PeriodoInscripciones periodo)
            throws SQLException {
        String sql = "INSERT INTO periodo_inscripciones"
                + " (id_ciclo_escolar, fecha_inicio, fecha_cierre)"
                + " VALUES (?, ?, ?)";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, periodo.getIdCicloEscolar());
            ps.setDate(2, Date.valueOf(periodo.getFechaInicio()));
            ps.setDate(3, Date.valueOf(periodo.getFechaCierre()));
            ps.executeUpdate();
        }
    }

    /**
     * Construye un {@link PeriodoInscripciones} desde la fila actual
     * del {@code ResultSet}. Las columnas {@code fecha_inicio} y
     * {@code fecha_cierre} son NOT NULL en el esquema, por lo que
     * no se requiere comprobación de nulo.
     *
     * @param rs ResultSet posicionado en la fila a mapear.
     * @return instancia con todos los campos poblados.
     * @throws SQLException si alguna columna no existe en el RS.
     */
    private PeriodoInscripciones mapearResultSet(ResultSet rs)
            throws SQLException {
        PeriodoInscripciones pi = new PeriodoInscripciones();
        pi.setIdPeriodoInscripciones(
                rs.getInt("id_periodo_inscripciones"));
        pi.setIdCicloEscolar(rs.getInt("id_ciclo_escolar"));
        pi.setFechaInicio(
                rs.getDate("fecha_inicio").toLocalDate());
        pi.setFechaCierre(
                rs.getDate("fecha_cierre").toLocalDate());
        return pi;
    }

}
