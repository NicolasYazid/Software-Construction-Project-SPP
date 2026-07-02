/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.persistencia.dao;

import java.sql.SQLException;
import mx.uv.spp.modelo.ResultadoAutenticacion;
import mx.uv.spp.modelo.TipoUsuario;

/**
 * Contrato de acceso a datos para la autenticación de usuarios.
 * Abstrae las cuatro tablas de usuario ({@code administrador},
 * {@code coordinador}, {@code profesor}, {@code estudiante}) detrás
 * de una interfaz uniforme, diferenciando el flujo por
 * {@link TipoUsuario}.
 *
 * <p>La implementación JDBC ({@code UsuarioDAOImpl}) es la única
 * clase que conoce los nombres de tablas y columnas. Nada fuera de
 * {@code persistencia.dao.impl} debe acceder a la BD directamente.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public interface UsuarioDAO {

    /**
     * Busca al usuario en la tabla que corresponde a {@code tipo},
     * descifra la contraseña almacenada (AES-128) y la compara con
     * la recibida. Nunca retorna {@code null}; el DTO indica éxito
     * o fallo y expone todos los campos de seguridad necesarios para
     * que la capa de negocio aplique SEG-01.
     *
     * <p>Identificador esperado según tipo:
     * <ul>
     * <li>ADMINISTRADOR, COORDINADOR, PROFESOR → correo electrónico</li>
     * <li>ESTUDIANTE → matrícula UV (ej. {@code zS21013417})</li>
     * </ul>
     *
     * @param identificador Correo o matrícula ingresado en la pantalla
     * de login, en texto plano.
     * @param contrasena Contraseña en texto plano; se cifra para
     * comparar con el valor almacenado.
     * @param tipo Rol seleccionado por el usuario en la UI;
     * determina la tabla destino.
     * @return {@link ResultadoAutenticacion} con {@code idUsuario = 0}
     * si el identificador no existe, o con todos los campos
     * poblados si el usuario fue encontrado.
     * @throws SQLException si ocurre un error de acceso a la base de datos.
     */
    ResultadoAutenticacion autenticar(String identificador,
            String contrasena, TipoUsuario tipo) throws SQLException;

    /**
     * Incrementa en 1 el contador {@code intentos_fallidos} del usuario.
     * Si el nuevo valor alcanza {@code Constantes.MAX_INTENTOS_LOGIN},
     * también persiste {@code fecha_bloqueo = NOW()} para iniciar el
     * periodo de bloqueo (SEG-01).
     *
     * <p>Solo debe llamarse cuando el usuario fue encontrado en BD
     * ({@code idUsuario > 0}) pero las credenciales son incorrectas.
     *
     * @param idUsuario Clave primaria en la tabla del tipo indicado.
     * @param tipo Determina en qué tabla aplicar la actualización.
     * @throws SQLException si ocurre un error de acceso a la base de datos.
     */
    void incrementarIntentosFallidos(int idUsuario,
            TipoUsuario tipo) throws SQLException;

    /**
     * Reinicia {@code intentos_fallidos} a {@code 0} y asigna
     * {@code NULL} a {@code fecha_bloqueo}. Se invoca tras un login
     * exitoso o cuando el bloqueo temporal ha expirado.
     *
     * @param idUsuario Clave primaria en la tabla del tipo indicado.
     * @param tipo Determina en qué tabla aplicar el reinicio.
     * @throws SQLException si ocurre un error de acceso a la base de datos.
     */
    void reiniciarIntentos(int idUsuario,
            TipoUsuario tipo) throws SQLException;

    /**
     * Persiste la nueva contraseña cifrada del usuario.
     *
     * <p>La contraseña debe llegar ya cifrada con AES-128 desde la
     * capa de negocio; el DAO no realiza cifrado.
     *
     * @param idUsuario Clave primaria en la tabla del tipo.
     * @param contrasenaCifrada Contraseña nueva ya cifrada con AES-128.
     * @param tipo Determina en qué tabla actualizar.
     * @throws SQLException si ocurre un error de acceso a la base de datos.
     */
    void actualizarContrasena(int idUsuario, String contrasenaCifrada,
            TipoUsuario tipo) throws SQLException;

}
