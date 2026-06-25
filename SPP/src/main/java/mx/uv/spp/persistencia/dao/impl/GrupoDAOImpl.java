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
        String sql = "SELECT id_grupo, id_ciclo_escolar,"
                + " id_profesor, nombre, nrc"
                + " FROM grupo"
                + " WHERE id_profesor = ?"
                + " ORDER BY nombre";
        List<Grupo> lista = new ArrayList<>();
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
     * Construye un {@link Grupo} desde la fila actual del
     * {@code ResultSet}.
     *
     * @param rs ResultSet posicionado en la fila a mapear.
     * @return instancia de {@link Grupo} con todos los campos.
     * @throws SQLException si alguna columna no existe en el RS.
     */
    private Grupo mapearResultSet(ResultSet rs)
            throws SQLException {
        Grupo grupo = new Grupo();
        grupo.setIdGrupo(rs.getInt("id_grupo"));
        grupo.setIdCicloEscolar(rs.getInt("id_ciclo_escolar"));
        grupo.setIdProfesor(rs.getInt("id_profesor"));
        grupo.setNombre(rs.getString("nombre"));
        grupo.setNrc(rs.getString("nrc"));
        return grupo;
    }

}
