Ollama es una aplicación que nos permite 
descargar y usar modelos de IA en local

Eso quiere decir no depende de API's externas
Y tener independencia

Existe ollama en Windows

Funciona mucho mejor en Linux

Paso 1: Arrancad una máquina virtual
Paso 2: Instalamos ollama

sudo apt install curl (si curl os da error)
curl -fsSL https://ollama.com/install.sh | sh

ollama list

Ahora nos bajamos un modelo de IA:
ollama pull qwen2.5:3b-instruct

Cuanto esté
ollama run qwen2.5:3b-instruct





