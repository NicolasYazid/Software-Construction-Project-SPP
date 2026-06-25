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
 * Representa un mensaje que el Profesor publica para un Grupo
 * completo de Estudiantes (CU-31). Va dirigido a todos los
 * integrantes del Grupo, no a un destinatario individual.
 * Puede contener texto, un PDF adjunto o ambos.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class MensajeGrupo {

    private int           idMensajeGrupo;
    private int           idGrupo;
    private int           idProfesor;
    private String        asunto;
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
     * @param idMensajeGrupo   Identificador del registro en la BD.
     * @param idGrupo          FK hacia {@code grupo}; identifica el
     *                         grupo receptor completo.
     * @param idProfesor       FK hacia {@code profesor}; autor del mensaje.
     * @param asunto           Asunto del mensaje; puede ser {@code null}.
     * @param texto            Cuerpo del mensaje; puede ser {@code null}
     *                         si el mensaje solo contiene un archivo adjunto.
     * @param rutaArchivo      Ruta local del PDF adjunto; {@code null} si
     *                         no hay adjunto.
     * @param nombreArchivo    Nombre del archivo adjunto para mostrar en la
     *                         UI; {@code null} si no hay adjunto.
     * @param fechaPublicacion Fecha y hora en que el Profesor publicó el
     *                         mensaje; no puede ser {@code null}.
     */
    public MensajeGrupo(int idMensajeGrupo, int idGrupo,
            int idProfesor, String asunto, String texto,
            String rutaArchivo, String nombreArchivo,
            LocalDateTime fechaPublicacion) {
        this.idMensajeGrupo   = idMensajeGrupo;
        this.idGrupo          = idGrupo;
        this.idProfesor       = idProfesor;
        this.asunto           = asunto;
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
     * Retorna el identificador del Grupo receptor del mensaje.
     *
     * @return FK hacia {@code grupo}.
     */
    public int getIdGrupo() {
        return idGrupo;
    }

    /**
     * Establece el Grupo al que va dirigido el mensaje.
     *
     * @param idGrupo FK hacia {@code grupo}.
     */
    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
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
     * Retorna el asunto del mensaje, o {@code null} si no se
     * proporcionó uno.
     *
     * @return asunto del mensaje o {@code null}.
     */
    public String getAsunto() {
        return asunto;
    }

    /**
     * Establece el asunto del mensaje.
     *
     * @param asunto Asunto del mensaje; acepta {@code null}.
     */
    public void setAsunto(String asunto) {
        this.asunto = asunto;
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
