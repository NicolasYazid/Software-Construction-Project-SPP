/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.persistencia.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import mx.uv.spp.modelo.Documento;

/**
 * Contrato de acceso a datos para la tabla {@code documento}.
 * Centraliza las operaciones CRUD sobre documentos e evidencias
 * de todos los tipos (DocumentoInicial y Evidencia, sección 6).
 * La implementación JDBC es la única que conoce nombres de tablas
 * y columnas.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public interface DocumentoDAO {

    /**
     * Recupera un documento por su clave primaria, o {@code null}
     * si no existe ningún registro con ese identificador.
     *
     * @param idDocumento Clave primaria en la tabla {@code documento}.
     * @return el documento encontrado o {@code null}.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    Documento obtenerPorId(int idDocumento) throws SQLException;

    /**
     * Recupera todos los documentos de un tipo específico para
     * una inscripción dada.
     *
     * @param idInscripcion  FK de {@code estudiante_inscrito}.
     * @param idTipoEvidencia FK de {@code tipo_evidencia}.
     * @return lista (posiblemente vacía) de documentos encontrados.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    List<Documento> obtenerPorInscripcionYTipo(
            int idInscripcion, int idTipoEvidencia)
            throws SQLException;

    /**
     * Recupera el único documento de un tipo para una inscripción.
     * Retorna {@code null} si no existe ningún registro.
     *
     * @param idInscripcion   FK de {@code estudiante_inscrito}.
     * @param idTipoEvidencia FK de {@code tipo_evidencia}.
     * @return el documento encontrado, o {@code null}.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    Documento obtenerPorInscripcionYTipoUnico(
            int idInscripcion, int idTipoEvidencia)
            throws SQLException;

    /**
     * Inserta un nuevo documento y retorna el id generado por la BD.
     *
     * @param documento Datos del documento a insertar; su campo
     *                  {@code idDocumento} es ignorado.
     * @return identificador auto-generado del registro insertado.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    int insertar(Documento documento) throws SQLException;

    /**
     * Actualiza {@code ruta_archivo}, {@code nombre_archivo},
     * {@code fecha_entrega} e {@code id_estado_documento} de
     * un documento existente. Usado al entregar o re-entregar
     * un archivo (la re-entrega sobrescribe, sección 8).
     *
     * @param documento Documento con {@code idDocumento} válido
     *                  y los campos de entrega ya poblados.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    void actualizarEntrega(Documento documento) throws SQLException;

    /**
     * Actualiza {@code calificacion} e {@code id_estado_documento}
     * de un documento. Invocado por el Profesor al evaluar.
     *
     * @param idDocumento       Clave primaria del documento.
     * @param calificacion      Valor numérico en rango 0.0–10.0.
     * @param idEstadoDocumento Nuevo estado (p. ej. Aprobado).
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    void actualizarCalificacion(int idDocumento,
            double calificacion, int idEstadoDocumento)
            throws SQLException;

    /**
     * Actualiza la {@code fecha_prorroga} de un documento existente.
     *
     * @param idDocumento  Clave primaria del documento.
     * @param fechaProrroga Nueva fecha límite extendida.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    void actualizarProrroga(int idDocumento,
            LocalDate fechaProrroga) throws SQLException;

    /**
     * Recupera todos los documentos en estado {@code Entregado}
     * y sin calificación asignada ({@code calificacion IS NULL})
     * que pertenecen a Estudiantes asignados al Profesor indicado.
     * Usado por el Profesor en el CU-29 (Evaluar Evidencia).
     *
     * @param idProfesor Clave primaria en la tabla {@code profesor}.
     * @return lista (posiblemente vacía) de documentos pendientes
     *         de evaluación.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    List<Documento> obtenerEntregadosSinCalificarPorProfesor(
            int idProfesor) throws SQLException;

    /**
     * Recupera documentos de tipo EvaluacionOV (entregados y sin
     * calificación) de Estudiantes en grupos del Profesor dado.
     * Usado en CU-Est.-27 (Asignar calificación de la OV).
     *
     * @param idProfesor Clave primaria en la tabla {@code profesor}.
     * @return lista de documentos OV pendientes de calificación.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    List<Documento> obtenerOVSinCalificarPorProfesor(
            int idProfesor) throws SQLException;

}
