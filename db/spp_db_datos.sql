-- ============================================================
-- Sistema de Practicas Profesionales (SPP)
-- Copyright (c) 2026 Nicolas Cruz && Isaac Vazquez.
-- Todos los derechos reservados.
-- Fecha de creacion: 30 de junio del 2026
--
-- DATOS DE PRUEBA para spp_db
-- PREREQUISITO: ejecutar spp_db.sql antes que este archivo.
--
-- Contrasenas en texto plano (spp_db no usa AES).
--
-- USUARIOS DE PRUEBA:
--   Administrador : usuario   -> admin          / Admin2026
--   Coordinador   : correo    -> coord@fei.uv.mx / Coord2026ok
--   Profesor      : correo    -> prof@fei.uv.mx  / Profe2026ok
--   Estudiante 1  : matricula -> zS21013417      / Alum12026ok
--   Estudiante 2  : matricula -> zS21013418      / Alum22026ok
-- ============================================================

USE spp_db;

-- ============================================================
-- 1. ADMINISTRADOR
-- ============================================================
INSERT INTO administrador (usuario, contrasenia) VALUES
    ('admin', 'Admin2026');

-- ============================================================
-- 2. PROFESORES (uno es Coordinador)
-- ============================================================
INSERT INTO profesor
    (numero_personal, nombre, apellido_paterno, apellido_materno,
     correo_institucional, contrasenia, coordinador, estado)
VALUES
    ('P001', 'Ana', 'García', 'López',
     'coord@fei.uv.mx', 'Coord2026ok', TRUE, 'activo'),
    ('P002', 'Carlos', 'Martínez', 'Ruiz',
     'prof@fei.uv.mx',  'Profe2026ok', FALSE, 'activo');

-- ============================================================
-- 3. ESTUDIANTES
-- ============================================================
INSERT INTO estudiante
    (matricula, nombre, apellido_paterno, apellido_materno,
     correo_institucional, sexo, lengua_indigena,
     contrasenia, estado)
VALUES
    ('zS21013417', 'Luis',    'Pérez',    'Sánchez',
     'luis@estudiantes.uv.mx',    'Masculino', FALSE,
     'Alum12026ok', 'activo'),
    ('zS21013418', 'María',   'Hernández','Torres',
     'maria@estudiantes.uv.mx',   'Femenino',  FALSE,
     'Alum22026ok', 'activo'),
    ('zS21013419', 'Jorge',   'Ramírez',  'Díaz',
     'jorge@estudiantes.uv.mx',   'Masculino', FALSE,
     'Alum32026ok', 'activo');

-- ============================================================
-- 4. PERIODO ESCOLAR (iniciado)
-- ============================================================
INSERT INTO periodo_escolar
    (nombre, fecha_inicio, fecha_fin, estado)
VALUES
    ('FEB-JUL 2026', '2026-02-02', '2026-07-31', 'iniciado');

-- ============================================================
-- 5. GRUPO
-- ============================================================
INSERT INTO grupo
    (nrc, nombre, periodo_escolar_id, profesor_id)
VALUES
    ('12345', 'Grupo A', 1, 2);

-- ============================================================
-- 6. INSCRIPCIONES
-- ============================================================
INSERT INTO inscripcion
    (estudiante_id, grupo_id, estado)
VALUES
    (1, 1, 'enCurso'),
    (2, 1, 'enCurso'),
    (3, 1, 'enCurso');

-- ============================================================
-- 7. ORGANIZACIÓN VINCULADA
-- ============================================================
INSERT INTO organizacion_vinculada
    (nombre, razon_social, ciudad, codigo_postal, municipio,
     nombre_calle, numero_calle, numero_telefono,
     correo_contacto, sector, estado)
VALUES
    ('TechCorp SA', 'TechCorp S.A. de C.V.',
     'Xalapa', '91000', 'Xalapa',
     'Av. Murillo Vidal', '123', '2281234567',
     'contacto@techcorp.mx', 'privado', 'activo'),
    ('SEFIPLAN', 'Secretaría de Finanzas de Veracruz',
     'Xalapa', '91000', 'Xalapa',
     'Av. Ávila Camacho', '456', '2289876543',
     'contacto@sefiplan.gob.mx', 'publico', 'activo');

-- ============================================================
-- 8. PROYECTOS
-- ============================================================
INSERT INTO proyecto
    (nombre, descripcion_general, objetivos, metodologia,
     recursos, cupo_maximo, cupo_disponible,
     nombre_responsable, apellido_paterno_responsable,
     apellido_materno_responsable, correo_contacto_responsable,
     organizacion_vinculada_id, estado)
VALUES
    ('Sistema de Inventarios',
     'Desarrollo de un sistema de control de inventarios.',
     'Automatizar el registro de entradas y salidas.',
     'Scrum con sprints de 2 semanas.',
     'Computadora, acceso a red, licencias de software.',
     3, 3,
     'Roberto', 'González', 'Mora',
     'roberto@techcorp.mx', 1, 'activo'),
    ('Portal de Transparencia',
     'Desarrollo de portal web de transparencia gubernamental.',
     'Publicar información de gasto público en línea.',
     'Kanban con revisiones semanales.',
     'Servidor de pruebas, acceso a base de datos.',
     2, 2,
     'Elena', 'Flores', 'Vega',
     'elena@sefiplan.gob.mx', 2, 'activo'),
    ('App Móvil de Reportes',
     'Aplicación móvil para generación de reportes.',
     'Permitir a usuarios generar reportes desde celular.',
     'Metodología ágil XP.',
     'Equipos móviles Android para pruebas.',
     2, 2,
     'Miguel', 'Ríos', 'Castro',
     'miguel@techcorp.mx', 1, 'activo');

-- ============================================================
-- 9. ENTREGAS (una fila por estudiante por entregable)
-- Solo se crean las necesarias para los CUs implementados.
-- id_entregable 5=reporte_mensual_1, 9=informe_parcial,
-- 10=informe_final, 11=presentacion_1, 13=evaluacion_ov_1,
-- 15=autoevaluacion
-- ============================================================
INSERT INTO entrega (estudiante_id, entregable_id, estado)
VALUES
    -- Estudiante 1 (Luis)
    (1,  5, 'noEntregada'),
    (1,  6, 'noEntregada'),
    (1,  7, 'noEntregada'),
    (1,  8, 'noEntregada'),
    (1,  9, 'noEntregada'),
    (1, 10, 'noEntregada'),
    (1, 11, 'noEntregada'),
    (1, 12, 'noEntregada'),
    (1, 13, 'noEntregada'),
    (1, 14, 'noEntregada'),
    (1, 15, 'noEntregada'),
    -- Estudiante 2 (María)
    (2,  5, 'noEntregada'),
    (2,  6, 'noEntregada'),
    (2,  7, 'noEntregada'),
    (2,  8, 'noEntregada'),
    (2,  9, 'noEntregada'),
    (2, 10, 'noEntregada'),
    (2, 11, 'noEntregada'),
    (2, 12, 'noEntregada'),
    (2, 13, 'noEntregada'),
    (2, 14, 'noEntregada'),
    (2, 15, 'noEntregada'),
    -- Estudiante 3 (Jorge)
    (3,  5, 'noEntregada'),
    (3,  6, 'noEntregada'),
    (3,  7, 'noEntregada'),
    (3,  8, 'noEntregada'),
    (3,  9, 'noEntregada'),
    (3, 10, 'noEntregada'),
    (3, 11, 'noEntregada'),
    (3, 12, 'noEntregada'),
    (3, 13, 'noEntregada'),
    (3, 14, 'noEntregada'),
    (3, 15, 'noEntregada');
