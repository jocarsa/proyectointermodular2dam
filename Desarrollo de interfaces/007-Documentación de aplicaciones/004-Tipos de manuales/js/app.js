/**
 * Lógica de interfaz de la calculadora de precios.
 *
 * Este archivo se encarga de leer los datos del formulario, validarlos,
 * llamar a las funciones de cálculo y mostrar los resultados en pantalla.
 *
 * @file app.js
 * @author Pablo José Peral Tamarit
 */

const formulario = document.getElementById("formulario-precios");

const inputPrecio = document.getElementById("precio");
const inputCantidad = document.getElementById("cantidad");
const inputIVA = document.getElementById("iva");
const inputDescuento = document.getElementById("descuento");

const resultadoSubtotal = document.getElementById("resultado-subtotal");
const resultadoDescuento = document.getElementById("resultado-descuento");
const resultadoIVA = document.getElementById("resultado-iva");
const resultadoTotal = document.getElementById("resultado-total");

const mensajeError = document.getElementById("mensaje-error");

/**
 * Convierte el valor de un campo de formulario a número.
 *
 * Los valores recogidos desde un input llegan como texto. Esta función
 * transforma ese texto en un valor numérico para poder hacer cálculos.
 *
 * @param {HTMLInputElement} input Campo de formulario del que se obtiene el valor.
 * @returns {number} Valor numérico introducido por el usuario.
 *
 * @example
 * obtenerNumero(inputPrecio);
 * // Devuelve el contenido del input convertido a number
 */
function obtenerNumero(input) {
    return Number(input.value);
}

/**
 * Comprueba si los datos introducidos en el formulario son válidos.
 *
 * Un formulario se considera válido cuando:
 *
 * - el precio es mayor o igual que 0;
 * - la cantidad es mayor que 0;
 * - el IVA es mayor o igual que 0;
 * - el descuento es mayor o igual que 0;
 * - todos los valores son numéricos.
 *
 * @param {number} precio Precio unitario introducido por el usuario.
 * @param {number} cantidad Cantidad de unidades introducida.
 * @param {number} iva Porcentaje de IVA introducido.
 * @param {number} descuento Porcentaje de descuento introducido.
 * @returns {boolean} true si los datos son válidos, false en caso contrario.
 *
 * @example
 * validarDatos(100, 2, 21, 10);
 * // Devuelve true
 *
 * @example
 * validarDatos(100, 0, 21, 10);
 * // Devuelve false
 */
function validarDatos(precio, cantidad, iva, descuento) {
    if (Number.isNaN(precio) || Number.isNaN(cantidad) || Number.isNaN(iva) || Number.isNaN(descuento)) {
        return false;
    }

    if (precio < 0 || cantidad <= 0 || iva < 0 || descuento < 0) {
        return false;
    }

    return true;
}

/**
 * Formatea una cantidad numérica como importe en euros.
 *
 * La función recibe un número y devuelve un texto con dos decimales
 * y el símbolo del euro.
 *
 * @param {number} cantidad Cantidad numérica que se desea formatear.
 * @returns {string} Cantidad formateada como texto en euros.
 *
 * @example
 * formatearEuros(25);
 * // Devuelve "25.00 €"
 */
function formatearEuros(cantidad) {
    return cantidad.toFixed(2) + " €";
}

/**
 * Muestra los resultados de la operación en la interfaz.
 *
 * Esta función recibe los importes ya calculados y actualiza el contenido
 * de los elementos HTML donde se muestran los resultados.
 *
 * @param {number} subtotal Subtotal de la compra.
 * @param {number} descuento Importe descontado.
 * @param {number} iva Importe correspondiente al IVA.
 * @param {number} total Total final de la compra.
 * @returns {void}
 */
function mostrarResultados(subtotal, descuento, iva, total) {
    resultadoSubtotal.textContent = formatearEuros(subtotal);
    resultadoDescuento.textContent = formatearEuros(descuento);
    resultadoIVA.textContent = formatearEuros(iva);
    resultadoTotal.textContent = formatearEuros(total);
}

/**
 * Limpia los resultados mostrados en pantalla.
 *
 * Esta función se utiliza cuando los datos introducidos no son válidos
 * y no tiene sentido mantener resultados anteriores.
 *
 * @returns {void}
 */
function limpiarResultados() {
    resultadoSubtotal.textContent = "-";
    resultadoDescuento.textContent = "-";
    resultadoIVA.textContent = "-";
    resultadoTotal.textContent = "-";
}

/**
 * Muestra un mensaje de error en la interfaz.
 *
 * @param {string} mensaje Texto del error que se desea mostrar.
 * @returns {void}
 */
function mostrarError(mensaje) {
    mensajeError.textContent = mensaje;
}

/**
 * Limpia el mensaje de error de la interfaz.
 *
 * @returns {void}
 */
function limpiarError() {
    mensajeError.textContent = "";
}

/**
 * Gestiona el envío del formulario principal.
 *
 * Esta función realiza el flujo completo de la aplicación:
 *
 * 1. Cancela el envío tradicional del formulario.
 * 2. Lee los valores introducidos por el usuario.
 * 3. Valida los datos.
 * 4. Calcula el subtotal.
 * 5. Calcula el descuento.
 * 6. Calcula el IVA.
 * 7. Calcula el total final.
 * 8. Muestra los resultados en pantalla.
 *
 * @param {SubmitEvent} evento Evento producido al enviar el formulario.
 * @returns {void}
 */
function gestionarFormulario(evento) {
    evento.preventDefault();

    limpiarError();

    const precio = obtenerNumero(inputPrecio);
    const cantidad = obtenerNumero(inputCantidad);
    const porcentajeIVA = obtenerNumero(inputIVA);
    const porcentajeDescuento = obtenerNumero(inputDescuento);

    const datosValidos = validarDatos(precio, cantidad, porcentajeIVA, porcentajeDescuento);

    if (!datosValidos) {
        limpiarResultados();
        mostrarError("Introduce valores numéricos válidos. La cantidad debe ser mayor que cero.");
        return;
    }

    const subtotal = calcularSubtotal(precio, cantidad);
    const descuento = calcularDescuento(subtotal, porcentajeDescuento);
    const baseConDescuento = subtotal - descuento;
    const iva = calcularIVA(baseConDescuento, porcentajeIVA);
    const total = calcularTotal(subtotal, descuento, iva);

    mostrarResultados(subtotal, descuento, iva, total);
}

formulario.addEventListener("submit", gestionarFormulario);