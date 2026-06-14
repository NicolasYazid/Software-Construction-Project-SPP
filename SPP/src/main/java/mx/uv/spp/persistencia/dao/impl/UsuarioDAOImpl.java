/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.persistencia.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import mx.uv.spp.modelo.ResultadoAutenticacion;
import mx.uv.spp.modelo.TipoUsuario;
import mx.uv.spp.persistencia.ConexionBD;
import mx.uv.spp.persistencia.dao.UsuarioDAO;
import mx.uv.spp.util.CifradoAES;
import mx.uv.spp.util.Constantes;

/**
 * Implementación JDBC de {@link UsuarioDAO}.
 * Accede a las cuatro tablas de usuario ({@code administrador},
 * {@code coordinador}, {@code profesor}, {@code estudiante})
 * con {@code PreparedStatement}. Todos los identificadores y
 * contraseñas se comparan en texto plano tras descifrar los
 * valores almacenados con AES-128 (SEG-04).
 *
 * <p>Único archivo del proyecto con conocimiento de nombres de
 * tablas y columnas de usuario. Ninguna otra capa accede a la BD.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    /* ── Nombres de tablas y columnas ID (no son entradas de usuario) */

    private static final String TABLA_ADMIN    = "administrador";
    private static final String TABLA_COORD    = "coordinador";
    private static final String TABLA_PROFE    = "profesor";
    private static final String TABLA_ESTUDIA  = "estudiante";

    private static final String COL_ID_ADMIN   = "id_administrador";
    private static final String COL_ID_COORD   = "id_coordinador";
    private static final String COL_ID_PROFE   = "id_profesor";
    private static final String COL_ID_ESTUDIA = "id_estudiante";

    private static final String MENSAJE_CREDENCIALES =
            "Credenciales incorrectas.";

    /**
     * {@inheritDoc}
     *
     * <p>Cifra el identificador antes de compararlo con la BD, ya
     * que se almacena encriptado. La contraseña se descifra desde
     * la BD y se compara en texto plano con la entrada del usuario.
     */
    @Override
    public ResultadoAutenticacion autenticar(String identificador,
            String contrasena, TipoUsuario tipo) throws SQLException {
        switch (tipo) {
            case ADMINISTRADOR:
                return autenticarAdministrador(
                        identificador, contrasena);
            case COORDINADOR:
                return autenticarConApellidos(
                        identificador, contrasena,
                        TABLA_COORD, COL_ID_COORD,
                        TipoUsuario.COORDINADOR);
            case PROFESOR:
                return autenticarConApellidos(
                        identificador, contrasena,
                        TABLA_PROFE, COL_ID_PROFE,
                        TipoUsuario.PROFESOR);
            case PRACTICANTE:
                return autenticarEstudiante(
                        identificador, contrasena);
            default:
                throw new IllegalArgumentException(
                        "TipoUsuario no reconocido: " + tipo);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Ejecuta dos sentencias UPDATE: la primera incrementa el
     * contador; la segunda aplica {@code fecha_bloqueo = NOW()} solo
     * si el nuevo contador alcanza {@link Constantes#MAX_INTENTOS_LOGIN}
     * y la cuenta aún no tiene fecha de bloqueo registrada.
     */
    @Override
    public void incrementarIntentosFallidos(int idUsuario,
            TipoUsuario tipo) throws SQLException {
        String tabla = obtenerNombreTabla(tipo);
        String colId  = obtenerColumnaId(tipo);
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();

        String sqlIncrementar = "UPDATE " + tabla
                + " SET intentos_fallidos = intentos_fallidos + 1"
                + " WHERE " + colId + " = ?";
        try (PreparedStatement ps =
                con.prepareStatement(sqlIncrementar)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }

        String sqlBloqueo = "UPDATE " + tabla
                + " SET fecha_bloqueo = NOW()"
                + " WHERE " + colId + " = ?"
                + " AND intentos_fallidos >= ?"
                + " AND fecha_bloqueo IS NULL";
        try (PreparedStatement ps =
                con.prepareStatement(sqlBloqueo)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, Constantes.MAX_INTENTOS_LOGIN);
            ps.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reiniciarIntentos(int idUsuario,
            TipoUsuario tipo) throws SQLException {
        String tabla = obtenerNombreTabla(tipo);
        String colId  = obtenerColumnaId(tipo);
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();

        String sql = "UPDATE " + tabla
                + " SET intentos_fallidos = 0,"
                + " fecha_bloqueo = NULL"
                + " WHERE " + colId + " = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>La contraseña debe llegar ya cifrada con AES-128 desde la
     * capa de negocio; este método no cifra, solo persiste.
     */
    @Override
    public void actualizarContrasena(int idUsuario,
            String contrasenaCifrada,
            TipoUsuario tipo) throws SQLException {
        String tabla = obtenerNombreTabla(tipo);
        String colId  = obtenerColumnaId(tipo);
        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();

        String sql = "UPDATE " + tabla
                + " SET contrasena = ?,"
                + " contrasena_temporal = 0"
                + " WHERE " + colId + " = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, contrasenaCifrada);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    /* ── Métodos privados de autenticación por tipo ─────────── */

    /**
     * Autentica un Administrador. Solo tiene campo {@code nombre}
     * (sin apellidos), por eso tiene su propio método.
     *
     * @param correo    Correo en texto plano.
     * @param contrasena Contraseña en texto plano.
     * @return DTO con el resultado; {@code idUsuario = 0} si no existe.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    private ResultadoAutenticacion autenticarAdministrador(
            String correo, String contrasena) throws SQLException {
        String sql = "SELECT id_administrador, nombre, contrasena,"
                + " estado, contrasena_temporal,"
                + " intentos_fallidos, fecha_bloqueo"
                + " FROM administrador WHERE correo = ?";

        ResultadoAutenticacion resultado =
                new ResultadoAutenticacion();
        resultado.setTipo(TipoUsuario.ADMINISTRADOR);

        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, CifradoAES.cifrar(correo));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    resultado.setMensajeError(
                            MENSAJE_CREDENCIALES);
                    return resultado;
                }
                poblarCamposSeguridad(resultado, rs,
                        "id_administrador");
                resultado.setNombreCompleto(
                        CifradoAES.descifrar(
                                rs.getString("nombre")));
                String contrasenaBD = CifradoAES.descifrar(
                        rs.getString("contrasena"));
                resultado.setExitoso(
                        contrasena.equals(contrasenaBD));
                if (!resultado.isExitoso()) {
                    resultado.setMensajeError(
                            MENSAJE_CREDENCIALES);
                }
            }
        }
        return resultado;
    }

    /**
     * Autentica un Coordinador o Profesor. Ambas tablas tienen la
     * misma estructura de campos; se diferencian por {@code tabla}
     * y {@code colId}.
     *
     * @param correo    Correo en texto plano.
     * @param contrasena Contraseña en texto plano.
     * @param tabla     Nombre de la tabla destino.
     * @param colId     Nombre de la columna de clave primaria.
     * @param tipo      Rol del usuario para el DTO resultado.
     * @return DTO con el resultado; {@code idUsuario = 0} si no existe.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    private ResultadoAutenticacion autenticarConApellidos(
            String correo, String contrasena,
            String tabla, String colId,
            TipoUsuario tipo) throws SQLException {
        String sql = "SELECT " + colId
                + ", nombre, primer_apellido, segundo_apellido,"
                + " contrasena, estado, contrasena_temporal,"
                + " intentos_fallidos, fecha_bloqueo"
                + " FROM " + tabla + " WHERE correo = ?";

        ResultadoAutenticacion resultado =
                new ResultadoAutenticacion();
        resultado.setTipo(tipo);

        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, CifradoAES.cifrar(correo));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    resultado.setMensajeError(
                            MENSAJE_CREDENCIALES);
                    return resultado;
                }
                poblarCamposSeguridad(resultado, rs, colId);
                resultado.setNombreCompleto(
                        construirNombreConApellidos(rs));
                String contrasenaBD = CifradoAES.descifrar(
                        rs.getString("contrasena"));
                resultado.setExitoso(
                        contrasena.equals(contrasenaBD));
                if (!resultado.isExitoso()) {
                    resultado.setMensajeError(
                            MENSAJE_CREDENCIALES);
                }
            }
        }
        return resultado;
    }

    /**
     * Autentica un Practicante (Estudiante) usando matrícula en lugar
     * de correo, según la regla de login de la sección 4.
     *
     * @param matricula  Matrícula en texto plano (ej. S21013417).
     * @param contrasena Contraseña en texto plano.
     * @return DTO con el resultado; {@code idUsuario = 0} si no existe.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    private ResultadoAutenticacion autenticarEstudiante(
            String matricula, String contrasena) throws SQLException {
        String sql = "SELECT id_estudiante, nombre, primer_apellido,"
                + " segundo_apellido, contrasena, estado,"
                + " contrasena_temporal, intentos_fallidos,"
                + " fecha_bloqueo"
                + " FROM estudiante WHERE matricula = ?";

        ResultadoAutenticacion resultado =
                new ResultadoAutenticacion();
        resultado.setTipo(TipoUsuario.PRACTICANTE);

        Connection con = ConexionBD.obtenerInstancia()
                .obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, CifradoAES.cifrar(matricula));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    resultado.setMensajeError(
                            MENSAJE_CREDENCIALES);
                    return resultado;
                }
                poblarCamposSeguridad(resultado, rs,
                        "id_estudiante");
                resultado.setNombreCompleto(
                        construirNombreConApellidos(rs));
                String contrasenaBD = CifradoAES.descifrar(
                        rs.getString("contrasena"));
                resultado.setExitoso(
                        contrasena.equals(contrasenaBD));
                if (!resultado.isExitoso()) {
                    resultado.setMensajeError(
                            MENSAJE_CREDENCIALES);
                }
            }
        }
        return resultado;
    }

    /* ── Utilidades privadas ─────────────────────────────────── */

    /**
     * Rellena los campos de seguridad del DTO desde el ResultSet:
     * ID, estado, contraseña temporal, intentos fallidos y fecha
     * de bloqueo.
     *
     * @param resultado DTO donde se escriben los campos.
     * @param rs        ResultSet posicionado en la fila del usuario.
     * @param colId     Nombre de la columna de clave primaria.
     * @throws SQLException si una columna no existe en el ResultSet.
     */
    private void poblarCamposSeguridad(
            ResultadoAutenticacion resultado,
            ResultSet rs, String colId) throws SQLException {
        resultado.setIdUsuario(rs.getInt(colId));
        resultado.setEstado(rs.getString("estado"));
        resultado.setContrasenaTemporal(
                rs.getBoolean("contrasena_temporal"));
        resultado.setIntentosFallidos(
                rs.getInt("intentos_fallidos"));

        Timestamp tsBloqueo = rs.getTimestamp("fecha_bloqueo");
        if (tsBloqueo != null) {
            resultado.setFechaBloqueo(
                    tsBloqueo.toLocalDateTime());
        }
    }

    /**
     * Construye el nombre completo concatenando nombre,
     * primer apellido y (si existe) segundo apellido,
     * descifrado cada uno con AES-128.
     *
     * @param rs ResultSet posicionado en la fila del usuario.
     * @return nombre completo descifrado y concatenado.
     * @throws SQLException si una columna no existe en el ResultSet.
     */
    private String construirNombreConApellidos(ResultSet rs)
            throws SQLException {
        String nombre    = CifradoAES.descifrar(
                rs.getString("nombre"));
        String apellido1 = CifradoAES.descifrar(
                rs.getString("primer_apellido"));
        String apellido2Raw = rs.getString("segundo_apellido");

        StringBuilder sb = new StringBuilder(nombre)
                .append(" ").append(apellido1);
        if (apellido2Raw != null && !apellido2Raw.isEmpty()) {
            sb.append(" ").append(
                    CifradoAES.descifrar(apellido2Raw));
        }
        return sb.toString();
    }

    /**
     * Retorna el nombre de la tabla de BD correspondiente al tipo.
     *
     * @param tipo Rol del usuario.
     * @return nombre de la tabla en la base de datos.
     * @throws IllegalArgumentException si el tipo no está mapeado.
     */
    private String obtenerNombreTabla(TipoUsuario tipo) {
        switch (tipo) {
            case ADMINISTRADOR: return TABLA_ADMIN;
            case COORDINADOR:   return TABLA_COORD;
            case PROFESOR:      return TABLA_PROFE;
            case PRACTICANTE:   return TABLA_ESTUDIA;
            default:
                throw new IllegalArgumentException(
                        "TipoUsuario no mapeado: " + tipo);
        }
    }

    /**
     * Retorna el nombre de la columna de clave primaria según el tipo.
     *
     * @param tipo Rol del usuario.
     * @return nombre de la columna ID en la tabla correspondiente.
     * @throws IllegalArgumentException si el tipo no está mapeado.
     */
    private String obtenerColumnaId(TipoUsuario tipo) {
        switch (tipo) {
            case ADMINISTRADOR: return COL_ID_ADMIN;
            case COORDINADOR:   return COL_ID_COORD;
            case PROFESOR:      return COL_ID_PROFE;
            case PRACTICANTE:   return COL_ID_ESTUDIA;
            default:
                throw new IllegalArgumentException(
                        "TipoUsuario no mapeado: " + tipo);
        }
    }

}
