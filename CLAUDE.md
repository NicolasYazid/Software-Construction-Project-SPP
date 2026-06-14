# CLAUDE.md — Sistema de Prácticas Profesionales (SPP)

> Este archivo es la memoria persistente del proyecto. Claude Code lo lee al inicio de
> CADA sesión. Antes de escribir cualquier código, léelo completo y confirma que
> entendiste el contexto, el estándar y el estado actual.
>
> **Al final de cada sesión, ACTUALIZA la sección "BITÁCORA DE SESIONES" y
> "ESTADO DE IMPLEMENTACIÓN".** Esa es la única forma de no perder contexto entre sesiones.

---

## 0. REGLA DE ORO (leer primero)

1. **JDBC PURO. PROHIBIDO usar JPA, Hibernate o cualquier framework de persistencia.**
   La conexión a la BD se hace con las clases base de Java (`java.sql.Connection`,
   `PreparedStatement`, `ResultSet`). Esta es una restricción del profesor; violarla
   reprueba el criterio de persistencia.
2. **Sigue el ESTÁNDAR DE CODIFICACIÓN al pie de la letra** (sección 5). El criterio que
   más pesa de toda la rúbrica (8 pts) es "Revisión de código apegada al estándar".
3. **Toda clase y método que lo necesite DEBE tener manejo de excepciones y validación
   de entradas** (programación defensiva, criterio 7). No dejes un solo `catch` vacío.
4. **Idioma del código: español** (excepto palabras reservadas de Java). Codificación UTF-8.
5. **Nunca inventes nombres de paquetes, clases o CUs.** Usa los definidos aquí. Si algo
   no está definido, pregunta antes de crearlo.
6. **No uses "valores mágicos".** Todo número/cadena literal con significado va como
   constante `SCREAMING_SNAKE_CASE`.

---

## 1. CONTEXTO DEL PROYECTO

**Qué es:** Sistema de gestión de Prácticas Profesionales (SPP) para la Experiencia
Educativa (EE) obligatoria "Prácticas Profesionales" de la Licenciatura en Ingeniería de
Software (LIS) de la Facultad de Estadística e Informática (FEI), Universidad Veracruzana,
Xalapa. Los estudiantes desarrollan un proyecto de software en una Organización Vinculada (OV).

**Autores:** Cruz Hernández Nicolás Yazid && Vázquez Torres Isaac Adriano.

**Materia:** Principios de Construcción de Software, FEB–JUL 2026.

**Problemas que resuelve:**
- La entrega de documentos es lenta y se entorpece.
- El Coordinador pierde 1–2 días corrigiendo errores de escritura al generar documentos.
- Profesores tienen mala comunicación con Practicantes y Coordinador (retrasos de días).

**Objetivo del sistema:** digitalizar formatos, controlar practicantes mediante usuarios,
habilitar un canal de comunicación interno, generar indicadores y reducir errores y papel.

---

## 2. STACK TECNOLÓGICO Y RESTRICCIONES DURAS

| Aspecto | Valor | Nota |
|---|---|---|
| Lenguaje | **Java** | Idioma del código: español |
| Framework UI | **JavaFX** (FXML) | |
| BD | **MySQL 5.5+** | |
| Persistencia | **JDBC puro (`java.sql.*`)** | ⚠️ PROHIBIDO JPA/Hibernate/ORM |
| IDE oficial pedido | **NetBeans 8+** | ⚠️ Ver RIESGO abajo |
| IDE real en uso | IntelliJ IDEA + Maven | |
| Codificación archivos | **UTF-8** | Por caracteres á, é, ñ |
| Paquete raíz | `mx.uv.spp` | |

> **⚠️ RIESGO A GESTIONAR:** El documento del profesor pide entregar un "Proyecto completo
> de NetBeans". El proyecto real está en IntelliJ con Maven (`pom.xml`). Hay que asegurar
> que el proyecto **abra y compile en NetBeans** para la entrega, o aclararlo con el
> profesor con anticipación. NO ignorar esto.

> **Nota de evolución tecnológica:** la ERS original (v0.4) especificaba PHP 7+/MySQL 8+ con
> conexión PDO. El proyecto **cambió a Java/JavaFX**. Donde haya conflicto, manda Java/JavaFX
> + JDBC. Los requerimientos de negocio (RN, CU, RNF) siguen vigentes; solo cambió la tecnología.

---

## 3. ARQUITECTURA Y ESTRUCTURA DE PAQUETES

Arquitectura por capas con separación estricta de responsabilidades (la UI NO accede a la BD
directamente; pasa por controladores → DAOs). Esto lo exige la rúbrica (UI con separación de
capas, criterio 11; persistencia con interfaces bien definidas, criterio 6).

```
mx.uv.spp
├── App.java                      ← punto de entrada JavaFX (ya existe)
│
├── modelo                        ← POJOs del dominio (sin lógica de negocio pesada)
│   ├── Estudiante.java
│   ├── Practicante.java
│   ├── Coordinador.java
│   ├── Profesor.java
│   ├── Proyecto.java
│   ├── OrganizacionVinculada.java
│   └── ... (ver sección 6)
│
├── persistencia                  ← capa de acceso a datos (DAO + JDBC)
│   ├── ConexionBD.java           ← gestiona la Connection (singleton/factory)
│   ├── dao                       ← INTERFACES de los DAO (contratos)
│   │   ├── EstudianteDAO.java
│   │   ├── ProyectoDAO.java
│   │   └── ...
│   └── dao.impl                  ← implementaciones JDBC de los DAO
│       ├── EstudianteDAOImpl.java
│       └── ...
│
├── negocio                       ← lógica de negocio / servicios (reglas RN)
│   └── ...
│
├── controladores                 ← controladores JavaFX (uno por vista FXML)
│   ├── administrador
│   ├── coordinador
│   ├── profesor
│   ├── practicante
│   └── comun                     ← login, buzón, mensajes
│
└── util                          ← utilidades (cifrado AES, generador PDF, validadores,
                                     generador de contraseñas, constantes globales)

resources/mx/uv/spp
├── vistas                        ← archivos .fxml
├── css                           ← hojas de estilo
├── imagenes                      ← bg_*.png, img_*.png, ic_*.png
└── ...
```

**Nomenclatura de paquetes:** todo minúsculas, jerárquicos. Ej:
`mx.uv.spp.controladores.coordinador`, `mx.uv.spp.persistencia.dao.impl`.

---

## 4. ROLES Y SEGURIDAD (qué puede hacer cada actor)

| Actor | Puede | NO puede |
|---|---|---|
| **Administrador** | Registrar/inactivar Coordinador y Profesor | Acceder a info del Practicante |
| **Coordinador** | Registrar proyectos/OV/usuarios, asignar proyectos, generar indicadores, administrar formatos | Dar seguimiento al practicante / evaluar reportes |
| **Profesor** | Evaluar reportes, anotar observaciones, asignar calificación | Asignar proyectos / administrar usuarios |
| **Practicante** | Elegir proyectos, subir reportes/documentos, autoevaluarse | Modificar datos de proyecto / asignarse calificación |
| **Jefe de Carrera** | Recibe indicadores (PDF/mensaje) | NO accede directamente al sistema |

**Inicio de sesión (CRÍTICO — difiere por tipo de usuario):**
- Administrador, Coordinador, Profesor → **correo + contraseña**
- Practicante/Estudiante → **matrícula + contraseña** (ÚNICO que NO usa correo para login)
- Implicación en BD: todos tienen `correo` (UNIQUE); los estudiantes además tienen
  `matricula` (UNIQUE). El identificador de login varía según el tipo.

**Reglas de seguridad (SEG):**
- SEG-01: Bloquear cuenta tras **3 intentos fallidos** consecutivos por **10 minutos**.
- SEG-02: Forzar cambio de contraseña temporal en el primer inicio de sesión exitoso.
- SEG-03: Contraseña mínimo **10 caracteres** con mayúsculas, minúsculas y números.
- SEG-04: **Cifrar** nombre, usuario, contraseña de todos los usuarios + actividades de practicantes (**AES 128 bits**).
- Sesión expira tras **15 min** de inactividad (token).

---

## 5. ESTÁNDAR DE CODIFICACIÓN (Java/JavaFX) — OBLIGATORIO

### 5.1 Organización de cada archivo .java
1. Encabezado (bloque `/* */` con Copyright, autores, fecha en palabras)
2. `package`
3. `import`
4. JavaDoc de clase (`/** */`)
5. Código: 5.1 `main` (si aplica) → 5.2 atributos → 5.3 constructores → 5.4 getters/setters → 5.5 otros métodos

### 5.2 Encabezado obligatorio (formato exacto)
```java
/*
 * Copyright © 2026 Nicolás Cruz && Isaac Vázquez.
 * Todos los derechos reservados.
 *
 * Este software es de uso académico y privado.
 * Fecha de creación: 13 de junio del 2026
 */
```
- Fecha **en palabras** (NO "13/06/2026", SÍ "13 de junio del 2026").
- Incluir apellidos paternos completos.

### 5.3 JavaDoc de clase (después de imports, antes del código)
```java
/**
 * Descripción del propósito general de la clase.
 *
 * @author Nicolás Yazid Cruz Hernández
 * @author Isaac Adriano Vázquez Torres
 */
```
- `@author` SOLO en clases e interfaces, NUNCA en métodos.

### 5.4 JavaDoc de método (obligatorio en cada método público)
```java
/**
 * Propósito del método.
 *
 * @param nombreParam Descripción + límites/restricciones.
 * @return descripción del valor retornado y su significado (omitir si void).
 * @throws TipoExcepcion Condición que la provoca (el POR QUÉ, no solo el tipo).
 */
```
- NO usar `@return` en métodos `void`.
- `@throws` debe indicar el porqué.

### 5.5 Nomenclatura
- **Paquetes:** minúsculas, jerárquicos → `mx.uv.spp.coordinador.controladores`
- **Clases/Interfaces:** PascalCase → `RegistrarCoordinador`, `CalcularIndicadores`
- **Métodos:** camelCase, **verbos** → `calcularPromedio()`, `registrarEstudiante()`
- **Variables:** camelCase → `numeroAlumnos`. NO empezar con `_` ni `$`. NO de un solo
  carácter (salvo `i, j, k, m, n` en `for`).
- **Constantes:** SCREAMING_SNAKE_CASE → `MAX_INTENTOS_LOGIN = 3`, `IVA = 0.16`.
  **PROHIBIDOS los valores mágicos.**

### 5.6 Controles JavaFX (prefijo minúsculas + camelCase)
`btn`=Button, `txt`=TextField, `pwd`=PasswordField, `lbl`=Label, `cmb`=ComboBox,
`chb`=ChoiceBox, `chk`=CheckBox, `rbn`=RadioButton, `tbl`=TableView, `txa`=TextArea,
`img`=ImageView, `dtp`=DatePicker, `lst`=ListView.
Ej: `btnAceptar`, `txtNombre`, `pwdContrasena`, `tblUsuarios`.

### 5.7 Métodos de evento (onAction)
Formato: `on` + IdentificadorCompletoDelControl con primera letra mayúscula.
- BIEN: `onBtnIniciarSesion()`, `onTxtUsuario()`
- MAL: `onbtnIniciarSesion()`, `onBtn()`, `btnCerrarSesion()`

### 5.8 Recursos JavaFX
- Iconos: `ic_[ventana]_[desc].png` → `ic_login_contrasenia.png`
- Imágenes: `img_[ventana]_[desc].png`
- Fondos: `bg_[ventana].png` → `bg_login_principal.png`
- Formatos: `.png`, `.jpg/.jpeg`, `.svg`, `.gif`, `.mp3`, `.mp4`.

### 5.9 Comentarios
Regla fundamental: explicar el **POR QUÉ**, no el QUÉ. El código debe ser autoexplicativo.
PROHIBIDO: comentar lo obvio, comentar declaraciones de variables, usar comentarios para
desactivar código, decorar con cajas de asteriscos.

### 5.10 Formato y espaciado
- Indentación: **4 espacios** (Tab = 4 espacios).
- Llaves: `{` al final de línea (con espacio antes); `}` en su propia línea alineada.
- Longitud de línea: **máx 80 caracteres**; al romper → después de coma o antes de operador,
  continuación con 8 espacios.
- Espacios alrededor de operadores binarios y entre palabra clave de control y `(`
  (`if (`, `for (`, `while (`). Sin espacio antes de `;`. Nombre de método pegado al `(`.
- Una sola sentencia por línea.

### 5.11 Estructuras de control
- `if/else`: **SIEMPRE con llaves**, aunque sea una línea (sin llaves = error grave).
- `switch`: SIEMPRE `default` + `break` en cada `case`.
- `try-catch`: `catch` **nunca vacío**; puede incluir `finally`.

---

## 6. MODELO DE DOMINIO (clases principales)

**Herencia (generalización):**
- `Academico` → superclase de `Coordinador`, `Profesor`.
- `DocumentoInicial` → superclase de `OficioAceptacion`, `OficioAsignacion`, `HorarioClases`, `Cronograma`.
- `Evidencia` → superclase de `ReporteMensual`, `InformeParcial`, `InformeFinal`, `Presentacion`, `EvaluacionOV`, `Autoevaluacion`.

**Clases clave y atributos resumidos:**
- `Estudiante`: matricula, contrasena, nombre, primerApellido, segundoApellido, calificacion, estado, idioma, lenguaIndigena, periodo, semestre.
- `Coordinador` / `Profesor` (heredan de `Academico`): numPersonal, nombre, apellidos, estado, fechaRegistro, tiempoServicio, turno (profesor).
- `Proyecto`: nombreProyecto, descripcion, actividades, metodologia, duracion, meses, horario, recurso, responsabilidades, estado, datos del encargado.
- `OrganizacionVinculada`: nombreEmpresa, sector, ciudad, direccion, telefono, estado.
- `ResponsableDeProyecto`: nombreEncargado, cargoEncargado, emailEncargado.
- `CalificacionFinal`: valorNumerico, fechaCalificacion.
- `ReporteIndicadores`: género, edad, periodo, turno, sectorSocial, lenguaIndigena, conDiscapacidad, totales.
- `Mensaje`: asunto, cuerpoMensaje, destinatario.
- Evidencias: `InformeParcial`, `InformeFinal`, `ReporteMensual`, `Presentacion`, `EvaluacionOV`, `Autoevaluacion`.

**Distinciones críticas del glosario (NO confundir):**
- **Estudiante ≠ Practicante:** Estudiante = quiere inscribirse; Practicante = formalmente
  asignado a una OV. La transición ocurre al recibir el Oficio de Asignación.
- **HorarioClases** (horario de la EE) **≠ HorarioLaboral** (horario en la OV, firmado/sellado).
- **Coordinador de P.P. ≠ Profesor de P.P.** (jamás usarlos como sinónimos).
- **Inactivar = dar de baja manteniendo historial.** NUNCA eliminar físicamente un registro.
- **Expediente** = recopilación total de documentos del practicante al final del periodo.

---

## 7. REGLAS DE NEGOCIO (RN) CLAVE

- **RN-01:** Alumno debe haber cubierto ≥70% de créditos para inscribirse en la EE.
- **RN-02:** Práctica profesional = **420 horas** en un periodo escolar.
- **RN-03:** Informe parcial al concluir las primeras **210 hrs**; informe final a las **420 hrs**.
- **RN-11:** El estudiante ordena/elige proyectos por prioridad (en el sistema rediseñado,
  ordena TODOS los proyectos; acción **irreversible**).
- **RN-16:** El practicante realiza autoevaluación y evaluación a la OV.
- **RN-20:** Calificación final = evaluación del Profesor Asesor + autoevaluación del
  Practicante + evaluación de la OV. **⚠️ PENDIENTE:** la fórmula exacta, porcentajes y
  rangos de aprobación deben confirmarse con stakeholders.
- Inactivar al terminar el periodo: cambiar estado de practicantes a "No Activo" y generar
  expediente. Cuentas "No Activo" solo se consultan, no se editan.
- `.Calificar`: campo con valor entre **0 y 10**.

---

## 8. CASOS DE USO (nomenclatura vigente — rediseño primera entrega)

**Administrador:** Alta/Baja Coordinador, Alta/Baja Profesor.
(Solo 1 Coordinador y máx 1 Profesor activos a la vez; frecuencia ~1/año.)

**Coordinador / Control de Proyectos:** Alta OV, Actualizar OV, Publicar Proyecto
(nace "Disponible"), Dar de baja Proyecto (solo sin alumnos asignados), Actualizar Proyecto.

**Coordinador / Control de Practicantes:** Alta Practicante (genera contraseña aleatoria,
asigna profesor), Baja Practicante, Asignar Proyecto (crea `vinculacionProyecto`, reduce
cupos, genera PDF de Oficios de Aceptación y Asignación, descarga automática), Desasignar
Proyecto, Prórroga Documento Inicial, Reporte de Indicadores (PDF, filtros: género, edad,
periodo, sector social, lengua indígena, turno).

**Profesor / Evaluación:** Calificar documentos iniciales, Evaluar Reporte Mensual, Evaluar
Informe, Evaluar Presentación, Calificar OV, Calificar Autoevaluación, Calificación final,
Prórroga Evidencia.

**Practicante / Gestión:** Elegir proyectos por prioridad (irreversible), Entregar Documento
Inicial (un solo CU para todos: Oficios, Horario, Cronograma — `.pdf` máx 5MB, reentrega
sobrescribe), Entregar evaluación OV (irreversible), Entregar Informe Parcial/Final,
Entregar Presentación (`.pdf`/`.pptx` máx 5MB), Entregar Reporte Mensual, Realizar
Autoevaluación (5 criterios, escala 1–5).

**Académico / Comunicación:** Consultar Buzón, Enviar Mensaje.

**Autoevaluación — 5 criterios (escala 1–5):** Aplicación de conocimientos, Resolución de
problemas, Trabajo en equipo, Puntualidad y asistencia, Calidad de trabajo.

---

## 9. MÁQUINAS DE ESTADO

**Sesión de usuario:** NoAutenticado → (ingresar credenciales) → Autenticado →
(válidas / generar token) → Activa → (interactuar / renovar token) → Activa |
(15 min inactividad) → Expirada → NoAutenticado | (3 intentos inválidos) → Bloqueada (10 min).

**Practicante:** Inscrito → EnSelección → Asignado → Formalizado → EnDesarrollo →
(horas==420) → Evaluado → Concluido → Final.

---

## 10. MODELO DE DATOS (BD)

- Mantener integridad referencial basada en el modelo ER.
- Tablas identificadas (entre otras): `organizacion_vinculada`, `responsable_proyecto`,
  `proyecto`, `ciclo_escolar`, `estudiante`, `asignacion`, `calificacion`,
  `estudiante_inscrito`, `experiencia_educativa`, `profesor`, `estado_documento`,
  `tipo_evidencia`, `documento`.
- ⚠️ El modelo relacional visible NO tiene tabla `Coordinador` ni `Administrador` aún —
  **revisar y completar** al implementar autenticación de esos roles.
- Entregables de BD: **modelo relacional** + **script con información de prueba**.

---

## 11. RÚBRICA DE EVALUACIÓN (50 PTS) — orientar el trabajo hacia esto

| Pts | Criterio | Qué exige "completo" (100%) |
|---|---|---|
| **8** | Revisión de código vs estándar | Reporte formal estructurado; el código real sigue el estándar de la sección 5, por módulo |
| **6** | Entrega de documento | Documento formal con TODOS los rubros (ver sección 12) |
| **5** | GitHub / commits | Repo creado a tiempo, usado en todas las actualizaciones, balance entre integrantes, mensajes objetivos. **1 cambio = 1 commit** |
| **5** | Definición de la UI | Separación de capas, validación de TODOS los inputs, 100% funcional según diseño |
| **3** | Estándar de codificación | Documento que define: comentarios, bloques de código, constantes, nombres de variables y métodos |
| **3** | Modelos de diseño aprobados | Firmados por la **Mtra. de Principios de Diseño** ⚠️ acción externa |
| **3** | Modelo de datos | Modelo relacional + BD coherentes con el diseño |
| **3** | Capa de persistencia | Apegada al estándar, **interfaces (DAO) bien definidas**, completa |
| **3** | Programación defensiva | Manejo de excepciones y validación en TODOS los métodos/clases que lo necesiten |
| **3** | Defensa individualizada | Cada integrante defiende su parte; conocer el código propio a fondo |
| **3** | Pruebas unitarias | Reporte coherente y **TODAS** las pruebas pasan |
| **3** | Conclusiones | Percepción personalizada de cada estudiante |
| **2** | Referencias | Mínimo **10** referencias relevantes |

**Escala:** No entregado 0% · Parcial 40% · Completo 100%. Siempre apuntar a completo.

**Implicaciones para el código (lo que Claude Code debe hacer siempre):**
- Escribir cada clase apegada al estándar desde el inicio (no "limpiar después").
- DAOs como **interfaces** + implementaciones JDBC separadas.
- Validar entradas y manejar excepciones en todo método que lo requiera.
- Diseñar las clases para que sean **testeables con pruebas unitarias**.
- La UI (controladores FXML) no debe contener lógica de negocio ni acceso directo a BD.

---

## 12. ENTREGA FINAL (requisitos del profesor)

**Si el proyecto no viene completo (100% funcional con conexión a BD), el alumno NO aprueba.**

Entregables en Eminus:
1. Modelo relacional.
2. Script de BD (con información).
3. Proyecto completo (asegurar compatibilidad NetBeans — ver RIESGO sección 2).
4. Documento que contiene: Portada, Índice de tablas y figuras, Introducción, Definición del
   problema, Especificación de requerimientos, Artefactos de diseño*, Estándar de
   codificación, Conclusiones.
5. Presentación del proyecto ejecutándose en un solo equipo ante el profesor.

\* **Artefactos de diseño** (todos firmados/aprobados por la Mtra. de Principios de Diseño):
Casos de uso (descripciones + diagramas), Modelo de dominio, Diagramas de robustez,
Diagramas de secuencia, Prototipos.

---

## 13. ESTADO DE IMPLEMENTACIÓN

> Actualizar al final de cada sesión. Marcar: ✅ hecho · 🚧 en progreso · ⬜ pendiente.

### Infraestructura base
- ✅ Proyecto creado en IntelliJ + Maven, paquete `mx.uv.spp`.
- ✅ `App.java` (arranque JavaFX con FXMLLoader).
- ✅ Estructura completa de paquetes (modelo, persistencia, controladores, util) con `.gitkeep`.
- ✅ `module-info.java` actualizado con `requires java.sql`.
- ✅ `bd.properties` creado en resources (credenciales + clave/IV AES — solo valores de prueba).
- ✅ `util/Constantes.java` — todas las constantes de negocio y configuración.
- ✅ `persistencia/ConexionBD.java` — singleton JDBC, credenciales externas, sin catch vacío.
- ✅ `util/CifradoAES.java` — AES-128-CBC/PKCS5, lazy init hilo-seguro, clave/IV desde properties.
- ✅ `util/GeneradorContrasena.java` — SecureRandom + Fisher-Yates, cumple SEG-03.
- ✅ `util/Validador.java` — 7 métodos defensivos: cadena, longitud, contraseña, correo,
     matrícula, calificación, criterio autoevaluación, tamaño archivo.
- ✅ `db/spp_schema.sql` — 18 tablas MySQL 5.5+, llaves, restricciones UNIQUE, FKs en orden.
- ✅ `db/spp_datos.sql` — 1 admin, 1 coordinador, 1 profesor, 5 estudiantes, 2 OV, proyectos;
     contraseñas cifradas AES (misma clave/IV que bd.properties de prueba).
- ✅ POJOs `modelo/`: `Academico` (abstract), `Coordinador`, `Profesor`, `Administrador`,
     `Estudiante`, `OrganizacionVinculada`, `ResponsableDeProyecto`, `Proyecto`.
- ✅ `modelo/TipoUsuario.java` — enum con 4 roles; discrimina tabla y tipo de identificador.
- ✅ `modelo/ResultadoAutenticacion.java` — DTO de resultado de autenticación.
- ✅ `persistencia/dao/UsuarioDAO.java` — interfaz con 4 métodos (autenticar, incrementar
     intentos, reiniciar, actualizar contraseña).
- ✅ `persistencia/dao/impl/UsuarioDAOImpl.java` — implementación JDBC completa: cifra
     identificadores con AES para la cláusula WHERE, descifra contraseña almacenada y
     compara en texto plano, dos UPDATE para incremento+bloqueo atómico (SEG-01).
- ✅ `negocio/LoginServicio.java` — lógica SEG-01 (bloqueo 3 intentos/10 min) y SEG-02
     (contraseña temporal). Inyección de dependencia por constructor. Bug corregido:
     fuerza `exitoso=false` cuando la cuenta está inactiva.
- ✅ `resources/css/login.css` — paleta UV (#1C3A6E), tarjeta dos paneles, estilos error/info.
- ✅ `resources/vistas/login.fxml` — pantalla de login (StackPane → HBox tarjeta → panel
     marca + panel formulario). Sin lógica de negocio.
- ✅ `controladores/comun/LoginController.java` — valida UI, llama LoginServicio, gestiona
     mensajes; sigue convención `onBtnIniciarSesion()`.
- ✅ `App.java` — carga login.fxml como pantalla inicial (960×600, no resizable).
     `cambiarVista(String rutaFxml)` para navegación entre pantallas.
- ✅ `module-info.java` — agrega `opens mx.uv.spp.controladores.comun to javafx.fxml`.
- ✅ `pom.xml` — `mysql-connector-java:8.0.33`, `junit-jupiter:5.10.0`, Surefire 3.1.2
     con `<useModulePath>false</useModulePath>`.
- ✅ `test/negocio/LoginServicioTest.java` — 22 casos JUnit 5 con `UsuarioDAOStub`;
     **todos pasan (22/22)**. Cubre: entradas vacías, no encontrado, cuenta inactiva,
     bloqueo vigente/expirado, incremento de intentos, tercer intento, login exitoso,
     contraseña temporal, cambiar contraseña.
- ⬜ `util/GeneradorPDF.java` — generación de PDF (Oficios de Aceptación/Asignación).

### Módulos
- ✅ Login — UI + negocio + DAO JDBC completos. 22 pruebas unitarias pasan.
     ⚠️ Pendiente: probar UI contra BD en ejecución real (requiere MySQL corriendo con datos de prueba).
- ⬜ DAOs: interfaces + implementaciones para los demás módulos (Estudiante, Proyecto, OV…).
- ⬜ Administrador: Alta/Baja Coordinador y Profesor.
- ⬜ Coordinador: Control de Proyectos (Alta/Actualizar OV, Publicar/Baja/Actualizar Proyecto).
- ⬜ Coordinador: Control de Practicantes (Alta/Baja, Asignar/Desasignar, Indicadores).
- ⬜ Profesor: Evaluación (documentos, reportes, informes, presentación, OV, autoevaluación, final).
- ⬜ Practicante: Gestión (elegir proyectos, entregar documentos/reportes, autoevaluación).
- ⬜ Comunicación: Buzón + Enviar mensaje.
- ⬜ Pruebas unitarias por módulo (futuros).

---

## 14. BITÁCORA DE SESIONES

> Una entrada por sesión. Formato: fecha — qué se hizo — decisiones — qué sigue.

- **2026-06-13 (sesión 1)** — Creación del `CLAUDE.md`. Definida la arquitectura por capas y la
  estructura de paquetes. Confirmadas restricciones: JDBC puro (sin JPA), Java/JavaFX/MySQL.
  Detectado riesgo NetBeans vs IntelliJ.

- **2026-06-13 (sesión 2)** — Infraestructura base completada:
  - Diagnóstico del proyecto existente; `module-info.java` actualizado con `requires java.sql`.
  - Estructura de paquetes creada con `.gitkeep`; `bd.properties` con credenciales externas.
  - `Constantes.java`, `ConexionBD.java` (singleton JDBC sin credenciales en código).
  - `CifradoAES.java` (AES-128-CBC, lazy init hilo-seguro), `GeneradorContrasena.java`
    (SecureRandom + Fisher-Yates), `Validador.java` (7 métodos defensivos).
  - `db/spp_schema.sql` (18 tablas, MySQL 5.5+), `db/spp_datos.sql` (datos de prueba con
    contraseñas cifradas AES; clave/IV fijo de prueba en bd.properties).
  - POJOs modelo: `Academico`, `Coordinador`, `Profesor`, `Administrador`, `Estudiante`,
    `OrganizacionVinculada`, `ResponsableDeProyecto`, `Proyecto`.
  - Decisión: IV fijo en pruebas para que UNIQUE constraints funcionen; en producción debe
    ser IV aleatorio prefijado al ciphertext.
  - ⚠️ Pendiente: agregar `mysql-connector-java` y JUnit a `pom.xml`.
  - Próximo paso: DAOs (`persistencia/dao/` interfaces + `dao/impl/` JDBC) y módulo de Login.

- **2026-06-13 (sesión 3)** — Módulo de login completado (UI + negocio + DAO JDBC + tests):
  - `TipoUsuario` (enum), `ResultadoAutenticacion` (DTO), `UsuarioDAO` (interfaz).
  - `LoginServicio`: aplica SEG-01 (bloqueo 3 intentos / 10 min) y SEG-02 (contraseña
    temporal). Inyección de dependencia por constructor. Bug corregido al final: forzar
    `exitoso=false` cuando cuenta inactiva aunque credenciales sean correctas.
  - `login.fxml`: StackPane con tarjeta dos columnas (panel marca UV + panel formulario).
    Sin lógica ni acceso a BD.
  - `login.css`: paleta azul institucional UV (#1C3A6E / #152D56), estilos error/info.
  - `LoginController`: valida UI antes de llamar al servicio, usa `onBtnIniciarSesion()`,
    gestiona mensajes con clases CSS dinámicas.
  - `App.java` refactorizado: carga login.fxml (960×600, no resizable),
    `cambiarVista(rutaFxml)` como punto de navegación centralizado.
  - `module-info.java`: abierto `mx.uv.spp.controladores.comun` a `javafx.fxml`.
  - `UsuarioDAOImpl` implementado con JDBC completo: cifra identificador para WHERE,
    descifra contraseña BD y compara en plano, dos UPDATE para incremento+bloqueo.
  - `pom.xml`: dependencias `mysql-connector-java:8.0.33`, `junit-jupiter:5.10.0`,
    Surefire 3.1.2 con `useModulePath=false`.
  - `LoginServicioTest`: 22 casos JUnit 5 con `UsuarioDAOStub` — todos pasan (22/22).
  - Eliminados `PrimaryController.java` y `SecondaryController.java` (plantilla Maven;
    causaban error de compilación al referenciar `App.setRoot()` renombrado a
    `App.cambiarVista()`).
  - Decisión: navegación a pantallas principales y de cambio de contraseña marcadas
    como comentarios en el controlador (pantallas pendientes).
  - Próximo paso: implementar módulo Administrador (Alta/Baja Coordinador y Profesor)
    con sus DAOs + pantallas FXML.

---

## 15. CÓMO TRABAJAR CONMIGO (Claude Code) EN CADA SESIÓN

1. Al iniciar: leo este archivo completo y te confirmo qué entendí y en qué estado quedó todo.
2. Trabajamos un módulo/CU a la vez. Antes de codificar, reviso los archivos existentes
   relacionados para no duplicar ni romper nada.
3. Todo el código que genere sigue la sección 5 sin excepción.
4. Al cerrar la sesión: actualizo "ESTADO DE IMPLEMENTACIÓN" (sección 13) y agrego una
   entrada en "BITÁCORA DE SESIONES" (sección 14).
5. Si encuentro una decisión pendiente (ej. fórmula de calificación final RN-20), la marco
   con ⚠️ y la dejo anotada en lugar de inventar.
