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
 * Transfiere el resultado de un intento de autenticación entre la
 * capa de persistencia, la de negocio y el controlador JavaFX.
 * Nunca es nulo: incluso un intento fallido produce una instancia
 * con {@code exitoso = false} y un mensaje descriptivo en
 * {@code mensajeError}.
 *
 * <p>Flujo de lectura esperado en el controlador:</p>
 * <pre>
 *   if (resultado.isExitoso()) {
 *       // navegar a pantalla principal
 *   } else {
 *       // mostrar resultado.getMensajeError()
 *   }
 * </pre>
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class ResultadoAutenticacion {

    private boolean       exitoso;
    private String        mensajeError;
    private TipoUsuario   tipo;
    private int           idUsuario;
    private String        nombreCompleto;
    private String        estado;
    private int           intentosFallidos;
    private LocalDateTime fechaBloqueo;

    /**
     * Constructor sin argumentos para instanciación progresiva
     * mediante setters en la capa de persistencia.
     */
    public ResultadoAutenticacion() {
    }

    /**
     * Indica si el intento de autenticación fue exitoso.
     *
     * @return {@code true} si el usuario fue autenticado correctamente.
     */
    public boolean isExitoso() {
        return exitoso;
    }

    /**
     * Establece si el intento fue exitoso.
     *
     * @param exitoso {@code true} si las credenciales son válidas.
     */
    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }

    /**
     * Retorna el mensaje de error para mostrar al usuario.
     *
     * @return descripción del motivo del fallo, o {@code null} si exitoso.
     */
    public String getMensajeError() {
        return mensajeError;
    }

    /**
     * Establece el mensaje de error que verá el usuario.
     *
     * @param mensajeError Mensaje descriptivo del fallo de autenticación.
     */
    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    /**
     * Retorna el rol del usuario autenticado.
     *
     * @return {@link TipoUsuario} que determina la pantalla de destino.
     */
    public TipoUsuario getTipo() {
        return tipo;
    }

    /**
     * Establece el rol del usuario.
     *
     * @param tipo Rol reconocido durante la autenticación.
     */
    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna el identificador primario del usuario en su tabla.
     * Un valor de {@code 0} indica que el usuario no fue encontrado.
     *
     * @return identificador de BD, o {@code 0} si no existe el usuario.
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador del usuario recuperado de la BD.
     *
     * @param idUsuario Clave primaria en la tabla correspondiente al tipo.
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Retorna el nombre completo del usuario ya descifrado.
     * Es {@code null} cuando el usuario no fue encontrado.
     *
     * @return nombre para mostrar en la cabecera de la aplicación.
     */
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    /**
     * Establece el nombre completo descifrado del usuario.
     *
     * @param nombreCompleto Nombre a mostrar en la interfaz.
     */
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    /**
     * Retorna el estado de la cuenta del usuario.
     *
     * @return {@code Activo} o {@code No Activo}.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la cuenta leído de la BD.
     *
     * @param estado Valor de la columna {@code estado} en la tabla del usuario.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Retorna el número de intentos fallidos consecutivos actuales.
     * La capa de negocio usa este valor para calcular cuántos intentos
     * le quedan al usuario antes del bloqueo (SEG-01).
     *
     * @return contador de intentos fallidos sin reiniciar.
     */
    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    /**
     * Establece el contador de intentos fallidos leído de la BD.
     *
     * @param intentosFallidos Valor actual de la columna
     *                         {@code intentos_fallidos}.
     */
    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    /**
     * Retorna el momento en que se bloqueó la cuenta.
     *
     * @return instante del bloqueo, o {@code null} si no está bloqueada.
     */
    public LocalDateTime getFechaBloqueo() {
        return fechaBloqueo;
    }

    /**
     * Establece el momento del bloqueo leído de la BD.
     *
     * @param fechaBloqueo Valor de la columna {@code fecha_bloqueo},
     *                     o {@code null} si la cuenta no está bloqueada.
     */
    public void setFechaBloqueo(LocalDateTime fechaBloqueo) {
        this.fechaBloqueo = fechaBloqueo;
    }

}
