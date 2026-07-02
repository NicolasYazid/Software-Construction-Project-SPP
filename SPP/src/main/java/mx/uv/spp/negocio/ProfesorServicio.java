/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.negocio;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import mx.uv.spp.modelo.Documento;
import mx.uv.spp.modelo.MensajeGrupo;
import mx.uv.spp.persistencia.dao.DocumentoDAO;
import mx.uv.spp.persistencia.dao.MensajeGrupoDAO;
import mx.uv.spp.util.Constantes;

/**
 * Lógica de negocio para los casos de uso del Profesor Asesor.
 * Cubre la evaluación de evidencias, la calificación de la
 * OrganizacionVinculada, la prórroga de documentos y la publicación
 * de mensajes al grupo. No accede a la BD directamente; delega
 * en los DAO recibidos por inyección de dependencia.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class ProfesorServicio {

    private final DocumentoDAO documentoDAO;
    private final MensajeGrupoDAO mensajeGrupoDAO;

    /**
     * Construye el servicio con los DAOs inyectados.
     *
     * @param documentoDAO DAO de documentos y evidencias; no nulo.
     * @param mensajeGrupoDAO DAO de mensajes de grupo; no nulo.
     * @throws IllegalArgumentException si algún parámetro es nulo.
     */
    public ProfesorServicio(
            DocumentoDAO documentoDAO,
            MensajeGrupoDAO mensajeGrupoDAO) {
        if (documentoDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de documentos no puede ser nulo.");
        }
        if (mensajeGrupoDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de mensajes no puede ser nulo.");
        }
        this.documentoDAO = documentoDAO;
        this.mensajeGrupoDAO = mensajeGrupoDAO;
    }

    /**
     * Recupera las Evidencias entregadas y sin calificar de los
     * Estudiantes asignados al Profesor dado.
     *
     * @param idProfesor FK del Profesor en tabla {@code profesor}.
     * @return lista de Documentos pendientes de calificación;
     * nunca nula, puede estar vacía.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public List<Documento> obtenerEvidenciasPendientes(
            int idProfesor) throws SQLException {
        return documentoDAO
                .obtenerEntregadosSinCalificarPorProfesor(
                        idProfesor);
    }

    /**
     * Registra la calificación de una Evidencia. Valida que la
     * calificación sea un número entre 1 y 10 inclusive (acepta
     * decimales). Actualiza el estado del Documento a evaluado.
     *
     * @param idDocumento Clave primaria del documento en la BD.
     * @param calificacion Valor numérico a registrar.
     * @throws IllegalArgumentException si {@code calificacion < 1}
     * o {@code calificacion > 10}.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public void calificarEvidencia(
            int idDocumento,
            double calificacion)
            throws SQLException, IllegalArgumentException {
        validarRangoCalificacion(calificacion);
        documentoDAO.actualizarCalificacion(
                idDocumento,
                calificacion,
                Constantes.ESTADO_DOCUMENTO_EVALUADO);
    }

    /**
     * Registra la calificación de la OrganizacionVinculada para un
     * Estudiante. Valida que la calificación sea un número entre 1 y
     * 10 inclusive. Busca el Documento de tipo EvaluacionOV de la
     * inscripción y actualiza su calificación.
     *
     * @param idInscripcion FK de {@code estudiante_inscrito}.
     * @param calificacion Valor numérico a registrar.
     * @throws IllegalArgumentException si {@code calificacion < 1}
     * o {@code calificacion > 10}.
     * @throws IllegalStateException si no existe Documento de tipo
     * EvaluacionOV para esa inscripción, o si ya tiene una
     * calificación registrada.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public void registrarCalificacionOV(
            int idInscripcion,
            double calificacion)
            throws SQLException, IllegalArgumentException,
                   IllegalStateException {
        validarRangoCalificacion(calificacion);
        Documento doc =
                documentoDAO.obtenerPorInscripcionYTipoUnico(
                        idInscripcion,
                        Constantes.TIPO_EVIDENCIA_EVALUACION_OV);
        if (doc == null) {
            throw new IllegalStateException(
                    "No existe un documento de tipo EvaluacionOV "
                    + "para la inscripción " + idInscripcion + ".");
        }
        if (doc.getCalificacion()
                >= Constantes.CALIFICACION_ESCALA_MINIMA) {
            throw new IllegalStateException(
                    "La EvaluacionOV ya tiene calificación "
                    + "registrada: " + doc.getCalificacion() + ".");
        }
        documentoDAO.actualizarCalificacion(
                doc.getIdDocumento(),
                calificacion,
                Constantes.ESTADO_DOCUMENTO_EVALUADO);
    }

    /**
     * Otorga una prórroga a una Evidencia específica. Valida que la
     * nueva fecha sea posterior a la fecha actual y que la Evidencia
     * no tenga ya una prórroga activa.
     *
     * @param idDocumento Clave primaria del documento en la BD.
     * @param nuevaFecha Nueva fecha límite extendida.
     * @throws IllegalArgumentException si {@code nuevaFecha} es nula
     * o no es posterior a la fecha actual.
     * @throws IllegalStateException si la Evidencia ya tiene una
     * prórroga activa vigente.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public void otorgarProrroga(
            int idDocumento,
            LocalDate nuevaFecha)
            throws SQLException, IllegalArgumentException,
                   IllegalStateException {
        if (nuevaFecha == null) {
            throw new IllegalArgumentException(
                    "La fecha de prórroga no puede ser nula.");
        }
        if (!nuevaFecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de prórroga debe ser posterior "
                    + "a la fecha actual.");
        }
        Documento doc = documentoDAO.obtenerPorId(idDocumento);
        if (doc == null) {
            throw new IllegalArgumentException(
                    "No existe un documento con id "
                    + idDocumento + ".");
        }
        LocalDate prorroga = doc.getFechaProrroga();
        if (prorroga != null && prorroga.isAfter(LocalDate.now())) {
            throw new IllegalStateException(
                    "La evidencia ya tiene una prórroga activa "
                    + "vigente hasta " + prorroga + ".");
        }
        documentoDAO.actualizarProrroga(idDocumento, nuevaFecha);
    }

    /**
     * Publica un Mensaje del Profesor a su Grupo completo. Valida
     * que texto y rutaArchivo no sean ambos nulos o vacíos al mismo
     * tiempo (al menos uno debe tener contenido).
     *
     * @param idGrupo FK de {@code grupo}; identifica el grupo
     * receptor completo.
     * @param idProfesor FK del Profesor en tabla {@code profesor}.
     * @param asunto Asunto del mensaje; puede ser nulo.
     * @param texto Cuerpo del mensaje; puede ser nulo si hay
     * archivo adjunto.
     * @param rutaArchivo Ruta local del PDF adjunto; puede ser nula
     * si hay texto.
     * @param nombreArchivo Nombre del archivo adjunto; puede ser nulo.
     * @throws IllegalArgumentException si tanto {@code texto} como
     * {@code rutaArchivo} están vacíos o nulos.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public void publicarMensaje(
            int idGrupo,
            int idProfesor,
            String asunto,
            String texto,
            String rutaArchivo,
            String nombreArchivo)
            throws SQLException, IllegalArgumentException {
        boolean sinTexto = (texto == null
                || texto.trim().isEmpty());
        boolean sinArchivo = (rutaArchivo == null
                || rutaArchivo.trim().isEmpty());
        if (sinTexto && sinArchivo) {
            throw new IllegalArgumentException(
                    "El mensaje debe tener al menos texto o un "
                    + "archivo adjunto.");
        }
        MensajeGrupo mensaje = new MensajeGrupo(
                0,
                idGrupo,
                idProfesor,
                asunto,
                sinTexto ? null : texto.trim(),
                sinArchivo ? null : rutaArchivo,
                nombreArchivo,
                LocalDateTime.now());
        mensajeGrupoDAO.insertar(mensaje);
    }

    /**
     * Valida que una calificación sea un entero en el rango [1, 10].
     *
     * @param calificacion Valor a validar.
     * @throws IllegalArgumentException si está fuera del rango o
     * no es un número entero.
     */
    private void validarRangoCalificacion(double calificacion) {
        if (calificacion % 1.0 != 0.0
                || calificacion < Constantes.CALIFICACION_EVIDENCIA_MINIMA
                || calificacion > Constantes.CALIFICACION_EVIDENCIA_MAXIMA) {
            throw new IllegalArgumentException(
                    "La calificación debe ser un número entero"
                    + " del 1 al 10.");
        }
    }

}
