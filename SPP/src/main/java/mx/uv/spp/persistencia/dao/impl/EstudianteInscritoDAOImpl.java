/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de junio del 2026
 */
package mx.uv.spp.persistencia.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.EstudianteInscritoDAO;

/**
 * Implementación JDBC de {@link EstudianteInscritoDAO}.
 * Consulta la tabla {@code estudiante_inscrito} cruzada con
 * {@code ciclo_escolar} para localizar la inscripción activa
 * del Estudiante en el ciclo con estado {@code 'Iniciado'}.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class EstudianteInscritoDAOImpl
        implements EstudianteInscritoDAO {

    private static final String SQL_ID_INSCRIPCION =
            "SELECT ei.id_inscripcion "
            + "FROM estudiante_inscrito ei "
            + "JOIN ciclo_escolar ce "
            + "    ON ei.id_ciclo_escolar = ce.id_ciclo_escolar "
            + "WHERE ei.id_estudiante = ? "
            + "  AND ce.estado = 'Iniciado' "
            + "LIMIT 1";

    @Override
    public int obtenerIdInscripcionActivo(int idEstudiante)
            throws SQLException {
        try (Connection con =
                     ConexionBD.obtenerInstancia().obtenerConexion();
             PreparedStatement ps =
                     con.prepareStatement(SQL_ID_INSCRIPCION)) {

            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_inscripcion");
                }
            }
        }
        return 0;
    }

}
