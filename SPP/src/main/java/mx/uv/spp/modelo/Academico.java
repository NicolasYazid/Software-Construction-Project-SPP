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
 * Superclase abstracta que agrupa los atributos comunes de los
 * actores académicos del sistema: Coordinador y Profesor.
 * No debe instanciarse directamente; usar las subclases concretas.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public abstract class Academico {

    private int           id;
    private String        numPersonal;
    private String        nombre;
    private String        primerApellido;
    private String        segundoApellido;
    private String        correo;
    private String        contrasena;
    private String        estado;
    private LocalDate     fechaRegistro;
    private int           tiempoServicio;
    private int           intentosFallidos;
    private LocalDateTime fechaBloqueo;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    protected Academico() {
    }

    /**
     * Constructor completo para inicializar un académico con todos
     * sus datos.
     *
     * @param id                 Identificador del registro en la BD.
     * @param numPersonal        Número de personal UV del académico.
     * @param nombre             Nombre(s) almacenado cifrado AES-128.
     * @param primerApellido     Primer apellido, cifrado con AES-128.
     * @param segundoApellido    Segundo apellido cifrado; null si no aplica.
     * @param correo             Correo cifrado; identificador de login
     *                           para actores académicos (sec. 4).
     * @param contrasena         Contraseña cifrada con AES-128 (SEG-04).
     * @param estado             Estado: Activo o No Activo.
     * @param fechaRegistro      Fecha de creación de la cuenta.
     * @param tiempoServicio     Años de servicio en la institución.
     * @param intentosFallidos   Intentos de login fallidos consecutivos;
     *                           al llegar a 3 se bloquea la cuenta (SEG-01).
     * @param fechaBloqueo       Momento del bloqueo; null si no está bloqueado.
     */
    protected Academico(int id, String numPersonal,
            String nombre, String primerApellido,
            String segundoApellido, String correo,
            String contrasena, String estado,
            LocalDate fechaRegistro, int tiempoServicio,
            int intentosFallidos, LocalDateTime fechaBloqueo) {
        this.id              = id;
        this.numPersonal     = numPersonal;
        this.nombre          = nombre;
        this.primerApellido  = primerApellido;
        this.segundoApellido = segundoApellido;
        this.correo          = correo;
        this.contrasena      = contrasena;
        this.estado          = estado;
        this.fechaRegistro   = fechaRegistro;
        this.tiempoServicio  = tiempoServicio;
        this.intentosFallidos = intentosFallidos;
        this.fechaBloqueo    = fechaBloqueo;
    }

    /**
     * Retorna el identificador único del registro en la base de datos.
     *
     * @return identificador de la subclase (id_coordinador o id_profesor).
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador único del registro.
     *
     * @param id Identificador asignado por la base de datos.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retorna el número de personal UV del académico.
     *
     * @return número de personal institucional.
     */
    public String getNumPersonal() {
        return numPersonal;
    }

    /**
     * Establece el número de personal UV.
     *
     * @param numPersonal Clave institucional única del académico.
     */
    public void setNumPersonal(String numPersonal) {
        this.numPersonal = numPersonal;
    }

    /**
     * Retorna el nombre del académico en formato cifrado.
     *
     * @return nombre cifrado con AES-128.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del académico.
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
     * Establece el primer apellido del académico.
     *
     * @param primerApellido Primer apellido cifrado con AES-128.
     */
    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    /**
     * Retorna el segundo apellido en formato cifrado,
     * o {@code null} si el académico no tiene segundo apellido.
     *
     * @return segundo apellido cifrado, o {@code null}.
     */
    public String getSegundoApellido() {
        return segundoApellido;
    }

    /**
     * Establece el segundo apellido del académico.
     *
     * @param segundoApellido Segundo apellido cifrado; acepta {@code null}.
     */
    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    /**
     * Retorna el correo institucional cifrado, que sirve como
     * identificador de login para los actores académicos.
     *
     * @return correo cifrado con AES-128.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo institucional del académico.
     *
     * @param correo Correo electrónico cifrado con AES-128.
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
     * Establece la contraseña del académico.
     *
     * @param contrasena Contraseña cifrada con AES-128 (SEG-04).
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Construye el nombre completo a partir de nombre y apellidos,
     * omitiendo el segundo apellido si no está presente. Se usa en
     * las tablas del Administrador (columna "Nombre").
     *
     * @return nombre completo en un solo texto, nunca nulo.
     */
    public String getNombreCompleto() {
        StringBuilder completo = new StringBuilder(
                nombre != null ? nombre : "");
        if (primerApellido != null && !primerApellido.isEmpty()) {
            completo.append(" ").append(primerApellido);
        }
        if (segundoApellido != null && !segundoApellido.isEmpty()) {
            completo.append(" ").append(segundoApellido);
        }
        return completo.toString();
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
     * @param fechaRegistro Fecha en que el Administrador dio de alta la cuenta.
     */
    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Retorna los años de servicio del académico en la institución.
     *
     * @return años de servicio (valor entero positivo).
     */
    public int getTiempoServicio() {
        return tiempoServicio;
    }

    /**
     * Establece los años de servicio del académico.
     *
     * @param tiempoServicio Años completos de servicio en la UV.
     */
    public void setTiempoServicio(int tiempoServicio) {
        this.tiempoServicio = tiempoServicio;
    }

    /**
     * Retorna el número de intentos de login fallidos consecutivos.
     *
     * @return valor entre 0 y MAX_INTENTOS_LOGIN (3).
     */
    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    /**
     * Establece el contador de intentos de login fallidos.
     *
     * @param intentosFallidos Intentos fallidos consecutivos acumulados.
     */
    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    /**
     * Retorna la fecha y hora en que se bloqueó la cuenta,
     * o {@code null} si la cuenta no está bloqueada.
     *
     * @return fecha/hora del bloqueo o {@code null}.
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
