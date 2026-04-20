/**
 * @jest-environment jsdom
 * 
 * Para instalar jsdom (test al DOM)
 * npm.cmd install --save-dev jest-environment-jsdom
 * 
 * Ejecutar los test específicos
 * npm.cmd run test:integracion
 */

const fs = require("fs");
const path = require("path");

function cargarDOM() {
  const rutaHtml = path.join(__dirname, "calculadora.html");
  const html = fs.readFileSync(rutaHtml, "utf8");
  const bodyMatch = html.match(/<body[^>]*>([\s\S]*?)<\/body>/i);

  document.body.innerHTML = bodyMatch ? bodyMatch[1] : html;
}

function cargarAplicacion() {
  jest.resetModules();
  cargarDOM();
  globalThis.Calculadora = require("./calculadora");
  require("./script");
}

function enviarFormulario() {
  const formulario = document.getElementById("formCalculadora");
  formulario.dispatchEvent(
    new Event("submit", { bubbles: true, cancelable: true })
  );
}

describe("Integración Bottom-Up de la calculadora", () => {
  beforeEach(() => {
    cargarAplicacion();
  });

  test("el estado inicial muestra el historial vacío y el resultado sin calcular", () => {
    const resultado = document.getElementById("resultado");
    const historial = document.getElementById("historial");

    expect(resultado.textContent).toBe("Resultado: --");
    expect(historial.querySelector(".historial-vacio").textContent).toBe(
      "Todavía no hay operaciones."
    );
  });

  test("una suma válida integra cálculo, mensaje, resultado e historial", () => {
    document.getElementById("numero1").value = "2";
    document.getElementById("numero2").value = "3";
    document.getElementById("operacion").value = "sumar";

    enviarFormulario();

    const mensaje = document.getElementById("mensaje");
    const resultado = document.getElementById("resultado");
    const historial = document.getElementById("historial");

    expect(mensaje.textContent).toBe("Operación realizada correctamente.");
    expect(mensaje.className).toBe("mensaje correcto");
    expect(resultado.textContent).toBe("Resultado: 5");
    expect(historial.querySelector("li").textContent).toBe("2 + 3 = 5");
  });

  test("una división con decimales integra correctamente la lógica y la interfaz", () => {
    document.getElementById("numero1").value = "7.5";
    document.getElementById("numero2").value = "2.5";
    document.getElementById("operacion").value = "dividir";

    enviarFormulario();

    const resultado = document.getElementById("resultado");
    const historial = document.getElementById("historial");

    expect(resultado.textContent).toBe("Resultado: 3");
    expect(historial.querySelector("li").textContent).toBe("7.5 ÷ 2.5 = 3");
  });

  test("si se divide entre cero, el error del núcleo se refleja en la interfaz", () => {
    document.getElementById("numero1").value = "8";
    document.getElementById("numero2").value = "0";
    document.getElementById("operacion").value = "dividir";

    enviarFormulario();

    const mensaje = document.getElementById("mensaje");
    const resultado = document.getElementById("resultado");
    const historial = document.getElementById("historial");

    expect(mensaje.textContent).toBe("No se puede dividir entre cero.");
    expect(mensaje.className).toBe("mensaje error");
    expect(resultado.textContent).toBe("Resultado: --");
    expect(historial.querySelector(".historial-vacio").textContent).toBe(
      "Todavía no hay operaciones."
    );
  });

  test("si se introduce un dato no numérico, el error llega correctamente a la interfaz", () => {
    document.getElementById("numero1").value = "hola";
    document.getElementById("numero2").value = "2";
    document.getElementById("operacion").value = "sumar";

    enviarFormulario();

    const mensaje = document.getElementById("mensaje");
    const resultado = document.getElementById("resultado");

    expect(mensaje.textContent).toBe(
      "Los valores introducidos deben ser numéricos."
    );
    expect(mensaje.className).toBe("mensaje error");
    expect(resultado.textContent).toBe("Resultado: --");
  });

  test("el botón limpiar reinicia formulario, mensaje y resultado", () => {
    document.getElementById("numero1").value = "4";
    document.getElementById("numero2").value = "5";
    document.getElementById("operacion").value = "multiplicar";

    enviarFormulario();
    document.getElementById("btnLimpiar").click();

    expect(document.getElementById("numero1").value).toBe("");
    expect(document.getElementById("numero2").value).toBe("");
    expect(document.getElementById("operacion").value).toBe("");
    expect(document.getElementById("mensaje").textContent).toBe("");
    expect(document.getElementById("resultado").textContent).toBe("Resultado: --");
  });

  test("el botón vaciar historial elimina las operaciones integradas previamente", () => {
    document.getElementById("numero1").value = "4";
    document.getElementById("numero2").value = "5";
    document.getElementById("operacion").value = "multiplicar";

    enviarFormulario();
    document.getElementById("btnVaciarHistorial").click();

    const historial = document.getElementById("historial");

    expect(historial.querySelector(".historial-vacio").textContent).toBe(
      "Todavía no hay operaciones."
    );
  });
});