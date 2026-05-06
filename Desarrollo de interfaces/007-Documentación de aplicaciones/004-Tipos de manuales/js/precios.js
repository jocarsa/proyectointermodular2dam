/**
 * Funciones de cálculo de precios.
 *
 * Este archivo contiene funciones relacionadas con el cálculo de importes,
 * descuentos, IVA y total final de una compra.
 *
 * Las funciones de este archivo son funciones puras:
 * reciben datos, calculan un resultado y devuelven ese resultado.
 * No modifican directamente el HTML ni acceden al DOM.
 *
 * @file precios.js
 * @author Pablo José Peral Tamarit
 */

/**
 * Calcula el subtotal de una compra.
 *
 * El subtotal se obtiene multiplicando el precio unitario de un producto
 * por la cantidad de unidades compradas.
 *
 * @param {number} precioUnitario Precio de una unidad del producto.
 * @param {number} cantidad Número de unidades compradas.
 * @returns {number} Subtotal de la compra.
 *
 * @example
 * calcularSubtotal(10, 3);
 * // Devuelve 30
 */
function calcularSubtotal(precioUnitario, cantidad) {
    return precioUnitario * cantidad;
}

/**
 * Calcula el importe correspondiente al IVA.
 *
 * El IVA se calcula aplicando un porcentaje sobre un importe base.
 *
 * En esta aplicación, el importe base será normalmente el subtotal
 * después de haber aplicado el descuento.
 *
 * @param {number} importeBase Cantidad sobre la que se aplica el IVA.
 * @param {number} porcentajeIVA Porcentaje de IVA que se desea aplicar.
 * @returns {number} Importe del IVA calculado.
 *
 * @example
 * calcularIVA(100, 21);
 * // Devuelve 21
 */
function calcularIVA(importeBase, porcentajeIVA) {
    return importeBase * porcentajeIVA / 100;
}

/**
 * Calcula el importe de un descuento.
 *
 * El descuento se calcula aplicando un porcentaje sobre el subtotal
 * de la compra.
 *
 * @param {number} subtotal Importe inicial antes de aplicar el descuento.
 * @param {number} porcentajeDescuento Porcentaje de descuento aplicado.
 * @returns {number} Importe que se descuenta del subtotal.
 *
 * @example
 * calcularDescuento(200, 10);
 * // Devuelve 20
 */
function calcularDescuento(subtotal, porcentajeDescuento) {
    return subtotal * porcentajeDescuento / 100;
}

/**
 * Calcula el total final de una compra.
 *
 * El total final se obtiene restando el descuento al subtotal y sumando
 * después el IVA correspondiente.
 *
 * Fórmula utilizada:
 *
 * total = subtotal - descuento + iva
 *
 * @param {number} subtotal Importe inicial de la compra.
 * @param {number} descuento Importe descontado.
 * @param {number} iva Importe correspondiente al IVA.
 * @returns {number} Total final de la compra.
 *
 * @example
 * calcularTotal(200, 20, 37.8);
 * // Devuelve 217.8
 */
function calcularTotal(subtotal, descuento, iva) {
    return subtotal - descuento + iva;
}