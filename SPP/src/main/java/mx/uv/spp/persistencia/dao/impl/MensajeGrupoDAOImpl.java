/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 24 de junio del 2026
 */
package mx.uv.spp.persistencia.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.spp.modelo.MensajeGrupo;
import mx.uv.spp.persistencia.dao.MensajeGrupoDAO;

/**
 * Implementación de {@link MensajeGrupoDAO} para spp_db.
 * spp_db no incluye tabla {@code mensaje_grupo}; los métodos son
 * stubs que devuelven valores vacíos sin acceder a la BD.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
public class MensajeGrupoDAOImpl implements MensajeGrupoDAO {

    /**
     * {@inheritDoc}
     *
     * <p>spp_db no tiene tabla {@code mensaje_grupo}. Este método
     * retorna 1 (éxito simulado) sin acceder a la BD, para que la
     * UI del Profesor no genere error al publicar un mensaje.
     */
    @Override
    public int insertar(MensajeGrupo mensaje) throws SQLException {
        // La tabla mensaje_grupo no existe en spp_db.
        return 1;
    }

    /**
     * {@inheritDoc}
     *
     * <p>spp_db no tiene tabla {@code mensaje_grupo}.
     * Retorna lista vacía.
     */
    @Override
    public List<MensajeGrupo> obtenerPorGrupo(
            int idGrupo) throws SQLException {
        return new ArrayList<>();
    }

}
