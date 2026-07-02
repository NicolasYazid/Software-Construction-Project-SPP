/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 1 de julio del 2026
 */
package mx.uv.spp.persistencia.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import mx.uv.spp.modelo.Profesor;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.ProfesorDAO;
import mx.uv.spp.util.CifradoAES;
import mx.uv.spp.util.Constantes;

/**
 * Implementación JDBC de {@link ProfesorDAO} para spp_db.
 * Accede a la tabla {@code profesor} con {@code PreparedStatement}.
 * Las contraseñas se cifran con AES-128-CBC (SEG-04) antes de
 * persistirse; ver {@link CifradoAES}.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class ProfesorDAOImpl implements ProfesorDAO {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existeNumeroPersonal(String numeroPersonal)
            throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM profesor"
                + " WHERE numero_personal = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numeroPersonal);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("total") > 0;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existeCorreoInstitucional(
            String correoInstitucional) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM profesor"
                + " WHERE correo_institucional = ?";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correoInstitucional);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("total") > 0;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>El campo {@code coordinador} no se envía: la BD lo
     * inicializa en {@code FALSE} por definición de la columna, y
     * ese rol solo se activa después mediante CU-Admin.-03.
     */
    @Override
    public void registrar(Profesor profesor) throws SQLException {
        String sql = "INSERT INTO profesor (numero_personal, nombre,"
                + " apellido_paterno, apellido_materno,"
                + " correo_institucional, contrasenia, estado)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, profesor.getNumeroPersonal());
            ps.setString(2, profesor.getNombre());
            ps.setString(3, profesor.getApellidoPaterno());

            String apellidoMaterno = profesor.getApellidoMaterno();
            if (apellidoMaterno == null
                    || apellidoMaterno.isEmpty()) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, apellidoMaterno);
            }

            ps.setString(5, profesor.getCorreo());
            ps.setString(6, CifradoAES.cifrar(profesor.getContrasena()));
            ps.setString(7, Constantes.ESTADO_ACTIVO);
            ps.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Profesor> listarTodos() throws SQLException {
        String sql = "SELECT id, numero_personal, nombre,"
                + " apellido_paterno, apellido_materno,"
                + " correo_institucional, estado"
                + " FROM profesor"
                + " ORDER BY nombre";
        List<Profesor> lista = new ArrayList<>();
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        }
        return lista;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Profesor> listarActivosNoCoordinador()
            throws SQLException {
        String sql = "SELECT id, numero_personal, nombre,"
                + " apellido_paterno, apellido_materno,"
                + " correo_institucional, estado"
                + " FROM profesor"
                + " WHERE estado = ? AND coordinador = FALSE"
                + " ORDER BY nombre";
        List<Profesor> lista = new ArrayList<>();
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, Constantes.ESTADO_ACTIVO);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Profesor obtenerCoordinadorActual() throws SQLException {
        String sql = "SELECT id, numero_personal, nombre,"
                + " apellido_paterno, apellido_materno,"
                + " correo_institucional, estado"
                + " FROM profesor"
                + " WHERE coordinador = TRUE"
                + " LIMIT 1";
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapearResultSet(rs);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Primero desactiva al Coordinador anterior (si existe) y
     * después activa al nuevo, en ese orden, para no violar la
     * restricción de unicidad de la columna {@code coordinador}
     * mientras ambas filas están siendo actualizadas.
     */
    @Override
    public void transferirRolCoordinador(int idNuevoCoordinador,
            Integer idCoordinadorAnterior) throws SQLException {
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        boolean autoCommitOriginal = con.getAutoCommit();
        try {
            con.setAutoCommit(false);

            if (idCoordinadorAnterior != null) {
                String sqlAnterior = "UPDATE profesor"
                        + " SET coordinador = FALSE"
                        + " WHERE id = ?";
                try (PreparedStatement ps =
                        con.prepareStatement(sqlAnterior)) {
                    ps.setInt(1, idCoordinadorAnterior);
                    ps.executeUpdate();
                }
            }

            String sqlNuevo = "UPDATE profesor"
                    + " SET coordinador = TRUE"
                    + " WHERE id = ?";
            try (PreparedStatement ps =
                    con.prepareStatement(sqlNuevo)) {
                ps.setInt(1, idNuevoCoordinador);
                ps.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(autoCommitOriginal);
        }
    }

    /**
     * Construye un {@link Profesor} desde la fila actual del
     * {@code ResultSet}. La contraseña no se proyecta en los
     * listados; solo se usa para registrar (CU-Admin.-01).
     *
     * @param rs ResultSet posicionado en la fila a mapear.
     * @return instancia de {@link Profesor} con los datos visibles
     * en los listados del Administrador.
     * @throws SQLException si alguna columna no existe en el RS.
     */
    private Profesor mapearResultSet(ResultSet rs)
            throws SQLException {
        return new Profesor(
                rs.getInt("id"),
                rs.getString("numero_personal"),
                rs.getString("nombre"),
                rs.getString("apellido_paterno"),
                rs.getString("apellido_materno"),
                rs.getString("correo_institucional"),
                null,
                rs.getString("estado"),
                null,
                0, 0, null, null);
    }

}
