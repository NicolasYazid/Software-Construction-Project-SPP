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
 * Representa al Profesor Asesor de Prácticas Profesionales.
 * Es el actor responsable de evaluar reportes, evidencias y
 * asignar la calificación final (sección 4).
 * Extiende {@link Academico} con el atributo de turno.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class Profesor extends Academico {

    private String turno;

    /**
     * Constructor sin argumentos requerido para instanciación
     * desde {@code ResultSet} en la capa de persistencia.
     */
    public Profesor() {
        super();
    }

    /**
     * Constructor completo para inicializar un Profesor con todos
     * sus datos, incluido el turno que lo distingue de un Coordinador.
     *
     * @param id Identificador del registro en la BD.
     * @param numeroPersonal Número de personal UV.
     * @param nombre Nombre(s) cifrado con AES-128.
     * @param apellidoPaterno Apellido paterno cifrado.
     * @param apellidoMaterno Apellido materno cifrado; null si no aplica.
     * @param correo Correo cifrado; identificador de login.
     * @param contrasena Contraseña cifrada con AES-128.
     * @param estado Activo o No Activo.
     * @param fechaRegistro Fecha de creación de la cuenta.
     * @param tiempoServicio Años de servicio en la institución.
     * @param intentosFallidos Intentos de login fallidos consecutivos.
     * @param fechaBloqueo Momento del bloqueo; null si no bloqueado.
     * @param turno Turno de trabajo: Matutino o Vespertino.
     */
    public Profesor(int id, String numeroPersonal,
            String nombre, String apellidoPaterno,
            String apellidoMaterno, String correo,
            String contrasena, String estado,
            LocalDate fechaRegistro, int tiempoServicio,
            int intentosFallidos, LocalDateTime fechaBloqueo,
            String turno) {
        super(id, numeroPersonal, nombre, apellidoPaterno,
                apellidoMaterno, correo, contrasena, estado,
                fechaRegistro, tiempoServicio,
                intentosFallidos, fechaBloqueo);
        this.turno = turno;
    }

    /**
     * Retorna el turno de trabajo del Profesor.
     *
     * @return Matutino o Vespertino.
     */
    public String getTurno() {
        return turno;
    }

    /**
     * Establece el turno de trabajo del Profesor.
     *
     * @param turno Matutino o Vespertino.
     */
    public void setTurno(String turno) {
        this.turno = turno;
    }

}
