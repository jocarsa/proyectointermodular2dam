// Para poder ejecutar el código hay que hacer:
// (instalación dentro de la carpeta)
// npm.cmd init -y
// npm.cmd install --save-dev jest
// (ejecución de los test)
// npm.cmd test

const Calculadora = require("./calculadora");

describe("Funciones básicas de la calculadora", () => {
  test("sumar devuelve la suma correcta", () => {
    expect(Calculadora.sumar(2, 3)).toBe(5);
  });

  test("restar devuelve la resta correcta", () => {
    expect(Calculadora.restar(10, 4)).toBe(6);
  });

  test("multiplicar devuelve la multiplicación correcta", () => {
    expect(Calculadora.multiplicar(3, 5)).toBe(15);
  });

  test("dividir devuelve la división correcta", () => {
    expect(Calculadora.dividir(8, 2)).toBe(4);
  });

  test("dividir entre cero lanza un error", () => {
    expect(() => Calculadora.dividir(8, 0)).toThrow("No se puede dividir entre cero.");
  });
});

describe("Validaciones auxiliares", () => {
  test("estaVacio detecta cadena vacía", () => {
    expect(Calculadora.estaVacio("")).toBe(true);
  });

  test("estaVacio detecta cadena con espacios", () => {
    expect(Calculadora.estaVacio("   ")).toBe(true);
  });

  test("estaVacio detecta null", () => {
    expect(Calculadora.estaVacio(null)).toBe(true);
  });

  test("estaVacio devuelve false con texto real", () => {
    expect(Calculadora.estaVacio("12")).toBe(false);
  });

  test("esNumeroValido devuelve true con enteros", () => {
    expect(Calculadora.esNumeroValido("25")).toBe(true);
  });

  test("esNumeroValido devuelve true con decimales", () => {
    expect(Calculadora.esNumeroValido("2.5")).toBe(true);
  });

  test("esNumeroValido devuelve true con negativos", () => {
    expect(Calculadora.esNumeroValido("-4")).toBe(true);
  });

  test("esNumeroValido devuelve false con texto", () => {
    expect(Calculadora.esNumeroValido("hola")).toBe(false);
  });

  test("esNumeroValido devuelve false con vacío", () => {
    expect(Calculadora.esNumeroValido("")).toBe(false);
  });
});

describe("Función calcular", () => {
  test("calcula correctamente una suma", () => {
    const resultado = Calculadora.calcular("2", "3", "sumar");

    expect(resultado.numero1).toBe(2);
    expect(resultado.numero2).toBe(3);
    expect(resultado.operacion).toBe("sumar");
    expect(resultado.simbolo).toBe("+");
    expect(resultado.resultado).toBe(5);
    expect(resultado.resultadoFormateado).toBe("5");
  });

  test("calcula correctamente una resta", () => {
    const resultado = Calculadora.calcular("10", "6", "restar");

    expect(resultado.resultado).toBe(4);
    expect(resultado.resultadoFormateado).toBe("4");
  });

  test("calcula correctamente una multiplicación", () => {
    const resultado = Calculadora.calcular("4", "5", "multiplicar");

    expect(resultado.resultado).toBe(20);
  });

  test("calcula correctamente una división", () => {
    const resultado = Calculadora.calcular("7.5", "2.5", "dividir");

    expect(resultado.resultado).toBe(3);
    expect(resultado.resultadoFormateado).toBe("3");
  });

  test("lanza error si falta el primer número", () => {
    expect(() => Calculadora.calcular("", "2", "sumar"))
      .toThrow("Debes introducir los dos números.");
  });

  test("lanza error si falta el segundo número", () => {
    expect(() => Calculadora.calcular("2", "", "sumar"))
      .toThrow("Debes introducir los dos números.");
  });

  test("lanza error si el primer valor no es numérico", () => {
    expect(() => Calculadora.calcular("hola", "2", "sumar"))
      .toThrow("Los valores introducidos deben ser numéricos.");
  });

  test("lanza error si el segundo valor no es numérico", () => {
    expect(() => Calculadora.calcular("2", "adios", "sumar"))
      .toThrow("Los valores introducidos deben ser numéricos.");
  });

  test("lanza error si no se selecciona operación", () => {
    expect(() => Calculadora.calcular("2", "3", ""))
      .toThrow("Debes seleccionar una operación.");
  });

  test("lanza error si la operación no es válida", () => {
    expect(() => Calculadora.calcular("2", "3", "potencia"))
      .toThrow("La operación seleccionada no es válida.");
  });

  test("lanza error al dividir entre cero desde calcular", () => {
    expect(() => Calculadora.calcular("8", "0", "dividir"))
      .toThrow("No se puede dividir entre cero.");
  });
});

describe("Formato del resultado", () => {
  test("formatea enteros sin decimales", () => {
    expect(Calculadora.formatearResultado(8)).toBe("8");
  });

  test("formatea decimales a un máximo de 4 cifras", () => {
    expect(Calculadora.formatearResultado(3.333333)).toBe("3.3333");
  });

  test("elimina ceros decimales innecesarios", () => {
    expect(Calculadora.formatearResultado(4.5)).toBe("4.5");
  });
});