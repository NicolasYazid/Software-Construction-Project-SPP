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
 * Implementación JDBC de {@link EstudianteInscritoDAO} para spp_db.
 * Consulta la tabla {@code inscripcion} para localizar la inscripción
 * activa del Estudiante (estado {@code 'enCurso'}).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class EstudianteInscritoDAOImpl
        implements EstudianteInscritoDAO {

    private static final String SQL_ID_INSCRIPCION =
            "SELECT id FROM inscripcion"
            + " WHERE estudiante_id = ?"
            + " AND estado = 'enCurso'"
            + " LIMIT 1";

    /**
     * {@inheritDoc}
     */
    @Override
    public int obtenerIdInscripcionActivo(int idEstudiante)
            throws SQLException {
        try (Connection con =
                     ConexionBD.obtenerInstancia().obtenerConexion();
             PreparedStatement psIdInscripcion =
                     con.prepareStatement(SQL_ID_INSCRIPCION)) {

            psIdInscripcion.setInt(1, idEstudiante);
            try (ResultSet rsInscripcion =
                    psIdInscripcion.executeQuery()) {
                if (rsInscripcion.next()) {
                    return rsInscripcion.getInt("id");
                }
            }
        }
        return 0;
    }

}
