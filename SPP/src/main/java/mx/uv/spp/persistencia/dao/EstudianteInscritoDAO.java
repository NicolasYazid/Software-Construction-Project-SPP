/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 25 de junio del 2026
 */
package mx.uv.spp.persistencia.dao;

import java.sql.SQLException;

/**
 * Contrato de acceso a datos para la tabla
 * {@code estudiante_inscrito}. Permite obtener el contexto de
 * inscripción del Estudiante autenticado, necesario para todas
 * las operaciones de su módulo (documentos, selección, autoevaluación).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public interface EstudianteInscritoDAO {

    /**
     * Recupera el {@code id_inscripcion} del Estudiante en el ciclo
     * escolar cuyo estado sea {@code 'Iniciado'}. Retorna {@code 0}
     * si el Estudiante no tiene inscripción activa.
     *
     * @param idEstudiante Clave primaria en la tabla
     * {@code estudiante}.
     * @return {@code id_inscripcion} del periodo activo, o
     * {@code 0} si no existe.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    int obtenerIdInscripcionActivo(int idEstudiante)
            throws SQLException;

}
