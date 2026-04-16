(function (global) {
  "use strict";

  function estaVacio(valor) {
    return valor === null || valor === undefined || String(valor).trim() === "";
  }

  function esNumeroValido(valor) {
    if (estaVacio(valor)) {
      return false;
    }

    return Number.isFinite(Number(valor));
  }

  function convertirANumero(valor) {
    return Number(valor);
  }

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
      throw new Error("No se puede dividir entre cero.");
    }

    return a / b;
  }

  function obtenerSimbolo(operacion) {
    const simbolos = {
      sumar: "+",
      restar: "-",
      multiplicar: "×",
      dividir: "÷"
    };

    return simbolos[operacion] || "?";
  }

  function formatearResultado(valor) {
    if (!Number.isFinite(valor)) {
      return String(valor);
    }

    if (Number.isInteger(valor)) {
      return String(valor);
    }

    return String(Number(valor.toFixed(4)));
  }

  function calcular(valor1, valor2, operacion) {
    if (estaVacio(valor1) || estaVacio(valor2)) {
      throw new Error("Debes introducir los dos números.");
    }

    if (!esNumeroValido(valor1) || !esNumeroValido(valor2)) {
      throw new Error("Los valores introducidos deben ser numéricos.");
    }

    if (estaVacio(operacion)) {
      throw new Error("Debes seleccionar una operación.");
    }

    const numero1 = convertirANumero(valor1);
    const numero2 = convertirANumero(valor2);

    let resultado;

    switch (operacion) {
      case "sumar":
        resultado = sumar(numero1, numero2);
        break;
      case "restar":
        resultado = restar(numero1, numero2);
        break;
      case "multiplicar":
        resultado = multiplicar(numero1, numero2);
        break;
      case "dividir":
        resultado = dividir(numero1, numero2);
        break;
      default:
        throw new Error("La operación seleccionada no es válida.");
    }

    return {
      numero1,
      numero2,
      operacion,
      simbolo: obtenerSimbolo(operacion),
      resultado,
      resultadoFormateado: formatearResultado(resultado)
    };
  }

  const api = {
    estaVacio,
    esNumeroValido,
    convertirANumero,
    sumar,
    restar,
    multiplicar,
    dividir,
    obtenerSimbolo,
    formatearResultado,
    calcular
  };

  if (typeof module !== "undefined" && module.exports) {
    module.exports = api;
  } else {
    global.Calculadora = api;
  }
})(typeof window !== "undefined" ? window : globalThis);