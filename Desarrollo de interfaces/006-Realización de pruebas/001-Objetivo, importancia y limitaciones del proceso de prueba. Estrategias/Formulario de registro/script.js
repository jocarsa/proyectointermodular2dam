// ===============================
// FUNCIONES DE LÓGICA
// ===============================

function estaVacio(texto) {
  return texto.trim() === "";
}

function tieneLongitudMinima(texto, minimo) {
  return texto.trim().length >= minimo;
}

function emailValido(email) {
  const emailLimpio = email.trim();

  if (emailLimpio === "") {
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

  if (isNaN(edadTexto)) {
    return false;
  }

  const edad = Number(edadTexto);

  return Number.isInteger(edad) && edad >= 18 && edad <= 120;
}

function passwordValida(password) {
  return !estaVacio(password) && password.length >= 6;
}

function passwordsCoinciden(password, repetirPassword) {
  return password === repetirPassword;
}

// ===============================
// ELEMENTOS DEL DOM
// ===============================

const formRegistro = document.getElementById("formRegistro");
const inputNombre = document.getElementById("nombre");
const inputEmail = document.getElementById("email");
const inputEdad = document.getElementById("edad");
const inputPassword = document.getElementById("password");
const inputRepetirPassword = document.getElementById("repetirPassword");
const inputTerminos = document.getElementById("terminos");
const btnLimpiar = document.getElementById("btnLimpiar");
const mensajeGeneral = document.getElementById("mensajeGeneral");

const errorNombre = document.getElementById("errorNombre");
const errorEmail = document.getElementById("errorEmail");
const errorEdad = document.getElementById("errorEdad");
const errorPassword = document.getElementById("errorPassword");
const errorRepetirPassword = document.getElementById("errorRepetirPassword");
const errorTerminos = document.getElementById("errorTerminos");

// ===============================
// FUNCIONES DE INTERFAZ
// ===============================

function limpiarEstilosInput(input) {
  input.classList.remove("input-error");
  input.classList.remove("input-correcto");
}

function marcarInputError(input) {
  input.classList.remove("input-correcto");
  input.classList.add("input-error");
}

function marcarInputCorrecto(input) {
  input.classList.remove("input-error");
  input.classList.add("input-correcto");
}

function limpiarErrores() {
  errorNombre.textContent = "";
  errorEmail.textContent = "";
  errorEdad.textContent = "";
  errorPassword.textContent = "";
  errorRepetirPassword.textContent = "";
  errorTerminos.textContent = "";

  limpiarEstilosInput(inputNombre);
  limpiarEstilosInput(inputEmail);
  limpiarEstilosInput(inputEdad);
  limpiarEstilosInput(inputPassword);
  limpiarEstilosInput(inputRepetirPassword);

  mensajeGeneral.textContent = "";
  mensajeGeneral.className = "mensaje-general";
}

function limpiarFormulario() {
  formRegistro.reset();
  limpiarErrores();
  inputNombre.focus();
}

function mostrarMensajeGeneral(texto, tipo) {
  mensajeGeneral.textContent = texto;
  mensajeGeneral.className = "mensaje-general " + tipo;
}

function validarFormulario() {
  limpiarErrores();

  let formularioEsValido = true;

  const nombre = inputNombre.value;
  const email = inputEmail.value;
  const edad = inputEdad.value;
  const password = inputPassword.value;
  const repetirPassword = inputRepetirPassword.value;
  const terminosAceptados = inputTerminos.checked;

  // Validación del nombre
  if (estaVacio(nombre)) {
    errorNombre.textContent = "El nombre es obligatorio.";
    marcarInputError(inputNombre);
    formularioEsValido = false;
  } else if (!tieneLongitudMinima(nombre, 3)) {
    errorNombre.textContent = "El nombre debe tener al menos 3 caracteres.";
    marcarInputError(inputNombre);
    formularioEsValido = false;
  } else {
    marcarInputCorrecto(inputNombre);
  }

  // Validación del email
  if (estaVacio(email)) {
    errorEmail.textContent = "El correo electrónico es obligatorio.";
    marcarInputError(inputEmail);
    formularioEsValido = false;
  } else if (!emailValido(email)) {
    errorEmail.textContent = "El correo electrónico no tiene un formato válido.";
    marcarInputError(inputEmail);
    formularioEsValido = false;
  } else {
    marcarInputCorrecto(inputEmail);
  }

  // Validación de la edad
  if (estaVacio(edad)) {
    errorEdad.textContent = "La edad es obligatoria.";
    marcarInputError(inputEdad);
    formularioEsValido = false;
  } else if (!edadValida(edad)) {
    errorEdad.textContent = "La edad debe ser un número entero entre 18 y 120.";
    marcarInputError(inputEdad);
    formularioEsValido = false;
  } else {
    marcarInputCorrecto(inputEdad);
  }

  // Validación de la contraseña
  if (estaVacio(password)) {
    errorPassword.textContent = "La contraseña es obligatoria.";
    marcarInputError(inputPassword);
    formularioEsValido = false;
  } else if (!passwordValida(password)) {
    errorPassword.textContent = "La contraseña debe tener al menos 6 caracteres.";
    marcarInputError(inputPassword);
    formularioEsValido = false;
  } else {
    marcarInputCorrecto(inputPassword);
  }

  // Validación de repetir contraseña
  if (estaVacio(repetirPassword)) {
    errorRepetirPassword.textContent = "Debes repetir la contraseña.";
    marcarInputError(inputRepetirPassword);
    formularioEsValido = false;
  } else if (!passwordsCoinciden(password, repetirPassword)) {
    errorRepetirPassword.textContent = "Las contraseñas no coinciden.";
    marcarInputError(inputRepetirPassword);
    formularioEsValido = false;
  } else {
    marcarInputCorrecto(inputRepetirPassword);
  }

  // Validación de términos
  if (!terminosAceptados) {
    errorTerminos.textContent = "Debes aceptar los términos y condiciones.";
    formularioEsValido = false;
  }

  return formularioEsValido;
}

// ===============================
// EVENTOS
// ===============================

formRegistro.addEventListener("submit", function (evento) {
  evento.preventDefault();

  const esValido = validarFormulario();

  if (esValido) {
    mostrarMensajeGeneral("Formulario enviado correctamente.", "correcto");
  } else {
    mostrarMensajeGeneral("Revisa los errores del formulario.", "error");
  }
});

btnLimpiar.addEventListener("click", limpiarFormulario);