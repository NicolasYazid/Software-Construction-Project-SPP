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
import mx.uv.spp.modelo.Profesor;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.ProfesorDAO;
import mx.uv.spp.util.Constantes;

/**
 * Implementación JDBC de {@link ProfesorDAO} para spp_db.
 * Accede a la tabla {@code profesor} con {@code PreparedStatement}.
 * Las contraseñas se almacenan en texto plano, igual que el resto
 * de la capa de persistencia migrada a spp_db.
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
            ps.setString(1, profesor.getNumPersonal());
            ps.setString(2, profesor.getNombre());
            ps.setString(3, profesor.getPrimerApellido());

            String segundoApellido = profesor.getSegundoApellido();
            if (segundoApellido == null
                    || segundoApellido.isEmpty()) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, segundoApellido);
            }

            ps.setString(5, profesor.getCorreo());
            ps.setString(6, profesor.getContrasena());
            ps.setString(7, Constantes.ESTADO_ACTIVO);
            ps.executeUpdate();
        }
    }

}
