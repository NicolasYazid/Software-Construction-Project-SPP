-- ============================================================
-- Sistema de Practicas Profesionales (SPP)
-- Copyright (c) 2026 Nicolas Cruz && Isaac Vazquez.
-- Todos los derechos reservados.
-- Fecha de creacion: 30 de junio del 2026
--
-- SCHEMA de la base de datos spp_db.
-- Ejecutar este archivo ANTES que spp_db_datos.sql.
--
-- Motor: MySQL 5.5+
-- Codificacion: UTF-8
-- ============================================================

CREATE DATABASE IF NOT EXISTS spp_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE spp_db;

-- ============================================================
-- 1. ADMINISTRADOR
-- ============================================================
CREATE TABLE IF NOT EXISTS administrador (
    id          INT          NOT NULL AUTO_INCREMENT,
    usuario     VARCHAR(60)  NOT NULL,
    contrasenia VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_administrador_usuario (usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 2. PROFESOR  (coordinador = TRUE => rol coordinador)
-- ============================================================
CREATE TABLE IF NOT EXISTS profesor (
    id                   INT         NOT NULL AUTO_INCREMENT,
    numero_personal      VARCHAR(20) NOT NULL,
    nombre               VARCHAR(80) NOT NULL,
    apellido_paterno     VARCHAR(80) NOT NULL,
    apellido_materno     VARCHAR(80) NOT NULL,
    correo_institucional VARCHAR(120) NOT NULL,
    contrasenia          VARCHAR(100) NOT NULL,
    coordinador          TINYINT(1)  NOT NULL DEFAULT 0,
    estado               ENUM('activo','inactivo') NOT NULL DEFAULT 'activo',
    PRIMARY KEY (id),
    UNIQUE KEY uq_profesor_num_personal      (numero_personal),
    UNIQUE KEY uq_profesor_correo            (correo_institucional)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 3. ESTUDIANTE
-- ============================================================
CREATE TABLE IF NOT EXISTS estudiante (
    id                   INT         NOT NULL AUTO_INCREMENT,
    matricula            VARCHAR(20) NOT NULL,
    nombre               VARCHAR(80) NOT NULL,
    apellido_paterno     VARCHAR(80) NOT NULL,
    apellido_materno     VARCHAR(80) NOT NULL,
    correo_institucional VARCHAR(120) NOT NULL,
    sexo                 VARCHAR(20) NOT NULL,
    lengua_indigena      TINYINT(1)  NOT NULL DEFAULT 0,
    contrasenia          VARCHAR(100) NOT NULL,
    estado               ENUM('activo','inactivo') NOT NULL DEFAULT 'activo',
    PRIMARY KEY (id),
    UNIQUE KEY uq_estudiante_matricula (matricula),
    UNIQUE KEY uq_estudiante_correo    (correo_institucional)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 4. PERIODO ESCOLAR
-- ============================================================
CREATE TABLE IF NOT EXISTS periodo_escolar (
    id           INT         NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(40) NOT NULL,
    fecha_inicio DATE        NOT NULL,
    fecha_fin    DATE        NOT NULL,
    estado       ENUM('creado','iniciado','finalizado')
                             NOT NULL DEFAULT 'creado',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 5. GRUPO
-- ============================================================
CREATE TABLE IF NOT EXISTS grupo (
    id                INT         NOT NULL AUTO_INCREMENT,
    periodo_escolar_id INT        NOT NULL,
    profesor_id       INT         NOT NULL,
    nombre            VARCHAR(60) NOT NULL,
    nrc               VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_grupo_nrc (nrc),
    CONSTRAINT fk_grupo_periodo  FOREIGN KEY (periodo_escolar_id)
        REFERENCES periodo_escolar (id),
    CONSTRAINT fk_grupo_profesor FOREIGN KEY (profesor_id)
        REFERENCES profesor (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 6. INSCRIPCION  (Estudiante <-> Grupo)
-- ============================================================
CREATE TABLE IF NOT EXISTS inscripcion (
    id                INT            NOT NULL AUTO_INCREMENT,
    estudiante_id     INT            NOT NULL,
    grupo_id          INT            NOT NULL,
    calificacion_final DECIMAL(4,2)  NULL,
    estado            ENUM('enCurso','aprobado','reprobado','cancelada')
                                     NOT NULL DEFAULT 'enCurso',
    PRIMARY KEY (id),
    CONSTRAINT fk_inscripcion_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiante (id),
    CONSTRAINT fk_inscripcion_grupo      FOREIGN KEY (grupo_id)
        REFERENCES grupo (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 7. ORGANIZACION VINCULADA
-- ============================================================
CREATE TABLE IF NOT EXISTS organizacion_vinculada (
    id               INT          NOT NULL AUTO_INCREMENT,
    nombre           VARCHAR(120) NOT NULL,
    razon_social     VARCHAR(150) NOT NULL,
    ciudad           VARCHAR(60)  NOT NULL,
    codigo_postal    VARCHAR(10)  NOT NULL,
    municipio        VARCHAR(80)  NOT NULL,
    nombre_calle     VARCHAR(100) NOT NULL,
    numero_calle     VARCHAR(10)  NOT NULL,
    numero_telefono  VARCHAR(20)  NOT NULL,
    correo_contacto  VARCHAR(120) NOT NULL,
    sector           ENUM('publico','privado','educativo') NOT NULL,
    estado           ENUM('activo','inactivo') NOT NULL DEFAULT 'activo',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 8. PROYECTO
-- ============================================================
CREATE TABLE IF NOT EXISTS proyecto (
    id                            INT          NOT NULL AUTO_INCREMENT,
    nombre                        VARCHAR(150) NOT NULL,
    descripcion_general           TEXT         NOT NULL,
    objetivos                     TEXT         NOT NULL,
    metodologia                   TEXT         NOT NULL,
    recursos                      TEXT         NOT NULL,
    cupo_maximo                   INT          NOT NULL,
    cupo_disponible               INT          NOT NULL,
    nombre_responsable            VARCHAR(80)  NOT NULL,
    apellido_paterno_responsable  VARCHAR(80)  NOT NULL,
    apellido_materno_responsable  VARCHAR(80)  NOT NULL,
    correo_contacto_responsable   VARCHAR(120) NOT NULL,
    organizacion_vinculada_id     INT          NOT NULL,
    estado                        ENUM('activo','inactivo') NOT NULL DEFAULT 'activo',
    PRIMARY KEY (id),
    CONSTRAINT fk_proyecto_ov FOREIGN KEY (organizacion_vinculada_id)
        REFERENCES organizacion_vinculada (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 9. LISTA DE PRIORIDADES  (seleccion de proyectos por Estudiante)
--    Columnas requeridas por SeleccionProyectoDAOImpl:
--    estudiante_id, proyecto_id, posicion
-- ============================================================
CREATE TABLE IF NOT EXISTS lista_prioridades (
    id            INT NOT NULL AUTO_INCREMENT,
    estudiante_id INT NOT NULL,
    proyecto_id   INT NOT NULL,
    posicion      INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_lp_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiante (id),
    CONSTRAINT fk_lp_proyecto   FOREIGN KEY (proyecto_id)
        REFERENCES proyecto (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 10. ENTREGABLE  (catalogo: ids 1-15 fijos)
-- ============================================================
CREATE TABLE IF NOT EXISTS entregable (
    id     INT         NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    tipo   ENUM('DocumentoInicial','Evidencia') NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Datos del catalogo (deben coincidir con Constantes.java ids 1-15)
INSERT INTO entregable (id, nombre, tipo) VALUES
    ( 1, 'Oficio de Asignacion',     'DocumentoInicial'),
    ( 2, 'Oficio de Aceptacion',     'DocumentoInicial'),
    ( 3, 'Horario de Clases',        'DocumentoInicial'),
    ( 4, 'Cronograma',               'DocumentoInicial'),
    ( 5, 'Reporte Mensual 1',        'Evidencia'),
    ( 6, 'Reporte Mensual 2',        'Evidencia'),
    ( 7, 'Reporte Mensual 3',        'Evidencia'),
    ( 8, 'Reporte Mensual 4',        'Evidencia'),
    ( 9, 'Informe Parcial',          'Evidencia'),
    (10, 'Informe Final',            'Evidencia'),
    (11, 'Presentacion 1',           'Evidencia'),
    (12, 'Presentacion 2',           'Evidencia'),
    (13, 'Evaluacion OV 1',          'Evidencia'),
    (14, 'Evaluacion OV 2',          'Evidencia'),
    (15, 'Autoevaluacion',           'Evidencia');

-- ============================================================
-- 11. ENTREGA
--     Columnas requeridas por DocumentoDAOImpl:
--     id, estudiante_id, entregable_id, estado,
--     archivo_adjunto, fecha_entrega, calificacion
-- ============================================================
CREATE TABLE IF NOT EXISTS entrega (
    id             INT      NOT NULL AUTO_INCREMENT,
    estudiante_id  INT      NOT NULL,
    entregable_id  INT      NOT NULL,
    estado         ENUM('noEntregada','entregada','evaluada','evaluadaConRetardo')
                            NOT NULL DEFAULT 'noEntregada',
    archivo_adjunto TEXT    NULL,
    fecha_entrega  DATETIME NULL,
    calificacion   DECIMAL(4,2) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_entrega_estudiante  FOREIGN KEY (estudiante_id)
        REFERENCES estudiante (id),
    CONSTRAINT fk_entrega_entregable  FOREIGN KEY (entregable_id)
        REFERENCES entregable (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 12. PRORROGA
--     Columnas requeridas por DocumentoDAOImpl.insertarProrroga:
--     id, entrega_id, estado, fecha_inicio, fecha_fin
-- ============================================================
CREATE TABLE IF NOT EXISTS prorroga (
    id          INT  NOT NULL AUTO_INCREMENT,
    entrega_id  INT  NOT NULL,
    estado      ENUM('activa','vencida') NOT NULL DEFAULT 'activa',
    fecha_inicio DATE NOT NULL,
    fecha_fin    DATE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_prorroga_entrega FOREIGN KEY (entrega_id)
        REFERENCES entrega (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 13. RESPUESTA AUTOEVALUACION
--     Columnas requeridas por AutoevaluacionDAOImpl:
--     id, entrega_id, numero_afirmacion, valor
-- ============================================================
CREATE TABLE IF NOT EXISTS respuesta_autoevaluacion (
    id                INT NOT NULL AUTO_INCREMENT,
    entrega_id        INT NOT NULL,
    numero_afirmacion INT NOT NULL,
    valor             INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ra_entrega FOREIGN KEY (entrega_id)
        REFERENCES entrega (id),
    CONSTRAINT chk_ra_valor CHECK (valor BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
