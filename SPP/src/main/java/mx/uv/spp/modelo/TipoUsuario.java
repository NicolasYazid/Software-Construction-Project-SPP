/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.modelo;

/**
 * Roles de usuario del sistema SPP.
 * Determina qué tabla de la base de datos se consulta al autenticar
 * y qué campo se usa como identificador de login: correo electrónico
 * para ADMINISTRADOR, COORDINADOR y PROFESOR; matrícula UV para
 * ESTUDIANTE (sección 4 del CLAUDE.md).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public enum TipoUsuario {

    /** Gestiona altas y bajas de Coordinadores y Profesores. */
    ADMINISTRADOR,

    /** Gestiona proyectos, OV y estudiantes del periodo. */
    COORDINADOR,

    /** Evalúa evidencias y califica a los estudiantes asignados. */
    PROFESOR,

    /**
     * Estudiante asignado formalmente a una OV.
     * Es el único rol que se autentica con matrícula en lugar de
     * correo electrónico.
     */
    ESTUDIANTE
}
