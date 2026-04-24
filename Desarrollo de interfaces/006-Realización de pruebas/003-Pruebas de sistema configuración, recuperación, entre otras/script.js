"use strict";

const CLAVE_STORAGE = "dam2_app_tareas";

const formTarea = document.getElementById("formTarea");
const inputTituloTarea = document.getElementById("tituloTarea");
const errorFormulario = document.getElementById("errorFormulario");

const btnCargarEjemplo = document.getElementById("btnCargarEjemplo");
const btnCargarMuchas = document.getElementById("btnCargarMuchas");
const btnCorromperStorage = document.getElementById("btnCorromperStorage");
const btnVaciarStorage = document.getElementById("btnVaciarStorage");
const btnBorrarTodas = document.getElementById("btnBorrarTodas");

const mensajeSistema = document.getElementById("mensajeSistema");
const totalTareas = document.getElementById("totalTareas");
const tareasCompletadas = document.getElementById("tareasCompletadas");
const tareasPendientes = document.getElementById("tareasPendientes");
const estadoStorage = document.getElementById("estadoStorage");

const listaTareas = document.getElementById("listaTareas");
const mensajeListaVacia = document.getElementById("mensajeListaVacia");

let tareas = [];
let siguienteId = 1;

function mostrarMensajeSistema(texto, tipo) {
  mensajeSistema.textContent = texto;
  mensajeSistema.className = "mensaje-sistema " + tipo;
}

function limpiarErrorFormulario() {
  errorFormulario.textContent = "";
}

function mostrarErrorFormulario(texto) {
  errorFormulario.textContent = texto;
}

function actualizarEstadoStorage(texto) {
  estadoStorage.textContent = texto;
}

function crearTarea(id, titulo, completada) {
  return {
    id: id,
    titulo: titulo,
    completada: completada
  };
}

function esTareaValida(objeto) {
  return (
    typeof objeto === "object" &&
    objeto !== null &&
    typeof objeto.id === "number" &&
    typeof objeto.titulo === "string" &&
    objeto.titulo.trim() !== "" &&
    typeof objeto.completada === "boolean"
  );
}

function guardarTareas() {
  if (tareas.length === 0) {
    localStorage.removeItem(CLAVE_STORAGE);
    actualizarEstadoStorage("Sin clave guardada");
    return;
  }

  localStorage.setItem(CLAVE_STORAGE, JSON.stringify(tareas));
  actualizarEstadoStorage("Datos válidos guardados");
}

function recalcularSiguienteId() {
  if (tareas.length === 0) {
    siguienteId = 1;
    return;
  }

  const maxId = Math.max(...tareas.map(tarea => tarea.id));
  siguienteId = maxId + 1;
}

function cargarTareasDesdeStorage() {
  const contenido = localStorage.getItem(CLAVE_STORAGE);

  if (contenido === null) {
    tareas = [];
    siguienteId = 1;
    actualizarEstadoStorage("Sin clave guardada");
    mostrarMensajeSistema("La aplicación ha arrancado sin tareas guardadas.", "info");
    return;
  }

  if (contenido.trim() === "") {
    tareas = [];
    siguienteId = 1;
    localStorage.removeItem(CLAVE_STORAGE);
    actualizarEstadoStorage("Clave vacía detectada");
    mostrarMensajeSistema("Se ha detectado localStorage vacío. La aplicación se ha recuperado dejando la lista vacía.", "warning");
    return;
  }

  try {
    const datos = JSON.parse(contenido);

    if (!Array.isArray(datos)) {
      tareas = [];
      siguienteId = 1;
      guardarTareas();
      mostrarMensajeSistema("Los datos guardados no tenían el formato esperado. La aplicación se ha recuperado reiniciando el almacenamiento.", "warning");
      return;
    }

    const tareasValidas = datos.filter(esTareaValida);

    if (tareasValidas.length !== datos.length) {
      tareas = tareasValidas;
      recalcularSiguienteId();
      guardarTareas();
      mostrarMensajeSistema("Se han detectado tareas inválidas en localStorage. La aplicación se ha recuperado conservando solo las tareas válidas.", "warning");
      return;
    }

    tareas = tareasValidas;
    recalcularSiguienteId();
    actualizarEstadoStorage("Datos válidos cargados");
    mostrarMensajeSistema("Las tareas guardadas se han cargado correctamente.", "ok");
  } catch (error) {
    tareas = [];
    siguienteId = 1;
    localStorage.removeItem(CLAVE_STORAGE);
    actualizarEstadoStorage("Datos corruptos detectados");
    mostrarMensajeSistema("localStorage estaba corrupto. La aplicación se ha recuperado reiniciando los datos.", "error");
  }
}

function actualizarEstadisticas() {
  const total = tareas.length;
  const completadas = tareas.filter(tarea => tarea.completada).length;
  const pendientes = total - completadas;

  totalTareas.textContent = total;
  tareasCompletadas.textContent = completadas;
  tareasPendientes.textContent = pendientes;
}

function crearElementoTarea(tarea) {
  const li = document.createElement("li");
  li.className = "tarea";
  if (tarea.completada) {
    li.classList.add("completada");
  }

  const bloqueIzquierda = document.createElement("div");
  bloqueIzquierda.className = "tarea-izquierda";

  const checkbox = document.createElement("input");
  checkbox.type = "checkbox";
  checkbox.checked = tarea.completada;
  checkbox.addEventListener("change", function () {
    cambiarEstadoTarea(tarea.id, checkbox.checked);
  });

  const texto = document.createElement("span");
  texto.className = "tarea-texto";
  texto.textContent = tarea.titulo;

  bloqueIzquierda.appendChild(checkbox);
  bloqueIzquierda.appendChild(texto);

  const btnEliminar = document.createElement("button");
  btnEliminar.type = "button";
  btnEliminar.textContent = "Eliminar";
  btnEliminar.className = "btn-eliminar";
  btnEliminar.addEventListener("click", function () {
    eliminarTarea(tarea.id);
  });

  li.appendChild(bloqueIzquierda);
  li.appendChild(btnEliminar);

  return li;
}

function renderizarTareas() {
  listaTareas.innerHTML = "";

  if (tareas.length === 0) {
    mensajeListaVacia.classList.remove("oculto");
  } else {
    mensajeListaVacia.classList.add("oculto");
  }

  tareas.forEach(function (tarea) {
    const elemento = crearElementoTarea(tarea);
    listaTareas.appendChild(elemento);
  });

  actualizarEstadisticas();
}

function anadirTarea(titulo) {
  const tituloLimpio = titulo.trim();

  if (tituloLimpio === "") {
    mostrarErrorFormulario("El título de la tarea no puede estar vacío.");
    return;
  }

  const nuevaTarea = crearTarea(siguienteId, tituloLimpio, false);
  siguienteId++;
  tareas.push(nuevaTarea);
  guardarTareas();
  renderizarTareas();
  mostrarMensajeSistema("La tarea se ha añadido correctamente.", "ok");
}

function cambiarEstadoTarea(id, completada) {
  const tarea = tareas.find(t => t.id === id);

  if (!tarea) {
    mostrarMensajeSistema("No se ha encontrado la tarea indicada.", "error");
    return;
  }

  tarea.completada = completada;
  guardarTareas();
  renderizarTareas();
  mostrarMensajeSistema("El estado de la tarea se ha actualizado.", "ok");
}

function eliminarTarea(id) {
  tareas = tareas.filter(tarea => tarea.id !== id);
  recalcularSiguienteId();
  guardarTareas();
  renderizarTareas();
  mostrarMensajeSistema("La tarea se ha eliminado correctamente.", "ok");
}

function borrarTodasLasTareas() {
  tareas = [];
  siguienteId = 1;
  guardarTareas();
  renderizarTareas();
  mostrarMensajeSistema("Se han borrado todas las tareas. La aplicación ha quedado en estado vacío.", "warning");
}

function cargarTareasEjemplo() {
  tareas = [
    crearTarea(1, "Preparar examen de interfaces", false),
    crearTarea(2, "Corregir actividad de DOM", true),
    crearTarea(3, "Explicar pruebas de sistema", false),
    crearTarea(4, "Revisar ejercicios de Jest", false),
    crearTarea(5, "Subir materiales al aula virtual", true)
  ];

  recalcularSiguienteId();
  guardarTareas();
  renderizarTareas();
  mostrarMensajeSistema("Se han cargado 5 tareas de ejemplo correctamente.", "ok");
}

function cargarMuchasTareas() {
  tareas = [];

  for (let i = 1; i <= 200; i++) {
    tareas.push(crearTarea(i, "Tarea de prueba número " + i, i % 3 === 0));
  }

  recalcularSiguienteId();
  guardarTareas();
  renderizarTareas();
  mostrarMensajeSistema("Se han cargado 200 tareas para probar el sistema con mucha información.", "warning");
}

function corromperLocalStorage() {
  localStorage.setItem(CLAVE_STORAGE, "{esto no es un JSON válido");
  actualizarEstadoStorage("Datos corruptos introducidos manualmente");
  mostrarMensajeSistema("Se ha corrompido localStorage a propósito. Recarga la página para probar la recuperación.", "error");
}

function vaciarLocalStorage() {
  localStorage.setItem(CLAVE_STORAGE, "");
  actualizarEstadoStorage("Clave vacía introducida manualmente");
  mostrarMensajeSistema("Se ha dejado la clave de localStorage vacía. Recarga la página para probar la recuperación.", "warning");
}

formTarea.addEventListener("submit", function (evento) {
  evento.preventDefault();
  limpiarErrorFormulario();
  anadirTarea(inputTituloTarea.value);
  inputTituloTarea.value = "";
  inputTituloTarea.focus();
});

btnCargarEjemplo.addEventListener("click", cargarTareasEjemplo);
btnCargarMuchas.addEventListener("click", cargarMuchasTareas);
btnCorromperStorage.addEventListener("click", corromperLocalStorage);
btnVaciarStorage.addEventListener("click", vaciarLocalStorage);
btnBorrarTodas.addEventListener("click", borrarTodasLasTareas);

cargarTareasDesdeStorage();
renderizarTareas();