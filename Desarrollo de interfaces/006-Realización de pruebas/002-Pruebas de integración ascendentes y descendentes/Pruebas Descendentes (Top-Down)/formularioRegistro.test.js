const FormularioRegistro = require("./formularioRegistro");

describe("Funciones auxiliares", () => {
  /*
    TODO: Casos a testear:

    limpiarTexto
    - Debe eliminar espacios al principio y al final.
    - Debe devolver cadena vacía si recibe null.
    - Debe devolver cadena vacía si recibe undefined.
    - Debe convertir a texto otros valores si fuese necesario.

    estaVacio
    - Debe devolver true con cadena vacía.
    - Debe devolver true con cadena formada solo por espacios.
    - Debe devolver false con texto real.

    tieneLongitudMinima
    - Debe devolver true si el texto cumple la longitud mínima.
    - Debe devolver false si el texto no cumple la longitud mínima.
    - Debe tener en cuenta espacios sobrantes al principio y al final.
  */
});

describe("Validación de email", () => {
  /*
    TODO: Casos a testear:

    emailValido
    - Debe devolver true con un email correcto.
    - Debe devolver false si falta la arroba.
    - Debe devolver false si falta el punto final.
    - Debe devolver false si contiene espacios.
    - Debe devolver false si está vacío.
    - Debe devolver false si la arroba está al principio.
    - Debe devolver false si el punto está justo después de la arroba.
    - Debe devolver false si el punto está al final.
  */
});

describe("Validación de edad", () => {
  /*
    TODO: Casos a testear:

    edadValida
    - Debe devolver true con 18.
    - Debe devolver true con 120.
    - Debe devolver false con 17.
    - Debe devolver false con 121.
    - Debe devolver false con un número decimal.
    - Debe devolver false con texto.
    - Debe devolver false con cadena vacía.
    - Debe devolver false con espacios.
  */
});

describe("Validación de contraseña", () => {
  /*
    TODO: Casos a testear:

    passwordValida
    - Debe devolver true con 6 caracteres.
    - Debe devolver true con más de 6 caracteres.
    - Debe devolver false con menos de 6 caracteres.
    - Debe devolver false con cadena vacía.

    passwordsCoinciden
    - Debe devolver true cuando ambas contraseñas son iguales.
    - Debe devolver false cuando son distintas.
    - Debe devolver true si ambas están vacías y solo se compara igualdad literal.
    - Debe devolver false si una está rellena y la otra vacía.
  */
});

describe("Validación de términos", () => {
  /*
    TODO: Casos a testear:

    terminosAceptados
    - Debe devolver true si el checkbox está marcado.
    - Debe devolver false si el checkbox no está marcado.
  */
});

describe("Validaciones por campo", () => {
  /*
    TODO: Casos a testear:

    validarNombre
    - Debe devolver error si está vacío.
    - Debe devolver error si tiene menos de 3 caracteres.
    - Debe devolver cadena vacía si es correcto.
    - Debe ignorar espacios sobrantes al principio y al final.

    validarEmail
    - Debe devolver error si está vacío.
    - Debe devolver error si no tiene formato válido.
    - Debe devolver cadena vacía si es correcto.

    validarEdad
    - Debe devolver error si está vacía.
    - Debe devolver error si no es válida.
    - Debe devolver cadena vacía si es correcta.

    validarPassword
    - Debe devolver error si está vacía.
    - Debe devolver error si tiene menos de 6 caracteres.
    - Debe devolver cadena vacía si es correcta.

    validarRepetirPassword
    - Debe devolver error si está vacía.
    - Debe devolver error si no coincide con la contraseña original.
    - Debe devolver cadena vacía si coincide.

    validarTerminos
    - Debe devolver error si no se aceptan.
    - Debe devolver cadena vacía si se aceptan.
  */
});

describe("Validación global del formulario", () => {
  /*
    TODO: Casos a testear:

    validarRegistro
    - Debe devolver esValido true si todos los datos son correctos.
    - Debe devolver todos los errores vacíos si el formulario es válido.
    - Debe devolver los datos limpios sin espacios sobrantes.
    - Debe devolver esValido false si varios campos son incorrectos.
    - Debe devolver error de nombre obligatorio si el nombre está vacío.
    - Debe devolver error de nombre corto si tiene menos de 3 caracteres.
    - Debe devolver error de email obligatorio si el email está vacío.
    - Debe devolver error de email inválido si no tiene formato correcto.
    - Debe devolver error de edad obligatoria si la edad está vacía.
    - Debe devolver error de edad inválida si es menor de 18.
    - Debe devolver error de edad inválida si es mayor de 120.
    - Debe devolver error de edad inválida si no es entera.
    - Debe devolver error de contraseña obligatoria si está vacía.
    - Debe devolver error de contraseña corta si tiene menos de 6 caracteres.
    - Debe devolver error de repetir contraseña obligatoria si está vacía.
    - Debe devolver error si las contraseñas no coinciden.
    - Debe devolver error si no se aceptan los términos.
    - Debe permitir un formulario correcto aunque los campos de texto tengan espacios alrededor.
  */
});