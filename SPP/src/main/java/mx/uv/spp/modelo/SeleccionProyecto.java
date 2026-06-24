/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.modelo;

import java.time.LocalDateTime;

/**
 * Representa la selección de proyectos que el Estudiante realiza por
 * orden de prioridad (RN-11). El Estudiante ordena todos los proyectos
 * disponibles de mayor a menor preferencia. Esta acción es irreversible:
 * una vez confirmada, no puede modificarse. Cada fila corresponde a un
 * proyecto en la lista de prioridades del Estudiante para su inscripción.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class SeleccionProyecto {

    private int           idSeleccion;
    private int           idInscripcion;
    private int           idProyecto;
    private int           prioridad;
    private LocalDateTime fechaSeleccion;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    public SeleccionProyecto() {
    }

    /**
     * Constructor completo para inicializar una SeleccionProyecto
     * con todos sus datos.
     *
     * @param idSeleccion   Identificador del registro en la BD.
     * @param idInscripcion FK hacia {@code estudiante_inscrito};
     *                      identifica al Estudiante que seleccionó.
     * @param idProyecto    FK hacia {@code proyecto}; proyecto elegido.
     * @param prioridad     Número de prioridad asignado (1 = mayor
     *                      preferencia). Único por inscripción.
     * @param fechaSeleccion Fecha y hora en que el Estudiante confirmó
     *                       la selección; no puede ser {@code null}.
     */
    public SeleccionProyecto(int idSeleccion, int idInscripcion,
            int idProyecto, int prioridad,
            LocalDateTime fechaSeleccion) {
        this.idSeleccion    = idSeleccion;
        this.idInscripcion  = idInscripcion;
        this.idProyecto     = idProyecto;
        this.prioridad      = prioridad;
        this.fechaSeleccion = fechaSeleccion;
    }

    /**
     * Retorna el identificador único de esta SeleccionProyecto en la BD.
     *
     * @return identificador del registro.
     */
    public int getIdSeleccion() {
        return idSeleccion;
    }

    /**
     * Establece el identificador único de la selección.
     *
     * @param idSeleccion Identificador asignado por la base de datos.
     */
    public void setIdSeleccion(int idSeleccion) {
        this.idSeleccion = idSeleccion;
    }

    /**
     * Retorna el identificador de la inscripción propietaria de
     * esta selección.
     *
     * @return FK hacia {@code estudiante_inscrito}.
     */
    public int getIdInscripcion() {
        return idInscripcion;
    }

    /**
     * Establece la inscripción propietaria de esta selección.
     *
     * @param idInscripcion FK hacia {@code estudiante_inscrito}.
     */
    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    /**
     * Retorna el identificador del proyecto seleccionado.
     *
     * @return FK hacia {@code proyecto}.
     */
    public int getIdProyecto() {
        return idProyecto;
    }

    /**
     * Establece el proyecto seleccionado.
     *
     * @param idProyecto FK hacia {@code proyecto}.
     */
    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    /**
     * Retorna el número de prioridad asignado al proyecto dentro de la
     * selección del Estudiante. El valor 1 indica la mayor preferencia.
     *
     * @return número de prioridad (entero positivo, único por inscripción).
     */
    public int getPrioridad() {
        return prioridad;
    }

    /**
     * Establece el número de prioridad del proyecto.
     *
     * @param prioridad Número de preferencia; 1 = mayor prioridad.
     */
    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    /**
     * Retorna la fecha y hora en que el Estudiante confirmó la selección.
     *
     * @return fecha y hora de la selección; no es {@code null}.
     */
    public LocalDateTime getFechaSeleccion() {
        return fechaSeleccion;
    }

    /**
     * Establece la fecha y hora de confirmación de la selección.
     *
     * @param fechaSeleccion Momento en que el Estudiante confirmó su
     *                       lista de prioridades (irreversible, RN-11).
     */
    public void setFechaSeleccion(LocalDateTime fechaSeleccion) {
        this.fechaSeleccion = fechaSeleccion;
    }

}
