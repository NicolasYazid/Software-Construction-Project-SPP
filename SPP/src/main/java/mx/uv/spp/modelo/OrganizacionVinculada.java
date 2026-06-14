/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.modelo;

/**
 * Representa a una Organización Vinculada (OV), que es la empresa
 * o institución donde el Practicante realiza su práctica profesional.
 * Solo el Coordinador puede dar de alta, actualizar o inactivar OVs.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class OrganizacionVinculada {

    private int    idOrganizacion;
    private String nombreEmpresa;
    private String sector;
    private String ciudad;
    private String direccion;
    private String telefono;
    private String estado;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    public OrganizacionVinculada() {
    }

    /**
     * Constructor completo para inicializar una Organización Vinculada.
     *
     * @param idOrganizacion Identificador del registro en la BD.
     * @param nombreEmpresa  Nombre legal o comercial de la organización.
     * @param sector         Sector al que pertenece: Tecnología,
     *                       Investigación, Salud, etc.
     * @param ciudad         Ciudad donde opera la organización.
     * @param direccion      Dirección completa de la organización.
     * @param telefono       Teléfono de contacto; null si no disponible.
     * @param estado         Activo o No Activo.
     */
    public OrganizacionVinculada(int idOrganizacion, String nombreEmpresa,
            String sector, String ciudad, String direccion,
            String telefono, String estado) {
        this.idOrganizacion = idOrganizacion;
        this.nombreEmpresa  = nombreEmpresa;
        this.sector         = sector;
        this.ciudad         = ciudad;
        this.direccion      = direccion;
        this.telefono       = telefono;
        this.estado         = estado;
    }

    /**
     * Retorna el identificador único de la OV en la BD.
     *
     * @return identificador del registro.
     */
    public int getIdOrganizacion() {
        return idOrganizacion;
    }

    /**
     * Establece el identificador único de la OV.
     *
     * @param idOrganizacion Identificador asignado por la base de datos.
     */
    public void setIdOrganizacion(int idOrganizacion) {
        this.idOrganizacion = idOrganizacion;
    }

    /**
     * Retorna el nombre de la organización.
     *
     * @return nombre legal o comercial de la empresa.
     */
    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    /**
     * Establece el nombre de la organización.
     *
     * @param nombreEmpresa Nombre legal o comercial de la empresa.
     */
    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    /**
     * Retorna el sector al que pertenece la organización.
     *
     * @return sector: Tecnología, Investigación, Salud, etc.
     */
    public String getSector() {
        return sector;
    }

    /**
     * Establece el sector de la organización.
     *
     * @param sector Sector productivo o social de la OV.
     */
    public void setSector(String sector) {
        this.sector = sector;
    }

    /**
     * Retorna la ciudad donde opera la organización.
     *
     * @return ciudad de la OV.
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * Establece la ciudad de la organización.
     *
     * @param ciudad Ciudad donde se ubica la OV.
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    /**
     * Retorna la dirección completa de la organización.
     *
     * @return dirección de la OV.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección de la organización.
     *
     * @param direccion Dirección completa de la OV.
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Retorna el teléfono de la organización, o {@code null}
     * si no fue registrado.
     *
     * @return teléfono de contacto o {@code null}.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono de la organización.
     *
     * @param telefono Teléfono de contacto; acepta {@code null}.
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Retorna el estado de la organización.
     *
     * @return {@code Activo} o {@code No Activo}.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la organización.
     *
     * @param estado Activo o No Activo; el registro nunca se elimina.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

}
