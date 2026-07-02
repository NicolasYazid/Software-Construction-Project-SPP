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
 * o institución donde el Estudiante realiza su práctica profesional.
 * Solo el Coordinador puede dar de alta, actualizar o inactivar OVs.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class OrganizacionVinculada {

    private int idOrganizacion;
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
     * @param nombreEmpresa Nombre legal o comercial de la organización.
     * @param sector Sector al que pertenece: Tecnología,
     * Investigación, Salud, etc.
     * @param ciudad Ciudad donde opera la organización.
     * @param direccion Dirección completa de la organización.
     * @param telefono Teléfono de contacto; null si no disponible.
     * @param estado Activo o No Activo.
     */
    public OrganizacionVinculada(int idOrganizacion, String nombreEmpresa,
            String sector, String ciudad, String direccion,
            String telefono, String estado) {
        this.idOrganizacion = idOrganizacion;
        this.nombreEmpresa = nombreEmpresa;
        this.sector = sector;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.telefono = telefono;
        this.estado = estado;
    }

    public int getIdOrganizacion() { return idOrganizacion; }
    public void setIdOrganizacion(int idOrganizacion) { this.idOrganizacion = idOrganizacion; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

}
