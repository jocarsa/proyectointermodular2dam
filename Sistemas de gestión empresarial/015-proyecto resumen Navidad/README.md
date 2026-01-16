# ERP educativo (Express + Pug + MySQL)

Mini proyecto de ERP para **estudiantes**.

Incluye:
- Login con tabla `usuarios`
- Dashboard que detecta automáticamente las tablas de la BD
- CRUD dinámico: **listar, insertar, editar y borrar**
- Interfaz sencilla, profesional y en español

## Requisitos
- Node.js + npm
- MySQL (con una base de datos ya creada)

## 1) Instalación

```bash
npm install
```

## 2) Configurar la base de datos

Por defecto el servidor usa estas variables (puedes cambiarlas en `server.js` o por entorno):

- `DB_HOST` (por defecto: `localhost`)
- `DB_USER` (por defecto: `aplicacionempresarial`)
- `DB_PASS` (por defecto: `Aplicacionempresarial123$`)
- `DB_NAME` (por defecto: `aplicacionempresarial`)

Ejemplo ejecutando con variables de entorno:

```bash
DB_HOST=localhost DB_USER=usuario DB_PASS=clave DB_NAME=mi_bd npm start
```

## 3) Crear el usuario de login

Ejecuta el script SQL:

```sql
SOURCE sql/001-usuarios.sql;
```

Esto crea la tabla `usuarios` y un usuario de ejemplo:
- usuario: `admin`
- contraseña: `admin`

## 4) Arrancar

```bash
npm start
```

Abre:
- `http://localhost:5000`

## 5) Cómo funciona el CRUD

1. El panel lista automáticamente las tablas de tu base de datos.
2. Al seleccionar una tabla, carga **columnas y filas** automáticamente.
3. Puedes:
   - **Insertar** (botón Insertar)
   - **Editar** y **Borrar** (por fila, requiere PK)

> Nota: Editar/Borrar requieren una **clave primaria (PK) simple** detectada en la tabla.

## Ideas para ampliar en clase
- Validación por tipo de dato (fecha, número, etc.)
- Paginación y ordenación
- Búsqueda por columna
- Roles/permisos por tabla
- Contraseñas hasheadas (bcrypt)
