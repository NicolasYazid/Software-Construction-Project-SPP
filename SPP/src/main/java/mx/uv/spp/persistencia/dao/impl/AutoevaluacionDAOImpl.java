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
import mx.uv.spp.modelo.Autoevaluacion;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.AutoevaluacionDAO;

/**
 * Implementación JDBC de {@link AutoevaluacionDAO}.
 * Accede a las tablas {@code autoevaluacion} y {@code documento}
 * para verificar y persistir la autoevaluación del Estudiante.
 * La verificación de existencia hace JOIN porque la tabla
 * {@code autoevaluacion} referencia {@code documento}, y el
 * vínculo con la inscripción está en {@code documento}.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class AutoevaluacionDAOImpl implements AutoevaluacionDAO {

    /**
     * {@inheritDoc}
     *
     * <p>Hace JOIN con {@code documento} para relacionar la
     * autoevaluación con la inscripción, ya que la tabla
     * {@code autoevaluacion} no almacena directamente
     * {@code id_inscripcion}.
     */
    @Override
    public boolean existePorInscripcion(int idInscripcion)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM autoevaluacion a"
                + " JOIN documento d"
                + " ON a.id_documento = d.id_documento"
                + " WHERE d.id_inscripcion = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
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
    public void insertar(Autoevaluacion autoevaluacion)
            throws SQLException {
        String sql = "INSERT INTO autoevaluacion"
                + " (id_documento,"
                + " afirmacion_1, afirmacion_2, afirmacion_3,"
                + " afirmacion_4, afirmacion_5, afirmacion_6,"
                + " afirmacion_7, afirmacion_8, afirmacion_9,"
                + " afirmacion_10,"
                + " puntuacion_total, calificacion)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, autoevaluacion.getIdDocumento());
            ps.setInt(2, autoevaluacion.getAfirmacion1());
            ps.setInt(3, autoevaluacion.getAfirmacion2());
            ps.setInt(4, autoevaluacion.getAfirmacion3());
            ps.setInt(5, autoevaluacion.getAfirmacion4());
            ps.setInt(6, autoevaluacion.getAfirmacion5());
            ps.setInt(7, autoevaluacion.getAfirmacion6());
            ps.setInt(8, autoevaluacion.getAfirmacion7());
            ps.setInt(9, autoevaluacion.getAfirmacion8());
            ps.setInt(10, autoevaluacion.getAfirmacion9());
            ps.setInt(11, autoevaluacion.getAfirmacion10());
            ps.setInt(12, autoevaluacion.getPuntuacionTotal());
            ps.setDouble(13, autoevaluacion.getCalificacion());
            ps.executeUpdate();
        }
    }

}
