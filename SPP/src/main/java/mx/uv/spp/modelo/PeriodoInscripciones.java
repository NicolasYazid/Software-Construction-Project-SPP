/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.modelo;

import java.time.LocalDate;

/**
 * Representa el periodo de inscripciones habilitado por el Coordinador
 * para un ciclo escolar específico (CU-20: Establecer periodo de
 * inscripciones). Solo puede existir un periodo de inscripciones por
 * ciclo escolar.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class PeriodoInscripciones {

    private int       idPeriodoInscripciones;
    private int       idCicloEscolar;
    private LocalDate fechaInicio;
    private LocalDate fechaCierre;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    public PeriodoInscripciones() {
    }

    /**
     * Constructor completo para inicializar un PeriodoInscripciones
     * con todos sus datos.
     *
     * @param idPeriodoInscripciones Identificador del registro en la BD.
     * @param idCicloEscolar         FK hacia el ciclo escolar al que
     *                               pertenece este periodo.
     * @param fechaInicio            Fecha a partir de la cual los
     *                               Estudiantes pueden inscribirse.
     * @param fechaCierre            Fecha límite de inscripción; debe ser
     *                               posterior a {@code fechaInicio}.
     */
    public PeriodoInscripciones(int idPeriodoInscripciones,
            int idCicloEscolar, LocalDate fechaInicio,
            LocalDate fechaCierre) {
        this.idPeriodoInscripciones = idPeriodoInscripciones;
        this.idCicloEscolar         = idCicloEscolar;
        this.fechaInicio            = fechaInicio;
        this.fechaCierre            = fechaCierre;
    }

    /**
     * Retorna el identificador único del periodo de inscripciones en la BD.
     *
     * @return identificador del registro.
     */
    public int getIdPeriodoInscripciones() {
        return idPeriodoInscripciones;
    }

    /**
     * Establece el identificador único del periodo de inscripciones.
     *
     * @param idPeriodoInscripciones Identificador asignado por la BD.
     */
    public void setIdPeriodoInscripciones(int idPeriodoInscripciones) {
        this.idPeriodoInscripciones = idPeriodoInscripciones;
    }

    /**
     * Retorna el identificador del ciclo escolar al que pertenece
     * este periodo.
     *
     * @return FK hacia la tabla ciclo_escolar.
     */
    public int getIdCicloEscolar() {
        return idCicloEscolar;
    }

    /**
     * Establece el ciclo escolar al que pertenece este periodo.
     *
     * @param idCicloEscolar FK hacia la tabla ciclo_escolar.
     */
    public void setIdCicloEscolar(int idCicloEscolar) {
        this.idCicloEscolar = idCicloEscolar;
    }

    /**
     * Retorna la fecha de apertura del periodo de inscripciones.
     *
     * @return fecha de inicio; no puede ser {@code null}.
     */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Establece la fecha de apertura del periodo de inscripciones.
     *
     * @param fechaInicio Fecha a partir de la cual se aceptan inscripciones.
     */
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Retorna la fecha límite del periodo de inscripciones.
     *
     * @return fecha de cierre; debe ser posterior a {@code fechaInicio}.
     */
    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    /**
     * Establece la fecha límite del periodo de inscripciones.
     *
     * @param fechaCierre Fecha límite de inscripción.
     */
    public void setFechaCierre(LocalDate fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

}
