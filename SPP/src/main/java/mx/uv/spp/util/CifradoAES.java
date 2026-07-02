/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utilidad de cifrado y descifrado AES-128 en modo CBC (SEG-04).
 * Cifra nombre, correo y contraseña de todos los usuarios, y las
 * actividades de los estudiantes, antes de persistirlos en la BD.
 * La clave y el IV se leen de {@code bd.properties} para evitar
 * credenciales en el código fuente.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public final class CifradoAES {

    private static final String RUTA_CONFIG = "/bd.properties";

    private static final int LONGITUD_CLAVE_BYTES =
            Constantes.BITS_CLAVE_AES / 8;

    private static SecretKeySpec claveSecreta;
    private static IvParameterSpec vectorInicializacion;

    private CifradoAES() {
    }

    /**
     * Cifra un texto plano con AES-128-CBC y lo devuelve en Base64.
     * El resultado es el valor que se almacena en la base de datos.
     *
     * @param texto Texto plano a cifrar; no puede ser nulo ni vacío.
     * @return cadena en Base64 que representa el texto cifrado.
     * @throws IllegalArgumentException si {@code texto} es nulo o vacío.
     * @throws RuntimeException si ocurre un error del motor criptográfico,
     * lo que indica una configuración incorrecta del entorno.
     */
    public static String cifrar(String texto) {
        if (texto == null || texto.isEmpty()) {
            throw new IllegalArgumentException(
                    "El texto a cifrar no puede ser nulo ni vacío.");
        }
        inicializarClavesSiNecesario();
        try {
            Cipher cifrador = Cipher.getInstance(
                    Constantes.TRANSFORMACION_CIFRADO);
            cifrador.init(Cipher.ENCRYPT_MODE, claveSecreta,
                    vectorInicializacion);
            byte[] bytesTexto = texto.getBytes(StandardCharsets.UTF_8);
            byte[] bytesCifrados = cifrador.doFinal(bytesTexto);
            return Base64.getEncoder().encodeToString(bytesCifrados);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(
                    "Error al cifrar con AES-128: " + e.getMessage(), e);
        }
    }

    /**
     * Descifra un texto cifrado en Base64 con AES-128-CBC.
     * Se usa para leer de la BD y comparar con entradas del usuario.
     *
     * @param textoCifrado Cadena Base64 proveniente de la base de datos;
     * no puede ser nula ni vacía.
     * @return texto plano original antes del cifrado.
     * @throws IllegalArgumentException si {@code textoCifrado} es nulo,
     * vacío o no es un Base64 válido.
     * @throws RuntimeException si los datos están corruptos o la clave
     * no corresponde a la usada al cifrar.
     */
    public static String descifrar(String textoCifrado) {
        if (textoCifrado == null || textoCifrado.isEmpty()) {
            throw new IllegalArgumentException(
                    "El texto cifrado no puede ser nulo ni vacío.");
        }
        inicializarClavesSiNecesario();
        try {
            Cipher cifrador = Cipher.getInstance(
                    Constantes.TRANSFORMACION_CIFRADO);
            cifrador.init(Cipher.DECRYPT_MODE, claveSecreta,
                    vectorInicializacion);
            byte[] bytesCifrados = Base64.getDecoder()
                    .decode(textoCifrado);
            byte[] bytesDescifrados = cifrador.doFinal(bytesCifrados);
            return new String(bytesDescifrados, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "El texto cifrado no es Base64 válido: "
                    + e.getMessage(), e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(
                    "Error al descifrar: verifique que la clave y el IV "
                    + "coincidan con los usados al cifrar. "
                    + e.getMessage(), e);
        }
    }

    /**
     * Inicializa la clave y el IV una sola vez de forma hilo-segura.
     * Si ya están inicializados, retorna sin hacer nada.
     *
     * @throws RuntimeException si el archivo de propiedades no existe,
     * si faltan las claves requeridas, o si su longitud no es
     * exactamente {@value #LONGITUD_CLAVE_BYTES} caracteres.
     */
    private static synchronized void inicializarClavesSiNecesario() {
        if (claveSecreta != null) {
            return;
        }
        Properties propiedades = cargarPropiedades();
        String clave = propiedades.getProperty("bd.clave_cifrado");
        String iv = propiedades.getProperty("bd.iv_cifrado");

        if (clave == null || iv == null) {
            throw new RuntimeException(
                    "Faltan bd.clave_cifrado o bd.iv_cifrado en "
                    + RUTA_CONFIG);
        }
        if (clave.length() != LONGITUD_CLAVE_BYTES
                || iv.length() != LONGITUD_CLAVE_BYTES) {
            throw new RuntimeException(
                    "La clave y el IV deben tener exactamente "
                    + LONGITUD_CLAVE_BYTES + " caracteres UTF-8.");
        }
        byte[] bytesClave = clave.getBytes(StandardCharsets.UTF_8);
        byte[] bytesIv = iv.getBytes(StandardCharsets.UTF_8);
        claveSecreta = new SecretKeySpec(bytesClave,
                Constantes.ALGORITMO_CIFRADO);
        vectorInicializacion = new IvParameterSpec(bytesIv);
    }

    /**
     * Carga el archivo de propiedades desde el classpath.
     *
     * @return objeto {@link Properties} con el contenido del archivo.
     * @throws RuntimeException si el archivo no se encuentra o no
     * puede leerse, ya que sin él la capa de cifrado no opera.
     */
    private static Properties cargarPropiedades() {
        Properties propiedades = new Properties();
        try (InputStream flujo =
                CifradoAES.class.getResourceAsStream(RUTA_CONFIG)) {
            if (flujo == null) {
                throw new RuntimeException(
                        "Archivo no encontrado en el classpath: "
                        + RUTA_CONFIG);
            }
            propiedades.load(flujo);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error al leer la configuración de cifrado: "
                    + e.getMessage(), e);
        }
        return propiedades;
    }

}
