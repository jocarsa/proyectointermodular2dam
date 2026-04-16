"use strict";

const formCalculadora = document.getElementById("formCalculadora");
const inputNumero1 = document.getElementById("numero1");
const inputNumero2 = document.getElementById("numero2");
const selectOperacion = document.getElementById("operacion");
const btnLimpiar = document.getElementById("btnLimpiar");
const btnVaciarHistorial = document.getElementById("btnVaciarHistorial");
const mensaje = document.getElementById("mensaje");
const resultado = document.getElementById("resultado");
const historial = document.getElementById("historial");

let operacionesRealizadas = [];

function mostrarError(texto) {
  mensaje.textContent = texto;
  mensaje.className = "mensaje error";
  resultado.textContent = "Resultado: --";
}

function mostrarResultado(textoResultado) {
  mensaje.textContent = "Operación realizada correctamente.";
  mensaje.className = "mensaje correcto";
  resultado.textContent = "Resultado: " + textoResultado;
}

function renderizarHistorial() {
  historial.innerHTML = "";

  if (operacionesRealizadas.length === 0) {
    historial.innerHTML = '<li class="historial-vacio">Todavía no hay operaciones.</li>';
    return;
  }

  operacionesRealizadas.forEach(function (operacionTexto) {
    const elemento = document.createElement("li");
    elemento.textContent = operacionTexto;
    historial.appendChild(elemento);
  });
}

function anadirAlHistorial(datosOperacion) {
  const texto = `${datosOperacion.numero1} ${datosOperacion.simbolo} ${datosOperacion.numero2} = ${datosOperacion.resultadoFormateado}`;

  operacionesRealizadas.unshift(texto);

  if (operacionesRealizadas.length > 10) {
    operacionesRealizadas.pop();
  }

  renderizarHistorial();
}

function limpiarFormulario() {
  formCalculadora.reset();
  mensaje.textContent = "";
  mensaje.className = "mensaje";
  resultado.textContent = "Resultado: --";
  inputNumero1.focus();
}

function vaciarHistorial() {
  operacionesRealizadas = [];
  renderizarHistorial();
}

function procesarCalculo(evento) {
  evento.preventDefault();

  try {
    const datosOperacion = Calculadora.calcular(
      inputNumero1.value,
      inputNumero2.value,
      selectOperacion.value
    );

    mostrarResultado(datosOperacion.resultadoFormateado);
    anadirAlHistorial(datosOperacion);
  } catch (error) {
    mostrarError(error.message);
  }
}

formCalculadora.addEventListener("submit", procesarCalculo);
btnLimpiar.addEventListener("click", limpiarFormulario);
btnVaciarHistorial.addEventListener("click", vaciarHistorial);

renderizarHistorial();