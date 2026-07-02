/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.modelo;

/**
 * Representa al encargado dentro de la Organización Vinculada (OV)
 * que supervisa directamente al Estudiante en campo.
 * Cada proyecto tiene un único responsable; un responsable puede
 * estar asociado a varios proyectos dentro de la misma OV.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class ResponsableDeProyecto {

    private int idResponsable;
    private int idOrganizacion;
    private String nombreEncargado;
    private String cargoEncargado;
    private String emailEncargado;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    public ResponsableDeProyecto() {
    }

    /**
     * Constructor completo para inicializar un ResponsableDeProyecto.
     *
     * @param idResponsable Identificador del registro en la BD.
     * @param idOrganizacion FK hacia la OV a la que pertenece.
     * @param nombreEncargado Nombre completo del encargado en la OV.
     * @param cargoEncargado Cargo o puesto del encargado en la OV.
     * @param emailEncargado Correo electrónico de contacto del encargado.
     */
    public ResponsableDeProyecto(int idResponsable, int idOrganizacion,
            String nombreEncargado, String cargoEncargado,
            String emailEncargado) {
        this.idResponsable = idResponsable;
        this.idOrganizacion = idOrganizacion;
        this.nombreEncargado = nombreEncargado;
        this.cargoEncargado = cargoEncargado;
        this.emailEncargado = emailEncargado;
    }

    /**
     * Retorna el identificador único del responsable en la BD.
     *
     * @return identificador del registro.
     */
    public int getIdResponsable() {
        return idResponsable;
    }

    /**
     * Establece el identificador único del responsable.
     *
     * @param idResponsable Identificador asignado por la base de datos.
     */
    public void setIdResponsable(int idResponsable) {
        this.idResponsable = idResponsable;
    }

    /**
     * Retorna el identificador de la OV a la que pertenece
     * este responsable.
     *
     * @return FK de {@link OrganizacionVinculada}.
     */
    public int getIdOrganizacion() {
        return idOrganizacion;
    }

    /**
     * Establece la OV a la que pertenece este responsable.
     *
     * @param idOrganizacion FK hacia {@link OrganizacionVinculada}.
     */
    public void setIdOrganizacion(int idOrganizacion) {
        this.idOrganizacion = idOrganizacion;
    }

    /**
     * Retorna el nombre completo del encargado en la OV.
     *
     * @return nombre del encargado.
     */
    public String getNombreEncargado() {
        return nombreEncargado;
    }

    /**
     * Establece el nombre completo del encargado.
     *
     * @param nombreEncargado Nombre completo del responsable de la OV.
     */
    public void setNombreEncargado(String nombreEncargado) {
        this.nombreEncargado = nombreEncargado;
    }

    /**
     * Retorna el cargo del encargado dentro de la OV.
     *
     * @return cargo o puesto del encargado.
     */
    public String getCargoEncargado() {
        return cargoEncargado;
    }

    /**
     * Establece el cargo del encargado dentro de la OV.
     *
     * @param cargoEncargado Puesto o rol del responsable en la organización.
     */
    public void setCargoEncargado(String cargoEncargado) {
        this.cargoEncargado = cargoEncargado;
    }

    /**
     * Retorna el correo de contacto del encargado.
     *
     * @return correo electrónico del responsable.
     */
    public String getEmailEncargado() {
        return emailEncargado;
    }

    /**
     * Establece el correo de contacto del encargado.
     *
     * @param emailEncargado Correo electrónico del responsable en la OV.
     */
    public void setEmailEncargado(String emailEncargado) {
        this.emailEncargado = emailEncargado;
    }

}
