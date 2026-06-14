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
    private String recurso;
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
     * @param idResponsable     FK hacia {@link ResponsableDeProyecto}.
     * @param nombreProyecto    Nombre descriptivo del proyecto.
     * @param descripcion       Descripción general del proyecto.
     * @param actividades       Actividades que realizará el Practicante.
     * @param metodologia       Metodología de trabajo; null si no especificada.
     * @param duracionMeses     Duración en meses (debe cubrir 420 hrs, RN-02).
     * @param horarioLaboral    Horario en la OV (distinto al HorarioClases).
     * @param recurso           Recursos provistos por la OV; null si ninguno.
     * @param responsabilidades Responsabilidades adicionales del Practicante;
     *                          null si no aplica.
     * @param cupoMaximo        Número máximo de Practicantes que admite.
     * @param cupoDisponible    Cupos restantes; se decrementa al asignar.
     * @param estado            Disponible o No Activo.
     */
    public Proyecto(int idProyecto, int idOrganizacion,
            int idResponsable, String nombreProyecto,
            String descripcion, String actividades,
            String metodologia, int duracionMeses,
            String horarioLaboral, String recurso,
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
        this.recurso           = recurso;
        this.responsabilidades = responsabilidades;
        this.cupoMaximo        = cupoMaximo;
        this.cupoDisponible    = cupoDisponible;
        this.estado            = estado;
    }

    /**
     * Retorna el identificador único del Proyecto en la BD.
     *
     * @return identificador del registro.
     */
    public int getIdProyecto() {
        return idProyecto;
    }

    /**
     * Establece el identificador único del Proyecto.
     *
     * @param idProyecto Identificador asignado por la base de datos.
     */
    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    /**
     * Retorna el identificador de la OV dueña del proyecto.
     *
     * @return FK hacia {@link OrganizacionVinculada}.
     */
    public int getIdOrganizacion() {
        return idOrganizacion;
    }

    /**
     * Establece la OV dueña del proyecto.
     *
     * @param idOrganizacion FK hacia {@link OrganizacionVinculada}.
     */
    public void setIdOrganizacion(int idOrganizacion) {
        this.idOrganizacion = idOrganizacion;
    }

    /**
     * Retorna el identificador del responsable del proyecto en la OV.
     *
     * @return FK hacia {@link ResponsableDeProyecto}.
     */
    public int getIdResponsable() {
        return idResponsable;
    }

    /**
     * Establece el responsable del proyecto en la OV.
     *
     * @param idResponsable FK hacia {@link ResponsableDeProyecto}.
     */
    public void setIdResponsable(int idResponsable) {
        this.idResponsable = idResponsable;
    }

    /**
     * Retorna el nombre descriptivo del proyecto.
     *
     * @return nombre del proyecto.
     */
    public String getNombreProyecto() {
        return nombreProyecto;
    }

    /**
     * Establece el nombre del proyecto.
     *
     * @param nombreProyecto Nombre descriptivo del proyecto.
     */
    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    /**
     * Retorna la descripción general del proyecto.
     *
     * @return descripción del proyecto.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción general del proyecto.
     *
     * @param descripcion Descripción del alcance y objetivo del proyecto.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Retorna las actividades que realizará el Practicante.
     *
     * @return descripción de actividades asignadas.
     */
    public String getActividades() {
        return actividades;
    }

    /**
     * Establece las actividades del Practicante en el proyecto.
     *
     * @param actividades Descripción de las tareas a realizar.
     */
    public void setActividades(String actividades) {
        this.actividades = actividades;
    }

    /**
     * Retorna la metodología de trabajo del proyecto, o {@code null}.
     *
     * @return metodología o {@code null} si no fue especificada.
     */
    public String getMetodologia() {
        return metodologia;
    }

    /**
     * Establece la metodología de trabajo del proyecto.
     *
     * @param metodologia Metodología: Scrum, Kanban, etc.; acepta {@code null}.
     */
    public void setMetodologia(String metodologia) {
        this.metodologia = metodologia;
    }

    /**
     * Retorna la duración del proyecto en meses.
     * Debe ser suficiente para cubrir las 420 horas requeridas (RN-02).
     *
     * @return duración en meses (entero positivo).
     */
    public int getDuracionMeses() {
        return duracionMeses;
    }

    /**
     * Establece la duración del proyecto.
     *
     * @param duracionMeses Duración en meses del proyecto.
     */
    public void setDuracionMeses(int duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    /**
     * Retorna el horario de trabajo en la OV.
     * Distinto al HorarioClases (horario de la EE). Ver glosario sec. 6.
     *
     * @return horario laboral en la OV o {@code null}.
     */
    public String getHorarioLaboral() {
        return horarioLaboral;
    }

    /**
     * Establece el horario de trabajo en la OV.
     *
     * @param horarioLaboral Horario de asistencia en la organización.
     */
    public void setHorarioLaboral(String horarioLaboral) {
        this.horarioLaboral = horarioLaboral;
    }

    /**
     * Retorna los recursos provistos por la OV al Practicante,
     * o {@code null} si no se especificaron.
     *
     * @return recursos disponibles o {@code null}.
     */
    public String getRecurso() {
        return recurso;
    }

    /**
     * Establece los recursos que la OV provee al Practicante.
     *
     * @param recurso Equipos, software u otros recursos; acepta {@code null}.
     */
    public void setRecurso(String recurso) {
        this.recurso = recurso;
    }

    /**
     * Retorna las responsabilidades adicionales del Practicante,
     * o {@code null} si no aplican.
     *
     * @return responsabilidades adicionales o {@code null}.
     */
    public String getResponsabilidades() {
        return responsabilidades;
    }

    /**
     * Establece las responsabilidades adicionales del Practicante.
     *
     * @param responsabilidades Responsabilidades adicionales; acepta {@code null}.
     */
    public void setResponsabilidades(String responsabilidades) {
        this.responsabilidades = responsabilidades;
    }

    /**
     * Retorna el número máximo de Practicantes que admite el proyecto.
     *
     * @return cupo máximo (entero positivo).
     */
    public int getCupoMaximo() {
        return cupoMaximo;
    }

    /**
     * Establece el cupo máximo del proyecto.
     *
     * @param cupoMaximo Número máximo de Practicantes permitidos.
     */
    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    /**
     * Retorna los cupos disponibles restantes en el proyecto.
     * Se decrementa cada vez que el Coordinador asigna un Practicante.
     *
     * @return cupos disponibles (entero ≥ 0).
     */
    public int getCupoDisponible() {
        return cupoDisponible;
    }

    /**
     * Establece los cupos disponibles del proyecto.
     *
     * @param cupoDisponible Cupos restantes después de asignaciones.
     */
    public void setCupoDisponible(int cupoDisponible) {
        this.cupoDisponible = cupoDisponible;
    }

    /**
     * Retorna el estado del proyecto.
     *
     * @return {@code Disponible} o {@code No Activo}.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado del proyecto.
     *
     * @param estado Disponible o No Activo; el registro nunca se elimina.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

}
