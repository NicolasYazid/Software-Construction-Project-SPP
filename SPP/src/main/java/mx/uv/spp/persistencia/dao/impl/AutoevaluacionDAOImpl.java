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
import mx.uv.spp.util.Constantes;

/**
 * Implementación JDBC de {@link AutoevaluacionDAO} para spp_db.
 * En spp_db no existe tabla {@code autoevaluacion} con columnas
 * {@code afirmacion_1..10}. En su lugar se usan:
 * <ul>
 * <li>{@code entrega} — fila para el Estudiante con
 * {@code entregable_id = TIPO_EVIDENCIA_AUTOEVALUACION}.</li>
 * <li>{@code respuesta_autoevaluacion} — una fila por afirmación
 * (10 filas en total).</li>
 * </ul>
 * El campo {@code idDocumento} del POJO {@link Autoevaluacion}
 * corresponde al {@code id} de la fila en {@code entrega}.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class AutoevaluacionDAOImpl implements AutoevaluacionDAO {

    /**
     * {@inheritDoc}
     *
     * <p>Verifica si la entrega de autoevaluación del Estudiante
     * ya tiene respuestas registradas en
     * {@code respuesta_autoevaluacion}.
     */
    @Override
    public boolean existePorInscripcion(int idInscripcion)
            throws SQLException {
        String sqlExisteAutoevaluacion = "SELECT COUNT(*)"
                + " FROM respuesta_autoevaluacion ra"
                + " JOIN entrega e"
                + " ON e.id = ra.entrega_id"
                + " JOIN inscripcion i"
                + " ON i.estudiante_id = e.estudiante_id"
                + " WHERE i.id = ?"
                + " AND e.entregable_id = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement psExisteAutoevaluacion =
                con.prepareStatement(sqlExisteAutoevaluacion)) {
            psExisteAutoevaluacion.setInt(1, idInscripcion);
            psExisteAutoevaluacion.setInt(
                    2, Constantes.TIPO_EVIDENCIA_AUTOEVALUACION);
            try (ResultSet rsConteo =
                    psExisteAutoevaluacion.executeQuery()) {
                if (rsConteo.next()) {
                    return rsConteo.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Inserta 10 filas en {@code respuesta_autoevaluacion} y
     * actualiza {@code entrega.estado = 'entregada'}. El campo
     * {@code autoevaluacion.getIdDocumento()} es el {@code id}
     * de la fila en {@code entrega}.
     */
    @Override
    public void insertar(Autoevaluacion autoevaluacion)
            throws SQLException {
        int entregaId = autoevaluacion.getIdDocumento();

        String sqlRespuesta = "INSERT INTO respuesta_autoevaluacion"
                + " (entrega_id, numero_afirmacion, valor)"
                + " VALUES (?, ?, ?)";

        int[] valores = {
            autoevaluacion.getAfirmacion1(),
            autoevaluacion.getAfirmacion2(),
            autoevaluacion.getAfirmacion3(),
            autoevaluacion.getAfirmacion4(),
            autoevaluacion.getAfirmacion5(),
            autoevaluacion.getAfirmacion6(),
            autoevaluacion.getAfirmacion7(),
            autoevaluacion.getAfirmacion8(),
            autoevaluacion.getAfirmacion9(),
            autoevaluacion.getAfirmacion10()
        };

        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try {
            con.setAutoCommit(false);

            for (int i = 0; i < valores.length; i++) {
                try (PreparedStatement psRespuesta =
                        con.prepareStatement(sqlRespuesta)) {
                    psRespuesta.setInt(1, entregaId);
                    psRespuesta.setInt(2, i + 1);
                    psRespuesta.setInt(3, valores[i]);
                    psRespuesta.executeUpdate();
                }
            }

            String sqlActualizarEstado = "UPDATE entrega"
                    + " SET estado = 'entregada'"
                    + " WHERE id = ?";
            try (PreparedStatement psActualizarEstado =
                    con.prepareStatement(sqlActualizarEstado)) {
                psActualizarEstado.setInt(1, entregaId);
                psActualizarEstado.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.err.println(
                        "Error en rollback de autoevaluacion: "
                        + ex.getMessage());
            }
            throw e;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println(
                        "Error al restaurar autoCommit: "
                        + e.getMessage());
            }
        }
    }

}
