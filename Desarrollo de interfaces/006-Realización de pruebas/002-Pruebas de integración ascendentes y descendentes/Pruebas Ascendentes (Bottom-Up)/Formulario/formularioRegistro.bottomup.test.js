/**
 * @jest-environment jsdom
 * 
 * npm.cmd run test:topdown
 */

const fs = require("fs");
const path = require("path");

function cargarDOM() {
  const rutaHtml = path.join(__dirname, "formulario.html");
  const html = fs.readFileSync(rutaHtml, "utf8");
  const bodyMatch = html.match(/<body[^>]*>([\s\S]*?)<\/body>/i);

  document.body.innerHTML = bodyMatch ? bodyMatch[1] : html;
}

function cargarAplicacion() {
  jest.resetModules();
  cargarDOM();
  globalThis.FormularioRegistro = require("./formularioRegistro");
  require("./script");
}

function enviarFormulario() {
  const formulario = document.getElementById("formRegistro");
  formulario.dispatchEvent(
    new Event("submit", { bubbles: true, cancelable: true })
  );
}

function rellenarFormulario(datos = {}) {
  document.getElementById("nombre").value = datos.nombre ?? "";
  document.getElementById("email").value = datos.email ?? "";
  document.getElementById("edad").value = datos.edad ?? "";
  document.getElementById("password").value = datos.password ?? "";
  document.getElementById("repetirPassword").value = datos.repetirPassword ?? "";
  document.getElementById("terminos").checked = datos.terminos ?? false;
}

function clickLimpiar() {
  document.getElementById("btnLimpiar").click();
}

function textoDe(id) {
  return document.getElementById(id).textContent;
}

function tieneClase(id, clase) {
  return document.getElementById(id).classList.contains(clase);
}

function resumenVisible() {
  return document.getElementById("resumen").classList.contains("visible");
}

describe("Carga inicial de la aplicación", () => {
  beforeEach(() => {
    cargarAplicacion();
  });

  /*
    TODO: Casos a testear:

    - El mensaje general debe empezar vacío.
    - El resumen debe empezar oculto.
    - Los tres campos del resumen deben mostrar "--".
    - Todos los mensajes de error por campo deben empezar vacíos.
    - Ningún input debe empezar marcado como correcto o error.
  */

  test.todo("Completar los tests de este bloque");
});

describe("Envío válido del formulario", () => {
  beforeEach(() => {
    cargarAplicacion();
  });

  /*
    TODO: Casos a testear:

    - Con todos los datos correctos debe aparecer el mensaje general correcto.
    - Con todos los datos correctos el resumen debe mostrarse.
    - El resumen debe mostrar los datos limpios (sin espacios sobrantes).
    - No debe aparecer ningún mensaje de error por campo.
    - Los inputs de texto válidos deben quedar marcados como correctos.
    - El mensaje general debe llevar la clase CSS de correcto.
  */

  test.todo("Completar los tests de este bloque");
});

describe("Errores de validación integrados en el DOM", () => {
  beforeEach(() => {
    cargarAplicacion();
  });

  /*
    TODO: Casos a testear:

    Nombre
    - Si el nombre está vacío debe aparecer el error correspondiente en errorNombre.
    - Si el nombre es demasiado corto debe aparecer el error correspondiente en errorNombre.
    - Si el nombre es inválido, el input nombre debe quedar con clase input-error.

    Email
    - Si el email está vacío debe aparecer el error correspondiente en errorEmail.
    - Si el email tiene formato inválido debe aparecer el error correspondiente en errorEmail.
    - Si el email es inválido, el input email debe quedar con clase input-error.

    Edad
    - Si la edad está vacía debe aparecer el error correspondiente en errorEdad.
    - Si la edad es menor de 18 debe aparecer el error correspondiente en errorEdad.
    - Si la edad no es un entero válido debe aparecer el error correspondiente en errorEdad.
    - Si la edad es inválida, el input edad debe quedar con clase input-error.

    Contraseña
    - Si la contraseña está vacía debe aparecer el error correspondiente en errorPassword.
    - Si la contraseña es demasiado corta debe aparecer el error correspondiente en errorPassword.
    - Si la contraseña es inválida, el input password debe quedar con clase input-error.

    Repetir contraseña
    - Si repetirPassword está vacía debe aparecer el error correspondiente en errorRepetirPassword.
    - Si no coincide con la contraseña debe aparecer el error correspondiente en errorRepetirPassword.
    - Si repetirPassword es inválida, el input repetirPassword debe quedar con clase input-error.

    Términos
    - Si no se aceptan los términos debe aparecer el error correspondiente en errorTerminos.

    Comportamiento general con error
    - Si hay cualquier error, el mensaje general debe indicar que se revisen los errores.
    - Si hay cualquier error, el mensaje general debe llevar la clase CSS de error.
    - Si hay cualquier error, el resumen debe permanecer oculto.
  */

  test.todo("Completar los tests de este bloque");
});

describe("Varios errores simultáneos", () => {
  beforeEach(() => {
    cargarAplicacion();
  });

  /*
    TODO: Casos a testear:

    - Si varios campos son incorrectos a la vez, deben aparecer varios mensajes de error simultáneamente.
    - Si varios campos son incorrectos a la vez, cada input incorrecto debe quedar marcado con input-error.
    - Si varios campos son incorrectos a la vez, el resumen debe seguir oculto.
    - Si varios campos son incorrectos a la vez, el mensaje general debe ser el de error.
  */

  test.todo("Completar los tests de este bloque");
});

describe("Integración del resumen", () => {
  beforeEach(() => {
    cargarAplicacion();
  });

  /*
    TODO: Casos a testear:

    - Tras un envío válido, resumenNombre debe mostrar el nombre.
    - Tras un envío válido, resumenEmail debe mostrar el email.
    - Tras un envío válido, resumenEdad debe mostrar la edad.
    - Tras un envío inválido, el resumen no debe mostrarse.
    - Si antes hubo un envío válido y luego uno inválido, el resumen debe ocultarse otra vez.
  */

  test.todo("Completar los tests de este bloque");
});

describe("Botón limpiar", () => {
  beforeEach(() => {
    cargarAplicacion();
  });

  /*
    TODO: Casos a testear:

    - Debe vaciar todos los inputs de texto.
    - Debe desmarcar el checkbox de términos.
    - Debe borrar todos los mensajes de error.
    - Debe borrar el mensaje general.
    - Debe ocultar el resumen.
    - Debe restaurar los textos del resumen a "--".
    - Debe quitar las clases input-error e input-correcto de los inputs.
  */

  test.todo("Completar los tests de este bloque");
});