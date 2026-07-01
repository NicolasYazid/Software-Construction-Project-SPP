/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.util;

import java.util.regex.Pattern;

/**
 * Validaciones de entrada reutilizables para toda la aplicación SPP.
 * Cada método lanza {@link IllegalArgumentException} cuando la entrada
 * no cumple la restricción, de modo que la capa de presentación pueda
 * capturar la excepción y mostrar el mensaje al usuario directamente.
 * Ningún método modifica estado; todos son estáticos y sin efectos
 * secundarios.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public final class Validador {

    private static final Pattern PATRON_CORREO = Pattern.compile(
            "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    // Formato UV: z minúscula fija + S mayúscula fija + 8 dígitos.
    private static final Pattern PATRON_MATRICULA =
            Pattern.compile("^zS\\d{8}$");

    // Número de personal del Profesor: exactamente 10 dígitos.
    private static final Pattern PATRON_NUMERO_PERSONAL =
            Pattern.compile("^\\d{10}$");

    // Correo institucional de Profesor/Coordinador: dominio fijo
    // @fei.uv.mx, sin caracteres atípicos en la parte local.
    private static final Pattern PATRON_CORREO_PROFESOR = Pattern.compile(
            "^[a-zA-Z0-9][a-zA-Z0-9._%+\\-]*@fei\\.uv\\.mx$");

    // Nombres y apellidos: solo letras (con acentos/ñ), espacios,
    // apóstrofes y guiones; sin dígitos ni símbolos atípicos (FA-04).
    private static final Pattern PATRON_SOLO_LETRAS =
            Pattern.compile("^[\\p{L}\\s'.-]+$");

    private Validador() {
    }

    /**
     * Verifica que una cadena de texto no sea nula ni esté vacía.
     *
     * @param valor      Valor a validar.
     * @param nombreCampo Nombre del campo, incluido en el mensaje de error.
     * @throws IllegalArgumentException si {@code valor} es nulo o vacío.
     */
    public static void validarCadenaNoVacia(String valor,
            String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El campo " + nombreCampo
                    + " no puede estar vacío.");
        }
    }

    /**
     * Verifica que una cadena no supere una longitud máxima.
     *
     * @param valor      Valor a validar; no puede ser nulo.
     * @param maximo     Longitud máxima permitida (valor positivo).
     * @param nombreCampo Nombre del campo para el mensaje de error.
     * @throws IllegalArgumentException si {@code valor} es nulo o supera
     *         {@code maximo} caracteres.
     */
    public static void validarLongitudMaxima(String valor, int maximo,
            String nombreCampo) {
        if (valor == null) {
            throw new IllegalArgumentException(
                    "El campo " + nombreCampo + " no puede ser nulo.");
        }
        if (valor.length() > maximo) {
            throw new IllegalArgumentException(
                    "El campo " + nombreCampo
                    + " no puede superar " + maximo + " caracteres.");
        }
    }

    /**
     * Verifica que una contraseña cumpla SEG-03: mínimo
     * {@value Constantes#MIN_CARACTERES_CONTRASENA} caracteres,
     * con al menos una mayúscula, una minúscula y un dígito.
     *
     * @param contrasena Contraseña en texto plano a validar.
     * @throws IllegalArgumentException si {@code contrasena} es nula,
     *         vacía, demasiado corta o no contiene los tipos requeridos.
     */
    public static void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException(
                    "La contraseña no puede estar vacía.");
        }
        if (contrasena.length() < Constantes.MIN_CARACTERES_CONTRASENA) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener mínimo "
                    + Constantes.MIN_CARACTERES_CONTRASENA
                    + " caracteres.");
        }
        boolean tieneMayuscula = false;
        boolean tieneMinuscula = false;
        boolean tieneDigito    = false;

        for (char caracter : contrasena.toCharArray()) {
            if (Character.isUpperCase(caracter)) {
                tieneMayuscula = true;
            }
            if (Character.isLowerCase(caracter)) {
                tieneMinuscula = true;
            }
            if (Character.isDigit(caracter)) {
                tieneDigito = true;
            }
        }
        if (!tieneMayuscula || !tieneMinuscula || !tieneDigito) {
            throw new IllegalArgumentException(
                    "La contraseña debe contener al menos una "
                    + "mayúscula, una minúscula y un número (SEG-03).");
        }
    }

    /**
     * Verifica que un correo electrónico tenga formato válido.
     * Se usa como dato de contacto para todos los usuarios y como
     * identificador de login para actores académicos (sección 4).
     *
     * @param correo Dirección de correo a validar.
     * @throws IllegalArgumentException si {@code correo} es nulo, vacío
     *         o no cumple el formato estándar de correo electrónico.
     */
    public static void validarCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El correo electrónico no puede estar vacío.");
        }
        if (!PATRON_CORREO.matcher(correo.trim()).matches()) {
            throw new IllegalArgumentException(
                    "El correo \"" + correo
                    + "\" no tiene un formato válido.");
        }
    }

    /**
     * Verifica que una matrícula tenga el formato UV: {@code z}
     * minúscula fija, seguida de {@code S} mayúscula fija, seguida
     * de 8 dígitos. La matrícula es el identificador de login del
     * Estudiante (sección 4).
     *
     * @param matricula Matrícula a validar (ej. zS21013417).
     * @throws IllegalArgumentException si {@code matricula} es nula, vacía
     *         o no sigue el formato esperado.
     */
    public static void validarMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La matrícula no puede estar vacía.");
        }
        if (!PATRON_MATRICULA.matcher(matricula.trim()).matches()) {
            throw new IllegalArgumentException(
                    "La matrícula \"" + matricula
                    + "\" debe tener formato zS + 8 dígitos "
                    + "(ej. zS21013417).");
        }
    }

    /**
     * Verifica que el número de personal de un Profesor tenga
     * exactamente {@value Constantes#LONGITUD_NUMERO_PERSONAL}
     * dígitos numéricos (CU-Admin.-01).
     *
     * @param numeroPersonal Número de personal a validar.
     * @throws IllegalArgumentException si es nulo, vacío o no tiene
     *         exactamente {@value Constantes#LONGITUD_NUMERO_PERSONAL}
     *         dígitos.
     */
    public static void validarNumeroPersonal(String numeroPersonal) {
        if (numeroPersonal == null
                || numeroPersonal.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El número de personal no puede estar vacío.");
        }
        if (!PATRON_NUMERO_PERSONAL.matcher(
                numeroPersonal.trim()).matches()) {
            throw new IllegalArgumentException(
                    "El número de personal debe tener exactamente "
                    + Constantes.LONGITUD_NUMERO_PERSONAL
                    + " dígitos numéricos.");
        }
    }

    /**
     * Verifica que el correo institucional de un Profesor o
     * Coordinador use el dominio fijo
     * {@value Constantes#DOMINIO_CORREO_PROFESOR} y no contenga
     * caracteres atípicos en la parte local (CU-Admin.-01).
     *
     * @param correo Correo institucional a validar.
     * @throws IllegalArgumentException si es nulo, vacío o no cumple
     *         el formato {@code nombre@fei.uv.mx}.
     */
    public static void validarCorreoInstitucionalProfesor(
            String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El correo institucional no puede estar vacío.");
        }
        if (!PATRON_CORREO_PROFESOR.matcher(
                correo.trim()).matches()) {
            throw new IllegalArgumentException(
                    "El correo \"" + correo
                    + "\" debe tener el formato nombre"
                    + Constantes.DOMINIO_CORREO_PROFESOR + ".");
        }
    }

    /**
     * Verifica que un campo de texto (nombre o apellido) contenga
     * únicamente letras, espacios, apóstrofes, puntos o guiones, sin
     * dígitos ni símbolos atípicos (CU-Admin.-01, FA-04).
     *
     * @param valor       Valor a validar.
     * @param nombreCampo Nombre del campo, incluido en el mensaje de error.
     * @throws IllegalArgumentException si {@code valor} es nulo, vacío
     *         o contiene dígitos u otros caracteres no permitidos.
     */
    public static void validarSoloLetras(String valor,
            String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El campo " + nombreCampo
                    + " no puede estar vacío.");
        }
        if (!PATRON_SOLO_LETRAS.matcher(valor.trim()).matches()) {
            throw new IllegalArgumentException(
                    "El campo " + nombreCampo
                    + " no debe contener números ni símbolos, "
                    + "solo letras.");
        }
    }

    /**
     * Verifica que una calificación esté dentro del rango válido:
     * entre {@value Constantes#CALIFICACION_MINIMA} y
     * {@value Constantes#CALIFICACION_MAXIMA} (inclusive).
     *
     * @param calificacion Valor numérico de la calificación a validar.
     * @throws IllegalArgumentException si {@code calificacion} está fuera
     *         del rango [0, 10].
     */
    public static void validarCalificacion(double calificacion) {
        if (calificacion < Constantes.CALIFICACION_MINIMA
                || calificacion > Constantes.CALIFICACION_MAXIMA) {
            throw new IllegalArgumentException(
                    "La calificación debe ser un valor entre "
                    + Constantes.CALIFICACION_MINIMA + " y "
                    + Constantes.CALIFICACION_MAXIMA
                    + ". Valor recibido: " + calificacion);
        }
    }

    /**
     * Verifica que la puntuación de un criterio de autoevaluación
     * esté en la escala válida: entre
     * {@value Constantes#ESCALA_AUTOEVALUACION_MIN} y
     * {@value Constantes#ESCALA_AUTOEVALUACION_MAX} (inclusive).
     *
     * @param puntuacion  Valor entero del criterio a validar.
     * @param nombreCriterio Nombre del criterio evaluado, para el mensaje.
     * @throws IllegalArgumentException si {@code puntuacion} está fuera
     *         del rango [1, 5] definido en la sección 8 del proyecto.
     */
    public static void validarCriterioAutoevaluacion(int puntuacion,
            String nombreCriterio) {
        if (puntuacion < Constantes.ESCALA_AUTOEVALUACION_MIN
                || puntuacion > Constantes.ESCALA_AUTOEVALUACION_MAX) {
            throw new IllegalArgumentException(
                    "El criterio \"" + nombreCriterio
                    + "\" debe tener un valor entre "
                    + Constantes.ESCALA_AUTOEVALUACION_MIN + " y "
                    + Constantes.ESCALA_AUTOEVALUACION_MAX
                    + ". Valor recibido: " + puntuacion);
        }
    }

    /**
     * Verifica que el tamaño de un archivo no supere el límite
     * permitido de {@value Constantes#TAMANO_MAX_ARCHIVO_MB} MB.
     *
     * @param tamanoBytes   Tamaño del archivo en bytes.
     * @param nombreArchivo Nombre del archivo, incluido en el mensaje.
     * @throws IllegalArgumentException si {@code tamanoBytes} es negativo
     *         o supera {@link Constantes#TAMANO_MAX_ARCHIVO_BYTES}.
     */
    public static void validarTamanoArchivo(long tamanoBytes,
            String nombreArchivo) {
        if (tamanoBytes < 0) {
            throw new IllegalArgumentException(
                    "El tamaño del archivo no puede ser negativo.");
        }
        if (tamanoBytes > Constantes.TAMANO_MAX_ARCHIVO_BYTES) {
            throw new IllegalArgumentException(
                    "El archivo \"" + nombreArchivo
                    + "\" supera el tamaño máximo permitido de "
                    + Constantes.TAMANO_MAX_ARCHIVO_MB + " MB.");
        }
    }

}
