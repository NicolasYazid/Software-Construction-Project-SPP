-- ============================================================
-- Sistema de Practicas Profesionales (SPP)
-- Copyright (c) 2026 Nicolas Cruz && Isaac Vazquez.
-- Todos los derechos reservados.
-- Fecha de creacion: 01 de julio del 2026
--
-- DATOS DE PRUEBA COMPLETOS para demostracion de 5 CUs de Isaac
-- PREREQUISITO: ejecutar spp_db.sql antes que este archivo.
--
-- USUARIOS DE PRUEBA:
--   Administrador : usuario   -> admin             / Admin2026
--   Coordinador   : correo    -> coord@fei.uv.mx   / Coord2026ok
--   Profesor 1    : correo    -> prof@fei.uv.mx    / Profe2026ok
--   Profesor 2    : correo    -> prof2@fei.uv.mx   / Profe22026ok
--
--   Estudiante A  (estado limpio, puede hacer tod o):
--                  matricula -> zS21013417          / Alum12026ok
--   Estudiante B  (documentos iniciales parcialmente entregados):
--                  matricula -> zS21013418          / Alum22026ok
--   Estudiante C  (autoevaluacion ya respondida — bloqueada):
--                  matricula -> zS21013419          / Alum32026ok
--   Estudiante D  (evidencias entregadas para que el Profesor evalúe):
--                  matricula -> zS21013420          / Alum42026ok
--   Estudiante E  (cuenta bloqueada por intentos fallidos):
--                  matricula -> zS21013421          / Alum52026ok
-- ============================================================

USE spp_db;

-- ============================================================
-- LIMPIEZA (ejecutar primero)
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE respuesta_autoevaluacion;
TRUNCATE TABLE prorroga;
TRUNCATE TABLE entrega;
TRUNCATE TABLE asignacion;
TRUNCATE TABLE lista_prioridades;
TRUNCATE TABLE inscripcion;
TRUNCATE TABLE grupo;
TRUNCATE TABLE proyecto;
TRUNCATE TABLE organizacion_vinculada;
TRUNCATE TABLE periodo_escolar;
TRUNCATE TABLE estudiante;
TRUNCATE TABLE profesor;
TRUNCATE TABLE administrador;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 1. ADMINISTRADOR
-- ============================================================
-- Contraseñas cifradas con AES-128-CBC (SEG-04), clave/IV de bd.properties
-- (bd.clave_cifrado=ClavesPrueba2026, bd.iv_cifrado=IVPrueba20262026).
-- Contraseñas en claro para pruebas de login (ver comentario de cada fila).
INSERT INTO administrador (usuario, contrasenia) VALUES
    ('admin', 'A4cs60jOsvcFp9Xm117mpA=='); -- Admin2026

-- ============================================================
-- 2. PROFESORES
-- ============================================================
INSERT INTO profesor
    (numero_personal, nombre, apellido_paterno, apellido_materno,
     correo_institucional, contrasenia, coordinador, estado)
VALUES
    ('P001', 'Ana',    'García',   'López',
     'coord@fei.uv.mx',  'kCFP16wkvF9qZ2mgNNj+rQ==',  TRUE,  'activo'), -- Coord2026ok
    ('P002', 'Carlos', 'Martínez', 'Ruiz',
     'prof@fei.uv.mx',   'iJ9hmXOpAFyCI854gvbSLw==',  FALSE, 'activo'), -- Profe2026ok
    ('P003', 'Laura',  'Sánchez',  'Pérez',
     'prof2@fei.uv.mx',  'XyeyO5yKYs76z/Z+oH6aTg==', FALSE, 'activo'); -- Profe22026ok

-- ============================================================
-- 3. ESTUDIANTES
-- id=1 Luis   — estado limpio, puede hacer todos los CUs
-- id=2 María  — documentos iniciales parcialmente entregados
-- id=3 Jorge  — autoevaluacion ya respondida (bloqueado)
-- id=4 Diego  — evidencias entregadas (para demo del Profesor)
-- id=5 Sofía  — cuenta bloqueada por 3 intentos fallidos
-- ============================================================
INSERT INTO estudiante
    (matricula, nombre, apellido_paterno, apellido_materno,
     correo_institucional, sexo, lengua_indigena,
     contrasenia, estado, intentos_fallidos, fecha_bloqueo)
VALUES
    ('zS21013417', 'Luis',  'Pérez',    'Sánchez',
     'luis@uv.mx',  'Masculino', FALSE, 'uWIuMEiYkhXrL4Wwql/2og==', 'activo',  0, NULL), -- Alum12026ok
    ('zS21013418', 'María', 'Hernández','Torres',
     'maria@uv.mx', 'Femenino',  FALSE, 'ogxh6stDtU6UPKFJCubYJg==', 'activo',  0, NULL), -- Alum22026ok
    ('zS21013419', 'Jorge', 'Ramírez',  'Díaz',
     'jorge@uv.mx', 'Masculino', FALSE, 'cOI0j70iuiuY3piMBPih4A==', 'activo',  0, NULL), -- Alum32026ok
    ('zS21013420', 'Diego', 'Morales',  'Castro',
     'diego@uv.mx', 'Masculino', FALSE, 'p6M7tnwdQ14dL4ZdlCSRKQ==', 'activo',  0, NULL), -- Alum42026ok
    ('zS21013421', 'Sofía', 'Vega',     'Luna',
     'sofia@uv.mx', 'Femenino',  FALSE, 'nrEfaEGD9fIcuSvdRwv/Ng==', 'activo',  3, NOW()); -- Alum52026ok

-- ============================================================
-- 4. PERIODO ESCOLAR (iniciado)
-- ============================================================
INSERT INTO periodo_escolar
    (nombre, fecha_inicio, fecha_fin, estado)
VALUES
    ('FEB-JUL 2026', '2026-02-02', '2026-07-31', 'iniciado');

-- ============================================================
-- 5. GRUPOS
-- Grupo A → Profesor Carlos (id=2)
-- Grupo B → Profesora Laura (id=3)
-- ============================================================
INSERT INTO grupo (nrc, nombre, periodo_escolar_id, profesor_id)
VALUES
    ('12345', 'Grupo A', 1, 2),
    ('67890', 'Grupo B', 1, 3);

-- ============================================================
-- 6. INSCRIPCIONES
-- Luis, María, Diego → Grupo A (Profesor Carlos)
-- Jorge, Sofía      → Grupo B (Profesora Laura)
-- ============================================================
INSERT INTO inscripcion (estudiante_id, grupo_id, estado)
VALUES
    (1, 1, 'enCurso'),
    (2, 1, 'enCurso'),
    (3, 2, 'enCurso'),
    (4, 1, 'enCurso'),
    (5, 2, 'enCurso');

-- ============================================================
-- 7. ORGANIZACIONES VINCULADAS
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
     'contacto@sefiplan.gob.mx', 'publico', 'activo'),
    ('UV Innovación', 'Universidad Veracruzana',
     'Xalapa', '91090', 'Xalapa',
     'Av. Xalapa', '1', '2288421700',
     'innovacion@uv.mx', 'educativo', 'activo');

-- ============================================================
-- 8. PROYECTOS (15 proyectos para demo CU-22)
-- ============================================================
INSERT INTO proyecto
    (nombre, descripcion_general, objetivos, metodologia,
     recursos, cupo_maximo, cupo_disponible,
     nombre_responsable, apellido_paterno_responsable,
     apellido_materno_responsable, correo_contacto_responsable,
     organizacion_vinculada_id, estado)
VALUES
    ('Sistema de Inventarios',
     'Control de entradas y salidas de productos.',
     'Automatizar el registro de inventario.',
     'Scrum con sprints de 2 semanas.',
     'Computadora, acceso a red.',
     3, 3, 'Roberto', 'González', 'Mora',
     'roberto@techcorp.mx', 1, 'activo'),

    ('Portal de Transparencia',
     'Publicación de información de gasto público.',
     'Transparentar el uso de recursos.',
     'Kanban con revisiones semanales.',
     'Servidor web, base de datos.',
     2, 2, 'Elena', 'Flores', 'Vega',
     'elena@sefiplan.gob.mx', 2, 'activo'),

    ('App Móvil de Reportes',
     'Aplicación para generación de reportes.',
     'Generar reportes desde celular.',
     'Metodología ágil XP.',
     'Equipos móviles Android.',
     2, 2, 'Miguel', 'Ríos', 'Castro',
     'miguel@techcorp.mx', 1, 'activo'),

    ('Sistema de Nómina',
     'Cálculo y pago de nómina automatizado.',
     'Reducir errores en cálculo de salarios.',
     'Scrum con sprints de 2 semanas.',
     'Computadora, licencias Office.',
     2, 2, 'Laura', 'Mendoza', 'Ríos',
     'laura@techcorp.mx', 1, 'activo'),

    ('Portal de Recursos Humanos',
     'Gestión de personal y vacantes.',
     'Digitalizar procesos de contratación.',
     'Kanban semanal.',
     'Servidor web, MySQL.',
     3, 3, 'Pedro', 'Sánchez', 'Luna',
     'pedro@sefiplan.gob.mx', 2, 'activo'),

    ('App de Control de Asistencia',
     'Registro de entrada y salida de empleados.',
     'Eliminar el registro manual.',
     'Metodología ágil XP.',
     'Lectores de huella, servidor.',
     2, 2, 'María', 'Torres', 'Vega',
     'maria@techcorp.mx', 1, 'activo'),

    ('Sistema de Facturación Electrónica',
     'Generación y envío de facturas CFDI.',
     'Cumplir regulaciones SAT.',
     'Scrum quincenal.',
     'Certificados SAT, servidor.',
     2, 2, 'Jorge', 'Ramírez', 'Cruz',
     'jorge@sefiplan.gob.mx', 2, 'activo'),

    ('Plataforma de Capacitación en Línea',
     'Cursos y evaluaciones para empleados.',
     'Mejorar capacitación interna.',
     'Kanban semanal.',
     'Servidor web, videoconferencia.',
     3, 3, 'Ana', 'García', 'Flores',
     'ana@techcorp.mx', 1, 'activo'),

    ('Sistema de Inventario de Activos',
     'Control de equipos y mobiliario institucional.',
     'Rastrear activos y su estado.',
     'Cascada.',
     'Lectores de código de barras, BD.',
     2, 2, 'Carlos', 'López', 'Morales',
     'carlos@sefiplan.gob.mx', 2, 'activo'),

    ('Portal Ciudadano de Trámites',
     'Trámites gubernamentales en línea.',
     'Reducir filas y tiempos de atención.',
     'Scrum.',
     'Servidor web, APIs gobierno.',
     3, 3, 'Sofía', 'Vázquez', 'Reyes',
     'sofia@techcorp.mx', 1, 'activo'),

    ('Sistema de Control Escolar',
     'Gestión de calificaciones y expedientes.',
     'Centralizar información académica.',
     'XP con iteraciones cortas.',
     'BD relacional, servidor.',
     2, 2, 'Miguel', 'Pérez', 'Gutiérrez',
     'miguel@uv.mx', 3, 'activo'),

    ('App de Monitoreo de Infraestructura',
     'Supervisión de servidores y redes.',
     'Detectar fallas en tiempo real.',
     'Kanban con alertas.',
     'Herramientas de monitoreo.',
     2, 2, 'Daniela', 'Ortiz', 'Jiménez',
     'daniela@uv.mx', 3, 'activo'),

    ('Sistema de Compras y Proveedores',
     'Órdenes de compra y catálogo de proveedores.',
     'Optimizar adquisiciones.',
     'Scrum quincenal.',
     'BD, servidor web.',
     2, 2, 'Fernando', 'Ruiz', 'Medina',
     'fernando@sefiplan.gob.mx', 2, 'activo'),

    ('Plataforma de Encuestas de Satisfacción',
     'Encuestas a ciudadanos sobre servicios.',
     'Medir calidad de servicios.',
     'Cascada simplificada.',
     'Servidor web, análisis de datos.',
     2, 2, 'Patricia', 'Flores', 'Aguilar',
     'patricia@uv.mx', 3, 'activo'),

    ('Sistema de Gestión Documental',
     'Digitalización y archivo de documentos.',
     'Reducir papel y agilizar búsquedas.',
     'Kanban diario.',
     'Escáneres, servidor.',
     3, 3, 'Roberto', 'Hernández', 'Castillo',
     'roberto@sefiplan.gob.mx', 2, 'activo');

-- ============================================================
-- 9. ENTREGAS (15 por estudiante = 75 total)
-- Todas noEntregada por defecto; luego se actualizan casos demo
--
-- IDs de entregable:
--  1=OficioAsignacion  2=OficioAceptacion  3=HorarioClases
--  4=Cronograma        5=ReporteMensual1   6=ReporteMensual2
--  7=ReporteMensual3   8=ReporteMensual4   9=InformeParcial
-- 10=InformeFinal     11=Presentacion1    12=Presentacion2
-- 13=EvaluacionOV1    14=EvaluacionOV2    15=Autoevaluacion
-- ============================================================
INSERT INTO entrega (estudiante_id, entregable_id, estado)
VALUES
    -- Estudiante 1: Luis (estado limpio para demo completa)
    (1,  1, 'noEntregada'), (1,  2, 'noEntregada'),
    (1,  3, 'noEntregada'), (1,  4, 'noEntregada'),
    (1,  5, 'noEntregada'), (1,  6, 'noEntregada'),
    (1,  7, 'noEntregada'), (1,  8, 'noEntregada'),
    (1,  9, 'noEntregada'), (1, 10, 'noEntregada'),
    (1, 11, 'noEntregada'), (1, 12, 'noEntregada'),
    (1, 13, 'noEntregada'), (1, 14, 'noEntregada'),
    (1, 15, 'noEntregada'),
    -- Estudiante 2: María (docs iniciales parcialmente entregados)
    (2,  1, 'noEntregada'), (2,  2, 'entregada'),
    (2,  3, 'entregada'),   (2,  4, 'noEntregada'),
    (2,  5, 'noEntregada'), (2,  6, 'noEntregada'),
    (2,  7, 'noEntregada'), (2,  8, 'noEntregada'),
    (2,  9, 'noEntregada'), (2, 10, 'noEntregada'),
    (2, 11, 'noEntregada'), (2, 12, 'noEntregada'),
    (2, 13, 'noEntregada'), (2, 14, 'noEntregada'),
    (2, 15, 'noEntregada'),
    -- Estudiante 3: Jorge (autoevaluacion ya respondida)
    (3,  1, 'noEntregada'), (3,  2, 'noEntregada'),
    (3,  3, 'noEntregada'), (3,  4, 'noEntregada'),
    (3,  5, 'noEntregada'), (3,  6, 'noEntregada'),
    (3,  7, 'noEntregada'), (3,  8, 'noEntregada'),
    (3,  9, 'noEntregada'), (3, 10, 'noEntregada'),
    (3, 11, 'noEntregada'), (3, 12, 'noEntregada'),
    (3, 13, 'noEntregada'), (3, 14, 'noEntregada'),
    (3, 15, 'entregada'),
    -- Estudiante 4: Diego (evidencias entregadas para que Profesor evalúe)
    (4,  1, 'noEntregada'), (4,  2, 'noEntregada'),
    (4,  3, 'noEntregada'), (4,  4, 'noEntregada'),
    (4,  5, 'entregada'),   (4,  6, 'entregada'),
    (4,  7, 'noEntregada'), (4,  8, 'noEntregada'),
    (4,  9, 'entregada'),   (4, 10, 'noEntregada'),
    (4, 11, 'noEntregada'), (4, 12, 'noEntregada'),
    (4, 13, 'entregada'),   (4, 14, 'noEntregada'),
    (4, 15, 'noEntregada'),
    -- Estudiante 5: Sofía (cuenta bloqueada, entregas limpias)
    (5,  1, 'noEntregada'), (5,  2, 'noEntregada'),
    (5,  3, 'noEntregada'), (5,  4, 'noEntregada'),
    (5,  5, 'noEntregada'), (5,  6, 'noEntregada'),
    (5,  7, 'noEntregada'), (5,  8, 'noEntregada'),
    (5,  9, 'noEntregada'), (5, 10, 'noEntregada'),
    (5, 11, 'noEntregada'), (5, 12, 'noEntregada'),
    (5, 13, 'noEntregada'), (5, 14, 'noEntregada'),
    (5, 15, 'noEntregada');

-- ============================================================
-- 10. AUTOEVALUACION DE JORGE (ya respondida — id entrega = 45)
-- Jorge es estudiante 3, entregable 15 → entrega #45
-- ============================================================
INSERT INTO respuesta_autoevaluacion
    (entrega_id, numero_afirmacion, valor)
VALUES
    (45, 1, 4), (45, 2, 5), (45, 3, 3),
    (45, 4, 4), (45, 5, 5), (45, 6, 3),
    (45, 7, 4), (45, 8, 5), (45, 9, 4),
    (45, 10, 5);
