# Calculadora de precios documentada

Ejemplo didáctico para trabajar la documentación de aplicaciones en el módulo Desarrollo de Interfaces.

La aplicación está hecha con HTML y JavaScript base. Permite calcular el precio final de una compra aplicando cantidad, IVA y descuento.

---

## Funcionalidad

La aplicación calcula:

- subtotal;
- descuento aplicado;
- IVA;
- total final.

El usuario introduce:

- precio unitario;
- cantidad;
- porcentaje de IVA;
- porcentaje de descuento.

---

## Tecnologías utilizadas

- HTML
- JavaScript base
- Node.js
- npm
- JSDoc

---

## Estructura del proyecto

```txt
004-Tipos de manuales/
│
├── calculadoraPrecios.html
├── ayuda.html
├── package.json
├── package-lock.json
├── jsdoc.json
├── README.md
│
├── js/
│   ├── app.js
│   ├── precios.js
│   └── instrucciones.txt
│
└── docs/
    └── api/
        └── index.html
```

---

## Archivos principales

`calculadoraPrecios.html`

Página principal de la aplicación. Contiene el formulario y muestra los resultados.

`ayuda.html`

Ayuda de usuario. Explica cómo usar la calculadora y cómo interpretar los resultados.

`js/precios.js`

Contiene las funciones de cálculo:

- `calcularSubtotal()`
- `calcularIVA()`
- `calcularDescuento()`
- `calcularTotal()`

`js/app.js`

Contiene la lógica de interfaz:

- lectura del formulario;
- validación de datos;
- llamada a las funciones de cálculo;
- muestra de resultados y errores.

`docs/api/index.html`

Documentación técnica generada automáticamente con JSDoc.

---

## Instalación

Para generar la documentación técnica hace falta tener instalado Node.js.

Desde la carpeta del proyecto, ejecutar:

```bash
npm.cmd install
```

Este comando instala JSDoc y las dependencias necesarias.

---

## Generar documentación técnica

Ejecutar:

```bash
npm.cmd run generar-docs
```

La documentación se genera en:

```txt
docs/api/index.html
```

---

## Ejecutar la aplicación

Abrir en el navegador:

```txt
calculadoraPrecios.html
```

También puede abrirse con Live Server desde Visual Studio Code.

---

## Tipos de documentación incluidos

### Ayuda de usuario

Archivo:

```txt
ayuda.html
```

Explica la aplicación desde el punto de vista del usuario final.

### Documentación técnica

Archivo:

```txt
docs/api/index.html
```

Se genera automáticamente con JSDoc a partir de los comentarios del código JavaScript.

---

## Comentarios JSDoc usados

En el proyecto se usan principalmente:

```txt
@file
@author
@param
@returns
@example
```

Ejemplo:

```js
/**
 * Calcula el subtotal de una compra.
 *
 * @param {number} precioUnitario Precio de una unidad.
 * @param {number} cantidad Número de unidades.
 * @returns {number} Subtotal de la compra.
 */
function calcularSubtotal(precioUnitario, cantidad) {
    return precioUnitario * cantidad;
}
```

---

## Comandos importantes

Instalar dependencias:

```bash
npm.cmd install
```

Generar documentación:

```bash
npm.cmd run generar-docs
```

Abrir documentación técnica:

```txt
docs/api/index.html
```

Abrir ayuda de usuario:

```txt
ayuda.html
```

---

## Errores frecuentes

### `package not found`

Suele ocurrir si se ejecuta:

```bash
npm docs
```

El comando correcto es:

```bash
npm.cmd run generar-docs
```

### `There are no input files to process`

JSDoc no encuentra los archivos JavaScript.

Comprueba que existen:

```txt
js/app.js
js/precios.js
```

y que el script de `package.json` apunta correctamente a ellos.

---

## Posibles ampliaciones

- Añadir buscador en `ayuda.html`.
- Crear manual de usuario en Markdown.
- Crear guía rápida.
- Crear FAQ.
- Montar una web de documentación con Docsify.
- Añadir diagramas con Mermaid.
- Exportar la ayuda a PDF.
- Publicar la documentación con GitHub Pages.