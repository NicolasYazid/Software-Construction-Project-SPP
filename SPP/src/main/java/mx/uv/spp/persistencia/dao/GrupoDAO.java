/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de junio del 2026
 */
package mx.uv.spp.persistencia.dao;

import java.sql.SQLException;
import java.util.List;
import mx.uv.spp.modelo.Grupo;

/**
 * Contrato de acceso a datos para la tabla {@code grupo}.
 * Permite obtener los Grupos de un Profesor Asesor para
 * operaciones de publicación de mensajes (CU-31).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public interface GrupoDAO {

    /**
     * Recupera todos los Grupos asignados a un Profesor Asesor,
     * ordenados por nombre de forma ascendente.
     *
     * @param idProfesor FK del Profesor en tabla {@code profesor}.
     * @return lista (posiblemente vacía) de Grupos del Profesor.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    List<Grupo> obtenerPorProfesor(int idProfesor)
            throws SQLException;

}
