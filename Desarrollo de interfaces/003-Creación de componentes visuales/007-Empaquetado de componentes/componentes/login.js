(function () {
    class Login {
        constructor(storageKey = "loginCredentials") {
            this.storageKey = storageKey;

            const credsJSON = localStorage.getItem(this.storageKey);
            this.correctCreds = credsJSON !== null ? JSON.parse(credsJSON) : null;

            this.element = document.createElement("div");
            this.element.className = "login";

            this.title = document.createElement("h2");
            this.title.textContent = "Login";

            this.inputUser = document.createElement("input");
            this.inputUser.type = "text";
            this.inputUser.placeholder = "Usuario";

            this.inputPass = document.createElement("input");
            this.inputPass.type = "password";
            this.inputPass.placeholder = "Contraseña";

            this.button = document.createElement("button");
            this.button.textContent = "Entrar";
            this.button.addEventListener("click", () => this.checkLogin());

            this.message = document.createElement("div");
            this.message.className = "message";

            this.element.append(
                this.title,
                this.inputUser,
                this.inputPass,
                this.button,
                this.message
            );

            // Si no hay credenciales válidas, avisamos
            if (
                this.correctCreds === null ||
                typeof this.correctCreds.user !== "string" ||
                typeof this.correctCreds.pass !== "string"
            ) {
                this.showError("No hay credenciales válidas en localStorage.");
            }
        }

        checkLogin() {
            if (
                this.correctCreds === null ||
                typeof this.correctCreds.user !== "string" ||
                typeof this.correctCreds.pass !== "string"
            ) {
                this.showError("No hay credenciales válidas en localStorage.");
                return;
            }

            const usuario = this.inputUser.value;
            const password = this.inputPass.value;

            if (usuario === this.correctCreds.user && password === this.correctCreds.pass) {
                this.showSuccess("Bienvenido, " + usuario);
            } else {
                this.showError("Usuario o contraseña incorrectos");
            }
        }

        showSuccess(text) {
            this.message.textContent = text;
            this.message.className = "message success";
        }

        showError(text) {
            this.message.textContent = text;
            this.message.className = "message error";
        }

        render(container) {
            container.appendChild(this.element);
        }
    }

    // Exponemos el componente
    window.Login = Login;
})();
