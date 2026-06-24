-- ============================================================
-- Sistema de Prácticas Profesionales (SPP)
-- Copyright (c) 2026 Nicolas Cruz && Isaac Vazquez.
-- Todos los derechos reservados.
-- Fecha de creacion: 13 de junio del 2026
--
-- ESQUEMA DE BASE DE DATOS
-- Motor:  InnoDB (soporte de llaves foraneas)
-- Juego de caracteres: utf8 / utf8_spanish_ci
-- Compatible con MySQL 5.5+
--
-- NOTA: los campos marcados [CIFRADO] se almacenan como
-- Base64(AES-128-CBC). El cifrado/descifrado ocurre en la
-- capa Java (CifradoAES.java); MySQL los trata como VARCHAR.
--
-- Orden de ejecucion: este archivo primero, luego spp_datos.sql
-- ============================================================

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS spp
    DEFAULT CHARACTER SET utf8
    DEFAULT COLLATE utf8_spanish_ci;

USE spp;


-- ------------------------------------------------------------
-- CATALOGOS
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS tipo_evidencia (
    id_tipo_evidencia INT         NOT NULL AUTO_INCREMENT,
    nombre            VARCHAR(50) NOT NULL,
    descripcion       VARCHAR(300),
    CONSTRAINT pk_tipo_evidencia
        PRIMARY KEY (id_tipo_evidencia),
    CONSTRAINT uq_tipo_evidencia_nombre
        UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS estado_documento (
    id_estado_documento INT         NOT NULL AUTO_INCREMENT,
    nombre              VARCHAR(50) NOT NULL,
    CONSTRAINT pk_estado_documento
        PRIMARY KEY (id_estado_documento),
    CONSTRAINT uq_estado_documento_nombre
        UNIQUE (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ------------------------------------------------------------
-- USUARIOS
-- Todos los campos [CIFRADO] usan VARCHAR(500) para alojar
-- el resultado Base64 del cifrado AES-128-CBC.
-- Columnas de seguridad comunes en todas las tablas de usuario:
--   intentos_fallidos  -> contador SEG-01 (max 3)
--   fecha_bloqueo      -> timestamp del bloqueo (10 min)
--   contrasena_temporal-> 1 = debe cambiar en primer login SEG-02
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS administrador (
    id_administrador    INT          NOT NULL AUTO_INCREMENT,
    nombre              VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    correo              VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    contrasena          VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    estado              VARCHAR(20)  NOT NULL DEFAULT 'Activo',
    fecha_registro      DATE         NOT NULL,
    contrasena_temporal TINYINT(1)   NOT NULL DEFAULT 1,
    intentos_fallidos   INT          NOT NULL DEFAULT 0,
    fecha_bloqueo       DATETIME,
    CONSTRAINT pk_administrador
        PRIMARY KEY (id_administrador),
    CONSTRAINT uq_administrador_correo
        UNIQUE (correo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS coordinador (
    id_coordinador      INT          NOT NULL AUTO_INCREMENT,
    num_personal        VARCHAR(20)  NOT NULL,
    nombre              VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    primer_apellido     VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    segundo_apellido    VARCHAR(500)          COMMENT '[CIFRADO]',
    correo              VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    contrasena          VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    estado              VARCHAR(20)  NOT NULL DEFAULT 'Activo',
    fecha_registro      DATE         NOT NULL,
    tiempo_servicio     INT          NOT NULL DEFAULT 0,
    contrasena_temporal TINYINT(1)   NOT NULL DEFAULT 1,
    intentos_fallidos   INT          NOT NULL DEFAULT 0,
    fecha_bloqueo       DATETIME,
    CONSTRAINT pk_coordinador
        PRIMARY KEY (id_coordinador),
    CONSTRAINT uq_coordinador_num_personal
        UNIQUE (num_personal),
    CONSTRAINT uq_coordinador_correo
        UNIQUE (correo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS profesor (
    id_profesor         INT          NOT NULL AUTO_INCREMENT,
    num_personal        VARCHAR(20)  NOT NULL,
    nombre              VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    primer_apellido     VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    segundo_apellido    VARCHAR(500)          COMMENT '[CIFRADO]',
    correo              VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    contrasena          VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    estado              VARCHAR(20)  NOT NULL DEFAULT 'Activo',
    fecha_registro      DATE         NOT NULL,
    tiempo_servicio     INT          NOT NULL DEFAULT 0,
    turno               VARCHAR(20)  NOT NULL DEFAULT 'Matutino',
    contrasena_temporal TINYINT(1)   NOT NULL DEFAULT 1,
    intentos_fallidos   INT          NOT NULL DEFAULT 0,
    fecha_bloqueo       DATETIME,
    CONSTRAINT pk_profesor
        PRIMARY KEY (id_profesor),
    CONSTRAINT uq_profesor_num_personal
        UNIQUE (num_personal),
    CONSTRAINT uq_profesor_correo
        UNIQUE (correo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Login del estudiante es por matricula, no por correo (sec. 4).
-- Ambos campos son UNIQUE para garantizar identidad unica.
CREATE TABLE IF NOT EXISTS estudiante (
    id_estudiante       INT          NOT NULL AUTO_INCREMENT,
    matricula           VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    nombre              VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    primer_apellido     VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    segundo_apellido    VARCHAR(500)          COMMENT '[CIFRADO]',
    correo              VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    contrasena          VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    idioma              VARCHAR(100),
    lengua_indigena     VARCHAR(100),
    semestre            INT          NOT NULL DEFAULT 9,
    estado              VARCHAR(20)  NOT NULL DEFAULT 'Activo',
    contrasena_temporal TINYINT(1)   NOT NULL DEFAULT 1,
    intentos_fallidos   INT          NOT NULL DEFAULT 0,
    fecha_bloqueo       DATETIME,
    CONSTRAINT pk_estudiante
        PRIMARY KEY (id_estudiante),
    CONSTRAINT uq_estudiante_matricula
        UNIQUE (matricula),
    CONSTRAINT uq_estudiante_correo
        UNIQUE (correo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ------------------------------------------------------------
-- ESTRUCTURA ACADEMICA
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ciclo_escolar (
    id_ciclo_escolar INT         NOT NULL AUTO_INCREMENT,
    periodo          VARCHAR(20) NOT NULL,
    fecha_inicio     DATE        NOT NULL,
    fecha_fin        DATE        NOT NULL,
    activo           TINYINT(1)  NOT NULL DEFAULT 0,
    CONSTRAINT pk_ciclo_escolar
        PRIMARY KEY (id_ciclo_escolar),
    CONSTRAINT uq_ciclo_escolar_periodo
        UNIQUE (periodo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS experiencia_educativa (
    id_ee    INT          NOT NULL AUTO_INCREMENT,
    nombre   VARCHAR(200) NOT NULL,
    clave    VARCHAR(20)  NOT NULL,
    creditos INT          NOT NULL,
    CONSTRAINT pk_experiencia_educativa
        PRIMARY KEY (id_ee),
    CONSTRAINT uq_ee_clave
        UNIQUE (clave)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ------------------------------------------------------------
-- ORGANIZACIONES Y PROYECTOS
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS organizacion_vinculada (
    id_organizacion INT          NOT NULL AUTO_INCREMENT,
    nombre_empresa  VARCHAR(200) NOT NULL,
    sector          VARCHAR(100) NOT NULL,
    ciudad          VARCHAR(100) NOT NULL,
    direccion       VARCHAR(300) NOT NULL,
    telefono        VARCHAR(20),
    estado          VARCHAR(20)  NOT NULL DEFAULT 'Activo',
    CONSTRAINT pk_organizacion_vinculada
        PRIMARY KEY (id_organizacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS responsable_proyecto (
    id_responsable   INT          NOT NULL AUTO_INCREMENT,
    id_organizacion  INT          NOT NULL,
    nombre_encargado VARCHAR(200) NOT NULL,
    cargo_encargado  VARCHAR(100) NOT NULL,
    email_encargado  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_responsable_proyecto
        PRIMARY KEY (id_responsable),
    CONSTRAINT fk_responsable_organizacion
        FOREIGN KEY (id_organizacion)
        REFERENCES organizacion_vinculada (id_organizacion)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS proyecto (
    id_proyecto       INT          NOT NULL AUTO_INCREMENT,
    id_organizacion   INT          NOT NULL,
    id_responsable    INT          NOT NULL,
    nombre_proyecto   VARCHAR(200) NOT NULL,
    descripcion       TEXT         NOT NULL,
    actividades       TEXT         NOT NULL,
    metodologia       VARCHAR(200),
    duracion_meses    INT          NOT NULL,
    horario_laboral   VARCHAR(200),
    recurso           VARCHAR(200),
    responsabilidades TEXT,
    cupo_maximo       INT          NOT NULL DEFAULT 1,
    cupo_disponible   INT          NOT NULL,
    estado            VARCHAR(20)  NOT NULL DEFAULT 'Disponible',
    CONSTRAINT pk_proyecto
        PRIMARY KEY (id_proyecto),
    CONSTRAINT fk_proyecto_organizacion
        FOREIGN KEY (id_organizacion)
        REFERENCES organizacion_vinculada (id_organizacion)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_proyecto_responsable
        FOREIGN KEY (id_responsable)
        REFERENCES responsable_proyecto (id_responsable)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ------------------------------------------------------------
-- GESTION DE PRACTICAS
-- ------------------------------------------------------------

-- Registra la inscripcion formal de un estudiante a la EE en un
-- periodo. La transicion de Estudiante a Practicante se refleja
-- en estado_practica = 'Asignado' (maquina de estados, sec. 9).
-- id_profesor es NULL hasta que el Coordinador lo asigne.
CREATE TABLE IF NOT EXISTS estudiante_inscrito (
    id_inscripcion    INT         NOT NULL AUTO_INCREMENT,
    id_estudiante     INT         NOT NULL,
    id_ciclo_escolar  INT         NOT NULL,
    id_ee             INT         NOT NULL,
    id_profesor       INT,
    estado_practica   VARCHAR(30) NOT NULL DEFAULT 'Inscrito',
    horas_acumuladas  INT         NOT NULL DEFAULT 0,
    fecha_inscripcion DATE        NOT NULL,
    CONSTRAINT pk_estudiante_inscrito
        PRIMARY KEY (id_inscripcion),
    CONSTRAINT uq_inscripcion_unica
        UNIQUE (id_estudiante, id_ciclo_escolar),
    CONSTRAINT fk_inscripcion_estudiante
        FOREIGN KEY (id_estudiante)
        REFERENCES estudiante (id_estudiante)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_inscripcion_ciclo
        FOREIGN KEY (id_ciclo_escolar)
        REFERENCES ciclo_escolar (id_ciclo_escolar)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_inscripcion_ee
        FOREIGN KEY (id_ee)
        REFERENCES experiencia_educativa (id_ee)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_inscripcion_profesor
        FOREIGN KEY (id_profesor)
        REFERENCES profesor (id_profesor)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- RN-11: el estudiante ordena TODOS los proyectos por prioridad;
-- accion irreversible. Las dos restricciones UNIQUE garantizan
-- que no haya prioridades ni proyectos duplicados por inscripcion.
CREATE TABLE IF NOT EXISTS seleccion_proyecto (
    id_seleccion    INT      NOT NULL AUTO_INCREMENT,
    id_inscripcion  INT      NOT NULL,
    id_proyecto     INT      NOT NULL,
    prioridad       INT      NOT NULL,
    fecha_seleccion DATETIME NOT NULL,
    CONSTRAINT pk_seleccion_proyecto
        PRIMARY KEY (id_seleccion),
    CONSTRAINT uq_seleccion_proyecto
        UNIQUE (id_inscripcion, id_proyecto),
    CONSTRAINT uq_seleccion_prioridad
        UNIQUE (id_inscripcion, prioridad),
    CONSTRAINT fk_seleccion_inscripcion
        FOREIGN KEY (id_inscripcion)
        REFERENCES estudiante_inscrito (id_inscripcion)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_seleccion_proyecto
        FOREIGN KEY (id_proyecto)
        REFERENCES proyecto (id_proyecto)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- La UNIQUE en id_inscripcion garantiza que un Practicante tenga
-- asignado maximo un proyecto en todo el periodo.
CREATE TABLE IF NOT EXISTS asignacion (
    id_asignacion    INT  NOT NULL AUTO_INCREMENT,
    id_inscripcion   INT  NOT NULL,
    id_proyecto      INT  NOT NULL,
    fecha_asignacion DATE NOT NULL,
    fecha_prorroga   DATE,
    CONSTRAINT pk_asignacion
        PRIMARY KEY (id_asignacion),
    CONSTRAINT uq_asignacion_inscripcion
        UNIQUE (id_inscripcion),
    CONSTRAINT fk_asignacion_inscripcion
        FOREIGN KEY (id_inscripcion)
        REFERENCES estudiante_inscrito (id_inscripcion)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_asignacion_proyecto
        FOREIGN KEY (id_proyecto)
        REFERENCES proyecto (id_proyecto)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ------------------------------------------------------------
-- DOCUMENTOS Y EVIDENCIAS
-- Las jerarquias DocumentoInicial y Evidencia (sec. 6) se
-- representan en una sola tabla diferenciada por tipo_evidencia
-- (patron tabla-por-jerarquia). Simplifica el acceso JDBC.
-- La re-entrega sobreescribe ruta_archivo (CU Practicante).
-- CHECK sobre calificacion ignorado en MySQL 5.5 (documentado).
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS documento (
    id_documento        INT           NOT NULL AUTO_INCREMENT,
    id_inscripcion      INT           NOT NULL,
    id_tipo_evidencia   INT           NOT NULL,
    id_estado_documento INT           NOT NULL,
    ruta_archivo        VARCHAR(500),
    nombre_archivo      VARCHAR(200),
    fecha_entrega       DATETIME,
    fecha_limite        DATE,
    fecha_prorroga      DATE,
    observaciones       TEXT,
    calificacion        DECIMAL(4,2),
    CONSTRAINT pk_documento
        PRIMARY KEY (id_documento),
    CONSTRAINT fk_documento_inscripcion
        FOREIGN KEY (id_inscripcion)
        REFERENCES estudiante_inscrito (id_inscripcion)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_documento_tipo
        FOREIGN KEY (id_tipo_evidencia)
        REFERENCES tipo_evidencia (id_tipo_evidencia)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_documento_estado
        FOREIGN KEY (id_estado_documento)
        REFERENCES estado_documento (id_estado_documento)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Las 10 afirmaciones tienen escala Likert 1-5.
-- calificacion = (puntuacion_total / 50.0) * 10; rango 2.0-10.0.
-- CHECK sobre rango 1-5 ignorado en MySQL 5.5 (documentado).
-- UNIQUE en id_documento: un Practicante entrega exactamente
-- una autoevaluacion por documento (accion irreversible, sec. 8).
CREATE TABLE IF NOT EXISTS autoevaluacion (
    id_autoevaluacion  INT          NOT NULL AUTO_INCREMENT,
    id_documento       INT          NOT NULL,
    afirmacion_1       INT          NOT NULL
        COMMENT 'Mi participacion en la OV fue productiva.',
    afirmacion_2       INT          NOT NULL
        COMMENT 'Logre la aplicacion de conocimientos teorico-practicos.',
    afirmacion_3       INT          NOT NULL
        COMMENT 'Me senti seguro al realizar las actividades.',
    afirmacion_4       INT          NOT NULL
        COMMENT 'Las actividades despertaron mi interes.',
    afirmacion_5       INT          NOT NULL
        COMMENT 'La OV me proporciono informacion y facilidades adecuadas.',
    afirmacion_6       INT          NOT NULL
        COMMENT 'La OV me dio a conocer las reglas internas.',
    afirmacion_7       INT          NOT NULL
        COMMENT 'El Responsable del Proyecto me oriento correctamente.',
    afirmacion_8       INT          NOT NULL
        COMMENT 'El Responsable realizo seguimiento efectivo.',
    afirmacion_9       INT          NOT NULL
        COMMENT 'El proyecto es congruente con la formacion de mi carrera.',
    afirmacion_10      INT          NOT NULL
        COMMENT 'Considero que las practicas son importantes para mi formacion.',
    puntuacion_total   INT          NOT NULL
        COMMENT 'Suma de afirmacion_1 a afirmacion_10. Rango: 10-50.',
    calificacion       DECIMAL(4,2) NOT NULL
        COMMENT 'Formula: (puntuacion_total / 50.0) * 10. Rango: 2.0-10.0.',
    CONSTRAINT pk_autoevaluacion
        PRIMARY KEY (id_autoevaluacion),
    CONSTRAINT uq_autoevaluacion_documento
        UNIQUE (id_documento),
    CONSTRAINT fk_autoevaluacion_documento
        FOREIGN KEY (id_documento)
        REFERENCES documento (id_documento)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ------------------------------------------------------------
-- CALIFICACION FINAL (RN-20)
-- UNIQUE en id_inscripcion: un Practicante tiene exactamente
-- una calificacion final por periodo.
-- ⚠️ Formula pendiente de confirmacion con stakeholders.
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS calificacion (
    id_calificacion    INT          NOT NULL AUTO_INCREMENT,
    id_inscripcion     INT          NOT NULL,
    valor_numerico     DECIMAL(4,2) NOT NULL,
    fecha_calificacion DATE         NOT NULL,
    CONSTRAINT pk_calificacion
        PRIMARY KEY (id_calificacion),
    CONSTRAINT uq_calificacion_inscripcion
        UNIQUE (id_inscripcion),
    CONSTRAINT fk_calificacion_inscripcion
        FOREIGN KEY (id_inscripcion)
        REFERENCES estudiante_inscrito (id_inscripcion)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ------------------------------------------------------------
-- COMUNICACION
-- Sin FK formales en id_remitente/id_destinatario porque el
-- remitente puede ser cualquier tipo de usuario (polimorfismo).
-- tipo_remitente / tipo_destinatario discriminan la tabla origen.
-- Valores validos: ADMINISTRADOR, COORDINADOR, PROFESOR,
--                  ESTUDIANTE.
-- asunto y cuerpo_mensaje se almacenan cifrados (AES-128).
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS mensaje (
    id_mensaje         INT          NOT NULL AUTO_INCREMENT,
    asunto             VARCHAR(500) NOT NULL COMMENT '[CIFRADO]',
    cuerpo_mensaje     TEXT         NOT NULL COMMENT '[CIFRADO]',
    id_remitente       INT          NOT NULL,
    tipo_remitente     VARCHAR(20)  NOT NULL,
    id_destinatario    INT          NOT NULL,
    tipo_destinatario  VARCHAR(20)  NOT NULL,
    fecha_envio        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    leido              TINYINT(1)   NOT NULL DEFAULT 0,
    CONSTRAINT pk_mensaje
        PRIMARY KEY (id_mensaje)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ------------------------------------------------------------
-- PERIODO DE INSCRIPCIONES (CU-20: Establecer periodo)
-- UNIQUE en id_ciclo_escolar: solo un periodo de inscripciones
-- por ciclo escolar.
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS periodo_inscripciones (
    id_periodo_inscripciones INT  NOT NULL AUTO_INCREMENT,
    id_ciclo_escolar         INT  NOT NULL,
    fecha_inicio             DATE NOT NULL,
    fecha_cierre             DATE NOT NULL,
    CONSTRAINT pk_periodo_inscripciones
        PRIMARY KEY (id_periodo_inscripciones),
    CONSTRAINT uq_pi_ciclo
        UNIQUE (id_ciclo_escolar),
    CONSTRAINT fk_pi_ciclo
        FOREIGN KEY (id_ciclo_escolar)
        REFERENCES ciclo_escolar (id_ciclo_escolar)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ------------------------------------------------------------
-- MENSAJE DE GRUPO (CU-31: Profesor envia mensaje a su grupo)
-- Se crea una tabla separada de `mensaje` para no alterar el
-- esquema de mensajes individuales ya definido. Soporta adjunto
-- PDF opcional (ruta_archivo y nombre_archivo pueden ser NULL).
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS mensaje_grupo (
    id_mensaje_grupo  INT          NOT NULL AUTO_INCREMENT,
    id_inscripcion    INT          NOT NULL
        COMMENT 'FK al grupo via estudiante_inscrito',
    id_profesor       INT          NOT NULL,
    texto             TEXT,
    ruta_archivo      VARCHAR(500)
        COMMENT 'Ruta local del PDF adjunto, puede ser NULL',
    nombre_archivo    VARCHAR(200),
    fecha_publicacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_mensaje_grupo
        PRIMARY KEY (id_mensaje_grupo),
    CONSTRAINT fk_mg_inscripcion
        FOREIGN KEY (id_inscripcion)
        REFERENCES estudiante_inscrito (id_inscripcion)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_mg_profesor
        FOREIGN KEY (id_profesor)
        REFERENCES profesor (id_profesor)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


SET FOREIGN_KEY_CHECKS = 1;
