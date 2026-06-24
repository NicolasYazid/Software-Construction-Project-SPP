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
 * Representa un mensaje que el Profesor publica para su grupo de
 * Practicantes (CU-31). El mensaje puede incluir un archivo PDF
 * adjunto opcional. Se diferencia de {@code Mensaje} en que va
 * dirigido a todos los integrantes de una inscripción grupal,
 * no a un destinatario individual.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class MensajeGrupo {

    private int           idMensajeGrupo;
    private int           idInscripcion;
    private int           idProfesor;
    private String        texto;
    private String        rutaArchivo;
    private String        nombreArchivo;
    private LocalDateTime fechaPublicacion;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    public MensajeGrupo() {
    }

    /**
     * Constructor completo para inicializar un MensajeGrupo con
     * todos sus datos.
     *
     * @param idMensajeGrupo  Identificador del registro en la BD.
     * @param idInscripcion   FK hacia {@code estudiante_inscrito};
     *                        identifica el grupo receptor.
     * @param idProfesor      FK hacia {@code profesor}; autor del mensaje.
     * @param texto           Cuerpo del mensaje; puede ser {@code null}
     *                        si el mensaje solo contiene un archivo adjunto.
     * @param rutaArchivo     Ruta local del PDF adjunto; {@code null} si
     *                        no hay adjunto.
     * @param nombreArchivo   Nombre del archivo adjunto para mostrar en la
     *                        UI; {@code null} si no hay adjunto.
     * @param fechaPublicacion Fecha y hora en que el Profesor publicó el
     *                        mensaje; no puede ser {@code null}.
     */
    public MensajeGrupo(int idMensajeGrupo, int idInscripcion,
            int idProfesor, String texto, String rutaArchivo,
            String nombreArchivo, LocalDateTime fechaPublicacion) {
        this.idMensajeGrupo   = idMensajeGrupo;
        this.idInscripcion    = idInscripcion;
        this.idProfesor       = idProfesor;
        this.texto            = texto;
        this.rutaArchivo      = rutaArchivo;
        this.nombreArchivo    = nombreArchivo;
        this.fechaPublicacion = fechaPublicacion;
    }

    /**
     * Retorna el identificador único del MensajeGrupo en la BD.
     *
     * @return identificador del registro.
     */
    public int getIdMensajeGrupo() {
        return idMensajeGrupo;
    }

    /**
     * Establece el identificador único del MensajeGrupo.
     *
     * @param idMensajeGrupo Identificador asignado por la base de datos.
     */
    public void setIdMensajeGrupo(int idMensajeGrupo) {
        this.idMensajeGrupo = idMensajeGrupo;
    }

    /**
     * Retorna el identificador de la inscripción grupal receptora.
     *
     * @return FK hacia {@code estudiante_inscrito}.
     */
    public int getIdInscripcion() {
        return idInscripcion;
    }

    /**
     * Establece la inscripción grupal a la que va dirigido el mensaje.
     *
     * @param idInscripcion FK hacia {@code estudiante_inscrito}.
     */
    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    /**
     * Retorna el identificador del Profesor autor del mensaje.
     *
     * @return FK hacia {@code profesor}.
     */
    public int getIdProfesor() {
        return idProfesor;
    }

    /**
     * Establece el Profesor autor del mensaje.
     *
     * @param idProfesor FK hacia {@code profesor}.
     */
    public void setIdProfesor(int idProfesor) {
        this.idProfesor = idProfesor;
    }

    /**
     * Retorna el cuerpo del mensaje, o {@code null} si el mensaje
     * consiste únicamente de un archivo adjunto.
     *
     * @return texto del mensaje o {@code null}.
     */
    public String getTexto() {
        return texto;
    }

    /**
     * Establece el cuerpo del mensaje.
     *
     * @param texto Cuerpo del mensaje; acepta {@code null}.
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * Retorna la ruta local del archivo PDF adjunto, o {@code null}
     * si no hay adjunto.
     *
     * @return ruta del archivo adjunto o {@code null}.
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }

    /**
     * Establece la ruta local del archivo PDF adjunto.
     *
     * @param rutaArchivo Ruta del archivo adjunto; acepta {@code null}.
     */
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    /**
     * Retorna el nombre del archivo adjunto para mostrar en la UI,
     * o {@code null} si no hay adjunto.
     *
     * @return nombre del archivo adjunto o {@code null}.
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * Establece el nombre del archivo adjunto.
     *
     * @param nombreArchivo Nombre del archivo adjunto; acepta {@code null}.
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * Retorna la fecha y hora en que el Profesor publicó el mensaje.
     *
     * @return fecha y hora de publicación; no es {@code null}.
     */
    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    /**
     * Establece la fecha y hora de publicación del mensaje.
     *
     * @param fechaPublicacion Momento en que el Profesor publicó el mensaje.
     */
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

}
