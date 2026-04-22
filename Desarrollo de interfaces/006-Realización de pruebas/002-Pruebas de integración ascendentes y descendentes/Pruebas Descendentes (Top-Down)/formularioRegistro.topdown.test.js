/**
 * @jest-environment jsdom
 */

const fs = require("fs");
const path = require("path");

function cargarDOM() {
  const rutaHtml = path.join(__dirname, "formulario.html");
  const html = fs.readFileSync(rutaHtml, "utf8");
  const bodyMatch = html.match(/<body[^>]*>([\s\S]*?)<\/body>/i);

  document.body.innerHTML = bodyMatch ? bodyMatch[1] : html;
}

function cargarAplicacionConStub(resultadoValidacion) {
  jest.resetModules();
  cargarDOM();

  globalThis.FormularioRegistro = {
    validarRegistro: jest.fn(() => resultadoValidacion)
  };

  require("./script");
}

function rellenarFormulario(datos = {}) {
  document.getElementById("nombre").value = datos.nombre ?? "";
  document.getElementById("email").value = datos.email ?? "";
  document.getElementById("edad").value = datos.edad ?? "";
  document.getElementById("password").value = datos.password ?? "";
  document.getElementById("repetirPassword").value = datos.repetirPassword ?? "";
  document.getElementById("terminos").checked = datos.terminos ?? false;
}

function enviarFormulario() {
  const formulario = document.getElementById("formRegistro");
  formulario.dispatchEvent(
    new Event("submit", { bubbles: true, cancelable: true })
  );
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

describe("Top-Down del formulario: respuesta visual ante validación correcta", () => {
  beforeEach(() => {
    cargarAplicacionConStub({
      esValido: true,
      errores: {
        nombre: "",
        email: "",
        edad: "",
        password: "",
        repetirPassword: "",
        terminos: ""
      },
      datosLimpios: {
        nombre: "Pablo",
        email: "pablo@correo.com",
        edad: "20",
        password: "abc123",
        repetirPassword: "abc123",
        terminos: true
      }
    });
  });

  test("llama a validarRegistro al enviar el formulario", () => {
    rellenarFormulario({
      nombre: "Pablo",
      email: "pablo@correo.com",
      edad: "20",
      password: "abc123",
      repetirPassword: "abc123",
      terminos: true
    });

    enviarFormulario();

    expect(globalThis.FormularioRegistro.validarRegistro).toHaveBeenCalledTimes(1);
  });

  test("envía a validarRegistro los datos recogidos del DOM", () => {
    rellenarFormulario({
      nombre: "Pablo",
      email: "pablo@correo.com",
      edad: "20",
      password: "abc123",
      repetirPassword: "abc123",
      terminos: true
    });

    enviarFormulario();

    expect(globalThis.FormularioRegistro.validarRegistro).toHaveBeenCalledWith({
      nombre: "Pablo",
      email: "pablo@correo.com",
      edad: "20",
      password: "abc123",
      repetirPassword: "abc123",
      terminos: true
    });
  });

  test("muestra mensaje general correcto", () => {
    rellenarFormulario({
      nombre: "Pablo",
      email: "pablo@correo.com",
      edad: "20",
      password: "abc123",
      repetirPassword: "abc123",
      terminos: true
    });

    enviarFormulario();

    expect(textoDe("mensajeGeneral")).toBe("Formulario enviado correctamente.");
    expect(document.getElementById("mensajeGeneral").className).toBe("mensaje-general correcto");
  });

  test("muestra el resumen con los datos limpios que devuelve el stub", () => {
    rellenarFormulario({
      nombre: "Da igual",
      email: "mal",
      edad: "99",
      password: "xxxxxx",
      repetirPassword: "yyyyyy",
      terminos: false
    });

    enviarFormulario();

    expect(resumenVisible()).toBe(true);
    expect(textoDe("resumenNombre")).toBe("Pablo");
    expect(textoDe("resumenEmail")).toBe("pablo@correo.com");
    expect(textoDe("resumenEdad")).toBe("20");
  });

  test("con validación correcta se muestra el mensaje, el resumen y no aparecen errores", () => {
    rellenarFormulario({
      nombre: "Pablo",
      email: "pablo@correo.com",
      edad: "20",
      password: "abc123",
      repetirPassword: "abc123",
      terminos: true
    });

    enviarFormulario();

    expect(textoDe("mensajeGeneral")).toBe("Formulario enviado correctamente.");
    expect(document.getElementById("mensajeGeneral").className).toBe("mensaje-general correcto");

    expect(resumenVisible()).toBe(true);
    expect(textoDe("resumenNombre")).toBe("Pablo");
    expect(textoDe("resumenEmail")).toBe("pablo@correo.com");
    expect(textoDe("resumenEdad")).toBe("20");

    expect(textoDe("errorNombre")).toBe("");
    expect(textoDe("errorEmail")).toBe("");
    expect(textoDe("errorEdad")).toBe("");
    expect(textoDe("errorPassword")).toBe("");
    expect(textoDe("errorRepetirPassword")).toBe("");
    expect(textoDe("errorTerminos")).toBe("");
  });
});

describe("Top-Down del formulario: respuesta visual ante validación incorrecta", () => {
  beforeEach(() => {
    cargarAplicacionConStub({
      esValido: false,
      errores: {
        nombre: "El nombre es obligatorio.",
        email: "El correo electrónico no tiene un formato válido.",
        edad: "La edad debe ser un número entero entre 18 y 120.",
        password: "La contraseña debe tener al menos 6 caracteres.",
        repetirPassword: "Las contraseñas no coinciden.",
        terminos: "Debes aceptar los términos y condiciones."
      },
      datosLimpios: {
        nombre: "",
        email: "",
        edad: "",
        password: "",
        repetirPassword: "",
        terminos: false
      }
    });
  });

  test("muestra mensaje general de error", () => {
    enviarFormulario();

    expect(textoDe("mensajeGeneral")).toBe("Revisa los errores del formulario.");
    expect(document.getElementById("mensajeGeneral").className).toBe("mensaje-general error");
  });

  test("pinta todos los errores que devuelve el stub", () => {
    enviarFormulario();

    expect(textoDe("errorNombre")).toBe("El nombre es obligatorio.");
    expect(textoDe("errorEmail")).toBe("El correo electrónico no tiene un formato válido.");
    expect(textoDe("errorEdad")).toBe("La edad debe ser un número entero entre 18 y 120.");
    expect(textoDe("errorPassword")).toBe("La contraseña debe tener al menos 6 caracteres.");
    expect(textoDe("errorRepetirPassword")).toBe("Las contraseñas no coinciden.");
    expect(textoDe("errorTerminos")).toBe("Debes aceptar los términos y condiciones.");
  });

  test("marca como input-error los campos que llegan con error", () => {
    enviarFormulario();

    expect(tieneClase("nombre", "input-error")).toBe(true);
    expect(tieneClase("email", "input-error")).toBe(true);
    expect(tieneClase("edad", "input-error")).toBe(true);
    expect(tieneClase("password", "input-error")).toBe(true);
    expect(tieneClase("repetirPassword", "input-error")).toBe(true);
  });

  test("mantiene oculto el resumen cuando el stub devuelve error", () => {
    enviarFormulario();

    expect(resumenVisible()).toBe(false);
    expect(textoDe("resumenNombre")).toBe("--");
    expect(textoDe("resumenEmail")).toBe("--");
    expect(textoDe("resumenEdad")).toBe("--");
  });
});

describe("Top-Down del formulario: limpieza de interfaz", () => {
  beforeEach(() => {
    cargarAplicacionConStub({
      esValido: false,
      errores: {
        nombre: "El nombre es obligatorio.",
        email: "El correo electrónico no tiene un formato válido.",
        edad: "La edad debe ser un número entero entre 18 y 120.",
        password: "La contraseña debe tener al menos 6 caracteres.",
        repetirPassword: "Las contraseñas no coinciden.",
        terminos: "Debes aceptar los términos y condiciones."
      },
      datosLimpios: {
        nombre: "",
        email: "",
        edad: "",
        password: "",
        repetirPassword: "",
        terminos: false
      }
    });
  });

  test("el botón limpiar vacía inputs y desmarca el checkbox", () => {
    rellenarFormulario({
      nombre: "Pablo",
      email: "correo",
      edad: "17",
      password: "123",
      repetirPassword: "456",
      terminos: true
    });

    enviarFormulario();
    clickLimpiar();

    expect(document.getElementById("nombre").value).toBe("");
    expect(document.getElementById("email").value).toBe("");
    expect(document.getElementById("edad").value).toBe("");
    expect(document.getElementById("password").value).toBe("");
    expect(document.getElementById("repetirPassword").value).toBe("");
    expect(document.getElementById("terminos").checked).toBe(false);
  });

  test("el botón limpiar borra errores y mensaje general", () => {
    enviarFormulario();
    clickLimpiar();

    expect(textoDe("errorNombre")).toBe("");
    expect(textoDe("errorEmail")).toBe("");
    expect(textoDe("errorEdad")).toBe("");
    expect(textoDe("errorPassword")).toBe("");
    expect(textoDe("errorRepetirPassword")).toBe("");
    expect(textoDe("errorTerminos")).toBe("");
    expect(textoDe("mensajeGeneral")).toBe("");
  });

  test('el botón limpiar oculta el resumen y restaura "--"', () => {
    document.getElementById("resumen").classList.add("visible");
    document.getElementById("resumenNombre").textContent = "Pablo";
    document.getElementById("resumenEmail").textContent = "pablo@correo.com";
    document.getElementById("resumenEdad").textContent = "20";

    clickLimpiar();

    expect(resumenVisible()).toBe(false);
    expect(textoDe("resumenNombre")).toBe("--");
    expect(textoDe("resumenEmail")).toBe("--");
    expect(textoDe("resumenEdad")).toBe("--");
  });

  test("el botón limpiar quita clases input-error e input-correcto", () => {
    enviarFormulario();
    clickLimpiar();

    expect(tieneClase("nombre", "input-error")).toBe(false);
    expect(tieneClase("nombre", "input-correcto")).toBe(false);

    expect(tieneClase("email", "input-error")).toBe(false);
    expect(tieneClase("email", "input-correcto")).toBe(false);

    expect(tieneClase("edad", "input-error")).toBe(false);
    expect(tieneClase("edad", "input-correcto")).toBe(false);

    expect(tieneClase("password", "input-error")).toBe(false);
    expect(tieneClase("password", "input-correcto")).toBe(false);

    expect(tieneClase("repetirPassword", "input-error")).toBe(false);
    expect(tieneClase("repetirPassword", "input-correcto")).toBe(false);
  });
});