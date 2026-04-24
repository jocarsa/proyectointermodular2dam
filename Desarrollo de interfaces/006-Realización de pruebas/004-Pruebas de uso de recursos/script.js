"use strict";

const btn5 = document.getElementById("btn5");
const btn500 = document.getElementById("btn500");
const btn5000 = document.getElementById("btn5000");
const btnVaciar = document.getElementById("btnVaciar");

const totalTareas = document.getElementById("totalTareas");
const tareasCompletadas = document.getElementById("tareasCompletadas");
const tareasPendientes = document.getElementById("tareasPendientes");
const tiempoRender = document.getElementById("tiempoRender");
const mensajeEstado = document.getElementById("mensajeEstado");

const listaTareas = document.getElementById("listaTareas");
const mensajeVacio = document.getElementById("mensajeVacio");

let tareas = [];

function crearTareas(cantidad) {
  const nuevasTareas = [];

  for (let i = 1; i <= cantidad; i++) {
    nuevasTareas.push({
      id: i,
      titulo: "Tarea número " + i,
      completada: i % 3 === 0
    });
  }

  return nuevasTareas;
}

function vaciarListaVisual() {
  listaTareas.innerHTML = "";
}

function actualizarMensajeVacio() {
  if (tareas.length === 0) {
    mensajeVacio.classList.remove("oculto");
  } else {
    mensajeVacio.classList.add("oculto");
  }
}

function actualizarEstadoTexto(ms) {
  if (tareas.length === 0) {
    mensajeEstado.textContent = "No hay tareas cargadas.";
    return;
  }

  if (tareas.length <= 5) {
    mensajeEstado.textContent = "Con pocos datos la aplicación responde con mucha fluidez.";
    return;
  }

  if (tareas.length <= 500) {
    mensajeEstado.textContent = "Con bastantes datos empieza a apreciarse más trabajo del navegador.";
    return;
  }

  mensajeEstado.textContent =
    "Con muchísimos datos el renderizado se vuelve pesado. Aquí ya se aprecia un problema de uso de recursos.";
}

function crearElementoTarea(tarea, posicion, resumenLento) {
  const li = document.createElement("li");
  li.className = "tarea";

  if (tarea.completada) {
    li.classList.add("completada");
  }

  const spanTitulo = document.createElement("span");
  spanTitulo.className = "titulo-tarea";
  spanTitulo.textContent = tarea.titulo + " | resumen interno: " + resumenLento;

  const spanEstado = document.createElement("span");
  spanEstado.className = "estado-tarea";
  spanEstado.textContent = tarea.completada ? "Completada" : "Pendiente";

  li.appendChild(spanTitulo);
  li.appendChild(spanEstado);

  return li;
}

function renderizarTareasIneficiente() {
  const inicio = performance.now();

  vaciarListaVisual();
  actualizarMensajeVacio();

  if (tareas.length === 0) {
    totalTareas.textContent = "0";
    tareasCompletadas.textContent = "0";
    tareasPendientes.textContent = "0";
    tiempoRender.textContent = "--";
    actualizarEstadoTexto(0);
    return;
  }

  for (let i = 0; i < tareas.length; i++) {
    const tareaActual = tareas[i];

    let completadas = 0;
    let resumenLento = 0;

    for (let j = 0; j < tareas.length; j++) {
      if (tareas[j].completada) {
        completadas++;
      }

      resumenLento += tareas[j].titulo.length;
    }

    const pendientes = tareas.length - completadas;

    totalTareas.textContent = String(tareas.length);
    tareasCompletadas.textContent = String(completadas);
    tareasPendientes.textContent = String(pendientes);

    const elemento = crearElementoTarea(tareaActual, i, resumenLento);
    listaTareas.appendChild(elemento);
  }

  const fin = performance.now();
  const ms = (fin - inicio).toFixed(2);

  tiempoRender.textContent = ms + " ms";
  actualizarEstadoTexto(ms);

  console.log("Tiempo de renderizado:", ms + " ms");
}

function cargarEscenario(cantidad) {
  tareas = crearTareas(cantidad);
  renderizarTareasIneficiente();
}

function vaciarEscenario() {
  tareas = [];
  renderizarTareasIneficiente();
}

btn5.addEventListener("click", function () {
  cargarEscenario(5);
});

btn500.addEventListener("click", function () {
  cargarEscenario(500);
});

btn5000.addEventListener("click", function () {
  cargarEscenario(5000);
});

btnVaciar.addEventListener("click", function () {
  vaciarEscenario();
});

renderizarTareasIneficiente();