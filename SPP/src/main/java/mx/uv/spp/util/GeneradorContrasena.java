/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.util;

import java.security.SecureRandom;

/**
 * Genera contraseñas aleatorias que cumplen con SEG-03:
 * mínimo {@value Constantes#MIN_CARACTERES_CONTRASENA} caracteres,
 * al menos una mayúscula, una minúscula y un dígito.
 * Usa {@link SecureRandom} para garantizar aleatoriedad criptográfica,
 * ya que las contraseñas se envían a usuarios del sistema.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public final class GeneradorContrasena {

    private static final String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITOS    = "0123456789";

    private static final String TODOS =
            MAYUSCULAS + MINUSCULAS + DIGITOS;

    // 2 caracteres adicionales sobre el mínimo para mayor entropía.
    private static final int LONGITUD_CONTRASENA_GENERADA =
            Constantes.MIN_CARACTERES_CONTRASENA + 2;

    private static final SecureRandom ALEATORIO = new SecureRandom();

    private GeneradorContrasena() {
    }

    /**
     * Genera una contraseña aleatoria que cumple SEG-03.
     * Garantiza al menos una mayúscula, una minúscula y un dígito,
     * y aplica una mezcla Fisher-Yates para que las posiciones
     * obligatorias no sean predecibles.
     *
     * @return contraseña aleatoria de
     *         {@value #LONGITUD_CONTRASENA_GENERADA} caracteres.
     */
    public static String generarContrasena() {
        char[] contrasena = new char[LONGITUD_CONTRASENA_GENERADA];

        contrasena[0] = MAYUSCULAS.charAt(
                ALEATORIO.nextInt(MAYUSCULAS.length()));
        contrasena[1] = MINUSCULAS.charAt(
                ALEATORIO.nextInt(MINUSCULAS.length()));
        contrasena[2] = DIGITOS.charAt(
                ALEATORIO.nextInt(DIGITOS.length()));

        for (int i = 3; i < LONGITUD_CONTRASENA_GENERADA; i++) {
            contrasena[i] = TODOS.charAt(
                    ALEATORIO.nextInt(TODOS.length()));
        }
        mezclarArreglo(contrasena);
        return new String(contrasena);
    }

    /**
     * Aplica el algoritmo Fisher-Yates para mezclar el arreglo de
     * caracteres en su lugar, evitando que los caracteres obligatorios
     * (mayúscula, minúscula, dígito) aparezcan siempre al inicio.
     *
     * @param arreglo Arreglo de caracteres a mezclar; no puede ser nulo.
     * @throws IllegalArgumentException si {@code arreglo} es nulo.
     */
    private static void mezclarArreglo(char[] arreglo) {
        if (arreglo == null) {
            throw new IllegalArgumentException(
                    "El arreglo a mezclar no puede ser nulo.");
        }
        for (int i = arreglo.length - 1; i > 0; i--) {
            int j = ALEATORIO.nextInt(i + 1);
            char temp  = arreglo[i];
            arreglo[i] = arreglo[j];
            arreglo[j] = temp;
        }
    }

}
