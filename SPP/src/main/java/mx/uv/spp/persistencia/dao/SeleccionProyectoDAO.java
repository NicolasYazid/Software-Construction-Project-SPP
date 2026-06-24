/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.persistencia.dao;

import java.sql.SQLException;
import java.util.List;
import mx.uv.spp.modelo.Proyecto;
import mx.uv.spp.modelo.SeleccionProyecto;

/**
 * Contrato de acceso a datos para la selección de proyectos por
 * prioridad (RN-11). Cubre la tabla {@code seleccion_proyecto}
 * y la consulta de proyectos disponibles. La implementación JDBC
 * garantiza que {@code insertarLista} se ejecute en una sola
 * transacción atómica.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public interface SeleccionProyectoDAO {

    /**
     * Indica si el Estudiante ya registró su lista de prioridades
     * para la inscripción dada. La selección es irreversible (RN-11),
     * por lo que este método se usa para bloquear una segunda entrega.
     *
     * @param idInscripcion FK de {@code estudiante_inscrito}.
     * @return {@code true} si existe al menos una fila para la
     *         inscripción en {@code seleccion_proyecto}.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    boolean existeSeleccionPorInscripcion(int idInscripcion)
            throws SQLException;

    /**
     * Inserta todas las selecciones de prioridad en una sola
     * transacción. Si cualquier INSERT falla, se hace rollback
     * de todos los inserts del lote.
     *
     * @param selecciones Lista completa de selecciones ordenadas
     *                    por prioridad; no debe ser vacía.
     * @throws SQLException si ocurre un error en la BD o en el
     *         rollback tras un fallo parcial.
     */
    void insertarLista(List<SeleccionProyecto> selecciones)
            throws SQLException;

    /**
     * Recupera todos los proyectos en estado Disponible, ordenados
     * por {@code id_proyecto} ascendente. Usado en CU-21 para
     * presentar la lista que el Estudiante debe ordenar.
     *
     * @return lista de proyectos disponibles; puede ser vacía.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    List<Proyecto> obtenerProyectosDisponibles() throws SQLException;

}
