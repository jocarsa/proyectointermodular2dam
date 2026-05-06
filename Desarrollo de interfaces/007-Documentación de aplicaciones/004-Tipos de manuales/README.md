# Calculadora de precios documentada

Este proyecto es un ejemplo didáctico para trabajar la documentación de aplicaciones en el módulo Desarrollo de Interfaces.

La aplicación está desarrollada con HTML y JavaScript base. Su objetivo principal no es crear una calculadora compleja, sino mostrar cómo documentar correctamente un proyecto usando comentarios JSDoc, documentación técnica generada automáticamente y ayuda de usuario en HTML.

---

## Descripción del proyecto

La aplicación permite calcular el precio final de una compra a partir de los siguientes datos:

- precio unitario;
- cantidad;
- porcentaje de IVA;
- porcentaje de descuento.

A partir de esos valores, la aplicación calcula:

- subtotal;
- descuento aplicado;
- IVA;
- total final.

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
004-Tipos de manuales
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
├── node_modules/
│
└── docs/
    └── api/
        └── index.html
```

---

## Archivos principales

### calculadoraPrecios.html

Es la página principal de la aplicación.

Contiene el formulario donde el usuario introduce el precio, la cantidad, el IVA y el descuento.

También muestra los resultados calculados.

---

### ayuda.html

Contiene la ayuda de usuario.

Está pensada para explicar la aplicación a una persona que no necesita conocer el código.

Incluye información sobre:

- qué hace la aplicación;
- cómo usar la calculadora;
- qué datos debe introducir el usuario;
- qué resultados se muestran;
- errores frecuentes;
- enlace a la documentación técnica.

---

### js/precios.js

Contiene las funciones principales de cálculo:

- `calcularSubtotal()`
- `calcularIVA()`
- `calcularDescuento()`
- `calcularTotal()`

Estas funciones están documentadas con comentarios JSDoc.

Este archivo contiene funciones puras, es decir, funciones que reciben datos, realizan un cálculo y devuelven un resultado. No modifican directamente el HTML ni acceden al DOM.

---

### js/app.js

Contiene la lógica de interfaz.

Se encarga de:

- leer los datos del formulario;
- validar los valores introducidos;
- llamar a las funciones de cálculo;
- mostrar los resultados en pantalla;
- mostrar mensajes de error cuando los datos no son válidos.

También está documentado con comentarios JSDoc.

---

### docs/api/index.html

Es la documentación técnica generada automáticamente con JSDoc.

Esta documentación se genera a partir de los comentarios escritos en los archivos JavaScript.

---

### package.json

Define la configuración básica del proyecto Node.

Incluye el script necesario para generar la documentación técnica con JSDoc.

---

### jsdoc.json

Archivo de configuración de JSDoc.

Permite indicar qué archivos deben documentarse y dónde debe generarse la documentación.

En este proyecto puede usarse para configurar la generación automática de documentación, aunque también es posible indicar los archivos directamente desde el script de `package.json`.

---

## Instalación

Para generar la documentación técnica es necesario tener instalado Node.js.

Una vez descargado o copiado el proyecto, abre una terminal en la carpeta raíz del proyecto.

Ejecuta:

```bash
npm.cmd install
```

Este comando instala las dependencias necesarias del proyecto.

En este caso, instala JSDoc como herramienta de desarrollo.

---

## Generar la documentación técnica

Para generar la documentación técnica con JSDoc, ejecuta:

```bash
npm.cmd run generar-docs
```

Este comando analiza los archivos JavaScript documentados y genera una carpeta con documentación HTML.

La documentación se genera en:

```txt
docs/api/
```

Para comprobar el resultado, abre en el navegador:

```txt
docs/api/index.html
```

---

## Ejecutar la aplicación

La aplicación no necesita servidor ni base de datos.

Para usarla, abre en el navegador el archivo:

```txt
calculadoraPrecios.html
```

También puedes abrirlo con Live Server desde Visual Studio Code.

---

## Flujo de trabajo recomendado

Cuando se modifique el código o los comentarios JSDoc, se recomienda seguir estos pasos:

1. Modificar el código JavaScript.
2. Añadir o actualizar los comentarios JSDoc.
3. Ejecutar:

```bash
npm.cmd run generar-docs
```

4. Abrir:

```txt
docs/api/index.html
```

5. Comprobar que la documentación técnica se ha actualizado correctamente.

---

## Tipos de documentación del proyecto

Este proyecto contiene dos tipos principales de documentación.

---

### Documentación de usuario

Está pensada para la persona que usa la aplicación.

Archivo principal:

```txt
ayuda.html
```

Explica cómo usar la calculadora y cómo interpretar los resultados.

No explica el código interno de la aplicación.

---

### Documentación técnica

Está pensada para desarrolladores.

Se genera automáticamente con JSDoc a partir de los comentarios del código.

Archivo principal:

```txt
docs/api/index.html
```

Explica las funciones, parámetros, valores devueltos y ejemplos de uso.

---

## Diferencia entre ayuda de usuario y documentación técnica

La ayuda de usuario responde a preguntas como:

- ¿Para qué sirve la aplicación?
- ¿Cómo se usa?
- ¿Qué datos tengo que introducir?
- ¿Qué significan los resultados?
- ¿Por qué aparece un error?

La documentación técnica responde a preguntas como:

- ¿Qué funciones tiene el código?
- ¿Qué parámetros recibe cada función?
- ¿Qué devuelve cada función?
- ¿Qué archivo contiene la lógica de cálculo?
- ¿Qué archivo contiene la lógica de interfaz?

---

## Comentarios JSDoc utilizados

En este proyecto se usan etiquetas como:

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
 * El subtotal se obtiene multiplicando el precio unitario de un producto
 * por la cantidad de unidades compradas.
 *
 * @param {number} precioUnitario Precio de una unidad del producto.
 * @param {number} cantidad Número de unidades compradas.
 * @returns {number} Subtotal de la compra.
 *
 * @example
 * calcularSubtotal(10, 3);
 * // Devuelve 30
 */
function calcularSubtotal(precioUnitario, cantidad) {
    return precioUnitario * cantidad;
}
```

---

## Funciones principales del proyecto

### calcularSubtotal()

Calcula el subtotal de una compra multiplicando el precio unitario por la cantidad.

Ejemplo:

```js
calcularSubtotal(10, 3);
// Devuelve 30
```

---

### calcularDescuento()

Calcula el importe descontado sobre un subtotal.

Ejemplo:

```js
calcularDescuento(200, 10);
// Devuelve 20
```

---

### calcularIVA()

Calcula el IVA de un importe base.

Ejemplo:

```js
calcularIVA(100, 21);
// Devuelve 21
```

---

### calcularTotal()

Calcula el total final sumando el IVA y restando el descuento.

Ejemplo:

```js
calcularTotal(200, 20, 37.8);
// Devuelve 217.8
```

---

### validarDatos()

Comprueba si los datos introducidos por el usuario son válidos.

Valida que:

- el precio sea mayor o igual que cero;
- la cantidad sea mayor que cero;
- el IVA sea mayor o igual que cero;
- el descuento sea mayor o igual que cero;
- todos los valores sean numéricos.

---

### gestionarFormulario()

Gestiona el envío del formulario principal.

Realiza el flujo completo de la aplicación:

1. Cancela el envío tradicional del formulario.
2. Lee los valores introducidos por el usuario.
3. Valida los datos.
4. Calcula el subtotal.
5. Calcula el descuento.
6. Calcula el IVA.
7. Calcula el total final.
8. Muestra los resultados en pantalla.

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

Abrir aplicación:

```txt
calculadoraPrecios.html
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

### Error: package not found

Este error puede aparecer si se ejecuta:

```bash
npm docs
```

Ese no es el comando correcto para este proyecto.

El comando correcto es:

```bash
npm.cmd run generar-docs
```

---

### Error: There are no input files to process

Este error aparece cuando JSDoc no encuentra los archivos JavaScript que debe documentar.

Hay que comprobar que existen estos archivos:

```txt
js/app.js
js/precios.js
```

También hay que revisar que el script del archivo `package.json` apunta correctamente a esos archivos.

Un script válido podría ser:

```json
{
  "scripts": {
    "generar-docs": "jsdoc js/app.js js/precios.js -d docs/api -P package.json"
  }
}
```

---

### La documentación no se actualiza

Si se modifica el código o los comentarios JSDoc, hay que volver a ejecutar:

```bash
npm.cmd run generar-docs
```

Después hay que recargar el archivo:

```txt
docs/api/index.html
```

---

### La página de ayuda no aparece

Hay que comprobar que el archivo existe:

```txt
ayuda.html
```

Y que el enlace desde la calculadora apunta correctamente a esa ruta.

Ejemplo:

```html
<a href="ayuda.html">Ver ayuda de usuario</a>
```

---

## Posibles ampliaciones

Este proyecto puede ampliarse añadiendo:

- buscador en `ayuda.html`;
- manual de usuario en Markdown;
- guía rápida;
- manual de instalación;
- FAQ;
- web de documentación con Docsify;
- diagramas con Mermaid;
- exportación de la ayuda a PDF;
- publicación con GitHub Pages.

---

## Estado actual del proyecto

Actualmente el proyecto incluye:

- aplicación funcional de cálculo de precios;
- funciones JavaScript documentadas con JSDoc;
- generación de documentación técnica;
- ayuda HTML de usuario;
- README general del proyecto.

Las siguientes ampliaciones recomendadas serían:

1. añadir un buscador a la ayuda HTML;
2. crear documentos Markdown separados;
3. montar una web de documentación con Docsify;
4. añadir diagramas con Mermaid;
5. exportar o publicar la documentación final.