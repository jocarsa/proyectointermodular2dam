// server.js
const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');

dotenv.config();

const app = express();

// Configuración
const PORT = process.env.PORT || 3000;
const OLLAMA_BASE_URL = process.env.OLLAMA_BASE_URL || 'http://localhost:11434';
const OLLAMA_MODEL = process.env.OLLAMA_MODEL || 'llama3';

// Middlewares
app.use(cors());
app.use(express.json());
app.use(express.static('public'));

// Ruta de prueba
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', model: OLLAMA_MODEL });
});

// Ruta de chat
app.post('/api/chat', async (req, res) => {
  try {
    const { message, history } = req.body;

    if (!message || typeof message !== 'string') {
      return res.status(400).json({ error: 'message requerido' });
    }

    // history es un array de objetos { role: 'user'|'assistant'|'system', content: '...' }
    const safeHistory = Array.isArray(history) ? history : [];

    const messages = [
      {
        role: 'system',
        content: 'Eres un asistente útil llamado microchatgpt. Responde de forma clara y concisa.'
      },
      ...safeHistory,
      { role: 'user', content: message }
    ];

    const ollamaResponse = await fetch(`${OLLAMA_BASE_URL}/api/chat`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        model: OLLAMA_MODEL,
        messages,
        stream: false
      })
    });

    if (!ollamaResponse.ok) {
      const text = await ollamaResponse.text();
      console.error('Error desde Ollama:', text);
      return res.status(500).json({ error: 'Error al llamar a Ollama', details: text });
    }

    const data = await ollamaResponse.json();

    // Formato típico de /api/chat: { message: { role, content }, ... }
    const reply = data?.message?.content || '';

    res.json({
      reply,
      raw: data
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error interno', details: String(err) });
  }
});

// Inicio del servidor
app.listen(PORT, () => {
  console.log(`microchatgpt escuchando en http://localhost:${PORT}`);
  console.log(`Usando Ollama en ${OLLAMA_BASE_URL} con modelo "${OLLAMA_MODEL}"`);
});
