/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.modelo;

/**
 * Representa un proyecto de práctica profesional publicado por
 * una Organización Vinculada. Los proyectos nacen con estado
 * Disponible (CU Publicar Proyecto) y solo pueden darse de baja
 * si no tienen alumnos asignados (sección 8).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class Proyecto {

    private int    idProyecto;
    private int    idOrganizacion;
    private int    idResponsable;
    private String nombreProyecto;
    private String descripcion;
    private String actividades;
    private String metodologia;
    private int    duracionMeses;
    private String horarioLaboral;
    private String recursos;
    private String responsabilidades;
    private int    cupoMaximo;
    private int    cupoDisponible;
    private String estado;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    public Proyecto() {
    }

    /**
     * Constructor completo para inicializar un Proyecto con todos
     * sus datos.
     *
     * @param idProyecto        Identificador del registro en la BD.
     * @param idOrganizacion    FK hacia {@link OrganizacionVinculada}.
     * @param idResponsable     FK hacia responsable del proyecto.
     * @param nombreProyecto    Nombre descriptivo del proyecto.
     * @param descripcion       Descripción general del proyecto.
     * @param actividades       Actividades que realizará el Estudiante.
     * @param metodologia       Metodología de trabajo.
     * @param duracionMeses     Duración en meses.
     * @param horarioLaboral    Horario en la OV.
     * @param recursos          Recursos provistos por la OV.
     * @param responsabilidades Responsabilidades adicionales.
     * @param cupoMaximo        Número máximo de Estudiantes.
     * @param cupoDisponible    Cupos restantes.
     * @param estado            Disponible o No Activo.
     */
    public Proyecto(int idProyecto, int idOrganizacion,
            int idResponsable, String nombreProyecto,
            String descripcion, String actividades,
            String metodologia, int duracionMeses,
            String horarioLaboral, String recursos,
            String responsabilidades, int cupoMaximo,
            int cupoDisponible, String estado) {
        this.idProyecto        = idProyecto;
        this.idOrganizacion    = idOrganizacion;
        this.idResponsable     = idResponsable;
        this.nombreProyecto    = nombreProyecto;
        this.descripcion       = descripcion;
        this.actividades       = actividades;
        this.metodologia       = metodologia;
        this.duracionMeses     = duracionMeses;
        this.horarioLaboral    = horarioLaboral;
        this.recursos          = recursos;
        this.responsabilidades = responsabilidades;
        this.cupoMaximo        = cupoMaximo;
        this.cupoDisponible    = cupoDisponible;
        this.estado            = estado;
    }

    public int getIdProyecto() { return idProyecto; }
    public void setIdProyecto(int idProyecto) { this.idProyecto = idProyecto; }

    public int getIdOrganizacion() { return idOrganizacion; }
    public void setIdOrganizacion(int idOrganizacion) { this.idOrganizacion = idOrganizacion; }

    public int getIdResponsable() { return idResponsable; }
    public void setIdResponsable(int idResponsable) { this.idResponsable = idResponsable; }

    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getActividades() { return actividades; }
    public void setActividades(String actividades) { this.actividades = actividades; }

    public String getMetodologia() { return metodologia; }
    public void setMetodologia(String metodologia) { this.metodologia = metodologia; }

    public int getDuracionMeses() { return duracionMeses; }
    public void setDuracionMeses(int duracionMeses) { this.duracionMeses = duracionMeses; }

    public String getHorarioLaboral() { return horarioLaboral; }
    public void setHorarioLaboral(String horarioLaboral) { this.horarioLaboral = horarioLaboral; }

    public String getRecursos() { return recursos; }
    public void setRecursos(String recursos) { this.recursos = recursos; }

    public String getResponsabilidades() { return responsabilidades; }
    public void setResponsabilidades(String responsabilidades) { this.responsabilidades = responsabilidades; }

    public int getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = cupoMaximo; }

    public int getCupoDisponible() { return cupoDisponible; }
    public void setCupoDisponible(int cupoDisponible) { this.cupoDisponible = cupoDisponible; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

}
