/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.util;

/**
 * Constantes globales del sistema SPP.
 * Centraliza todos los literales con significado de negocio
 * para eliminar valores mágicos en el resto del código.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public final class Constantes {

    private Constantes() {
    }

    // --- Seguridad y sesión (SEG-01, SEG-02, SEG-03) ---

    /** Máximo de intentos de login antes de bloquear la cuenta. */
    public static final int MAX_INTENTOS_LOGIN = 3;

    /** Minutos que permanece bloqueada una cuenta tras agotar intentos. */
    public static final int MINUTOS_BLOQUEO = 10;

    /** Longitud mínima de contraseña (mayúsculas + minúsculas + números). */
    public static final int MIN_CARACTERES_CONTRASENA = 10;

    /** Minutos de inactividad antes de expirar la sesión activa. */
    public static final int MINUTOS_EXPIRACION_SESION = 15;

    // --- Cifrado (SEG-04) ---

    /** Algoritmo de cifrado simétrico usado en datos sensibles. */
    public static final String ALGORITMO_CIFRADO = "AES";

    /** Transformación completa para el cifrador AES en modo CBC con relleno. */
    public static final String TRANSFORMACION_CIFRADO = "AES/CBC/PKCS5Padding";

    /** Longitud en bits de la clave AES exigida por el estándar del proyecto. */
    public static final int BITS_CLAVE_AES = 128;

    // --- Reglas de negocio de prácticas (RN-01, RN-02, RN-03) ---

    /**
     * Porcentaje mínimo de créditos cubiertos para inscribirse en la EE.
     */
    public static final int PORCENTAJE_CREDITOS_MINIMO = 70;

    /** Total de horas que constituyen una práctica profesional completa. */
    public static final int HORAS_PRACTICA = 420;

    /**
     * Horas acumuladas en que el estudiante debe entregar el informe parcial.
     */
    public static final int HORAS_INFORME_PARCIAL = 210;

    // --- Archivos entregables ---

    /** Tamaño máximo permitido para archivos subidos, expresado en megabytes. */
    public static final int TAMANO_MAX_ARCHIVO_MB = 5;

    /**
     * Tamaño máximo en bytes derivado de TAMANO_MAX_ARCHIVO_MB.
     * Se usa al validar el stream antes de persistir el archivo.
     */
    public static final long TAMANO_MAX_ARCHIVO_BYTES =
            (long) TAMANO_MAX_ARCHIVO_MB * 1024 * 1024;

    /** Extensión de documentos en formato PDF. */
    public static final String EXTENSION_PDF = ".pdf";

    /** Extensión de presentaciones PowerPoint Open XML. */
    public static final String EXTENSION_PPTX = ".pptx";

    // --- Calificaciones ---

    /** Valor numérico mínimo que puede recibir un estudiante. */
    public static final int CALIFICACION_MINIMA = 0;

    /** Valor numérico máximo que puede recibir un estudiante. */
    public static final int CALIFICACION_MAXIMA = 10;

    // --- Autoevaluación del estudiante ---

    /** Número de afirmaciones que componen la autoevaluación. */
    public static final int CRITERIOS_AUTOEVALUACION = 10;

    /** Valor mínimo de la escala de autoevaluación por criterio. */
    public static final int ESCALA_AUTOEVALUACION_MIN = 1;

    /** Valor máximo de la escala de autoevaluación por criterio. */
    public static final int ESCALA_AUTOEVALUACION_MAX = 5;

    // --- Estados de entidades (evita strings sueltos en la BD) ---

    /** Estado de un usuario o entidad operativa en el sistema. */
    public static final String ESTADO_ACTIVO = "Activo";

    /**
     * Estado de una cuenta dada de baja; solo permite consulta,
     * no edición (nunca se elimina el registro físicamente).
     */
    public static final String ESTADO_INACTIVO = "No Activo";

    /** Estado temporal de una cuenta bloqueada por intentos fallidos. */
    public static final String ESTADO_BLOQUEADO = "Bloqueado";

    /** Estado de un proyecto publicado y visible para los estudiantes. */
    public static final String ESTADO_PROYECTO_DISPONIBLE = "Disponible";

    // --- Tipos de evidencia (FK id_tipo_evidencia en BD) ---

    /** Oficio emitido por la OV aceptando al Estudiante. */
    public static final int TIPO_EVIDENCIA_OFICIO_ACEPTACION = 1;

    /** Oficio generado por el sistema al asignar el proyecto. */
    public static final int TIPO_EVIDENCIA_OFICIO_ASIGNACION = 2;

    /** Horario de la Experiencia Educativa (EE). */
    public static final int TIPO_EVIDENCIA_HORARIO_CLASES = 3;

    /** Horario en la OV firmado y sellado por el responsable. */
    public static final int TIPO_EVIDENCIA_HORARIO_LABORAL = 4;

    /** Cronograma de actividades del periodo de práctica. */
    public static final int TIPO_EVIDENCIA_CRONOGRAMA = 5;

    /** Reporte de avance mensual del Estudiante. */
    public static final int TIPO_EVIDENCIA_REPORTE_MENSUAL = 6;

    /** Informe al concluir las primeras 210 horas (RN-03). */
    public static final int TIPO_EVIDENCIA_INFORME_PARCIAL = 7;

    /** Informe al concluir las 420 horas requeridas (RN-02). */
    public static final int TIPO_EVIDENCIA_INFORME_FINAL = 8;

    /** Presentación final ante el Profesor Asesor. */
    public static final int TIPO_EVIDENCIA_PRESENTACION = 9;

    /** Evaluación de la OV realizada por el Estudiante. */
    public static final int TIPO_EVIDENCIA_EVALUACION_OV = 10;

    /** Autoevaluación del Estudiante (10 afirmaciones, escala 1-5). */
    public static final int TIPO_EVIDENCIA_AUTOEVALUACION = 11;

    // --- Estados de documento (FK id_estado_documento en BD) ---

    /** Documento creado; el Estudiante aún no ha entregado archivo. */
    public static final int ESTADO_DOCUMENTO_PENDIENTE = 1;

    /** El Estudiante subió el archivo; pendiente de evaluación. */
    public static final int ESTADO_DOCUMENTO_ENTREGADO = 2;

    /** El Profesor evaluó y aprobó la evidencia. */
    public static final int ESTADO_DOCUMENTO_APROBADO = 3;

    /** El Profesor evaluó y rechazó la evidencia. */
    public static final int ESTADO_DOCUMENTO_RECHAZADO = 4;

    /** El Coordinador o Profesor otorgó prórroga al documento. */
    public static final int ESTADO_DOCUMENTO_CON_PRORROGA = 5;

    /** El Profesor calificó la evidencia (estado propio para calif.). */
    public static final int ESTADO_DOCUMENTO_EVALUADO = 6;

    // --- Autoevaluación Likert (rediseño sesión 4) ---

    /** Afirmaciones que componen la autoevaluación del Estudiante. */
    public static final int NUM_AFIRMACIONES_AUTOEVALUACION = 10;

    /** Puntuación máxima posible (10 afirmaciones × escala máx. 5). */
    public static final int PUNTUACION_MAX_AUTOEVALUACION = 50;

    // --- Rango de calificación asignada por el Profesor (escala 1-10) ---

    /**
     * Mínimo que el Profesor puede asignar a una evidencia; distinto
     * de CALIFICACION_MINIMA (0) que aplica a la calificación final.
     */
    public static final double CALIFICACION_MIN = 1.0;

    /** Máximo que el Profesor puede asignar a una evidencia. */
    public static final double CALIFICACION_MAX = 10.0;

    // --- Centinela de calificación en capa POJO/DAO ---

    /**
     * Valor que el DAO asigna en Java cuando la BD almacena NULL en
     * la columna calificacion. Permite distinguir "sin calificar" de
     * una calificación real de 0.0 (que JDBC mapearía igual sin wasNull).
     */
    public static final double CENTINELA_SIN_CALIFICACION = -1.0;

}
