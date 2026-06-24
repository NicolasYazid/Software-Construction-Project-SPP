-- ============================================================
-- Sistema de Practicas Profesionales (SPP)
-- Copyright (c) 2026 Nicolas Cruz && Isaac Vazquez.
-- Todos los derechos reservados.
-- Fecha de creacion: 13 de junio del 2026
--
-- DATOS DE PRUEBA
-- PREREQUISITO: ejecutar spp_schema.sql antes que este archivo.
--
-- CIFRADO:
--   Algoritmo : AES-128-CBC / PKCS5Padding
--   Clave     : definida en bd.properties -> bd.clave_cifrado
--   IV        : definido en bd.properties -> bd.iv_cifrado
--   Salida    : Base64 estandar
--   Valores usados en este script (solo para pruebas):
--     bd.clave_cifrado=ClavesPrueba2026
--     bd.iv_cifrado=IVPrueba20262026
--   IMPORTANTE: cambiar clave e IV antes de produccion.
--
-- USUARIOS DE PRUEBA (contrasenas en texto claro):
--   Administrador : admin@fei.uv.mx        / AdminSPP2026
--   Coordinador   : coordinador@fei.uv.mx  / CoordSPP2026
--   Profesor      : profesor@fei.uv.mx     / ProfeSPP2026
--   Estudiante 1  : matricula S21013417    / Alumn1SPP2026
--   Estudiante 2  : matricula S21013418    / Alumn2SPP2026
--   Estudiante 3  : matricula S21013419    / Alumn3SPP2026
--   Estudiante 4  : matricula S21013420    / Alumn4SPP2026
--   Estudiante 5  : matricula S21013421    / Alumn5SPP2026
--
-- contrasena_temporal = 0 en datos de prueba para permitir
-- login directo sin flujo de cambio de contrasena.
-- ============================================================

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

USE spp;


-- ------------------------------------------------------------
-- CATALOGOS
-- ------------------------------------------------------------

INSERT INTO tipo_evidencia (nombre, descripcion) VALUES
    ('OficioAceptacion',
     'Oficio emitido por la OV aceptando al practicante'),
    ('OficioAsignacion',
     'Oficio generado por el sistema al asignar proyecto'),
    ('HorarioClases',
     'Horario de la Experiencia Educativa (EE)'),
    ('HorarioLaboral',
     'Horario en la OV firmado y sellado por el responsable'),
    ('Cronograma',
     'Cronograma de actividades del periodo de practica'),
    ('ReporteMensual',
     'Reporte de avance mensual del practicante'),
    ('InformeParcial',
     'Informe al concluir las primeras 210 horas (RN-03)'),
    ('InformeFinal',
     'Informe al concluir las 420 horas requeridas (RN-02)'),
    ('Presentacion',
     'Presentacion final ante el Profesor Asesor'),
    ('EvaluacionOV',
     'Evaluacion de la Organizacion Vinculada por el practicante'),
    ('Autoevaluacion',
     'Autoevaluacion del practicante (5 criterios, escala 1-5)');

INSERT INTO estado_documento (nombre) VALUES
    ('Pendiente'),
    ('Entregado'),
    ('Aprobado'),
    ('Rechazado'),
    ('ConProrroga');


-- ------------------------------------------------------------
-- ESTRUCTURA ACADEMICA
-- ------------------------------------------------------------

INSERT INTO ciclo_escolar
    (periodo, fecha_inicio, fecha_fin, activo)
VALUES
    ('FEB-JUL 2026', '2026-02-02', '2026-07-31', 1);

INSERT INTO experiencia_educativa
    (nombre, clave, creditos)
VALUES
    ('Practicas Profesionales', 'IIS-302', 10);


-- ------------------------------------------------------------
-- ORGANIZACIONES VINCULADAS Y RESPONSABLES
-- ------------------------------------------------------------

INSERT INTO organizacion_vinculada
    (nombre_empresa, sector, ciudad, direccion, telefono, estado)
VALUES
    ('Instituto de Ecologia A.C. (INECOL)',
     'Investigacion',
     'Xalapa',
     'Carretera antigua a Coatepec 351, El Haya, 91073 Xalapa',
     '2288421800',
     'Activo'),
    ('Coder House Xalapa',
     'Tecnologia',
     'Xalapa',
     'Av. Lazaro Cardenas 38, Col. Centro, 91000 Xalapa',
     '2281234567',
     'Activo');

INSERT INTO responsable_proyecto
    (id_organizacion, nombre_encargado, cargo_encargado, email_encargado)
VALUES
    (1,
     'Dr. Alejandro Perez Gutierrez',
     'Director de Proyectos de Software',
     'aperez@inecol.mx'),
    (2,
     'Lic. Patricia Morales Sandoval',
     'Coordinadora de Desarrollo',
     'pmorales@coderhouse.mx');


-- ------------------------------------------------------------
-- PROYECTOS
-- cupo_disponible refleja el estado tras las asignaciones de
-- prueba que se insertan mas adelante en este mismo script.
-- ------------------------------------------------------------

INSERT INTO proyecto (
    id_organizacion, id_responsable, nombre_proyecto,
    descripcion, actividades, metodologia,
    duracion_meses, horario_laboral, recurso,
    responsabilidades, cupo_maximo, cupo_disponible, estado
) VALUES
    (1, 1,
     'Sistema de monitoreo de biodiversidad',
     'Desarrollo de un sistema web para el registro y '
     'visualizacion de datos de biodiversidad recolectados '
     'por investigadores del INECOL en campo.',
     'Analisis de requerimientos, diseno de BD, '
     'desarrollo backend y frontend, pruebas e integracion.',
     'Scrum adaptado con sprints de dos semanas.',
     5,
     'Lunes a viernes 08:00-14:00',
     'Equipo de computo, acceso a internet, servidor local.',
     'Documentar avances semanales, asistir a reuniones '
     'de equipo, cumplir el cronograma acordado.',
     2, 1, 'Disponible'),

    (2, 2,
     'Plataforma e-learning de programacion',
     'Construccion de una plataforma de cursos en linea '
     'orientada a la ensenanza de programacion para '
     'principiantes, con seguimiento de progreso.',
     'Diseno UX/UI, implementacion de modulos de cursos, '
     'sistema de evaluaciones y tablero de progreso.',
     'Kanban con revision quincenal.',
     5,
     'Lunes a viernes 09:00-15:00',
     'MacBook Pro, licencias de software, acceso a AWS.',
     'Cumplir entregas semanales, participar en code '
     'reviews y mantener la documentacion actualizada.',
     3, 1, 'Disponible');


-- ------------------------------------------------------------
-- USUARIOS (valores cifrados AES-128-CBC/PKCS5 en Base64)
-- ------------------------------------------------------------

-- Administrador del sistema
INSERT INTO administrador (
    nombre, correo, contrasena,
    estado, fecha_registro, contrasena_temporal,
    intentos_fallidos
) VALUES (
    '4FbUdqUNVjZXKdL4NRxpQw==',
    'QDIuDaG2U5gv7GQFBO3gJw==',
    'n5Tb7mQTICc9S71cskZ6MA==',
    'Activo', '2026-02-01', 0, 0
);

-- Coordinador de Practicas Profesionales
INSERT INTO coordinador (
    num_personal, nombre, primer_apellido, segundo_apellido,
    correo, contrasena,
    estado, fecha_registro, tiempo_servicio,
    contrasena_temporal, intentos_fallidos
) VALUES (
    'UV-C-0041',
    'dSUM5EMKmnCL7s1wzR7vWg==',
    'BqOqTUPVzKKKaVWWSwo1AQ==',
    '19n1d5Xp8p6k1g0qtpt4sQ==',
    'TEo4kCS/EJ27Y3+Ly3yjGycZqH1cMqESpSxIzMNoEAY=',
    'cHDGC1yLcX4keCEXoYU07A==',
    'Activo', '2026-02-01', 8, 0, 0
);

-- Profesor Asesor
INSERT INTO profesor (
    num_personal, nombre, primer_apellido, segundo_apellido,
    correo, contrasena,
    estado, fecha_registro, tiempo_servicio, turno,
    contrasena_temporal, intentos_fallidos
) VALUES (
    'UV-P-0112',
    'pJu5izpl9qzoUH+50z0K8Q==',
    'qD1WEvefZYAD1LZZJcQsYA==',
    'iWvasJKCVw+NB2k002ppSQ==',
    'Gxrdc+ufxX0/skoeUm6gfM/EOStKzxPOU4cMBg1z7bs=',
    'paeQHgfbuV53Q7Wrc+JVdQ==',
    'Activo', '2026-02-01', 5, 'Matutino', 0, 0
);

-- Estudiante 1: Ana Garcia Ruiz (S21013417) — estado EnSeleccion
INSERT INTO estudiante (
    matricula, nombre, primer_apellido, segundo_apellido,
    correo, contrasena,
    semestre, estado, contrasena_temporal, intentos_fallidos
) VALUES (
    '531rJpX3s+//RxCSBqzIvQ==',
    'iZzRTRia2xeGGL3iooOwjw==',
    '8+Pvk2s48xRDFZTcmwsg+g==',
    'ofcL9Ebtt9H1tN/9URQomg==',
    'S6gJ2CgswlDdLQh6B36+aY3Jpn8INFb4+2iEDKuQH7Y=',
    'Tw7dKUXDh0wpTFrpBtbccA==',
    9, 'Activo', 0, 0
);

-- Estudiante 2: Luis Hernandez Mora (S21013418) — estado Asignado
INSERT INTO estudiante (
    matricula, nombre, primer_apellido, segundo_apellido,
    correo, contrasena,
    semestre, estado, contrasena_temporal, intentos_fallidos
) VALUES (
    'GSOky3T9Y4Lj1wOtAERNdQ==',
    '4praq4GfUwAXNFbo5Jm/Nw==',
    'xYjQ9p+G//XEOZXYjmLbqA==',
    'GDDEleVpOXQ50ZwaMyZZFQ==',
    '8HZ5joraT47OAjskjaYOb6h6U8CEJU5fDOO2W7oKf3rGI9RnS9yDgEIs7SnrxMTB',
    'yB2Do5uWsdEb74DRbfnTFw==',
    9, 'Activo', 0, 0
);

-- Estudiante 3: Sofia Martinez Cruz (S21013419) — estado Asignado
INSERT INTO estudiante (
    matricula, nombre, primer_apellido, segundo_apellido,
    correo, contrasena,
    semestre, estado, contrasena_temporal, intentos_fallidos
) VALUES (
    'dFBBySEnqhkckwx6mUtYtg==',
    'KIJOvnp0G36Km7OABPTDVA==',
    'DW4f3ajE9dmY/oqDG6ijXg==',
    'u7ohjKmQcTdfWqvLjCV3Ew==',
    'GKxMz7JcB+PGhHOYue69Ou9dTlC7y+CJERZwDR0hAeeFmW41OYg01IsV8ztNX7a2',
    '+8Jqv45G7JsS1Er0XDfrhg==',
    9, 'Activo', 0, 0
);

-- Estudiante 4: Diego Lopez Vega (S21013420) — estado Inscrito
-- sin profesor asignado aun
INSERT INTO estudiante (
    matricula, nombre, primer_apellido, segundo_apellido,
    correo, contrasena,
    semestre, estado, contrasena_temporal, intentos_fallidos
) VALUES (
    'eZj6AU01be+6hjMKQ6hJGg==',
    'rBOOpPG53L9qrpkewYhLjw==',
    '19n1d5Xp8p6k1g0qtpt4sQ==',
    'N1A3JuGlARPkYqxJaofF2w==',
    'Z9o0PhAgN+gh1z5Qb7+vkPV9aIeFoGkWZMggxKE50EY=',
    'm87dU3Ntq/Ezh8M0r84iyQ==',
    9, 'Activo', 0, 0
);

-- Estudiante 5: Valentina Cruz Diaz (S21013421) — estado EnDesarrollo
INSERT INTO estudiante (
    matricula, nombre, primer_apellido, segundo_apellido,
    correo, contrasena,
    semestre, estado, contrasena_temporal, intentos_fallidos
) VALUES (
    '36S2CMST4f0DFSGyEvaPkQ==',
    '2y3AHa5jjxd1MTY/n7WItQ==',
    'u7ohjKmQcTdfWqvLjCV3Ew==',
    'yIH9sKC3wML+blrroAHhfw==',
    '2c0k5bmdz5gYdt+W9r9ZAdFsS+XNiIIazFUb5LrXuCQRwr378plPy8DsZEHyRe4E',
    'yz2c6AKRKKjZa4l4odAJ5g==',
    9, 'Activo', 0, 0
);


-- ------------------------------------------------------------
-- INSCRIPCIONES (estudiante_inscrito)
-- Mapeo: id_estudiante -> id en la tabla estudiante
--   1=Ana  2=Luis  3=Sofia  4=Diego  5=Valentina
-- ------------------------------------------------------------

INSERT INTO estudiante_inscrito (
    id_estudiante, id_ciclo_escolar, id_ee, id_profesor,
    estado_practica, horas_acumuladas, fecha_inscripcion
) VALUES
    -- Ana: selecciono proyectos, en espera de asignacion
    (1, 1, 1, 1, 'EnSeleccion',  0, '2026-02-10'),
    -- Luis: asignado al proyecto 1 (INECOL)
    (2, 1, 1, 1, 'Asignado',     0, '2026-02-10'),
    -- Sofia: asignada al proyecto 2 (Coder House)
    (3, 1, 1, 1, 'Asignado',     0, '2026-02-10'),
    -- Diego: recien inscrito, sin profesor ni seleccion
    (4, 1, 1, NULL, 'Inscrito',  0, '2026-02-10'),
    -- Valentina: en desarrollo con documentos iniciales entregados
    (5, 1, 1, 1, 'EnDesarrollo', 50, '2026-02-10');


-- ------------------------------------------------------------
-- SELECCION DE PROYECTOS (RN-11: irreversible)
-- Ana ordeno ambos proyectos; Luis y Valentina ya fueron
-- asignados directamente por el Coordinador.
-- ------------------------------------------------------------

INSERT INTO seleccion_proyecto
    (id_inscripcion, id_proyecto, prioridad, fecha_seleccion)
VALUES
    (1, 1, 1, '2026-02-15 10:30:00'),
    (1, 2, 2, '2026-02-15 10:30:00');


-- ------------------------------------------------------------
-- ASIGNACIONES (CU Asignar Proyecto)
-- Luis  -> proyecto 1 (INECOL)     cupo_disponible paso de 2 a 1
-- Sofia -> proyecto 2 (Coder House) cupo_disponible paso de 3 a 2
-- Valentina -> proyecto 2            cupo_disponible paso de 2 a 1
-- Los cupo_disponible ya reflejan este estado en la tabla proyecto.
-- ------------------------------------------------------------

INSERT INTO asignacion
    (id_inscripcion, id_proyecto, fecha_asignacion)
VALUES
    (2, 1, '2026-02-20'),
    (3, 2, '2026-02-20'),
    (5, 2, '2026-02-20');


-- ------------------------------------------------------------
-- DOCUMENTOS DE VALENTINA (id_inscripcion = 5)
-- Escenario: lleva 50 horas, entrego documentos iniciales.
-- id_tipo_evidencia: 3=HorarioClases 4=HorarioLaboral
--                    5=Cronograma    6=ReporteMensual
-- id_estado_documento: 1=Pendiente 2=Entregado 3=Aprobado
-- ------------------------------------------------------------

INSERT INTO documento (
    id_inscripcion, id_tipo_evidencia, id_estado_documento,
    ruta_archivo, nombre_archivo,
    fecha_entrega, fecha_limite, calificacion, observaciones
) VALUES
    -- Horario de clases: aprobado con 9.0
    (5, 3, 3,
     '/uploads/5/horario_clases.pdf', 'horario_clases.pdf',
     '2026-02-25 11:00:00', '2026-03-01', 9.0, NULL),

    -- Horario laboral: aprobado con 8.5
    (5, 4, 3,
     '/uploads/5/horario_laboral.pdf', 'horario_laboral.pdf',
     '2026-02-25 11:05:00', '2026-03-01', 8.5, NULL),

    -- Cronograma: aprobado con 9.5
    (5, 5, 3,
     '/uploads/5/cronograma.pdf', 'cronograma.pdf',
     '2026-02-26 09:00:00', '2026-03-01', 9.5, NULL),

    -- Reporte mensual de febrero: entregado, pendiente evaluacion
    (5, 6, 2,
     '/uploads/5/reporte_febrero.pdf', 'reporte_febrero.pdf',
     '2026-03-01 16:00:00', '2026-03-05', NULL, NULL);


-- ------------------------------------------------------------
-- AUTOEVALUACION DE VALENTINA
-- No se inserta dato: en el escenario de prueba Valentina lleva
-- 50 horas (EnDesarrollo) y aun no ha entregado el documento de
-- tipo Autoevaluacion (id_tipo_evidencia=11). Cuando ese documento
-- exista, se podra insertar una fila en autoevaluacion referenciando
-- su id_documento.
-- ------------------------------------------------------------


SET FOREIGN_KEY_CHECKS = 1;
