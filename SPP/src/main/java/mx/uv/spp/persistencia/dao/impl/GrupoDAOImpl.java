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
import java.util.ArrayList;
import java.util.List;
import mx.uv.spp.modelo.Grupo;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.GrupoDAO;

/**
 * Implementación JDBC de {@link GrupoDAO}.
 * Accede a la tabla {@code grupo} con {@code PreparedStatement}.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class GrupoDAOImpl implements GrupoDAO {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Grupo> obtenerPorProfesor(int idProfesor)
            throws SQLException {
        String sqlObtenerPorProfesor = "SELECT id, periodo_escolar_id,"
                + " profesor_id, nombre, nrc"
                + " FROM grupo"
                + " WHERE profesor_id = ?"
                + " ORDER BY nombre";
        List<Grupo> lista = new ArrayList<>();
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement psObtenerPorProfesor =
                con.prepareStatement(sqlObtenerPorProfesor)) {
            psObtenerPorProfesor.setInt(1, idProfesor);
            try (ResultSet rsGrupos =
                    psObtenerPorProfesor.executeQuery()) {
                while (rsGrupos.next()) {
                    lista.add(mapearResultSet(rsGrupos));
                }
            }
        }
        return lista;
    }

    /**
     * Construye un {@link Grupo} desde la fila actual del
     * {@code ResultSet}.
     *
     * @param rsGrupo ResultSet posicionado en la fila a mapear.
     * @return instancia de {@link Grupo} con todos los campos.
     * @throws SQLException si alguna columna no existe en el RS.
     */
    private Grupo mapearResultSet(ResultSet rsGrupo)
            throws SQLException {
        Grupo grupo = new Grupo();
        grupo.setIdGrupo(rsGrupo.getInt("id"));
        grupo.setIdCicloEscolar(
                rsGrupo.getInt("periodo_escolar_id"));
        grupo.setIdProfesor(rsGrupo.getInt("profesor_id"));
        grupo.setNombre(rsGrupo.getString("nombre"));
        grupo.setNrc(rsGrupo.getString("nrc"));
        return grupo;
    }

}
