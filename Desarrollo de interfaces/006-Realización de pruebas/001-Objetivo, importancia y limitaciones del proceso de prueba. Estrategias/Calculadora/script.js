// ===============================
// FUNCIONES DE LÓGICA
// ===============================

function sumar(a, b) {
  return a + b;
}

function restar(a, b) {
  return a - b;
}

function multiplicar(a, b) {
  return a * b;
}

function dividir(a, b) {
  if (b === 0) {
    return null;
  }

  return a / b;
}

function esNumeroValido(valor) {
  // trim() elimina espacios al principio y al final
  if (valor.trim() === "") {
    return false;
  }

  return !isNaN(valor);
}

function calcularOperacion(numero1, numero2, operacion) {
  if (operacion === "sumar") {
    return sumar(numero1, numero2);
  }

  if (operacion === "restar") {
    return restar(numero1, numero2);
  }

  if (operacion === "multiplicar") {
    return multiplicar(numero1, numero2);
  }

  if (operacion === "dividir") {
    return dividir(numero1, numero2);
  }

  return null;
}

// ===============================
// FUNCIONES DE INTERFAZ
// ===============================

const inputNumero1 = document.getElementById("numero1");
const inputNumero2 = document.getElementById("numero2");
const selectOperacion = document.getElementById("operacion");
const btnCalcular = document.getElementById("btnCalcular");
const btnLimpiar = document.getElementById("btnLimpiar");
const mensaje = document.getElementById("mensaje");
const resultado = document.getElementById("resultado");

function mostrarError(texto) {
  mensaje.textContent = texto;
  mensaje.className = "mensaje error";
  resultado.textContent = "Resultado: --";
}

function mostrarResultado(valor) {
  mensaje.textContent = "Operación realizada correctamente";
  mensaje.className = "mensaje correcto";
  resultado.textContent = "Resultado: " + valor;
}

function limpiarFormulario() {
  inputNumero1.value = "";
  inputNumero2.value = "";
  selectOperacion.value = "";
  mensaje.textContent = "";
  mensaje.className = "mensaje";
  resultado.textContent = "Resultado: --";
  inputNumero1.focus();
}

function procesarCalculo() {
  const valor1 = inputNumero1.value;
  const valor2 = inputNumero2.value;
  const operacion = selectOperacion.value;

  // 1. Comprobar campos vacíos
  if (valor1.trim() === "" || valor2.trim() === "") {
    mostrarError("Debes introducir los dos números.");
    return;
  }

  // 2. Comprobar si son numéricos
  if (!esNumeroValido(valor1) || !esNumeroValido(valor2)) {
    mostrarError("Los valores introducidos deben ser numéricos.");
    return;
  }

  // 3. Comprobar operación seleccionada
  if (operacion === "") {
    mostrarError("Debes seleccionar una operación.");
    return;
  }

  const numero1 = Number(valor1);
  const numero2 = Number(valor2);

  // 4. Comprobar división entre cero
  if (operacion === "dividir" && numero2 === 0) {
    mostrarError("No se puede dividir entre cero.");
    return;
  }

  // 5. Calcular
  const calculo = calcularOperacion(numero1, numero2, operacion);

  // Seguridad extra por si la operación no fuese válida
  if (calculo === null) {
    mostrarError("No se ha podido realizar la operación.");
    return;
  }

  mostrarResultado(calculo);
}

// ===============================
// EVENTOS
// ===============================

btnCalcular.addEventListener("click", procesarCalculo);
btnLimpiar.addEventListener("click", limpiarFormulario);