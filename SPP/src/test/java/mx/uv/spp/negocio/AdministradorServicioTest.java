/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 1 de julio del 2026
 */
package mx.uv.spp.negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.spp.modelo.Profesor;
import mx.uv.spp.persistencia.dao.ProfesorDAO;
import mx.uv.spp.util.Constantes;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas unitarias de {@link AdministradorServicio} (JUnit 4).
 * No acceden a la base de datos: usan {@link ProfesorDAOStub} para
 * controlar la respuesta del DAO y verificar el comportamiento de
 * la lógica de negocio de forma aislada.
 *
 * <p>Casos cubiertos (CU-Admin.-01 "Dar de alta Profesor" y
 * CU-Admin.-03 "Cambiar Coordinador"):
 * <ul>
 * <li>Validación de campos obligatorios (FA-02)</li>
 * <li>Validación de formato de número de personal y correo (FA-03)</li>
 * <li>Validación de solo letras en nombre/apellidos (FA-04)</li>
 * <li>Apellido materno opcional</li>
 * <li>Validación de contraseña (SEG-03)</li>
 * <li>Profesor duplicado por número de personal o correo (FA-06)</li>
 * <li>Alta exitosa</li>
 * <li>Transferencia de rol de Coordinador</li>
 * </ul>
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class AdministradorServicioTest {

    private static final String NUMERO_PERSONAL_VALIDO = "1234567890";
    private static final String CORREO_VALIDO = "nuevo@fei.uv.mx";
    private static final String CONTRASENA_VALIDA = "Contrasena2026";

    private ProfesorDAOStub daoStub;
    private AdministradorServicio administradorServicio;

    @Before
    public void inicializar() {
        daoStub = new ProfesorDAOStub();
        administradorServicio = new AdministradorServicio(daoStub);
    }

    /* ── Constructor ────────────────────────────────────────── */

    @Test
    public void constructor_daoNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new AdministradorServicio(null));
    }

    /* ── FA-02: campos obligatorios vacíos ─────────────────── */

    @Test
    public void registrarProfesor_numeroPersonalVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio.registrarProfesor(
                        "", "Ana", "López", null,
                        CORREO_VALIDO, CONTRASENA_VALIDA));
    }

    @Test
    public void registrarProfesor_nombreVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio.registrarProfesor(
                        NUMERO_PERSONAL_VALIDO, "", "López", null,
                        CORREO_VALIDO, CONTRASENA_VALIDA));
    }

    @Test
    public void registrarProfesor_apellidoPaternoVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio.registrarProfesor(
                        NUMERO_PERSONAL_VALIDO, "Ana", "", null,
                        CORREO_VALIDO, CONTRASENA_VALIDA));
    }

    @Test
    public void registrarProfesor_correoVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio.registrarProfesor(
                        NUMERO_PERSONAL_VALIDO, "Ana", "López", null,
                        "", CONTRASENA_VALIDA));
    }

    @Test
    public void registrarProfesor_contrasenaVacia_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio.registrarProfesor(
                        NUMERO_PERSONAL_VALIDO, "Ana", "López", null,
                        CORREO_VALIDO, ""));
    }

    /* ── FA-03: formato inválido ────────────────────────────── */

    @Test
    public void registrarProfesor_numeroPersonalFormatoInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio.registrarProfesor(
                        "123", "Ana", "López", null,
                        CORREO_VALIDO, CONTRASENA_VALIDA));
    }

    @Test
    public void registrarProfesor_correoFormatoInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio.registrarProfesor(
                        NUMERO_PERSONAL_VALIDO, "Ana", "López", null,
                        "ana@gmail.com", CONTRASENA_VALIDA));
    }

    /* ── FA-04: solo letras ─────────────────────────────────── */

    @Test
    public void registrarProfesor_nombreConNumeros_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio.registrarProfesor(
                        NUMERO_PERSONAL_VALIDO, "Ana123", "López",
                        null, CORREO_VALIDO, CONTRASENA_VALIDA));
    }

    @Test
    public void registrarProfesor_apellidoPaternoConNumeros_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio.registrarProfesor(
                        NUMERO_PERSONAL_VALIDO, "Ana", "López2", null,
                        CORREO_VALIDO, CONTRASENA_VALIDA));
    }

    @Test
    public void registrarProfesor_apellidoMaternoConNumeros_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio.registrarProfesor(
                        NUMERO_PERSONAL_VALIDO, "Ana", "López",
                        "Cruz9", CORREO_VALIDO, CONTRASENA_VALIDA));
    }

    /* ── Apellido materno opcional ──────────────────────────── */

    @Test
    public void registrarProfesor_apellidoMaternoNulo_seRegistraConNull()
            throws SQLException {
        administradorServicio.registrarProfesor(
                NUMERO_PERSONAL_VALIDO, "Ana", "López", null,
                CORREO_VALIDO, CONTRASENA_VALIDA);

        assertNull(daoStub.profesorRegistrado.getApellidoMaterno());
    }

    @Test
    public void registrarProfesor_apellidoMaternoVacio_seRegistraConNull()
            throws SQLException {
        administradorServicio.registrarProfesor(
                NUMERO_PERSONAL_VALIDO, "Ana", "López", "   ",
                CORREO_VALIDO, CONTRASENA_VALIDA);

        assertNull(daoStub.profesorRegistrado.getApellidoMaterno());
    }

    /* ── SEG-03: formato de contraseña ──────────────────────── */

    @Test
    public void registrarProfesor_contrasenaSinMayuscula_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio.registrarProfesor(
                        NUMERO_PERSONAL_VALIDO, "Ana", "López", null,
                        CORREO_VALIDO, "contrasena2026"));
    }

    @Test
    public void registrarProfesor_contrasenaCorta_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio.registrarProfesor(
                        NUMERO_PERSONAL_VALIDO, "Ana", "López", null,
                        CORREO_VALIDO, "Corta1"));
    }

    /* ── FA-06: Profesor duplicado ──────────────────────────── */

    @Test
    public void registrarProfesor_numeroPersonalDuplicado_lanzaExcepcion() {
        daoStub.existeNumeroPersonalRespuesta = true;

        assertThrows(IllegalStateException.class,
                () -> administradorServicio.registrarProfesor(
                        NUMERO_PERSONAL_VALIDO, "Ana", "López", null,
                        CORREO_VALIDO, CONTRASENA_VALIDA));
        assertFalse(daoStub.seRegistro);
    }

    @Test
    public void registrarProfesor_correoDuplicado_lanzaExcepcion() {
        daoStub.existeCorreoRespuesta = true;

        assertThrows(IllegalStateException.class,
                () -> administradorServicio.registrarProfesor(
                        NUMERO_PERSONAL_VALIDO, "Ana", "López", null,
                        CORREO_VALIDO, CONTRASENA_VALIDA));
        assertFalse(daoStub.seRegistro);
    }

    /* ── Alta exitosa ───────────────────────────────────────── */

    @Test
    public void registrarProfesor_datosValidos_registraProfesorActivo()
            throws SQLException {
        administradorServicio.registrarProfesor(
                "  " + NUMERO_PERSONAL_VALIDO + "  ", "Ana", "López",
                "Cruz", "  " + CORREO_VALIDO + "  ", CONTRASENA_VALIDA);

        assertTrue(daoStub.seRegistro);
        Profesor registrado = daoStub.profesorRegistrado;
        assertEquals(NUMERO_PERSONAL_VALIDO,
                registrado.getNumeroPersonal());
        assertEquals(CORREO_VALIDO, registrado.getCorreo());
        assertEquals("Cruz", registrado.getApellidoMaterno());
        assertEquals(Constantes.ESTADO_ACTIVO, registrado.getEstado());
    }

    /* ── Listados (delegación simple al DAO) ────────────────── */

    @Test
    public void listarProfesores_delegaEnElDao() throws SQLException {
        daoStub.listaTodos.add(new Profesor());

        List<Profesor> resultado =
                administradorServicio.listarProfesores();

        assertEquals(1, resultado.size());
    }

    @Test
    public void listarCandidatosACoordinador_delegaEnElDao()
            throws SQLException {
        daoStub.listaCandidatos.add(new Profesor());

        List<Profesor> resultado =
                administradorServicio.listarCandidatosACoordinador();

        assertEquals(1, resultado.size());
    }

    @Test
    public void obtenerCoordinadorActual_delegaEnElDao()
            throws SQLException {
        Profesor coordinador = new Profesor();
        daoStub.coordinadorActual = coordinador;

        assertEquals(coordinador,
                administradorServicio.obtenerCoordinadorActual());
    }

    /* ── CU-Admin.-03: transferir rol de Coordinador ────────── */

    @Test
    public void transferirRolCoordinador_idInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio
                        .transferirRolCoordinador(0, null));
    }

    @Test
    public void transferirRolCoordinador_mismoQueElAnterior_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> administradorServicio
                        .transferirRolCoordinador(5, 5));
    }

    @Test
    public void transferirRolCoordinador_sinCoordinadorPrevio_transfiere()
            throws SQLException {
        administradorServicio.transferirRolCoordinador(5, null);

        assertEquals(Integer.valueOf(5), daoStub.idNuevoRecibido);
        assertNull(daoStub.idAnteriorRecibido);
    }

    @Test
    public void transferirRolCoordinador_datosValidos_transfiere()
            throws SQLException {
        administradorServicio.transferirRolCoordinador(5, 3);

        assertEquals(Integer.valueOf(5), daoStub.idNuevoRecibido);
        assertEquals(Integer.valueOf(3), daoStub.idAnteriorRecibido);
    }

    /* ── Doble de prueba del DAO (sin BD, sin red) ─────────── */

    /**
     * Doble de prueba de {@link ProfesorDAO}. Permite configurar las
     * respuestas de las consultas de duplicados y verificar los
     * datos con los que se invocó cada operación de escritura.
     */
    static class ProfesorDAOStub implements ProfesorDAO {

        boolean existeNumeroPersonalRespuesta = false;
        boolean existeCorreoRespuesta = false;
        boolean seRegistro = false;
        Profesor profesorRegistrado;
        List<Profesor> listaTodos = new ArrayList<>();
        List<Profesor> listaCandidatos = new ArrayList<>();
        Profesor coordinadorActual;
        Integer idNuevoRecibido;
        Integer idAnteriorRecibido;

        @Override
        public boolean existeNumeroPersonal(String numeroPersonal) {
            return existeNumeroPersonalRespuesta;
        }

        @Override
        public boolean existeCorreoInstitucional(
                String correoInstitucional) {
            return existeCorreoRespuesta;
        }

        @Override
        public void registrar(Profesor profesor) {
            seRegistro = true;
            profesorRegistrado = profesor;
        }

        @Override
        public List<Profesor> listarTodos() {
            return listaTodos;
        }

        @Override
        public List<Profesor> listarActivosNoCoordinador() {
            return listaCandidatos;
        }

        @Override
        public Profesor obtenerCoordinadorActual() {
            return coordinadorActual;
        }

        @Override
        public void transferirRolCoordinador(int idNuevoCoordinador,
                Integer idCoordinadorAnterior) {
            idNuevoRecibido = idNuevoCoordinador;
            idAnteriorRecibido = idCoordinadorAnterior;
        }
    }

}
