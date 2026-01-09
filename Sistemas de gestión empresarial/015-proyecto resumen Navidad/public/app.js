/* app.js — lógica del dashboard (cliente)
   - Carga automática de tablas y datos
   - CRUD: insertar / editar / borrar
   - UI simple y didáctica
*/

(function(){
  const $ = (sel) => document.querySelector(sel);
  const $$ = (sel) => Array.from(document.querySelectorAll(sel));

  const tableList = $("#tableList");
  if(!tableList) return; // estamos en /login

  const search = $("#tableSearch");
  const currentTableEl = $("#currentTable");
  const hintEl = $("#tableHint");
  const messagesEl = $("#messages");
  const dataTable = $("#dataTable");
  const btnInsert = $("#btnInsert");
  const btnRefresh = $("#btnRefresh");

  const editModal = $("#editModal");
  const deleteModal = $("#deleteModal");
  const editForm = $("#editForm");
  const formFields = $("#formFields");
  const modalTitle = $("#modalTitle");
  const confirmDeleteBtn = $("#confirmDelete");

  let currentTable = null;
  let currentMeta = null; // {columns, pk}
  let currentRows = [];
  let mode = null; // 'insert' | 'update'
  let rowToUpdate = null;
  let rowToDelete = null;

  function showMessage(type, text){
    const div = document.createElement("div");
    div.className = "msg " + (type || "info");
    div.textContent = text;
    messagesEl.innerHTML = "";
    messagesEl.appendChild(div);
    setTimeout(()=>{
      if(div.parentNode) div.parentNode.removeChild(div);
    }, 4000);
  }

  async function api(url, options){
    const res = await fetch(url, options);
    const data = await res.json().catch(()=>null);
    if(!res.ok || !data || data.ok === false){
      const msg = (data && data.error) ? data.error : ("Error HTTP " + res.status);
      throw new Error(msg);
    }
    return data;
  }

  function openModal(modal){
    modal.classList.remove("hidden");
    document.body.classList.add("noScroll");
  }

  function closeModal(modal){
    modal.classList.add("hidden");
    document.body.classList.remove("noScroll");
  }

  function wireModalClose(modal){
    modal.addEventListener("click", (e)=>{
      const close = e.target && e.target.getAttribute && e.target.getAttribute("data-close");
      if(close) closeModal(modal);
    });
  }

  wireModalClose(editModal);
  wireModalClose(deleteModal);

  async function selectTable(table){
    currentTable = table;
    currentTableEl.textContent = table;
    hintEl.textContent = "Cargando columnas y filas...";
    btnInsert.disabled = true;
    btnRefresh.disabled = true;

    try{
      currentMeta = (await api("/api/table/" + encodeURIComponent(table) + "/meta")).meta;
      const rowsData = await api("/api/table/" + encodeURIComponent(table) + "/rows?limit=200");
      currentRows = rowsData.rows || [];

      renderTable();
      btnInsert.disabled = false;
      btnRefresh.disabled = false;

      const pk = currentMeta.pk;
      hintEl.textContent = pk
        ? ("PK: " + pk + " · Insertar/Editar/Borrar disponibles")
        : "Sin PK detectada: editar/borrar puede estar desactivado";
    }catch(err){
      console.error(err);
      showMessage("error", err.message);
      hintEl.textContent = "No se pudo cargar la tabla";
      renderEmptyTable();
    }
  }

  function renderEmptyTable(){
    dataTable.innerHTML = "<thead><tr><th>Sin datos</th></tr></thead><tbody><tr><td></td></tr></tbody>";
  }

  function renderTable(){
    const cols = (currentMeta && currentMeta.columns) ? currentMeta.columns.map(c=>c.name) : [];
    const pk = currentMeta ? currentMeta.pk : null;

    // Encabezado
    const thead = document.createElement("thead");
    const trh = document.createElement("tr");

    // Columna acciones
    const thA = document.createElement("th");
    thA.textContent = "Acciones";
    trh.appendChild(thA);

    cols.forEach((c)=>{
      const th = document.createElement("th");
      th.textContent = c;
      trh.appendChild(th);
    });

    thead.appendChild(trh);

    // Cuerpo
    const tbody = document.createElement("tbody");

    if(!currentRows || currentRows.length === 0){
      const tr = document.createElement("tr");
      const td = document.createElement("td");
      td.colSpan = cols.length + 1;
      td.className = "muted";
      td.textContent = "No hay filas en esta tabla";
      tr.appendChild(td);
      tbody.appendChild(tr);
    } else {
      currentRows.forEach((row)=>{
        const tr = document.createElement("tr");

        const tdA = document.createElement("td");
        tdA.className = "actions";

        const btnE = document.createElement("button");
        btnE.type = "button";
        btnE.className = "miniBtn";
        btnE.textContent = "Editar";
        btnE.disabled = !pk;
        btnE.addEventListener("click", ()=>openUpdate(row));

        const btnD = document.createElement("button");
        btnD.type = "button";
        btnD.className = "miniBtn danger";
        btnD.textContent = "Borrar";
        btnD.disabled = !pk;
        btnD.addEventListener("click", ()=>openDelete(row));

        tdA.appendChild(btnE);
        tdA.appendChild(btnD);
        tr.appendChild(tdA);

        cols.forEach((c)=>{
          const td = document.createElement("td");
          const v = row[c];
          td.textContent = (v === null || v === undefined) ? "" : String(v);
          tr.appendChild(td);
        });

        tbody.appendChild(tr);
      });
    }

    dataTable.innerHTML = "";
    dataTable.appendChild(thead);
    dataTable.appendChild(tbody);
  }

  function buildFormFields(meta, row, forInsert){
    formFields.innerHTML = "";

    const cols = meta.columns;
    const pk = meta.pk;

    cols.forEach((c)=>{
      const name = c.name;
      const isPK = pk && name === pk;

      // En insert, si PK es auto_increment, lo omitimos
      const autoInc = String(c.extra || "").toLowerCase().includes("auto_increment");
      if(forInsert && isPK && autoInc) return;

      // En update, PK no se edita
      if(!forInsert && isPK) return;

      const wrap = document.createElement("div");
      wrap.className = "field";

      const label = document.createElement("label");
      label.textContent = name;

      const input = document.createElement("input");
      input.type = "text";
      input.name = name;
      input.autocomplete = "off";

      if(row && row[name] !== undefined && row[name] !== null){
        input.value = String(row[name]);
      } else {
        input.value = "";
      }

      wrap.appendChild(label);
      wrap.appendChild(input);
      formFields.appendChild(wrap);
    });

    // Si no hay campos
    if(!formFields.children.length){
      const p = document.createElement("p");
      p.className = "muted";
      p.textContent = "No hay campos editables en este formulario.";
      formFields.appendChild(p);
    }
  }

  function openInsert(){
    if(!currentMeta) return;
    mode = "insert";
    rowToUpdate = null;
    modalTitle.textContent = "Insertar en " + currentTable;
    buildFormFields(currentMeta, null, true);
    openModal(editModal);
  }

  function openUpdate(row){
    if(!currentMeta || !currentMeta.pk) return;
    mode = "update";
    rowToUpdate = row;
    modalTitle.textContent = "Editar fila en " + currentTable;
    buildFormFields(currentMeta, row, false);
    openModal(editModal);
  }

  function openDelete(row){
    if(!currentMeta || !currentMeta.pk) return;
    rowToDelete = row;
    openModal(deleteModal);
  }

  function readFormData(){
    const data = {};
    const inputs = Array.from(editForm.querySelectorAll("input[name]"));
    inputs.forEach((inp)=>{
      data[inp.name] = inp.value;
    });
    return data;
  }

  async function refreshCurrent(){
    if(!currentTable) return;
    const rowsData = await api("/api/table/" + encodeURIComponent(currentTable) + "/rows?limit=200");
    currentRows = rowsData.rows || [];
    renderTable();
  }

  // Eventos
  tableList.addEventListener("click", (e)=>{
    const btn = e.target.closest("button.tableBtn");
    if(!btn) return;
    $$("button.tableBtn").forEach(b=>b.classList.remove("active"));
    btn.classList.add("active");
    selectTable(btn.getAttribute("data-table"));
  });

  if(search){
    search.addEventListener("input", ()=>{
      const q = search.value.toLowerCase();
      $$("#tableList li").forEach((li)=>{
        const t = li.textContent.toLowerCase();
        li.style.display = t.includes(q) ? "" : "none";
      });
    });
  }

  btnInsert.addEventListener("click", openInsert);
  btnRefresh.addEventListener("click", async ()=>{
    try{ await refreshCurrent(); }
    catch(err){ showMessage("error", err.message); }
  });

  editForm.addEventListener("submit", async (e)=>{
    e.preventDefault();
    try{
      const data = readFormData();
      if(mode === "insert"){
        await api("/api/table/" + encodeURIComponent(currentTable) + "/insert", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ data })
        });
        closeModal(editModal);
        showMessage("ok", "Fila insertada.");
        await refreshCurrent();
      } else if(mode === "update"){
        const pk = currentMeta.pk;
        const pkValue = rowToUpdate ? rowToUpdate[pk] : undefined;
        await api("/api/table/" + encodeURIComponent(currentTable) + "/update", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ pkValue, data })
        });
        closeModal(editModal);
        showMessage("ok", "Fila actualizada.");
        await refreshCurrent();
      }
    }catch(err){
      console.error(err);
      showMessage("error", err.message);
    }
  });

  confirmDeleteBtn.addEventListener("click", async ()=>{
    try{
      const pk = currentMeta.pk;
      const pkValue = rowToDelete ? rowToDelete[pk] : undefined;
      await api("/api/table/" + encodeURIComponent(currentTable) + "/delete", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ pkValue })
      });
      closeModal(deleteModal);
      showMessage("ok", "Fila borrada.");
      await refreshCurrent();
    }catch(err){
      console.error(err);
      showMessage("error", err.message);
    }
  });

  // Autoseleccionar primera tabla
  const firstBtn = $("button.tableBtn");
  if(firstBtn){
    firstBtn.classList.add("active");
    selectTable(firstBtn.getAttribute("data-table"));
  }
})();
