"use strict";

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

const resumen = document.getElementById("resumen");
const resumenNombre = document.getElementById("resumenNombre");
const resumenEmail = document.getElementById("resumenEmail");
const resumenEdad = document.getElementById("resumenEdad");

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

function ocultarResumen() {
  resumen.classList.remove("visible");
  resumenNombre.textContent = "--";
  resumenEmail.textContent = "--";
  resumenEdad.textContent = "--";
}

function mostrarResumen(datos) {
  resumenNombre.textContent = datos.nombre;
  resumenEmail.textContent = datos.email;
  resumenEdad.textContent = datos.edad;
  resumen.classList.add("visible");
}

function mostrarMensajeGeneral(texto, tipo) {
  mensajeGeneral.textContent = texto;
  mensajeGeneral.className = "mensaje-general " + tipo;
}

function pintarErrores(errores) {
  if (errores.nombre !== "") {
    errorNombre.textContent = errores.nombre;
    marcarInputError(inputNombre);
  } else {
    marcarInputCorrecto(inputNombre);
  }

  if (errores.email !== "") {
    errorEmail.textContent = errores.email;
    marcarInputError(inputEmail);
  } else {
    marcarInputCorrecto(inputEmail);
  }

  if (errores.edad !== "") {
    errorEdad.textContent = errores.edad;
    marcarInputError(inputEdad);
  } else {
    marcarInputCorrecto(inputEdad);
  }

  if (errores.password !== "") {
    errorPassword.textContent = errores.password;
    marcarInputError(inputPassword);
  } else {
    marcarInputCorrecto(inputPassword);
  }

  if (errores.repetirPassword !== "") {
    errorRepetirPassword.textContent = errores.repetirPassword;
    marcarInputError(inputRepetirPassword);
  } else {
    marcarInputCorrecto(inputRepetirPassword);
  }

  if (errores.terminos !== "") {
    errorTerminos.textContent = errores.terminos;
  }
}

function limpiarFormulario() {
  formRegistro.reset();
  limpiarErrores();
  ocultarResumen();
  inputNombre.focus();
}

formRegistro.addEventListener("submit", function (evento) {
  evento.preventDefault();

  limpiarErrores();
  ocultarResumen();

  const datosFormulario = {
    nombre: inputNombre.value,
    email: inputEmail.value,
    edad: inputEdad.value,
    password: inputPassword.value,
    repetirPassword: inputRepetirPassword.value,
    terminos: inputTerminos.checked
  };

  const resultadoValidacion = FormularioRegistro.validarRegistro(datosFormulario);

  if (resultadoValidacion.esValido) {
    mostrarMensajeGeneral("Formulario enviado correctamente.", "correcto");
    mostrarResumen(resultadoValidacion.datosLimpios);
  } else {
    pintarErrores(resultadoValidacion.errores);
    mostrarMensajeGeneral("Revisa los errores del formulario.", "error");
  }
});

btnLimpiar.addEventListener("click", limpiarFormulario);

ocultarResumen();