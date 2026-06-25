/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.modelo;

import java.time.LocalDateTime;

/**
 * Representa a un Estudiante que desea inscribirse en la
 * Experiencia Educativa de Prácticas Profesionales.
 * Distinción crítica: un Estudiante queda formalmente asignado
 * a una OV solo al recibir su Oficio de Asignación (sección 6).
 * El login se realiza con matrícula, no con correo (sección 4).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class Estudiante {

    private int           idEstudiante;
    private String        matricula;
    private String        nombre;
    private String        primerApellido;
    private String        segundoApellido;
    private String        correo;
    private String        contrasena;
    private String        idioma;
    private String        lenguaIndigena;
    private int           semestre;
    private String        estado;
    private boolean       contrasenaTemporal;
    private int           intentosFallidos;
    private LocalDateTime fechaBloqueo;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    public Estudiante() {
    }

    /**
     * Constructor completo para inicializar un Estudiante con
     * todos sus datos.
     *
     * @param idEstudiante       Identificador del registro en la BD.
     * @param matricula          Matrícula cifrada con AES-128; identificador
     *                           único de login del estudiante (SEG-04).
     * @param nombre             Nombre(s) cifrado con AES-128.
     * @param primerApellido     Primer apellido cifrado con AES-128.
     * @param segundoApellido    Segundo apellido cifrado; null si no aplica.
     * @param correo             Correo electrónico cifrado (no se usa para
     *                           login, pero es dato de contacto).
     * @param contrasena         Contraseña cifrada con AES-128 (SEG-04).
     * @param idioma             Idioma adicional que habla el estudiante;
     *                           null si no aplica.
     * @param lenguaIndigena     Lengua indígena del estudiante; null si no aplica.
     * @param semestre           Semestre cursado al momento de la inscripción.
     * @param estado             Activo o No Activo.
     * @param contrasenaTemporal {@code true} si debe cambiar contraseña
     *                           en el primer inicio de sesión (SEG-02).
     * @param intentosFallidos   Intentos de login fallidos consecutivos;
     *                           al llegar a 3 la cuenta se bloquea (SEG-01).
     * @param fechaBloqueo       Momento del bloqueo; null si no bloqueado.
     */
    public Estudiante(int idEstudiante, String matricula,
            String nombre, String primerApellido,
            String segundoApellido, String correo,
            String contrasena, String idioma,
            String lenguaIndigena, int semestre, String estado,
            boolean contrasenaTemporal, int intentosFallidos,
            LocalDateTime fechaBloqueo) {
        this.idEstudiante       = idEstudiante;
        this.matricula          = matricula;
        this.nombre             = nombre;
        this.primerApellido     = primerApellido;
        this.segundoApellido    = segundoApellido;
        this.correo             = correo;
        this.contrasena         = contrasena;
        this.idioma             = idioma;
        this.lenguaIndigena     = lenguaIndigena;
        this.semestre           = semestre;
        this.estado             = estado;
        this.contrasenaTemporal = contrasenaTemporal;
        this.intentosFallidos   = intentosFallidos;
        this.fechaBloqueo       = fechaBloqueo;
    }

    /**
     * Retorna el identificador único del Estudiante en la BD.
     *
     * @return identificador del registro.
     */
    public int getIdEstudiante() {
        return idEstudiante;
    }

    /**
     * Establece el identificador único del Estudiante.
     *
     * @param idEstudiante Identificador asignado por la base de datos.
     */
    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    /**
     * Retorna la matrícula cifrada, que es el identificador de login
     * del Estudiante (distinto a los actores académicos).
     *
     * @return matrícula cifrada con AES-128.
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Establece la matrícula del Estudiante.
     *
     * @param matricula Matrícula cifrada con AES-128 (SEG-04).
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Retorna el nombre del Estudiante en formato cifrado.
     *
     * @return nombre cifrado con AES-128.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del Estudiante.
     *
     * @param nombre Nombre(s) cifrado con AES-128 (SEG-04).
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna el primer apellido en formato cifrado.
     *
     * @return primer apellido cifrado con AES-128.
     */
    public String getPrimerApellido() {
        return primerApellido;
    }

    /**
     * Establece el primer apellido del Estudiante.
     *
     * @param primerApellido Primer apellido cifrado con AES-128.
     */
    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    /**
     * Retorna el segundo apellido cifrado, o {@code null} si no tiene.
     *
     * @return segundo apellido cifrado, o {@code null}.
     */
    public String getSegundoApellido() {
        return segundoApellido;
    }

    /**
     * Establece el segundo apellido del Estudiante.
     *
     * @param segundoApellido Segundo apellido cifrado; acepta {@code null}.
     */
    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    /**
     * Retorna el correo electrónico cifrado del Estudiante.
     * No se usa para login; sirve como dato de contacto.
     *
     * @return correo cifrado con AES-128.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo electrónico del Estudiante.
     *
     * @param correo Correo cifrado con AES-128.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Retorna la contraseña en formato cifrado.
     *
     * @return contraseña cifrada con AES-128.
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña del Estudiante.
     *
     * @param contrasena Contraseña cifrada con AES-128 (SEG-04).
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Retorna el idioma adicional del Estudiante, o {@code null}.
     *
     * @return idioma adicional o {@code null} si no aplica.
     */
    public String getIdioma() {
        return idioma;
    }

    /**
     * Establece el idioma adicional del Estudiante.
     *
     * @param idioma Idioma adicional; acepta {@code null}.
     */
    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    /**
     * Retorna la lengua indígena del Estudiante, o {@code null}.
     *
     * @return lengua indígena o {@code null} si no aplica.
     */
    public String getLenguaIndigena() {
        return lenguaIndigena;
    }

    /**
     * Establece la lengua indígena del Estudiante.
     *
     * @param lenguaIndigena Lengua indígena; acepta {@code null}.
     */
    public void setLenguaIndigena(String lenguaIndigena) {
        this.lenguaIndigena = lenguaIndigena;
    }

    /**
     * Retorna el semestre que cursaba el Estudiante al inscribirse.
     *
     * @return número de semestre (valor positivo).
     */
    public int getSemestre() {
        return semestre;
    }

    /**
     * Establece el semestre del Estudiante.
     *
     * @param semestre Semestre cursado al momento de la inscripción.
     */
    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    /**
     * Retorna el estado de la cuenta del Estudiante.
     *
     * @return {@code Activo} o {@code No Activo}.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la cuenta del Estudiante.
     *
     * @param estado Activo o No Activo; el registro nunca se elimina.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Indica si la contraseña es temporal y debe cambiarse en el
     * próximo inicio de sesión exitoso (SEG-02).
     *
     * @return {@code true} si la contraseña es temporal.
     */
    public boolean isContrasenaTemporal() {
        return contrasenaTemporal;
    }

    /**
     * Establece si la contraseña del Estudiante es temporal.
     *
     * @param contrasenaTemporal {@code true} obliga el cambio en el
     *        primer inicio de sesión exitoso (SEG-02).
     */
    public void setContrasenaTemporal(boolean contrasenaTemporal) {
        this.contrasenaTemporal = contrasenaTemporal;
    }

    /**
     * Retorna el contador de intentos de login fallidos consecutivos.
     *
     * @return valor entre 0 y MAX_INTENTOS_LOGIN.
     */
    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    /**
     * Establece el contador de intentos de login fallidos.
     *
     * @param intentosFallidos Intentos fallidos acumulados consecutivamente.
     */
    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    /**
     * Retorna la fecha y hora del bloqueo de la cuenta,
     * o {@code null} si no está bloqueada.
     *
     * @return momento del bloqueo o {@code null}.
     */
    public LocalDateTime getFechaBloqueo() {
        return fechaBloqueo;
    }

    /**
     * Establece la fecha y hora del bloqueo de la cuenta.
     *
     * @param fechaBloqueo Momento del bloqueo; {@code null} para desbloquear.
     */
    public void setFechaBloqueo(LocalDateTime fechaBloqueo) {
        this.fechaBloqueo = fechaBloqueo;
    }

}
