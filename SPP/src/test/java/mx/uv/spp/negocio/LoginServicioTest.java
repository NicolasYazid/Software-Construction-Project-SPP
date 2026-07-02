/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
package mx.uv.spp.negocio;

import java.sql.SQLException;
import java.time.LocalDateTime;
import mx.uv.spp.modelo.ResultadoAutenticacion;
import mx.uv.spp.modelo.TipoUsuario;
import mx.uv.spp.persistencia.dao.UsuarioDAO;
import mx.uv.spp.util.Constantes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de {@link LoginServicio}.
 * No acceden a la base de datos: usan {@link UsuarioDAOStub} para
 * controlar la respuesta del DAO y verificar el comportamiento de
 * la lógica de negocio de forma aislada.
 *
 * <p>Casos cubiertos:
 * <ul>
 * <li>Validación de entradas vacías</li>
 * <li>Usuario no encontrado (idUsuario = 0)</li>
 * <li>Cuenta inactiva</li>
 * <li>Bloqueo vigente (SEG-01)</li>
 * <li>Bloqueo expirado: reinicio de contadores</li>
 * <li>Credenciales incorrectas: incremento de intentos</li>
 * <li>Tercer intento fallido: mensaje de bloqueo</li>
 * <li>Login exitoso: reinicio de intentos</li>
 * </ul>
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
class LoginServicioTest {

    private static final String CORREO_PRUEBA = "test@fei.uv.mx";
    private static final String PASS_PRUEBA = "ContrasenaTest2026";
    private static final int ID_USUARIO = 7;
    private static final TipoUsuario TIPO_PRUEBA =
            TipoUsuario.COORDINADOR;

    private UsuarioDAOStub daoStub;
    private LoginServicio loginServicio;

    @BeforeEach
    void inicializar() {
        daoStub = new UsuarioDAOStub();
        loginServicio = new LoginServicio(daoStub);
    }

    /* ── 1. Validación de entradas ─────────────────────────── */

    @Test
    @DisplayName("Identificador vacío lanza IllegalArgumentException")
    void autenticarUsuario_identificadorVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> loginServicio.autenticarUsuario(
                        "", PASS_PRUEBA, TIPO_PRUEBA));
    }

    @Test
    @DisplayName("Contraseña vacía lanza IllegalArgumentException")
    void autenticarUsuario_contrasenaVacia_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> loginServicio.autenticarUsuario(
                        CORREO_PRUEBA, "", TIPO_PRUEBA));
    }

    @Test
    @DisplayName("Identificador con solo espacios lanza excepción")
    void autenticarUsuario_soloEspacios_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> loginServicio.autenticarUsuario(
                        "   ", PASS_PRUEBA, TIPO_PRUEBA));
    }

    /* ── 2. Usuario no encontrado ──────────────────────────── */

    @Test
    @DisplayName("Usuario no encontrado retorna fallo con idUsuario=0")
    void autenticarUsuario_noEncontrado_retornaFallo()
            throws Exception {
        daoStub.respuesta = resultadoNoEncontrado();

        ResultadoAutenticacion r = loginServicio.autenticarUsuario(
                CORREO_PRUEBA, PASS_PRUEBA, TIPO_PRUEBA);

        assertFalse(r.isExitoso());
        assertEquals(0, r.getIdUsuario());
        assertFalse(daoStub.seIncrementoIntentos);
    }

    /* ── 3. Cuenta inactiva ────────────────────────────────── */

    @Test
    @DisplayName("Cuenta inactiva retorna fallo con mensaje descriptivo")
    void autenticarUsuario_cuentaInactiva_retornaFallo()
            throws Exception {
        daoStub.respuesta = resultadoConEstado(
                Constantes.ESTADO_INACTIVO, 0, null, true);

        ResultadoAutenticacion r = loginServicio.autenticarUsuario(
                CORREO_PRUEBA, PASS_PRUEBA, TIPO_PRUEBA);

        assertFalse(r.isExitoso());
        assertTrue(r.getMensajeError().contains("inactiva"));
        assertFalse(daoStub.seIncrementoIntentos);
    }

    /* ── 4. Bloqueo vigente (SEG-01) ───────────────────────── */

    @Test
    @DisplayName("Bloqueo vigente retorna fallo sin incrementar intentos")
    void autenticarUsuario_bloqueadoVigente_noIncrementaIntentos()
            throws Exception {
        LocalDateTime bloqueadoHace2Min =
                LocalDateTime.now().minusMinutes(2);
        daoStub.respuesta = resultadoConEstado(
                Constantes.ESTADO_ACTIVO,
                Constantes.MAX_INTENTOS_LOGIN,
                bloqueadoHace2Min, false);

        ResultadoAutenticacion r = loginServicio.autenticarUsuario(
                CORREO_PRUEBA, PASS_PRUEBA, TIPO_PRUEBA);

        assertFalse(r.isExitoso());
        assertTrue(r.getMensajeError().toLowerCase()
                .contains("bloqueada"));
        assertFalse(daoStub.seIncrementoIntentos);
        assertFalse(daoStub.seReinicioIntentos);
    }

    @Test
    @DisplayName("Mensaje de bloqueo indica minutos restantes")
    void autenticarUsuario_bloqueadoVigente_mensajeConMinutos()
            throws Exception {
        LocalDateTime bloqueadoHace1Min =
                LocalDateTime.now().minusMinutes(1);
        daoStub.respuesta = resultadoConEstado(
                Constantes.ESTADO_ACTIVO,
                Constantes.MAX_INTENTOS_LOGIN,
                bloqueadoHace1Min, false);

        ResultadoAutenticacion r = loginServicio.autenticarUsuario(
                CORREO_PRUEBA, PASS_PRUEBA, TIPO_PRUEBA);

        assertTrue(r.getMensajeError().contains("minuto"));
    }

    /* ── 5. Bloqueo expirado ───────────────────────────────── */

    @Test
    @DisplayName("Bloqueo expirado reinicia contadores en BD")
    void autenticarUsuario_bloqueadoExpirado_reiniciaIntentos()
            throws Exception {
        LocalDateTime bloqueadoHace15Min =
                LocalDateTime.now().minusMinutes(15);
        ResultadoAutenticacion respuesta = resultadoConEstado(
                Constantes.ESTADO_ACTIVO,
                Constantes.MAX_INTENTOS_LOGIN,
                bloqueadoHace15Min, false);
        respuesta.setExitoso(true);
        daoStub.respuesta = respuesta;

        ResultadoAutenticacion r = loginServicio.autenticarUsuario(
                CORREO_PRUEBA, PASS_PRUEBA, TIPO_PRUEBA);

        assertTrue(daoStub.seReinicioIntentos,
                "Debe llamarse reiniciarIntentos() cuando expira el bloqueo");
        assertTrue(r.isExitoso());
    }

    @Test
    @DisplayName("Bloqueo expirado con contraseña incorrecta incrementa intentos")
    void autenticarUsuario_bloqueadoExpiradoPassMal_incrementaIntentos()
            throws Exception {
        LocalDateTime bloqueadoHace15Min =
                LocalDateTime.now().minusMinutes(15);
        ResultadoAutenticacion respuesta = resultadoConEstado(
                Constantes.ESTADO_ACTIVO,
                Constantes.MAX_INTENTOS_LOGIN,
                bloqueadoHace15Min, false);
        respuesta.setExitoso(false);
        daoStub.respuesta = respuesta;

        loginServicio.autenticarUsuario(
                CORREO_PRUEBA, PASS_PRUEBA, TIPO_PRUEBA);

        assertTrue(daoStub.seReinicioIntentos);
        assertTrue(daoStub.seIncrementoIntentos);
    }

    /* ── 6. Credenciales incorrectas ───────────────────────── */

    @Test
    @DisplayName("Primer intento fallido incrementa contador y muestra intentos")
    void autenticarUsuario_primerIntentoFallido_incrementaYMuestraRestantes()
            throws Exception {
        daoStub.respuesta = resultadoCredencialesIncorrectas(0);

        ResultadoAutenticacion r = loginServicio.autenticarUsuario(
                CORREO_PRUEBA, PASS_PRUEBA, TIPO_PRUEBA);

        assertFalse(r.isExitoso());
        assertTrue(daoStub.seIncrementoIntentos);
        assertTrue(r.getMensajeError().contains("2"));
    }

    @Test
    @DisplayName("Segundo intento fallido muestra 1 intento restante")
    void autenticarUsuario_segundoIntentoFallido_muestraUnRestante()
            throws Exception {
        daoStub.respuesta = resultadoCredencialesIncorrectas(1);

        ResultadoAutenticacion r = loginServicio.autenticarUsuario(
                CORREO_PRUEBA, PASS_PRUEBA, TIPO_PRUEBA);

        assertTrue(daoStub.seIncrementoIntentos);
        assertTrue(r.getMensajeError().contains("1"));
    }

    /* ── 7. Tercer intento: bloqueo (SEG-01) ──────────────── */

    @Test
    @DisplayName("Tercer intento fallido muestra mensaje de bloqueo")
    void autenticarUsuario_tercerIntentoFallido_cuentaBloqueada()
            throws Exception {
        daoStub.respuesta = resultadoCredencialesIncorrectas(
                Constantes.MAX_INTENTOS_LOGIN - 1);

        ResultadoAutenticacion r = loginServicio.autenticarUsuario(
                CORREO_PRUEBA, PASS_PRUEBA, TIPO_PRUEBA);

        assertFalse(r.isExitoso());
        assertTrue(daoStub.seIncrementoIntentos);
        assertTrue(r.getMensajeError().toLowerCase()
                .contains("bloqueada")
                || r.getMensajeError().toLowerCase()
                        .contains("bloqueado"));
    }

    /* ── 8. Login exitoso ──────────────────────────────────── */

    @Test
    @DisplayName("Login exitoso retorna exitoso=true y reinicia intentos")
    void autenticarUsuario_exitoso_reiniciaIntentos()
            throws Exception {
        daoStub.respuesta = resultadoExitoso();

        ResultadoAutenticacion r = loginServicio.autenticarUsuario(
                CORREO_PRUEBA, PASS_PRUEBA, TIPO_PRUEBA);

        assertTrue(r.isExitoso());
        assertTrue(daoStub.seReinicioIntentos,
                "Debe llamar reiniciarIntentos() en login exitoso");
        assertFalse(daoStub.seIncrementoIntentos);
    }

    /* ── Constructor ───────────────────────────────────────── */

    @Test
    @DisplayName("Constructor con DAO nulo lanza IllegalArgumentException")
    void constructor_daoNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new LoginServicio(null));
    }

    /* ── Fábricas de objetos de prueba ─────────────────────── */

    private ResultadoAutenticacion resultadoNoEncontrado() {
        ResultadoAutenticacion r = new ResultadoAutenticacion();
        r.setIdUsuario(0);
        r.setExitoso(false);
        r.setMensajeError("Credenciales incorrectas.");
        r.setTipo(TIPO_PRUEBA);
        return r;
    }

    private ResultadoAutenticacion resultadoConEstado(
            String estado, int intentos,
            LocalDateTime fechaBloqueo,
            boolean exitoso) {
        ResultadoAutenticacion r = new ResultadoAutenticacion();
        r.setIdUsuario(ID_USUARIO);
        r.setEstado(estado);
        r.setIntentosFallidos(intentos);
        r.setFechaBloqueo(fechaBloqueo);
        r.setExitoso(exitoso);
        r.setTipo(TIPO_PRUEBA);
        r.setNombreCompleto("Usuario Prueba");
        return r;
    }

    private ResultadoAutenticacion resultadoCredencialesIncorrectas(
            int intentosPrevios) {
        ResultadoAutenticacion r = new ResultadoAutenticacion();
        r.setIdUsuario(ID_USUARIO);
        r.setEstado(Constantes.ESTADO_ACTIVO);
        r.setIntentosFallidos(intentosPrevios);
        r.setFechaBloqueo(null);
        r.setExitoso(false);
        r.setMensajeError("Credenciales incorrectas.");
        r.setTipo(TIPO_PRUEBA);
        return r;
    }

    private ResultadoAutenticacion resultadoExitoso() {
        ResultadoAutenticacion r = new ResultadoAutenticacion();
        r.setIdUsuario(ID_USUARIO);
        r.setEstado(Constantes.ESTADO_ACTIVO);
        r.setIntentosFallidos(0);
        r.setFechaBloqueo(null);
        r.setExitoso(true);
        r.setNombreCompleto("Coordinador Prueba");
        r.setTipo(TIPO_PRUEBA);
        return r;
    }

    /* ── Stub del DAO (sin BD, sin red) ────────────────────── */

    /**
     * Doble de prueba de {@link UsuarioDAO}.
     * Permite configurar la respuesta de {@code autenticar()} y
     * verificar que los métodos de actualización fueron invocados.
     */
    static class UsuarioDAOStub implements UsuarioDAO {

        ResultadoAutenticacion respuesta;
        boolean seIncrementoIntentos = false;
        boolean seReinicioIntentos = false;

        @Override
        public ResultadoAutenticacion autenticar(
                String identificador, String contrasena,
                TipoUsuario tipo) throws SQLException {
            return respuesta;
        }

        @Override
        public void incrementarIntentosFallidos(int idUsuario,
                TipoUsuario tipo) throws SQLException {
            seIncrementoIntentos = true;
        }

        @Override
        public void reiniciarIntentos(int idUsuario,
                TipoUsuario tipo) throws SQLException {
            seReinicioIntentos = true;
        }

        @Override
        public void actualizarContrasena(int idUsuario,
                String contrasenaCifrada,
                TipoUsuario tipo) throws SQLException {
        }
    }

}
