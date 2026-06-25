/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.persistencia.dao;

import java.sql.SQLException;
import mx.uv.spp.modelo.Autoevaluacion;

/**
 * Contrato de acceso a datos para la tabla {@code autoevaluacion}.
 * La autoevaluación consta de 10 afirmaciones con escala Likert 1-5;
 * su entrega es irreversible (sección 8). La calificación se calcula
 * como (puntuacion_total / 50.0) * 10 antes de persistir.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public interface AutoevaluacionDAO {

    /**
     * Indica si el Estudiante ya entregó su autoevaluación en el
     * ciclo escolar activo ({@code ciclo_escolar.activo = 1}).
     * Se usa para bloquear una segunda entrega (acción irreversible).
     *
     * @param idInscripcion FK de {@code estudiante_inscrito}.
     * @return {@code true} si existe una fila en {@code autoevaluacion}
     *         para el documento de tipo Autoevaluacion de esa
     *         inscripción.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    boolean existePorInscripcion(int idInscripcion)
            throws SQLException;

    /**
     * Inserta la autoevaluación completa del Estudiante.
     * El campo {@code idDocumento} de la autoevaluación debe
     * corresponder a un registro ya existente en {@code documento}.
     *
     * @param autoevaluacion Datos de la autoevaluación, con
     *                       {@code puntuacionTotal} y
     *                       {@code calificacion} ya calculados.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    void insertar(Autoevaluacion autoevaluacion) throws SQLException;

}
