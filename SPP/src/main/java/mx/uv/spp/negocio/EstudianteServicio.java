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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mx.uv.spp.modelo.Autoevaluacion;
import mx.uv.spp.modelo.Documento;
import mx.uv.spp.modelo.PeriodoInscripciones;
import mx.uv.spp.modelo.Proyecto;
import mx.uv.spp.modelo.SeleccionProyecto;
import mx.uv.spp.persistencia.dao.AutoevaluacionDAO;
import mx.uv.spp.persistencia.dao.DocumentoDAO;
import mx.uv.spp.persistencia.dao.PeriodoInscripcionesDAO;
import mx.uv.spp.persistencia.dao.SeleccionProyectoDAO;
import mx.uv.spp.util.Constantes;

/**
 * Lógica de negocio para los casos de uso del Estudiante.
 * Cubre la selección de proyectos por prioridad (RN-11), la entrega
 * de documentos y la autoevaluación. No accede a la BD directamente;
 * delega en los DAO recibidos por inyección de dependencia.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class EstudianteServicio {

    private final PeriodoInscripcionesDAO periodoDAO;
    private final SeleccionProyectoDAO    seleccionDAO;
    private final DocumentoDAO            documentoDAO;
    private final AutoevaluacionDAO       autoevaluacionDAO;

    /**
     * Construye el servicio con los DAOs inyectados.
     *
     * @param periodoDAO        DAO de periodos de inscripción; no nulo.
     * @param seleccionDAO      DAO de selección de proyectos; no nulo.
     * @param documentoDAO      DAO de documentos y evidencias; no nulo.
     * @param autoevaluacionDAO DAO de autoevaluaciones; no nulo.
     * @throws IllegalArgumentException si algún parámetro es nulo.
     */
    public EstudianteServicio(
            PeriodoInscripcionesDAO periodoDAO,
            SeleccionProyectoDAO seleccionDAO,
            DocumentoDAO documentoDAO,
            AutoevaluacionDAO autoevaluacionDAO) {
        if (periodoDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de periodos no puede ser nulo.");
        }
        if (seleccionDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de selección no puede ser nulo.");
        }
        if (documentoDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de documentos no puede ser nulo.");
        }
        if (autoevaluacionDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de autoevaluación no puede ser nulo.");
        }
        this.periodoDAO        = periodoDAO;
        this.seleccionDAO      = seleccionDAO;
        this.documentoDAO      = documentoDAO;
        this.autoevaluacionDAO = autoevaluacionDAO;
    }

    /**
     * Verifica si el PeriodoDeInscripciones está activo y si el
     * Estudiante ya registró su ListaDePrioridades. Retorna la lista
     * de Proyectos disponibles para ordenar.
     *
     * @param idInscripcion FK de {@code estudiante_inscrito}.
     * @return lista de proyectos disponibles; nunca nula.
     * @throws IllegalStateException si no hay PeriodoDeInscripciones
     *         activo, si la fecha actual está fuera de su vigencia,
     *         o si el Estudiante ya registró su lista.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public List<Proyecto> obtenerProyectosParaOrdenar(
            int idInscripcion)
            throws SQLException, IllegalStateException {
        PeriodoInscripciones periodo =
                periodoDAO.obtenerDelCicloActivo();
        if (periodo == null) {
            throw new IllegalStateException(
                    "No hay período de inscripciones configurado "
                    + "para el ciclo escolar activo.");
        }
        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(periodo.getFechaInicio())
                || hoy.isAfter(periodo.getFechaCierre())) {
            throw new IllegalStateException(
                    "El período de inscripciones no está vigente. "
                    + "Vigencia: " + periodo.getFechaInicio()
                    + " — " + periodo.getFechaCierre() + ".");
        }
        if (seleccionDAO.existeSeleccionPorInscripcion(
                idInscripcion)) {
            throw new IllegalStateException(
                    "Ya registraste tu lista de prioridades. "
                    + "Esta acción es irreversible (RN-11).");
        }
        return seleccionDAO.obtenerProyectosDisponibles();
    }

    /**
     * Registra la ListaDePrioridades del Estudiante.
     * Valida que la lista no esté vacía, que cada Proyecto tenga
     * un número de prioridad único, y que el número de prioridades
     * coincida con el total de Proyectos disponibles.
     *
     * @param idInscripcion FK de {@code estudiante_inscrito}.
     * @param selecciones   Lista ordenada de selecciones; el campo
     *                      {@code prioridad} debe ser único por
     *                      elemento y mayor que cero.
     * @throws IllegalArgumentException si la lista es inválida.
     * @throws IllegalStateException si el Estudiante ya registró
     *         su lista anteriormente.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public void registrarPrioridades(
            int idInscripcion,
            List<SeleccionProyecto> selecciones)
            throws SQLException, IllegalArgumentException,
                   IllegalStateException {
        if (selecciones == null || selecciones.isEmpty()) {
            throw new IllegalArgumentException(
                    "La lista de prioridades no puede ser vacía.");
        }
        if (seleccionDAO.existeSeleccionPorInscripcion(
                idInscripcion)) {
            throw new IllegalStateException(
                    "Ya registraste tu lista de prioridades. "
                    + "Esta acción es irreversible (RN-11).");
        }
        List<Proyecto> disponibles =
                seleccionDAO.obtenerProyectosDisponibles();
        if (selecciones.size() != disponibles.size()) {
            throw new IllegalArgumentException(
                    "Debes ordenar todos los proyectos disponibles. "
                    + "Esperados: " + disponibles.size()
                    + ", recibidos: " + selecciones.size() + ".");
        }
        validarUnicidadPrioridades(selecciones);
        LocalDateTime ahora = LocalDateTime.now();
        for (SeleccionProyecto sel : selecciones) {
            sel.setIdInscripcion(idInscripcion);
            sel.setFechaSeleccion(ahora);
        }
        seleccionDAO.insertarLista(selecciones);
    }

    /**
     * Registra la entrega de un Documento (PDF). Si ya existe una
     * entrega previa del mismo tipo para esa inscripción, la
     * reemplaza (actualiza). Si no existe, inserta una nueva.
     * Valida que la ruta no sea nula ni vacía.
     *
     * @param idInscripcion   FK de {@code estudiante_inscrito}.
     * @param idTipoEvidencia FK de {@code tipo_evidencia}.
     * @param rutaArchivo     Ruta local del PDF seleccionado;
     *                        no puede ser nula ni vacía.
     * @param nombreArchivo   Nombre del archivo PDF.
     * @throws IllegalArgumentException si {@code rutaArchivo} es
     *         nula o vacía.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public void entregarDocumento(
            int idInscripcion,
            int idTipoEvidencia,
            String rutaArchivo,
            String nombreArchivo)
            throws SQLException, IllegalArgumentException {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La ruta del archivo no puede ser nula ni vacía.");
        }
        Documento existente =
                documentoDAO.obtenerPorInscripcionYTipoUnico(
                        idInscripcion, idTipoEvidencia);
        LocalDateTime ahora = LocalDateTime.now();
        if (existente != null) {
            existente.setRutaArchivo(rutaArchivo);
            existente.setNombreArchivo(nombreArchivo);
            existente.setFechaEntrega(ahora);
            existente.setIdEstadoDocumento(
                    Constantes.ESTADO_DOCUMENTO_ENTREGADO);
            documentoDAO.actualizarEntrega(existente);
        } else {
            Documento nuevo = new Documento(
                    0,
                    idInscripcion,
                    idTipoEvidencia,
                    Constantes.ESTADO_DOCUMENTO_ENTREGADO,
                    rutaArchivo,
                    nombreArchivo,
                    ahora,
                    null,
                    null,
                    null,
                    Constantes.CENTINELA_SIN_CALIFICACION);
            documentoDAO.insertar(nuevo);
        }
    }

    /**
     * Calcula y registra la Autoevaluacion del Estudiante.
     * Primero inserta un Documento de tipo Autoevaluacion, luego
     * calcula {@code puntuacionTotal = suma de afirmacion1..10},
     * luego calcula
     * {@code calificacion = (puntuacionTotal / 50.0) * 10},
     * y finalmente inserta la Autoevaluacion con esos valores.
     * Valida que cada afirmacion esté entre 1 y 5 inclusive.
     *
     * @param idInscripcion FK de {@code estudiante_inscrito}.
     * @param respuestas    Array de 10 enteros, uno por afirmación.
     *                      Cada valor debe estar en [1, 5].
     * @throws IllegalStateException si el Estudiante ya entregó
     *         su Autoevaluacion en el periodo activo.
     * @throws IllegalArgumentException si {@code respuestas} tiene
     *         longitud distinta de 10, o si algún valor está fuera
     *         del rango [1, 5].
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public void entregarAutoevaluacion(
            int idInscripcion,
            int[] respuestas)
            throws SQLException, IllegalStateException,
                   IllegalArgumentException {
        if (respuestas == null
                || respuestas.length
                   != Constantes.NUM_AFIRMACIONES_AUTOEVALUACION) {
            throw new IllegalArgumentException(
                    "Se requieren exactamente "
                    + Constantes.NUM_AFIRMACIONES_AUTOEVALUACION
                    + " respuestas.");
        }
        for (int i = 0;
                i < Constantes.NUM_AFIRMACIONES_AUTOEVALUACION;
                i++) {
            if (respuestas[i] < Constantes.ESCALA_AUTOEVALUACION_MIN
                    || respuestas[i]
                       > Constantes.ESCALA_AUTOEVALUACION_MAX) {
                throw new IllegalArgumentException(
                        "La afirmación " + (i + 1)
                        + " debe estar entre "
                        + Constantes.ESCALA_AUTOEVALUACION_MIN
                        + " y "
                        + Constantes.ESCALA_AUTOEVALUACION_MAX
                        + ". Valor recibido: " + respuestas[i]);
            }
        }
        if (autoevaluacionDAO.existePorInscripcion(
                idInscripcion)) {
            throw new IllegalStateException(
                    "Ya entregaste tu autoevaluación. "
                    + "Esta acción es irreversible.");
        }
        LocalDateTime ahora = LocalDateTime.now();
        Documento docAuto = new Documento(
                0,
                idInscripcion,
                Constantes.TIPO_EVIDENCIA_AUTOEVALUACION,
                Constantes.ESTADO_DOCUMENTO_ENTREGADO,
                null,
                null,
                ahora,
                null,
                null,
                null,
                Constantes.CENTINELA_SIN_CALIFICACION);
        int idDocumento = documentoDAO.insertar(docAuto);
        int puntuacionTotal = 0;
        for (int respuesta : respuestas) {
            puntuacionTotal += respuesta;
        }
        double calificacion =
                (puntuacionTotal
                 / (double) Constantes.PUNTUACION_MAX_AUTOEVALUACION)
                * Constantes.CALIFICACION_MAXIMA;
        Autoevaluacion auto = new Autoevaluacion(
                0, idDocumento,
                respuestas[0],  respuestas[1],  respuestas[2],
                respuestas[3],  respuestas[4],  respuestas[5],
                respuestas[6],  respuestas[7],  respuestas[8],
                respuestas[9],  puntuacionTotal, calificacion);
        autoevaluacionDAO.insertar(auto);
    }

    /**
     * Valida que no existan prioridades duplicadas ni proyectos
     * duplicados en la lista de selección.
     *
     * @param selecciones Lista de selecciones a verificar.
     * @throws IllegalArgumentException si hay prioridades o proyectos
     *         duplicados, o si alguna prioridad es menor que 1.
     */
    private void validarUnicidadPrioridades(
            List<SeleccionProyecto> selecciones) {
        Set<Integer> prioridades = new HashSet<>();
        Set<Integer> proyectos   = new HashSet<>();
        for (SeleccionProyecto sel : selecciones) {
            if (sel.getPrioridad() < 1) {
                throw new IllegalArgumentException(
                        "Las prioridades deben ser mayores que cero.");
            }
            if (!prioridades.add(sel.getPrioridad())) {
                throw new IllegalArgumentException(
                        "Prioridad duplicada: "
                        + sel.getPrioridad() + ".");
            }
            if (!proyectos.add(sel.getIdProyecto())) {
                throw new IllegalArgumentException(
                        "Proyecto duplicado en la lista: "
                        + sel.getIdProyecto() + ".");
            }
        }
    }

}
