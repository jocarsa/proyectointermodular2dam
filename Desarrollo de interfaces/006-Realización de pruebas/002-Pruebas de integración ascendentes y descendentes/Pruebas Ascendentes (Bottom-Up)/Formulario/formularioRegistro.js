(function (global) {
  "use strict";

  function limpiarTexto(texto) {
    if (texto === null || texto === undefined) {
      return "";
    }

    return String(texto).trim();
  }

  function estaVacio(texto) {
    return limpiarTexto(texto) === "";
  }

  function tieneLongitudMinima(texto, minimo) {
    return limpiarTexto(texto).length >= minimo;
  }

  function emailValido(email) {
    const emailLimpio = limpiarTexto(email);

    if (emailLimpio === "") {
      return false;
    }

    if (emailLimpio.includes(" ")) {
      return false;
    }

    const posicionArroba = emailLimpio.indexOf("@");
    const posicionPunto = emailLimpio.lastIndexOf(".");

    return (
      posicionArroba > 0 &&
      posicionPunto > posicionArroba + 1 &&
      posicionPunto < emailLimpio.length - 1
    );
  }

  function edadValida(edadTexto) {
    if (estaVacio(edadTexto)) {
      return false;
    }

    const edad = Number(edadTexto);

    return Number.isInteger(edad) && edad >= 18 && edad <= 120;
  }

  function passwordValida(password) {
    return !estaVacio(password) && String(password).length >= 6;
  }

  function passwordsCoinciden(password, repetirPassword) {
    return String(password) === String(repetirPassword);
  }

  function terminosAceptados(valor) {
    return valor === true;
  }

  function validarNombre(nombre) {
    if (estaVacio(nombre)) {
      return "El nombre es obligatorio.";
    }

    if (!tieneLongitudMinima(nombre, 3)) {
      return "El nombre debe tener al menos 3 caracteres.";
    }

    return "";
  }

  function validarEmail(email) {
    if (estaVacio(email)) {
      return "El correo electrónico es obligatorio.";
    }

    if (!emailValido(email)) {
      return "El correo electrónico no tiene un formato válido.";
    }

    return "";
  }

  function validarEdad(edad) {
    if (estaVacio(edad)) {
      return "La edad es obligatoria.";
    }

    if (!edadValida(edad)) {
      return "La edad debe ser un número entero entre 18 y 120.";
    }

    return "";
  }

  function validarPassword(password) {
    if (estaVacio(password)) {
      return "La contraseña es obligatoria.";
    }

    if (!passwordValida(password)) {
      return "La contraseña debe tener al menos 6 caracteres.";
    }

    return "";
  }

  function validarRepetirPassword(password, repetirPassword) {
    if (estaVacio(repetirPassword)) {
      return "Debes repetir la contraseña.";
    }

    if (!passwordsCoinciden(password, repetirPassword)) {
      return "Las contraseñas no coinciden.";
    }

    return "";
  }

  function validarTerminos(terminos) {
    if (!terminosAceptados(terminos)) {
      return "Debes aceptar los términos y condiciones.";
    }

    return "";
  }

  function validarRegistro(datos) {
    const nombre = limpiarTexto(datos.nombre);
    const email = limpiarTexto(datos.email);
    const edad = limpiarTexto(datos.edad);
    const password = datos.password === undefined || datos.password === null ? "" : String(datos.password);
    const repetirPassword = datos.repetirPassword === undefined || datos.repetirPassword === null ? "" : String(datos.repetirPassword);
    const terminos = datos.terminos === true;

    const errores = {
      nombre: validarNombre(nombre),
      email: validarEmail(email),
      edad: validarEdad(edad),
      password: validarPassword(password),
      repetirPassword: validarRepetirPassword(password, repetirPassword),
      terminos: validarTerminos(terminos)
    };

    const esValido =
      errores.nombre === "" &&
      errores.email === "" &&
      errores.edad === "" &&
      errores.password === "" &&
      errores.repetirPassword === "" &&
      errores.terminos === "";

    return {
      esValido,
      errores,
      datosLimpios: {
        nombre,
        email,
        edad,
        password,
        repetirPassword,
        terminos
      }
    };
  }

  const api = {
    limpiarTexto,
    estaVacio,
    tieneLongitudMinima,
    emailValido,
    edadValida,
    passwordValida,
    passwordsCoinciden,
    terminosAceptados,
    validarNombre,
    validarEmail,
    validarEdad,
    validarPassword,
    validarRepetirPassword,
    validarTerminos,
    validarRegistro
  };

  if (typeof module !== "undefined" && module.exports) {
    module.exports = api;
  } else {
    global.FormularioRegistro = api;
  }
})(typeof window !== "undefined" ? window : globalThis);