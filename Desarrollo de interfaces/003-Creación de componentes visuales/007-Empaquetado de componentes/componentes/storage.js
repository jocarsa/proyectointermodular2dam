(function () {
    function ensureCredentialsInLocalStorage(storageKey) {
        const existing = localStorage.getItem(storageKey);
        if (existing === null) {
            const defaultCreds = { user: "admin", pass: "1234" };
            localStorage.setItem(storageKey, JSON.stringify(defaultCreds));
        }
    }

    // Exponemos la utilidad en el objeto global
    window.LoginStorage = {
        ensureCredentialsInLocalStorage
    };
})();
