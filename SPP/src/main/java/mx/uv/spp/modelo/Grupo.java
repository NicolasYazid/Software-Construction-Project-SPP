/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.modelo;

/**
 * Representa un Grupo de la Experiencia Educativa de Prácticas
 * Profesionales. Un Grupo pertenece a un ciclo escolar y es
 * atendido por un Profesor Asesor. El NRC identifica al Grupo de
 * forma única dentro del ciclo escolar (convención UV).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class Grupo {

    private int idGrupo;
    private int idCicloEscolar;
    private int idProfesor;
    private String nombre;
    private String nrc;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    public Grupo() {
    }

    /**
     * Constructor completo para inicializar un Grupo con todos
     * sus datos.
     *
     * @param idGrupo Identificador del registro en la BD.
     * @param idCicloEscolar FK hacia {@code ciclo_escolar};
     * ciclo al que pertenece el grupo.
     * @param idProfesor FK hacia {@code profesor};
     * Profesor Asesor responsable del grupo.
     * @param nombre Nombre descriptivo del grupo (p.ej. "Grupo A").
     * @param nrc Número de Registro de Curso asignado por la UV;
     * único dentro del ciclo escolar.
     */
    public Grupo(int idGrupo, int idCicloEscolar,
            int idProfesor, String nombre, String nrc) {
        this.idGrupo = idGrupo;
        this.idCicloEscolar = idCicloEscolar;
        this.idProfesor = idProfesor;
        this.nombre = nombre;
        this.nrc = nrc;
    }

    /**
     * Retorna el identificador único del Grupo en la BD.
     *
     * @return identificador del registro.
     */
    public int getIdGrupo() {
        return idGrupo;
    }

    /**
     * Establece el identificador único del Grupo.
     *
     * @param idGrupo Identificador asignado por la base de datos.
     */
    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    /**
     * Retorna el identificador del ciclo escolar al que pertenece
     * el grupo.
     *
     * @return FK hacia {@code ciclo_escolar}.
     */
    public int getIdCicloEscolar() {
        return idCicloEscolar;
    }

    /**
     * Establece el ciclo escolar del grupo.
     *
     * @param idCicloEscolar FK hacia {@code ciclo_escolar}.
     */
    public void setIdCicloEscolar(int idCicloEscolar) {
        this.idCicloEscolar = idCicloEscolar;
    }

    /**
     * Retorna el identificador del Profesor Asesor responsable
     * del grupo.
     *
     * @return FK hacia {@code profesor}.
     */
    public int getIdProfesor() {
        return idProfesor;
    }

    /**
     * Establece el Profesor Asesor responsable del grupo.
     *
     * @param idProfesor FK hacia {@code profesor}.
     */
    public void setIdProfesor(int idProfesor) {
        this.idProfesor = idProfesor;
    }

    /**
     * Retorna el nombre descriptivo del grupo.
     *
     * @return nombre del grupo (p.ej. "Grupo A").
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre descriptivo del grupo.
     *
     * @param nombre Nombre del grupo; no puede ser nulo ni vacío.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna el Número de Registro de Curso del grupo.
     * Identifica al grupo de forma única dentro del ciclo escolar.
     *
     * @return NRC del grupo.
     */
    public String getNrc() {
        return nrc;
    }

    /**
     * Establece el Número de Registro de Curso del grupo.
     *
     * @param nrc NRC asignado por la UV; único por ciclo escolar.
     */
    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

}
