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

    // --- Seguridad y sesión (SEG-01, SEG-03) ---

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

    /** Estado activo de usuario, proyecto u entidad en la nueva BD. */
    public static final String ESTADO_ACTIVO = "activo";

    /**
     * Estado de baja lógica; solo permite consulta,
     * no edición (nunca se elimina el registro físicamente).
     */
    public static final String ESTADO_INACTIVO = "inactivo";

    /** Estado temporal de una cuenta bloqueada por intentos fallidos. */
    public static final String ESTADO_BLOQUEADO = "Bloqueado";

    /** Estado de un proyecto publicado y visible para los Estudiantes. */
    public static final String ESTADO_PROYECTO_DISPONIBLE = "activo";

    // --- IDs del catálogo entregable (spp_db, tabla entregable) ---
    // Posición 1-15 según el INSERT semilla del script spp_db.sql.

    /** id=1 Oficio de Asignación (DocumentoInicial). */
    public static final int TIPO_EVIDENCIA_OFICIO_ASIGNACION = 1;

    /** id=2 Oficio de Aceptación (DocumentoInicial). */
    public static final int TIPO_EVIDENCIA_OFICIO_ACEPTACION = 2;

    /** id=3 Horario de Clases (DocumentoInicial). */
    public static final int TIPO_EVIDENCIA_HORARIO_CLASES = 3;

    /** id=4 Cronograma (DocumentoInicial). */
    public static final int TIPO_EVIDENCIA_CRONOGRAMA = 4;


    /** id=5 Reporte Mensual 1 (Evidencia). */
    public static final int TIPO_EVIDENCIA_REPORTE_MENSUAL = 5;

    /** id=6 Reporte Mensual 2 (Evidencia). */
    public static final int TIPO_EVIDENCIA_REPORTE_MENSUAL_2 = 6;

    /** id=7 Reporte Mensual 3 (Evidencia). */
    public static final int TIPO_EVIDENCIA_REPORTE_MENSUAL_3 = 7;

    /** id=8 Reporte Mensual 4 (Evidencia). */
    public static final int TIPO_EVIDENCIA_REPORTE_MENSUAL_4 = 8;

    /** id=9 Informe Parcial a las 210 hrs (RN-03). */
    public static final int TIPO_EVIDENCIA_INFORME_PARCIAL = 9;

    /** id=10 Informe Final a las 420 hrs (RN-02). */
    public static final int TIPO_EVIDENCIA_INFORME_FINAL = 10;

    /** id=11 Primera Presentación ante el Cuerpo Colegiado. */
    public static final int TIPO_EVIDENCIA_PRESENTACION = 11;

    /** id=12 Segunda Presentación ante el Cuerpo Colegiado. */
    public static final int TIPO_EVIDENCIA_PRESENTACION_2 = 12;

    /** id=13 Primera Evaluación de la Organización Vinculada. */
    public static final int TIPO_EVIDENCIA_EVALUACION_OV = 13;

    /** id=14 Segunda Evaluación de la Organización Vinculada. */
    public static final int TIPO_EVIDENCIA_EVALUACION_OV_2 = 14;

    /** id=15 Autoevaluación del Estudiante (10 afirmaciones). */
    public static final int TIPO_EVIDENCIA_AUTOEVALUACION = 15;

    // --- Estados de entrega (ENUM en columna entrega.estado) ---

    /** Entrega creada; el Estudiante aún no ha subido archivo. */
    public static final String ESTADO_ENTREGA_NO_ENTREGADA = "noEntregada";

    /** El Estudiante subió el archivo; pendiente de evaluación. */
    public static final String ESTADO_ENTREGA_ENTREGADA = "entregada";

    /** El Profesor evaluó la evidencia correctamente. */
    public static final String ESTADO_ENTREGA_EVALUADA = "evaluada";

    /** El Profesor evaluó la evidencia entregada con retardo. */
    public static final String ESTADO_ENTREGA_CON_RETARDO =
            "evaluadaConRetardo";

    // --- Constantes de estado de documento (enteros) ---
    // Usados por la capa de servicio para indicar el estado deseado.

    /** Código interno: sin entregar. */
    public static final int ESTADO_DOCUMENTO_PENDIENTE = 1;

    /** Código interno: archivo subido por el Estudiante. */
    public static final int ESTADO_DOCUMENTO_ENTREGADO = 2;

    /** Código interno: aprobado por el Profesor. */
    public static final int ESTADO_DOCUMENTO_APROBADO = 3;

    /** Código interno: rechazado por el Profesor. */
    public static final int ESTADO_DOCUMENTO_RECHAZADO = 4;

    /** Código interno: prórroga otorgada. */
    public static final int ESTADO_DOCUMENTO_CON_PRORROGA = 5;

    /** Código interno: calificado por el Profesor. */
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

    // --- Validación de datos del Profesor (CU-Admin.-01) ---

    /** Longitud exacta exigida para el número de personal del Profesor. */
    public static final int LONGITUD_NUMERO_PERSONAL = 10;

    /**
     * Dominio institucional obligatorio en el correo del Profesor
     * y del Coordinador.
     */
    public static final String DOMINIO_CORREO_PROFESOR = "@fei.uv.mx";

}
