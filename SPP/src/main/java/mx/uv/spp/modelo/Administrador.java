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
 * No tiene acceso a información de estudiantes (sección 4).
 * No forma parte de la jerarquía académica, por eso no extiende
 * {@link Academico}.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class Administrador {

    private int idAdministrador;
    private String nombre;
    private String correo;
    private String contrasena;
    private LocalDate fechaRegistro;
    private int intentosFallidos;
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
     * @param idAdministrador Identificador del registro en la BD.
     * @param nombre Nombre cifrado con AES-128 (SEG-04).
     * @param correo Correo cifrado; identificador de login.
     * @param contrasena Contraseña cifrada con AES-128 (SEG-04).
     * @param fechaRegistro Fecha de creación de la cuenta.
     * @param intentosFallidos Intentos fallidos consecutivos; al llegar
     * a 3 la cuenta se bloquea (SEG-01).
     * @param fechaBloqueo Momento del bloqueo; null si no bloqueado.
     */
    public Administrador(int idAdministrador, String nombre,
            String correo, String contrasena,
            LocalDate fechaRegistro, int intentosFallidos,
            LocalDateTime fechaBloqueo) {
        this.idAdministrador = idAdministrador;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.fechaRegistro = fechaRegistro;
        this.intentosFallidos = intentosFallidos;
        this.fechaBloqueo = fechaBloqueo;
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
