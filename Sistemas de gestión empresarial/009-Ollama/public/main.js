// public/main.js

const chatWindow = document.getElementById('chat-window');
const form = document.getElementById('chat-form');
const userInput = document.getElementById('user-input');

// Historial local de mensajes: { role, content }
const history = [];

// Renderizado de mensajes
function addMessage(role, content) {
  const div = document.createElement('div');
  div.classList.add('message', role);
  div.textContent = content;
  chatWindow.appendChild(div);
  chatWindow.scrollTop = chatWindow.scrollHeight;
}

// Mensaje inicial opcional
addMessage('system', 'Bienvenido a microchatgpt (Ollama local). Escribe tu pregunta.');

// Manejo de envío de formulario
form.addEventListener('submit', async (e) => {
  e.preventDefault();
  const text = userInput.value.trim();
  if (!text) return;

  // Añadir mensaje del usuario al chat e historia
  addMessage('user', text);
  history.push({ role: 'user', content: text });
  userInput.value = '';

  // Desactivar botón mientras se espera respuesta
  const submitButton = form.querySelector('button[type="submit"]');
  submitButton.disabled = true;
  submitButton.textContent = 'Pensando...';

  try {
    const response = await fetch('/api/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        message: text,
        history
      })
    });

    if (!response.ok) {
      const errorText = await response.text();
      addMessage('assistant', 'Error en el servidor: ' + errorText);
      return;
    }

    const data = await response.json();
    const reply = data.reply || '(sin respuesta)';

    addMessage('assistant', reply);
    history.push({ role: 'assistant', content: reply });
  } catch (err) {
    console.error(err);
    addMessage('assistant', 'Error de red: ' + String(err));
  } finally {
    submitButton.disabled = false;
    submitButton.textContent = 'Enviar';
    userInput.focus();
  }
});
