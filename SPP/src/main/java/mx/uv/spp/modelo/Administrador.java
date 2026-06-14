/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representa al Administrador del sistema SPP.
 * Su única función es registrar e inactivar Coordinadores y Profesores.
 * No tiene acceso a información de practicantes (sección 4).
 * No forma parte de la jerarquía académica, por eso no extiende
 * {@link Academico}.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class Administrador {

    private int           idAdministrador;
    private String        nombre;
    private String        correo;
    private String        contrasena;
    private String        estado;
    private LocalDate     fechaRegistro;
    private boolean       contrasenaTemporal;
    private int           intentosFallidos;
    private LocalDateTime fechaBloqueo;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    public Administrador() {
    }

    /**
     * Constructor completo para inicializar un Administrador con
     * todos sus datos.
     *
     * @param idAdministrador    Identificador del registro en la BD.
     * @param nombre             Nombre cifrado con AES-128 (SEG-04).
     * @param correo             Correo cifrado; identificador de login.
     * @param contrasena         Contraseña cifrada con AES-128 (SEG-04).
     * @param estado             Activo o No Activo.
     * @param fechaRegistro      Fecha de creación de la cuenta.
     * @param contrasenaTemporal {@code true} si debe cambiar contraseña
     *                           en el primer inicio de sesión (SEG-02).
     * @param intentosFallidos   Intentos fallidos consecutivos; al llegar
     *                           a 3 la cuenta se bloquea (SEG-01).
     * @param fechaBloqueo       Momento del bloqueo; null si no bloqueado.
     */
    public Administrador(int idAdministrador, String nombre,
            String correo, String contrasena, String estado,
            LocalDate fechaRegistro, boolean contrasenaTemporal,
            int intentosFallidos, LocalDateTime fechaBloqueo) {
        this.idAdministrador    = idAdministrador;
        this.nombre             = nombre;
        this.correo             = correo;
        this.contrasena         = contrasena;
        this.estado             = estado;
        this.fechaRegistro      = fechaRegistro;
        this.contrasenaTemporal = contrasenaTemporal;
        this.intentosFallidos   = intentosFallidos;
        this.fechaBloqueo       = fechaBloqueo;
    }

    /**
     * Retorna el identificador único del Administrador en la BD.
     *
     * @return identificador del registro.
     */
    public int getIdAdministrador() {
        return idAdministrador;
    }

    /**
     * Establece el identificador único del Administrador.
     *
     * @param idAdministrador Identificador asignado por la base de datos.
     */
    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    /**
     * Retorna el nombre del Administrador en formato cifrado.
     *
     * @return nombre cifrado con AES-128.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del Administrador.
     *
     * @param nombre Nombre cifrado con AES-128 (SEG-04).
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna el correo en formato cifrado, usado como
     * identificador de login.
     *
     * @return correo electrónico cifrado con AES-128.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo electrónico del Administrador.
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
     * Establece la contraseña del Administrador.
     *
     * @param contrasena Contraseña cifrada con AES-128 (SEG-04).
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Retorna el estado de la cuenta.
     *
     * @return {@code Activo} o {@code No Activo}.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la cuenta.
     *
     * @param estado Activo o No Activo; el registro nunca se elimina.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Retorna la fecha en que se creó la cuenta.
     *
     * @return fecha de registro de la cuenta.
     */
    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Establece la fecha de creación de la cuenta.
     *
     * @param fechaRegistro Fecha de alta de la cuenta en el sistema.
     */
    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
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
     * Establece si la contraseña es temporal.
     *
     * @param contrasenaTemporal {@code true} obliga el cambio de
     *        contraseña en el primer inicio de sesión (SEG-02).
     */
    public void setContrasenaTemporal(boolean contrasenaTemporal) {
        this.contrasenaTemporal = contrasenaTemporal;
    }

    /**
     * Retorna el número de intentos de login fallidos consecutivos.
     *
     * @return valor entre 0 y MAX_INTENTOS_LOGIN.
     */
    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    /**
     * Establece el contador de intentos de login fallidos.
     *
     * @param intentosFallidos Intentos fallidos acumulados de forma consecutiva.
     */
    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    /**
     * Retorna la fecha y hora del bloqueo de la cuenta,
     * o {@code null} si la cuenta no está bloqueada.
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
