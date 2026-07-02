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
import java.time.temporal.ChronoUnit;
import mx.uv.spp.modelo.ResultadoAutenticacion;
import mx.uv.spp.modelo.TipoUsuario;
import mx.uv.spp.persistencia.dao.UsuarioDAO;
import mx.uv.spp.util.Constantes;
import mx.uv.spp.util.Validador;

/**
 * Lógica de negocio del módulo de autenticación.
 * Aplica la regla de seguridad SEG-01 (bloqueo tras 3 intentos
 * fallidos por 10 minutos).
 *
 * <p>Esta clase no conoce tablas ni SQL; delega el acceso a datos en
 * {@link UsuarioDAO}. El controlador JavaFX no debe instanciar esta
 * clase con {@code new}; debe recibirla ya construida para permitir
 * pruebas unitarias con un DAO simulado.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class LoginServicio {

    private final UsuarioDAO usuarioDAO;

    /**
     * Construye el servicio con el DAO de usuario inyectado.
     *
     * @param usuarioDAO Implementación de acceso a datos de usuarios;
     * no puede ser nulo.
     * @throws IllegalArgumentException si {@code usuarioDAO} es nulo.
     */
    public LoginServicio(UsuarioDAO usuarioDAO) {
        if (usuarioDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de usuario no puede ser nulo.");
        }
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Autentica a un usuario aplicando todas las reglas de seguridad.
     *
     * <p>Flujo completo:
     * <ol>
     * <li>Valida que los campos no estén vacíos.</li>
     * <li>Consulta al DAO; si el identificador no existe retorna
     * un error genérico (sin revelar qué campo falló).</li>
     * <li>Verifica que la cuenta esté {@code Activo}.</li>
     * <li>Comprueba si existe un bloqueo vigente (SEG-01); si el
     * periodo de 10 min expiró, reinicia los contadores.</li>
     * <li>Si las credenciales son incorrectas, incrementa intentos
     * y, al llegar a 3, notifica el bloqueo.</li>
     * <li>Si son correctas, reinicia contadores y retorna éxito.</li>
     * </ol>
     *
     * @param identificador Correo (ADMIN/COORDINADOR/PROFESOR) o
     * matrícula (ESTUDIANTE) en texto plano.
     * @param contrasena Contraseña en texto plano ingresada por el usuario.
     * @param tipo Rol seleccionado en la pantalla de login.
     * @return {@link ResultadoAutenticacion} nunca nulo; consultar
     * {@code isExitoso()} y {@code getMensajeError()}.
     * @throws IllegalArgumentException si algún campo de entrada está vacío.
     * @throws SQLException si ocurre un error de acceso a la base de datos.
     */
    public ResultadoAutenticacion autenticarUsuario(
            String identificador, String contrasena,
            TipoUsuario tipo) throws SQLException {

        String campo = (tipo == TipoUsuario.ESTUDIANTE)
                ? "matrícula" : "correo electrónico";
        Validador.validarCadenaNoVacia(identificador, campo);
        Validador.validarCadenaNoVacia(contrasena, "contraseña");

        ResultadoAutenticacion resultado = usuarioDAO.autenticar(
                identificador.trim(), contrasena, tipo);

        // Identificador no existe: mensaje genérico para no revelar
        // si el usuario existe o no (principio de mínima información).
        if (resultado.getIdUsuario() == 0) {
            return resultado;
        }

        if (Constantes.ESTADO_INACTIVO.equals(resultado.getEstado())) {
            resultado.setExitoso(false);
            resultado.setMensajeError(
                    "La cuenta está inactiva. "
                    + "Contacte al administrador.");
            return resultado;
        }

        if (resultado.getFechaBloqueo() != null) {
            LocalDateTime desbloqueo = resultado.getFechaBloqueo()
                    .plusMinutes(Constantes.MINUTOS_BLOQUEO);

            if (LocalDateTime.now().isBefore(desbloqueo)) {
                long restantes = ChronoUnit.MINUTES.between(
                        LocalDateTime.now(), desbloqueo) + 1;
                resultado.setExitoso(false);
                resultado.setMensajeError(
                        "Cuenta bloqueada. Intente de nuevo en "
                        + restantes + " minuto(s).");
                return resultado;
            }

            // El bloqueo expiró: limpiar para permitir un nuevo intento.
            usuarioDAO.reiniciarIntentos(
                    resultado.getIdUsuario(), tipo);
            resultado.setFechaBloqueo(null);
            resultado.setIntentosFallidos(0);
        }

        if (!resultado.isExitoso()) {
            usuarioDAO.incrementarIntentosFallidos(
                    resultado.getIdUsuario(), tipo);
            int intentosTras = resultado.getIntentosFallidos() + 1;

            if (intentosTras >= Constantes.MAX_INTENTOS_LOGIN) {
                resultado.setMensajeError(
                        "Cuenta bloqueada por "
                        + Constantes.MINUTOS_BLOQUEO
                        + " minutos (3 intentos fallidos).");
            } else {
                int restantes =
                        Constantes.MAX_INTENTOS_LOGIN - intentosTras;
                resultado.setMensajeError(
                        "Credenciales incorrectas. "
                        + restantes + " intento(s) restante(s).");
            }
            return resultado;
        }

        // Login exitoso: siempre reiniciar el contador de intentos.
        usuarioDAO.reiniciarIntentos(resultado.getIdUsuario(), tipo);
        return resultado;
    }

}
