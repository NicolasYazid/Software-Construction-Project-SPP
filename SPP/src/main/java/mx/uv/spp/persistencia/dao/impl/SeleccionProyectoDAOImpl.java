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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import mx.uv.spp.modelo.Proyecto;
import mx.uv.spp.modelo.SeleccionProyecto;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.SeleccionProyectoDAO;
import mx.uv.spp.util.Constantes;

/**
 * Implementación JDBC de {@link SeleccionProyectoDAO}.
 * Accede a las tablas {@code seleccion_proyecto} y {@code proyecto}.
 * El método {@code insertarLista} usa una transacción explícita para
 * garantizar atomicidad: o se registran todas las prioridades o
 * ninguna, lo cual es crítico dado que la acción es irreversible
 * (RN-11).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class SeleccionProyectoDAOImpl implements SeleccionProyectoDAO {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existeSeleccionPorInscripcion(int idInscripcion)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM seleccion_proyecto"
                + " WHERE id_inscripcion = ?";
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
     *
     * <p>Flujo de la transacción:
     * <ol>
     *   <li>Se desactiva {@code autoCommit}.</li>
     *   <li>Se inserta cada selección con {@code PreparedStatement}.</li>
     *   <li>Si todo fue exitoso, se llama {@code commit()}.</li>
     *   <li>Si ocurre cualquier error, se llama {@code rollback()}
     *       y se relanza la excepción.</li>
     *   <li>En {@code finally}, se restaura {@code autoCommit = true}.</li>
     * </ol>
     */
    @Override
    public void insertarLista(List<SeleccionProyecto> selecciones)
            throws SQLException {
        String sql = "INSERT INTO seleccion_proyecto"
                + " (id_inscripcion, id_proyecto,"
                + " prioridad, fecha_seleccion)"
                + " VALUES (?, ?, ?, ?)";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try {
            con.setAutoCommit(false);
            for (SeleccionProyecto sel : selecciones) {
                try (PreparedStatement ps =
                        con.prepareStatement(sql)) {
                    ps.setInt(1, sel.getIdInscripcion());
                    ps.setInt(2, sel.getIdProyecto());
                    ps.setInt(3, sel.getPrioridad());
                    ps.setTimestamp(4, Timestamp.valueOf(
                            sel.getFechaSeleccion()));
                    ps.executeUpdate();
                }
            }
            con.commit();
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.err.println(
                        "Error en rollback de seleccion: "
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

    /**
     * {@inheritDoc}
     *
     * <p>Filtra por estado usando la constante
     * {@link Constantes#ESTADO_PROYECTO_DISPONIBLE} para evitar
     * valores mágicos.
     */
    @Override
    public List<Proyecto> obtenerProyectosDisponibles()
            throws SQLException {
        String sql = "SELECT id_proyecto, id_organizacion,"
                + " id_responsable, nombre_proyecto,"
                + " descripcion, actividades, metodologia,"
                + " duracion_meses, horario_laboral, recurso,"
                + " responsabilidades, cupo_maximo,"
                + " cupo_disponible, estado"
                + " FROM proyecto"
                + " WHERE estado = ?"
                + " ORDER BY id_proyecto";
        List<Proyecto> lista = new ArrayList<>();
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1,
                    Constantes.ESTADO_PROYECTO_DISPONIBLE);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Construye un {@link Proyecto} desde la fila actual del
     * {@code ResultSet}.
     *
     * @param rs ResultSet posicionado en la fila a mapear.
     * @return instancia de {@link Proyecto} con todos los campos.
     * @throws SQLException si alguna columna no existe en el RS.
     */
    private Proyecto mapearResultSet(ResultSet rs)
            throws SQLException {
        Proyecto p = new Proyecto();
        p.setIdProyecto(rs.getInt("id_proyecto"));
        p.setIdOrganizacion(rs.getInt("id_organizacion"));
        p.setIdResponsable(rs.getInt("id_responsable"));
        p.setNombreProyecto(rs.getString("nombre_proyecto"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setActividades(rs.getString("actividades"));
        p.setMetodologia(rs.getString("metodologia"));
        p.setDuracionMeses(rs.getInt("duracion_meses"));
        p.setHorarioLaboral(rs.getString("horario_laboral"));
        p.setRecurso(rs.getString("recurso"));
        p.setResponsabilidades(rs.getString("responsabilidades"));
        p.setCupoMaximo(rs.getInt("cupo_maximo"));
        p.setCupoDisponible(rs.getInt("cupo_disponible"));
        p.setEstado(rs.getString("estado"));
        return p;
    }

}
