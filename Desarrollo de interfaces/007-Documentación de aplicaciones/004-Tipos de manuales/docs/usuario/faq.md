# Preguntas frecuentes

Esta sección recoge dudas habituales sobre el uso de la calculadora de precios.

---

## ¿La aplicación guarda datos?

No.

La aplicación solo realiza el cálculo y muestra el resultado en pantalla.

No utiliza base de datos ni `localStorage`.

---

## ¿Por qué aparece un mensaje de error?

Aparece cuando alguno de los datos introducidos no es válido.

Por ejemplo:

- campos vacíos;
- cantidad igual a cero;
- valores negativos;
- datos no numéricos.

---

## ¿La cantidad puede ser cero?

No.

La cantidad representa el número de unidades compradas, por lo que debe ser mayor que cero.

Si se introduce una cantidad igual a cero, la aplicación mostrará un mensaje de error.

---

## ¿El IVA se calcula antes o después del descuento?

En esta aplicación, primero se aplica el descuento y después se calcula el IVA sobre la base con descuento.

Ejemplo:

```txt
Subtotal: 200 €
Descuento: 20 €
Base con descuento: 180 €
IVA: 21% de 180 €
```

---

## ¿Qué significa subtotal?

El subtotal es el resultado de multiplicar el precio unitario por la cantidad.

Ejemplo:

```txt
Precio unitario: 100 €
Cantidad: 2

Subtotal: 200 €
```

---

## ¿Qué significa descuento?

El descuento es la cantidad que se resta al subtotal.

Ejemplo:

```txt
Subtotal: 200 €
Descuento: 10%

Importe descontado: 20 €
```

---

## ¿Qué significa total final?

El total final es la cantidad definitiva que debe pagar el usuario.

Se obtiene aplicando el descuento y sumando después el IVA correspondiente.

---

## Volver al manual de usuario

[Volver al manual de usuario](README.md)