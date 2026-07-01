/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 1 de julio del 2026
 */
package mx.uv.spp.persistencia.dao;

import java.sql.SQLException;
import mx.uv.spp.modelo.Profesor;

/**
 * Contrato de acceso a datos para la tabla {@code profesor}, usado
 * por el módulo del Administrador para dar de alta nuevos Profesores
 * (CU-Admin.-01).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public interface ProfesorDAO {

    /**
     * Verifica si ya existe un Profesor, en estado activo o
     * inactivo, con el número de personal dado.
     *
     * @param numeroPersonal Número de personal a buscar; no nulo.
     * @return {@code true} si ya existe un registro con ese valor.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    boolean existeNumeroPersonal(String numeroPersonal)
            throws SQLException;

    /**
     * Verifica si ya existe un Profesor, en estado activo o
     * inactivo, con el correo institucional dado.
     *
     * @param correoInstitucional Correo a buscar; no nulo.
     * @return {@code true} si ya existe un registro con ese valor.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    boolean existeCorreoInstitucional(String correoInstitucional)
            throws SQLException;

    /**
     * Inserta un nuevo Profesor en estado activo.
     *
     * @param profesor Datos del nuevo Profesor; no nulo. El campo
     *                 {@code id} es ignorado (autogenerado por la BD).
     * @throws SQLException si ocurre un error de acceso a la BD, por
     *         ejemplo una violación de unicidad no detectada antes
     *         de la inserción.
     */
    void registrar(Profesor profesor) throws SQLException;

}
