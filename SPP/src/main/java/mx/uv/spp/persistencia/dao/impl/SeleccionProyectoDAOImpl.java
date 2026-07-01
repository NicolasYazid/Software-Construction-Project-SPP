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
import java.util.ArrayList;
import java.util.List;
import mx.uv.spp.modelo.Proyecto;
import mx.uv.spp.modelo.SeleccionProyecto;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.SeleccionProyectoDAO;
import mx.uv.spp.util.Constantes;

/**
 * Implementación JDBC de {@link SeleccionProyectoDAO} para spp_db.
 * Accede a las tablas {@code lista_prioridades} y {@code proyecto}.
 * En spp_db la selección se vincula al {@code estudiante_id};
 * cuando el servicio pasa {@code idInscripcion}, este DAO lo
 * convierte a {@code estudiante_id} con una subconsulta.
 * El método {@code insertarLista} usa transacción explícita
 * (RN-11: la selección de proyectos es irreversible).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class SeleccionProyectoDAOImpl implements SeleccionProyectoDAO {

    /**
     * {@inheritDoc}
     *
     * <p>Verifica en {@code lista_prioridades} si el estudiante de
     * la inscripción dada ya registró prioridades.
     */
    @Override
    public boolean existeSeleccionPorInscripcion(int idInscripcion)
            throws SQLException {
        String sqlExisteSeleccion =
                "SELECT COUNT(*) FROM lista_prioridades lp"
                + " JOIN inscripcion i"
                + " ON i.estudiante_id = lp.estudiante_id"
                + " WHERE i.id = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement psExisteSeleccion =
                con.prepareStatement(sqlExisteSeleccion)) {
            psExisteSeleccion.setInt(1, idInscripcion);
            try (ResultSet rsConteo =
                    psExisteSeleccion.executeQuery()) {
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
     * <p>Obtiene el {@code estudiante_id} desde la inscripción y luego
     * inserta cada fila en {@code lista_prioridades}. Usa transacción
     * explícita para garantizar atomicidad (RN-11).
     */
    @Override
    public void insertarLista(List<SeleccionProyecto> selecciones)
            throws SQLException {
        if (selecciones == null || selecciones.isEmpty()) {
            return;
        }

        int idInscripcion = selecciones.get(0).getIdInscripcion();
        int estudianteId = obtenerEstudianteId(idInscripcion);
        if (estudianteId == 0) {
            throw new SQLException(
                    "No se encontró estudiante_id para inscripción "
                    + idInscripcion);
        }

        String sqlInsertarPrioridad = "INSERT INTO lista_prioridades"
                + " (estudiante_id, proyecto_id, posicion)"
                + " VALUES (?, ?, ?)";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try {
            con.setAutoCommit(false);
            for (SeleccionProyecto sel : selecciones) {
                try (PreparedStatement psInsertarPrioridad =
                        con.prepareStatement(sqlInsertarPrioridad)) {
                    psInsertarPrioridad.setInt(1, estudianteId);
                    psInsertarPrioridad.setInt(
                            2, sel.getIdProyecto());
                    psInsertarPrioridad.setInt(
                            3, sel.getPrioridad());
                    psInsertarPrioridad.executeUpdate();
                }
            }
            con.commit();
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.err.println(
                        "Error en rollback de lista_prioridades: "
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
     * <p>Filtra proyectos con {@code estado = 'activo'} y mapea las
     * columnas de spp_db al POJO {@link Proyecto}.
     */
    @Override
    public List<Proyecto> obtenerProyectosDisponibles()
            throws SQLException {
        String sqlObtenerDisponibles =
                "SELECT id, nombre, descripcion_general,"
                + " metodologia, cupo_maximo, cupo_disponible,"
                + " estado"
                + " FROM proyecto"
                + " WHERE estado = ?"
                + " ORDER BY id";
        List<Proyecto> lista = new ArrayList<>();
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement psObtenerDisponibles =
                con.prepareStatement(sqlObtenerDisponibles)) {
            psObtenerDisponibles.setString(1,
                    Constantes.ESTADO_PROYECTO_DISPONIBLE);
            try (ResultSet rsProyectos =
                    psObtenerDisponibles.executeQuery()) {
                while (rsProyectos.next()) {
                    lista.add(mapearResultSet(rsProyectos));
                }
            }
        }
        return lista;
    }

    /**
     * Obtiene el {@code estudiante_id} a partir de un
     * {@code id} de inscripción.
     *
     * @param idInscripcion PK de la inscripción.
     * @return {@code estudiante_id} o {@code 0} si no existe.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    private int obtenerEstudianteId(int idInscripcion)
            throws SQLException {
        String sqlObtenerEstudianteId =
                "SELECT estudiante_id FROM inscripcion"
                + " WHERE id = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement psObtenerEstudianteId =
                con.prepareStatement(sqlObtenerEstudianteId)) {
            psObtenerEstudianteId.setInt(1, idInscripcion);
            try (ResultSet rsInscripcion =
                    psObtenerEstudianteId.executeQuery()) {
                if (rsInscripcion.next()) {
                    return rsInscripcion.getInt("estudiante_id");
                }
            }
        }
        return 0;
    }

    /**
     * Construye un {@link Proyecto} desde la fila actual del
     * {@code ResultSet} usando las columnas de spp_db.
     *
     * @param rsProyecto ResultSet posicionado en la fila a mapear.
     * @return instancia de {@link Proyecto} con los campos disponibles.
     * @throws SQLException si alguna columna no existe en el RS.
     */
    private Proyecto mapearResultSet(ResultSet rsProyecto)
            throws SQLException {
        Proyecto proyecto = new Proyecto();
        proyecto.setIdProyecto(rsProyecto.getInt("id"));
        proyecto.setNombreProyecto(rsProyecto.getString("nombre"));
        proyecto.setDescripcion(
                rsProyecto.getString("descripcion_general"));
        proyecto.setMetodologia(rsProyecto.getString("metodologia"));
        proyecto.setCupoMaximo(rsProyecto.getInt("cupo_maximo"));
        proyecto.setCupoDisponible(
                rsProyecto.getInt("cupo_disponible"));
        proyecto.setEstado(rsProyecto.getString("estado"));
        return proyecto;
    }

}
