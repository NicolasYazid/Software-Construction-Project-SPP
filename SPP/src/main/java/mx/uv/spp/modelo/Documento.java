/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representa un documento o evidencia dentro del sistema de Prácticas
 * Profesionales. Cubre tanto los DocumentosIniciales (OficioAceptacion,
 * OficioAsignacion, HorarioClases, HorarioLaboral, Cronograma) como las
 * Evidencias (ReporteMensual, InformeParcial, InformeFinal, Presentacion,
 * EvaluacionOV, Autoevaluacion). El tipo se discrimina mediante
 * {@code idTipoEvidencia} (patrón tabla-por-jerarquía, sección 3).
 * El valor -1.0 en {@code calificacion} indica que el documento aún
 * no ha sido calificado.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class Documento {

    private int           idDocumento;
    private int           idInscripcion;
    private int           idTipoEvidencia;
    private int           idEstadoDocumento;
    private String        rutaArchivo;
    private String        nombreArchivo;
    private LocalDateTime fechaEntrega;
    private LocalDate     fechaLimite;
    private LocalDate     fechaProrroga;
    private String        observaciones;
    private double        calificacion;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    public Documento() {
    }

    /**
     * Constructor completo para inicializar un Documento con todos
     * sus datos.
     *
     * @param idDocumento       Identificador del registro en la BD.
     * @param idInscripcion     FK hacia {@code estudiante_inscrito};
     *                          identifica al Estudiante propietario.
     * @param idTipoEvidencia   FK hacia {@code tipo_evidencia}; discrimina
     *                          el subtipo de documento (DocumentoInicial
     *                          o Evidencia, sección 6).
     * @param idEstadoDocumento FK hacia {@code estado_documento}:
     *                          Pendiente, Entregado, Aprobado, Rechazado
     *                          o ConProrroga.
     * @param rutaArchivo       Ruta local del archivo entregado;
     *                          {@code null} si aún no se ha entregado.
     * @param nombreArchivo     Nombre del archivo para la UI;
     *                          {@code null} si no aplica.
     * @param fechaEntrega      Momento de la entrega del Estudiante;
     *                          {@code null} si aún no fue entregado.
     * @param fechaLimite       Fecha límite de entrega original;
     *                          {@code null} si no fue establecida.
     * @param fechaProrroga     Fecha límite extendida por el Coordinador
     *                          o Profesor; {@code null} si no hubo prórroga.
     * @param observaciones     Comentarios del Profesor al evaluar;
     *                          {@code null} si no aplica.
     * @param calificacion      Calificación asignada por el Profesor;
     *                          usar -1.0 para indicar sin calificación.
     */
    public Documento(int idDocumento, int idInscripcion,
            int idTipoEvidencia, int idEstadoDocumento,
            String rutaArchivo, String nombreArchivo,
            LocalDateTime fechaEntrega, LocalDate fechaLimite,
            LocalDate fechaProrroga, String observaciones,
            double calificacion) {
        this.idDocumento       = idDocumento;
        this.idInscripcion     = idInscripcion;
        this.idTipoEvidencia   = idTipoEvidencia;
        this.idEstadoDocumento = idEstadoDocumento;
        this.rutaArchivo       = rutaArchivo;
        this.nombreArchivo     = nombreArchivo;
        this.fechaEntrega      = fechaEntrega;
        this.fechaLimite       = fechaLimite;
        this.fechaProrroga     = fechaProrroga;
        this.observaciones     = observaciones;
        this.calificacion      = calificacion;
    }

    /**
     * Retorna el identificador único del Documento en la BD.
     *
     * @return identificador del registro.
     */
    public int getIdDocumento() {
        return idDocumento;
    }

    /**
     * Establece el identificador único del Documento.
     *
     * @param idDocumento Identificador asignado por la base de datos.
     */
    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    /**
     * Retorna el identificador de la inscripción propietaria del documento.
     *
     * @return FK hacia {@code estudiante_inscrito}.
     */
    public int getIdInscripcion() {
        return idInscripcion;
    }

    /**
     * Establece la inscripción propietaria del documento.
     *
     * @param idInscripcion FK hacia {@code estudiante_inscrito}.
     */
    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    /**
     * Retorna el identificador del tipo de evidencia que discrimina
     * el subtipo del documento.
     *
     * @return FK hacia {@code tipo_evidencia}.
     */
    public int getIdTipoEvidencia() {
        return idTipoEvidencia;
    }

    /**
     * Establece el tipo de evidencia del documento.
     *
     * @param idTipoEvidencia FK hacia {@code tipo_evidencia}.
     */
    public void setIdTipoEvidencia(int idTipoEvidencia) {
        this.idTipoEvidencia = idTipoEvidencia;
    }

    /**
     * Retorna el identificador del estado actual del documento.
     *
     * @return FK hacia {@code estado_documento}.
     */
    public int getIdEstadoDocumento() {
        return idEstadoDocumento;
    }

    /**
     * Establece el estado actual del documento.
     *
     * @param idEstadoDocumento FK hacia {@code estado_documento}:
     *                          Pendiente, Entregado, Aprobado, Rechazado
     *                          o ConProrroga.
     */
    public void setIdEstadoDocumento(int idEstadoDocumento) {
        this.idEstadoDocumento = idEstadoDocumento;
    }

    /**
     * Retorna la ruta local del archivo entregado, o {@code null}
     * si aún no se ha entregado.
     *
     * @return ruta del archivo o {@code null}.
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }

    /**
     * Establece la ruta local del archivo entregado.
     *
     * @param rutaArchivo Ruta del archivo; acepta {@code null}.
     */
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    /**
     * Retorna el nombre del archivo para mostrar en la UI,
     * o {@code null} si no aplica.
     *
     * @return nombre del archivo o {@code null}.
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * Establece el nombre del archivo para la UI.
     *
     * @param nombreArchivo Nombre del archivo; acepta {@code null}.
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * Retorna la fecha y hora en que el Estudiante entregó el
     * documento, o {@code null} si aún no fue entregado.
     *
     * @return momento de entrega o {@code null}.
     */
    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    /**
     * Establece la fecha y hora de entrega del documento.
     *
     * @param fechaEntrega Momento de entrega; acepta {@code null}.
     */
    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    /**
     * Retorna la fecha límite de entrega original, o {@code null}
     * si no fue establecida.
     *
     * @return fecha límite original o {@code null}.
     */
    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    /**
     * Establece la fecha límite de entrega original.
     *
     * @param fechaLimite Fecha límite original; acepta {@code null}.
     */
    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    /**
     * Retorna la fecha límite extendida por prórroga, o {@code null}
     * si no hubo prórroga.
     *
     * @return fecha de prórroga o {@code null}.
     */
    public LocalDate getFechaProrroga() {
        return fechaProrroga;
    }

    /**
     * Establece la fecha de prórroga del documento.
     *
     * @param fechaProrroga Fecha límite extendida; acepta {@code null}.
     */
    public void setFechaProrroga(LocalDate fechaProrroga) {
        this.fechaProrroga = fechaProrroga;
    }

    /**
     * Retorna las observaciones del Profesor al evaluar el documento,
     * o {@code null} si no aplica.
     *
     * @return observaciones del evaluador o {@code null}.
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Establece las observaciones del evaluador sobre el documento.
     *
     * @param observaciones Comentarios del Profesor; acepta {@code null}.
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Retorna la calificación asignada al documento. El valor -1.0
     * indica que aún no ha sido calificado.
     *
     * @return calificación en rango 0.0–10.0, o -1.0 si sin calificación.
     */
    public double getCalificacion() {
        return calificacion;
    }

    /**
     * Establece la calificación del documento.
     *
     * @param calificacion Calificación en rango 0.0–10.0; usar -1.0
     *                     para indicar que aún no ha sido calificado.
     */
    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

}
