/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 1 de julio del 2026
 */
package mx.uv.spp.negocio;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import mx.uv.spp.modelo.Profesor;
import mx.uv.spp.persistencia.dao.ProfesorDAO;
import mx.uv.spp.util.Constantes;
import mx.uv.spp.util.Validador;

/**
 * Lógica de negocio para los casos de uso del Administrador.
 * Cubre el alta de Profesores (CU-Admin.-01). No accede a la BD
 * directamente; delega en el DAO recibido por inyección de
 * dependencia.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class AdministradorServicio {

    private final ProfesorDAO profesorDAO;

    /**
     * Construye el servicio con el DAO inyectado.
     *
     * @param profesorDAO DAO de profesores; no puede ser nulo.
     * @throws IllegalArgumentException si {@code profesorDAO} es nulo.
     */
    public AdministradorServicio(ProfesorDAO profesorDAO) {
        if (profesorDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de profesores no puede ser nulo.");
        }
        this.profesorDAO = profesorDAO;
    }

    /**
     * Registra a un nuevo Profesor en estado activo (CU-Admin.-01).
     * Revalida todos los campos de forma defensiva, sin confiar en
     * que la capa de presentación ya lo haya hecho, y verifica que
     * no exista ya un Profesor (activo o inactivo) con el mismo
     * número de personal o correo institucional (FA-06).
     *
     * @param numeroPersonal      Número de personal; exactamente
     *                            {@value Constantes#LONGITUD_NUMERO_PERSONAL}
     *                            dígitos.
     * @param nombre              Nombre(s); solo letras.
     * @param apellidoPaterno     Apellido paterno; solo letras.
     * @param apellidoMaterno     Apellido materno; opcional, puede
     *                            ser nulo o vacío.
     * @param correoInstitucional Correo con dominio
     *                            {@value Constantes#DOMINIO_CORREO_PROFESOR}.
     * @param contrasena          Contraseña en texto plano; debe
     *                            cumplir SEG-03.
     * @throws IllegalArgumentException si algún campo obligatorio
     *         está vacío o no cumple su formato.
     * @throws IllegalStateException si ya existe un Profesor con el
     *         mismo número de personal o correo institucional.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public void registrarProfesor(
            String numeroPersonal,
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            String correoInstitucional,
            String contrasena)
            throws SQLException, IllegalArgumentException,
                   IllegalStateException {

        Validador.validarCadenaNoVacia(
                numeroPersonal, "número de personal");
        Validador.validarCadenaNoVacia(nombre, "nombre");
        Validador.validarCadenaNoVacia(
                apellidoPaterno, "apellido paterno");
        Validador.validarCadenaNoVacia(
                correoInstitucional, "correo institucional");
        Validador.validarCadenaNoVacia(contrasena, "contraseña");

        Validador.validarNumeroPersonal(numeroPersonal);
        Validador.validarCorreoInstitucionalProfesor(
                correoInstitucional);
        Validador.validarSoloLetras(nombre, "nombre");
        Validador.validarSoloLetras(
                apellidoPaterno, "apellido paterno");

        String apellidoMaternoNormalizado = apellidoMaterno == null
                ? "" : apellidoMaterno.trim();
        if (!apellidoMaternoNormalizado.isEmpty()) {
            Validador.validarSoloLetras(
                    apellidoMaternoNormalizado, "apellido materno");
        }

        Validador.validarContrasena(contrasena);

        String numeroPersonalNormalizado = numeroPersonal.trim();
        String correoNormalizado = correoInstitucional.trim();

        if (profesorDAO.existeNumeroPersonal(numeroPersonalNormalizado)
                || profesorDAO.existeCorreoInstitucional(
                        correoNormalizado)) {
            throw new IllegalStateException(
                    "Ya existe un profesor registrado con el mismo "
                    + "número de personal o correo institucional.");
        }

        Profesor nuevo = new Profesor(
                0,
                numeroPersonalNormalizado,
                nombre.trim(),
                apellidoPaterno.trim(),
                apellidoMaternoNormalizado.isEmpty()
                        ? null : apellidoMaternoNormalizado,
                correoNormalizado,
                contrasena,
                Constantes.ESTADO_ACTIVO,
                LocalDate.now(),
                0, 0, null, null);

        profesorDAO.registrar(nuevo);
    }

    /**
     * Recupera todos los Profesores registrados (activos e
     * inactivos) para el panel de gestión del Administrador.
     *
     * @return lista de Profesores; nunca nula, puede ser vacía.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public List<Profesor> listarProfesores() throws SQLException {
        return profesorDAO.listarTodos();
    }

    /**
     * Recupera los Profesores activos que son candidatos válidos
     * para recibir el rol de Coordinador (CU-Admin.-03), es decir,
     * excluye al Coordinador vigente.
     *
     * @return lista de Profesores; nunca nula, puede ser vacía.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public List<Profesor> listarCandidatosACoordinador()
            throws SQLException {
        return profesorDAO.listarActivosNoCoordinador();
    }

    /**
     * Recupera al Profesor que actualmente posee el rol de
     * Coordinador (CU-Admin.-03, paso 2). Debe consultarse una sola
     * vez por operación de cambio de Coordinador y reutilizarse en
     * los pasos siguientes, en vez de volver a consultarlo.
     *
     * @return el Coordinador vigente, o {@code null} si ninguno
     *         posee el rol actualmente.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    public Profesor obtenerCoordinadorActual() throws SQLException {
        return profesorDAO.obtenerCoordinadorActual();
    }

    /**
     * Transfiere el rol de Coordinador a un Profesor (CU-Admin.-03,
     * paso 8). Revalida de forma defensiva que el destino sea
     * distinto del Coordinador anterior antes de delegar en el DAO
     * la actualización transaccional.
     *
     * @param idNuevoCoordinador    PK del Profesor seleccionado;
     *                              debe ser mayor que 0.
     * @param idCoordinadorAnterior PK del Coordinador vigente
     *                              obtenido previamente con
     *                              {@link #obtenerCoordinadorActual()};
     *                              {@code null} si no existía ninguno.
     * @throws IllegalArgumentException si {@code idNuevoCoordinador}
     *         no es válido o coincide con el Coordinador anterior.
     * @throws SQLException si ocurre un error de acceso a la BD; en
     *         ese caso ningún cambio queda aplicado.
     */
    public void transferirRolCoordinador(int idNuevoCoordinador,
            Integer idCoordinadorAnterior) throws SQLException {
        if (idNuevoCoordinador <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionarse un profesor válido.");
        }
        if (idCoordinadorAnterior != null
                && idCoordinadorAnterior == idNuevoCoordinador) {
            throw new IllegalArgumentException(
                    "El profesor seleccionado ya posee el rol de "
                    + "Coordinador.");
        }
        profesorDAO.transferirRolCoordinador(
                idNuevoCoordinador, idCoordinadorAnterior);
    }

}
