// =====================================================
// ESTADO INICIAL DE LA APLICACIÓN
// =====================================================

const datosIniciales = {
  pregunta: "¿Qué tecnología te gustaría practicar más?",
  opciones: [
    {
      id: "html",
      texto: "HTML y CSS",
      votos: 0
    },
    {
      id: "javascript",
      texto: "JavaScript",
      votos: 0
    },
    {
      id: "testing",
      texto: "Testing con Jest",
      votos: 0
    },
    {
      id: "documentacion",
      texto: "Documentación con JSDoc",
      votos: 0
    }
  ]
};

let estadoEncuesta = crearEstadoInicial();


// =====================================================
// FUNCIONES DE LÓGICA
// =====================================================

function crearEstadoInicial() {
  const opcionesIniciales = [];

  for (let i = 0; i < datosIniciales.opciones.length; i++) {
    const opcion = datosIniciales.opciones[i];

    opcionesIniciales.push({
      id: opcion.id,
      texto: opcion.texto,
      votos: 0
    });
  }

  return {
    pregunta: datosIniciales.pregunta,
    opciones: opcionesIniciales
  };
}

function calcularTotalVotos(encuesta) {
  let total = 0;

  for (let i = 0; i < encuesta.opciones.length; i++) {
    total += encuesta.opciones[i].votos;
  }

  return total;
}

function calcularPorcentaje(votos, total) {
  if (total === 0) {
    return 0;
  }

  return Math.round((votos / total) * 100);
}

function buscarOpcionPorId(encuesta, idOpcion) {
  for (let i = 0; i < encuesta.opciones.length; i++) {
    if (encuesta.opciones[i].id === idOpcion) {
      return encuesta.opciones[i];
    }
  }

  return null;
}

function registrarVoto(encuesta, idOpcion) {
  if (!idOpcion) {
    return {
      correcto: false,
      mensaje: "Debes seleccionar una opción antes de votar."
    };
  }

  const opcion = buscarOpcionPorId(encuesta, idOpcion);

  if (opcion === null) {
    return {
      correcto: false,
      mensaje: "La opción seleccionada no existe."
    };
  }

  opcion.votos++;

  return {
    correcto: true,
    mensaje: "Voto registrado correctamente."
  };
}

function obtenerResultados(encuesta) {
  const total = calcularTotalVotos(encuesta);
  const resultados = [];

  for (let i = 0; i < encuesta.opciones.length; i++) {
    const opcion = encuesta.opciones[i];

    resultados.push({
      id: opcion.id,
      texto: opcion.texto,
      votos: opcion.votos,
      porcentaje: calcularPorcentaje(opcion.votos, total)
    });
  }

  return resultados;
}

function reiniciarEncuesta() {
  estadoEncuesta = crearEstadoInicial();
}


// =====================================================
// FUNCIONES DE INTERFAZ
// =====================================================

function pintarPregunta() {
  const elementoPregunta = document.getElementById("pregunta");
  elementoPregunta.textContent = estadoEncuesta.pregunta;
}

function pintarOpciones() {
  const contenedorOpciones = document.getElementById("contenedorOpciones");
  contenedorOpciones.innerHTML = "";

  for (let i = 0; i < estadoEncuesta.opciones.length; i++) {
    const opcion = estadoEncuesta.opciones[i];

    const label = document.createElement("label");
    label.className = "opcion";

    const input = document.createElement("input");
    input.type = "radio";
    input.name = "opcionEncuesta";
    input.value = opcion.id;

    label.appendChild(input);
    label.appendChild(document.createTextNode(" " + opcion.texto));

    contenedorOpciones.appendChild(label);
  }
}

function pintarResultados() {
  const contenedorResultados = document.getElementById("contenedorResultados");
  const totalVotos = document.getElementById("totalVotos");

  contenedorResultados.innerHTML = "";

  const resultados = obtenerResultados(estadoEncuesta);
  const total = calcularTotalVotos(estadoEncuesta);

  for (let i = 0; i < resultados.length; i++) {
    const resultado = resultados[i];

    const divResultado = document.createElement("div");
    divResultado.className = "resultado";

    const texto = document.createElement("p");
    texto.textContent = resultado.texto + ": " + resultado.votos + " votos (" + resultado.porcentaje + "%)";

    const barraContenedor = document.createElement("div");
    barraContenedor.className = "barra-contenedor";

    const barra = document.createElement("div");
    barra.className = "barra";
    barra.style.width = resultado.porcentaje + "%";

    barraContenedor.appendChild(barra);

    divResultado.appendChild(texto);
    divResultado.appendChild(barraContenedor);

    contenedorResultados.appendChild(divResultado);
  }

  totalVotos.textContent = "Total de votos: " + total;
}

function mostrarMensaje(texto, tipo) {
  const mensaje = document.getElementById("mensaje");

  mensaje.textContent = texto;
  mensaje.className = "mensaje";

  if (tipo === "error") {
    mensaje.classList.add("error");
  }

  if (tipo === "exito") {
    mensaje.classList.add("exito");
  }
}

function obtenerOpcionSeleccionada() {
  const opcionSeleccionada = document.querySelector("input[name='opcionEncuesta']:checked");

  if (opcionSeleccionada === null) {
    return null;
  }

  return opcionSeleccionada.value;
}

function limpiarSeleccion() {
  const opciones = document.querySelectorAll("input[name='opcionEncuesta']");

  for (let i = 0; i < opciones.length; i++) {
    opciones[i].checked = false;
  }
}

function actualizarInterfaz() {
  pintarPregunta();
  pintarOpciones();
  pintarResultados();
}


// =====================================================
// EVENTOS
// =====================================================

const formulario = document.getElementById("formEncuesta");
const botonReiniciar = document.getElementById("btnReiniciar");

formulario.addEventListener("submit", function(evento) {
    evento.preventDefault();

    const idOpcion = obtenerOpcionSeleccionada();
    const resultado = registrarVoto(estadoEncuesta, idOpcion);

    if (resultado.correcto) {
        mostrarMensaje(resultado.mensaje, "exito");
        limpiarSeleccion();
        pintarResultados();
    } else {
        mostrarMensaje(resultado.mensaje, "error");
    }
    });

    botonReiniciar.addEventListener("click", function() {
    reiniciarEncuesta();
    actualizarInterfaz();
    mostrarMensaje("La encuesta se ha reiniciado.", "exito");
});


// =====================================================
// INICIO DE LA APLICACIÓN
// =====================================================

function iniciarAplicacion() {
  actualizarInterfaz();
  configurarEventos();
}

document.addEventListener("DOMContentLoaded", iniciarAplicacion);


// =====================================================
// EXPORTACIÓN PARA PRUEBAS
// =====================================================

globalThis.MiniEncuesta = {
  crearEstadoInicial,
  calcularTotalVotos,
  calcularPorcentaje,
  buscarOpcionPorId,
  registrarVoto,
  obtenerResultados,
  reiniciarEncuesta
};