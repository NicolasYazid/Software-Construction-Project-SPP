/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.persistencia.dao;

import java.sql.SQLException;
import mx.uv.spp.modelo.PeriodoInscripciones;

/**
 * Contrato de acceso a datos para la tabla
 * {@code periodo_inscripciones}. El Coordinador establece un único
 * periodo de inscripciones por ciclo escolar activo (CU-20).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public interface PeriodoInscripcionesDAO {

    /**
     * Recupera el periodo de inscripciones del ciclo escolar activo
     * ({@code ciclo_escolar.activo = 1}), o {@code null} si aún no
     * ha sido establecido.
     *
     * @return el {@link PeriodoInscripciones} del ciclo activo, o
     *         {@code null} si no existe.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    PeriodoInscripciones obtenerDelCicloActivo() throws SQLException;

    /**
     * Indica si el ciclo escolar activo ya tiene un periodo de
     * inscripciones registrado.
     *
     * @return {@code true} si existe al menos una fila en
     *         {@code periodo_inscripciones} para el ciclo activo.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    boolean existeEnCicloActivo() throws SQLException;

    /**
     * Inserta un nuevo periodo de inscripciones. Debe invocarse
     * solo si {@code existeEnCicloActivo()} retorna {@code false}.
     *
     * @param periodo Datos del periodo a insertar; su campo
     *                {@code idPeriodoInscripciones} es ignorado.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    void insertar(PeriodoInscripciones periodo) throws SQLException;

}
