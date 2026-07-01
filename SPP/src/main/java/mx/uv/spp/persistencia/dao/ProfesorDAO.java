/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 1 de julio del 2026
 */
package mx.uv.spp.persistencia.dao;

import java.sql.SQLException;
import java.util.List;
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

    /**
     * Recupera todos los Profesores registrados, en estado activo o
     * inactivo, ordenados por nombre. Usado por el panel de gestión
     * de Profesores del Administrador.
     *
     * @return lista (posiblemente vacía) de Profesores; nunca nula.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    List<Profesor> listarTodos() throws SQLException;

    /**
     * Recupera los Profesores activos que actualmente no tienen el
     * rol de Coordinador, ordenados por nombre. Usado por la
     * ventana de transferencia de rol (CU-Admin.-03), donde el
     * Coordinador vigente no es un destino válido.
     *
     * @return lista (posiblemente vacía) de Profesores; nunca nula.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    List<Profesor> listarActivosNoCoordinador() throws SQLException;

    /**
     * Recupera al Profesor que actualmente posee el rol de
     * Coordinador (columna {@code coordinador = TRUE}).
     *
     * @return el Profesor Coordinador vigente, o {@code null} si
     *         ninguno posee el rol actualmente.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    Profesor obtenerCoordinadorActual() throws SQLException;

    /**
     * Transfiere el rol de Coordinador a un Profesor en una única
     * transacción: desactiva el rol del Coordinador anterior (si lo
     * hay) y lo activa para el nuevo, o ninguna de las dos
     * actualizaciones se aplica si alguna falla (CU-Admin.-03, paso 8).
     *
     * @param idNuevoCoordinador     PK del Profesor que recibirá el
     *                               rol; no puede ser 0.
     * @param idCoordinadorAnterior  PK del Profesor que actualmente
     *                               posee el rol; {@code null} si
     *                               ninguno lo posee.
     * @throws SQLException si ocurre un error de acceso a la BD; en
     *         ese caso ningún cambio queda aplicado (rollback).
     */
    void transferirRolCoordinador(int idNuevoCoordinador,
            Integer idCoordinadorAnterior) throws SQLException;

}
