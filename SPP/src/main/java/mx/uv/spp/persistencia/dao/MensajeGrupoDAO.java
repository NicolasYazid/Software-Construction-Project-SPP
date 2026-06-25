/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.persistencia.dao;

import java.sql.SQLException;
import java.util.List;
import mx.uv.spp.modelo.MensajeGrupo;

/**
 * Contrato de acceso a datos para la tabla {@code mensaje_grupo}.
 * Permite al Profesor publicar mensajes (con adjunto PDF opcional)
 * visibles para todos los Estudiantes de su grupo (CU-31).
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public interface MensajeGrupoDAO {

    /**
     * Inserta un nuevo mensaje de grupo y retorna el id generado.
     *
     * @param mensaje Datos del mensaje; su campo
     *                {@code idMensajeGrupo} es ignorado.
     * @return identificador auto-generado del registro insertado.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    int insertar(MensajeGrupo mensaje) throws SQLException;

    /**
     * Recupera todos los mensajes publicados para el Grupo dado,
     * ordenados por {@code fecha_publicacion} descendente (más
     * recientes primero).
     *
     * @param idGrupo FK de {@code grupo}; identifica el grupo receptor.
     * @return lista (posiblemente vacía) de mensajes del grupo.
     * @throws SQLException si ocurre un error de acceso a la BD.
     */
    List<MensajeGrupo> obtenerPorGrupo(int idGrupo)
            throws SQLException;

}
