#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
util/main_batch_files.py — Genera un JSON por actividad en actividades/<lesson_rel>/<sha1>.json

Cambios clave (carpetas/subcarpetas + seguridad):
- NUEVA ubicación por lección: ACTIVIDADES_DIR / <lesson_rel_sanitizado> / <sha1>.json
- Fallback de lectura/existencia: si ya existe un JSON en la RAÍZ (legacy) con ese hash,
  NO se crea uno nuevo en subcarpeta (para NO duplicar ni arriesgar respuestas).
- NUNCA se sobreescribe un archivo existente (ni nuevo ni legacy).

Cambios recientes:
- Excluir cualquier archivo llamado "Contenidos básicos.md" (con/sin acento, case-insensitive).
- Centrar el contexto/conceptos EXCLUSIVAMENTE en la subcarpeta "101-Ejercicios".
- Sin límites internos (no-CLI) para mejorar calidad: archivos/snippets/tamaño/profundidad/árbol ilimitados.

CAMBIO SOLICITADO:
- Ya NO se llama a Ollama local.
- Se llama a una API remota (PHP) con cabecera X-API-Key y campo POST 'question',
  que devuelve JSON con clave 'answer' (como en tu proyecto ejemplo).
"""

from __future__ import annotations
import os, sys, csv, json, re, hashlib, time, argparse, subprocess
from pathlib import Path
from typing import Dict, List, Optional, Tuple

# --- REMOTE API deps ----------------------------------------------------------
import requests  # pip install requests
import urllib3   # pip install urllib3

# Disable SSL warnings because we'll use verify=False against ngrok
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

# --- paths base ---------------------------------------------------------------
THIS_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = THIS_DIR.parent
DB_DIR = PROJECT_ROOT / "db"
CSV_PATH = DB_DIR / "matriculas.csv"
REPOS_DIR = DB_DIR / "repositorios"
ABS_BASE = Path("/var/www/html")

# >>> Cambia esto si tu carpeta de actividades vive en otro sitio
ACTIVIDADES_DIR = PROJECT_ROOT / "actividades"

if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))

# --- IMPORTS DEL PROYECTO (intactos) ------------------------------------------
from util.fetch_lavender import fetch_student_json
from util.folder_structure import get_folder_structure
from util.generate_exercise import (
    load_student_and_hobbies,
    read_folder_listing_and_samples,
    extract_concepts_from_files,
    build_scope_block,
    build_prompt,
    DEFAULT_RUBRIC,
    render_tree_ascii,
)

# === JOBS (CONFIG EXPLÍCITA RAÍZ POR REPO) ====================================
JOBS: List[List] = [
    ["file:///var/www/html/programaciondam2526", "/var/www/html", 2,
     ["Ejercicios", "101-Ejercicios", "002-Ejercicios"],
     "/var/www/html/eval/db/repositorios/programaciondam2526.txt"],

    ["file:///var/www/html/programaciondaw2526", "/var/www/html", 2,
     ["Ejercicios", "101-Ejercicios", "002-Ejercicios"],
     "/var/www/html/eval/db/repositorios/programaciondaw2526.txt"],

    ["file:///var/www/html/interfacesweb", "/var/www/html", 2,
     ["Ejercicios", "101-Ejercicios", "002-Ejercicios"],
     "/var/www/html/eval/db/repositorios/interfacesweb.txt"],

    ["file:///var/www/html/proyectointermodular2daw", "/var/www/html", 2,
     ["Ejercicios", "101-Ejercicios", "002-Ejercicios"],
     "/var/www/html/eval/db/repositorios/proyectointermodular2daw.txt"],

    ["file:///var/www/html/sistemasdegestionempresarial", "/var/www/html", 2,
     ["Ejercicios", "101-Ejercicios", "002-Ejercicios"],
     "/var/www/html/eval/db/repositorios/sistemasdegestionempresarial.txt"],

    ["file:///var/www/html/dam2526/Primero/Bases de datos", "/var/www/html", 2,
     ["Ejercicios", "101-Ejercicios", "002-Ejercicios"],
     "/var/www/html/eval/db/repositorios/dam2526primerobasesdedatos.txt"],

    ["file:///var/www/html/dam2526/Primero/Programación", "/var/www/html", 2,
     ["Ejercicios", "101-Ejercicios", "002-Ejercicios"],
     "/var/www/html/eval/db/repositorios/dam2526primeroprogramacion.txt"],

    ["file:///var/www/html/dam2526/Primero/Lenguajes de marcas y sistemas de gestión de información", "/var/www/html", 2,
     ["Ejercicios", "101-Ejercicios", "002-Ejercicios"],
     "/var/www/html/eval/db/repositorios/dam2526primerolenguajesdemarcas.txt"],

    ["file:///var/www/html/dam2526/Primero/Entornos de desarrollo", "/var/www/html", 2,
     ["Ejercicios", "101-Ejercicios", "002-Ejercicios"],
     "/var/www/html/eval/db/repositorios/dam2526primeroentornosdedesarrollo.txt"],

    ["file:///var/www/html/dam2526/Primero/Sistemas informáticos", "/var/www/html", 2,
     ["Ejercicios", "101-Ejercicios", "002-Ejercicios"],
     "/var/www/html/eval/db/repositorios/dam2526primerosistemasinformaticos.txt"],

    ["file:///var/www/html/dam2526/Primero/Proyecto Intermodular", "/var/www/html", 2,
     ["Ejercicios", "101-Ejercicios", "002-Ejercicios"],
     "/var/www/html/eval/db/repositorios/dam2526primeroproyectointermodular.txt"],
]

def file_url_to_path(u: str) -> Path:
    if u.startswith("file://"):
        return Path(u.replace("file://", "", 1))
    return Path(u)

REPO_SCAN_ROOTS: Dict[str, Dict[str, object]] = {}
for root_url, base_prefix, max_depth, patterns, out_file in JOBS:
    out_path = Path(out_file)
    repo_key = out_path.stem
    REPO_SCAN_ROOTS[repo_key] = {
        "root": file_url_to_path(root_url),
        "base_prefix": Path(base_prefix),
        "max_depth": int(max_depth),
        "patterns": list(patterns),
        "output_file": out_path,
    }

# --- utils --------------------------------------------------------------------
def sniff_delimiter(sample: str) -> str:
    for d in (";", "\t", ",", "|"):
        if d in sample:
            return d
    return ","

def read_students(csv_path: Path) -> Tuple[List[Dict[str, str]], List[str]]:
    if not csv_path.is_file():
        raise FileNotFoundError(f"No existe {csv_path}")
    raw = csv_path.read_text(encoding="utf-8", errors="ignore")
    delim = sniff_delimiter(raw[:4096])
    r = csv.reader(raw.splitlines(), delimiter=delim)
    headers = next(r, None) or []
    headers = [h.replace("\ufeff", "").strip() for h in headers]
    rows: List[Dict[str, str]] = []
    for row in r:
        if not row:
            continue
        if len(row) < len(headers):
            row = row + [""] * (len(headers) - len(row))
        rows.append({headers[i]: row[i] for i in range(len(headers))})
    return rows, headers

def detect_dni_column(headers: List[str]) -> Optional[str]:
    aliases = ("dni", "documento", "nif", "doc")
    for h in headers:
        low = (h or "").strip().lower()
        if any(a in low for a in aliases):
            return h
    return None

def repo_keys() -> List[str]:
    return [f.stem for f in sorted(REPOS_DIR.glob("*.txt"))]

def detect_repos_for_row(row: Dict[str, str], keys: List[str]) -> List[str]:
    hay = " | ".join(str(v or "") for v in row.values()).lower()
    hits = [k for k in keys if k.lower() in hay]
    if not hits and keys:
        hits = [keys[0]]
    return hits

def read_repo_lessons(repo_key: str) -> List[str]:
    p = REPOS_DIR / f"{repo_key}.txt"
    if not p.is_file():
        return []
    out: List[str] = []
    for ln in p.read_text(encoding="utf-8", errors="ignore").splitlines():
        s = ln.strip()
        if s:
            out.append(s)
    return out

def as_abs(path_str: str) -> Path:
    p = Path(path_str.strip())
    if p.is_absolute():
        return p
    return (ABS_BASE / p).resolve()

def criterios_md_path(folder_abs: Path) -> Path:
    return folder_abs / "201-Criterios de evaluación" / "Criterios de evaluación.md"

def actividad_md_path(folder_abs: Path) -> Path:
    """Ruta al 001-actividad.md (nuevo comportamiento)"""
    return folder_abs / "201-Criterios de evaluación" / "001-actividad.md"

def needs_generation(folder_abs: Path) -> bool:
    md = criterios_md_path(folder_abs)
    if not md.is_file():
        return True
    try:
        txt = md.read_text(encoding="utf-8", errors="ignore").strip()
    except Exception:
        return True
    return (len(txt) == 0)

def repo_root_for(repo_key: str, fallback_from_specific: Optional[Path] = None) -> Path:
    cfg = REPO_SCAN_ROOTS.get(repo_key)
    if cfg:
        return Path(cfg["root"])
    if fallback_from_specific:
        parents = list(fallback_from_specific.parents)
        if len(parents) >= 2:
            return parents[2]
        return fallback_from_specific.parent
    return ABS_BASE

# --- fallback scanner ---------------------------------------------------------
import os as _os
EXCLUDE_DIRS = {".git", "__pycache__", ".venv", "node_modules", ".idea", ".vscode"}
TEXT_EXTS = {".md", ".txt", ".py", ".js", ".ts", ".html", ".css", ".sql", ".json", ".xml", ".csv", ".yml", ".yaml", ".php", ".ini"}

# ==== No límites por defecto ====
MAX_FILES = -1         # -1 = ilimitado
MAX_SNIPPETS = -1      # -1 = ilimitado
MAX_FILE_SIZE = -1     # -1 = ilimitado (leer tamaño completo)
SNIPPET_CHARS = -1     # -1 = snippet completo

# Profundidad fallback y líneas del árbol sin límites
FALLBACK_DEPTH = -1    # -1 = sin límite de profundidad
TREE_MAX_LINES = 10**9 # usar un número enorme; si render_tree_ascii acepta None, cámbialo abajo

def safe_read_text(p: Path, max_bytes: int = MAX_FILE_SIZE) -> str:
    try:
        if max_bytes != -1 and p.stat().st_size > max_bytes:
            return ""
        return p.read_text(encoding="utf-8", errors="ignore")
    except Exception:
        return ""

def fallback_list_and_snippets(root: Path, max_depth: int = FALLBACK_DEPTH):
    listing: List[Dict[str, object]] = []
    samples: List[Dict[str, str]] = []
    root = root.resolve()

    def depth_of(path: Path) -> int:
        try:
            return len(path.relative_to(root).parts)
        except Exception:
            return 0

    count = 0
    for dirpath, dirnames, filenames in _os.walk(root):
        dirnames[:] = [d for d in dirnames if d not in EXCLUDE_DIRS]
        cur = Path(dirpath)
        if max_depth != -1 and depth_of(cur) > max_depth:
            dirnames[:] = []
            continue

        for fname in filenames:
            if MAX_FILES != -1 and count >= MAX_FILES:
                break
            fpath = cur / fname
            try:
                size = fpath.stat().st_size
            except Exception:
                continue

            listing.append({"path": str(fpath), "size": int(size), "mtime": int(fpath.stat().st_mtime)})
            count += 1

            ext = fpath.suffix.lower()
            if (MAX_SNIPPETS == -1 or len(samples) < MAX_SNIPPETS) and (ext in TEXT_EXTS or ext == ""):
                txt = safe_read_text(fpath, MAX_FILE_SIZE)
                if txt:
                    snippet = txt if SNIPPET_CHARS == -1 else txt[:SNIPPET_CHARS]
                    samples.append({"path": str(fpath), "snippet": snippet})

        if MAX_FILES != -1 and count >= MAX_FILES:
            break

    return listing, samples

# === NUEVO: filtros para 101-Ejercicios y exclusión de "Contenidos básicos.md" ===
_EXCLUDED_FILENAMES_CASEFOLD = {
    "contenidos básicos.md".casefold(),
    "contenidos basicos.md".casefold(),  # sin acento
}

def _is_excluded_filename(p: Path) -> bool:
    return p.name.casefold() in _EXCLUDED_FILENAMES_CASEFOLD

def _is_under(base: Path, child: Path) -> bool:
    try:
        child.resolve().relative_to(base.resolve())
        return True
    except Exception:
        return False

def filter_to_101_and_exclude_basicos(
    folder_abs: Path,
    listing: List[Dict[str, object]],
    samples: List[Dict[str, str]]
) -> Tuple[List[Dict[str, object]], List[Dict[str, str]], Path]:
    """
    Devuelve listing/samples filtrados:
      - Solo elementos bajo <folder_abs>/101-Ejercicios
      - Excluye archivos llamados "Contenidos básicos.md"
    """
    ejercicios_dir = folder_abs / "101-Ejercicios"

    def keep_list_item(it: Dict[str, object]) -> bool:
        p = Path(str(it.get("path", "")))
        if not _is_under(ejercicios_dir, p):
            return False
        if _is_excluded_filename(p):
            return False
        return True

    def keep_sample_item(it: Dict[str, str]) -> bool:
        p = Path(str(it.get("path", "")))
        if not _is_under(ejercicios_dir, p):
            return False
        if _is_excluded_filename(p):
            return False
        return True

    listing_f = [it for it in listing if keep_list_item(it)]
    samples_f = [it for it in samples if keep_sample_item(it)]
    return listing_f, samples_f, ejercicios_dir

# === REMOTE API ===============================================================
API_URL = os.environ.get("REMOTE_API_URL", "https://covalently-untasked-daphne.ngrok-free.dev/api.php")
API_KEY = os.environ.get("REMOTE_API_KEY", "TEST_API_KEY_JOCARSA_123")

def call_remote_api(prompt: str, timeout: int = 300) -> str:
    """
    Llama a la API remota (PHP + modelo detrás) para obtener Markdown.
    Protocolo esperado (como en tu ejemplo):
      - POST form-data: question=<prompt>
      - Header: X-API-Key: <key>
      - Response JSON: {"answer": "..."}
    """
    try:
        resp = requests.post(
            API_URL,
            headers={"X-API-Key": API_KEY},
            data={"question": prompt},
            timeout=timeout,
            verify=False,  # SSL ngrok no verificado
        )
    except Exception as e:
        raise RuntimeError(f"Error al contactar con la API remota: {e}")

    if resp.status_code != 200:
        raise RuntimeError(f"API remota devolvió HTTP {resp.status_code}: {resp.text}")

    try:
        data = resp.json()
    except Exception:
        raise RuntimeError(f"No se pudo parsear JSON desde la API remota: {resp.text}")

    answer = data.get("answer")
    if not answer:
        raise RuntimeError(f"La respuesta de la API no contiene 'answer': {data}")

    return str(answer).strip()

# --- guards & prompt pieces ---------------------------------------------------
def looks_like_solution(text: str) -> bool:
    if "```" in text:
        return True
    patterns = [r"\bprint\s*\(", r"\binput\s*\(", r"\bdef\s+\w+\s*\(", r"\bclass\s+\w+\s*:", r"\breturn\b", r"\bimport\s+\w+"]
    return any(re.search(p, text) for p in patterns)

def contains_any(text: str, terms: List[str]) -> bool:
    t = text.lower()
    return any(term.lower() in t for term in terms if term.strip())

def build_personalized_prompt(base_prompt: str, student_hobbies: List[str], must_use_hobbies: List[str]) -> str:
    hobbies_str = ", ".join(student_hobbies) if student_hobbies else "sin datos"
    must_hobbies_str = ", ".join(must_use_hobbies) if must_use_hobbies else "Juegos de mesa / videojuegos"
    thematic_vars_hint = """
Ejemplos de nombres temáticos correctos (solo como guía, NO resolver):
- "Juegos de mesa / videojuegos": `puntos_dardos`, `nivel_jugador`, `nombre_avatar`
- "La naturaleza": `arboles_contados`, `litros_agua`, `sendero_actual`
- "Los animales": `animal_favorito`, `edad_mascota`, `contador_comidas`
- "Los libros": `titulo_libro`, `paginas_leidas`, `autor_favorito`
- "Series/películas": `episodios_vistos`, `minutos_pelicula`, `personaje_principal`
"""
    persona_block = f"""
### Perfil del estudiante
- Idioma: Español.
- Nivel: Iniciación (FP) — SOLO contenidos de la carpeta/tema actual.
- Hobbies (todos): {hobbies_str}
- Hobbies que DEBES usar en el enunciado: {must_hobbies_str}

### Reglas de personalización (OBLIGATORIAS)
- Integra explícitamente al menos 1 de estos hobbies en:
  a) la narrativa del **Contexto**,
  b) los **nombres de variables** mencionados en el enunciado (sin código resuelto),
  c) los **datos de ejemplo** del enunciado (texto plano).
- Mantén el alcance estricto del tema actual.

{thematic_vars_hint}

### Política de evaluación y formato de salida (OBLIGATORIO)
- Salida en Markdown con SOLO estas secciones:
  1. **Título**
  2. **Contexto** (debe mencionar esos hobbies)
  3. **Enunciado paso a paso**
  4. **Restricciones** (límites de conocimientos y qué NO usar)
  5. **Criterios de evaluación** (rúbrica breve)
- PROHIBIDO incluir soluciones, código final, salidas de ejemplo ejecutables ni pseudocódigo completo.
- Si introduces algo que parezca solución, cámbialo por “(El alumnado debe completar este apartado)”.

### Guardarraíl de alcance (OBLIGATORIO)
- No uses librerías externas,  ni estructuras no vistas.
"""
    return f"{persona_block.strip()}\n\n{base_prompt.strip()}"

def stable_id(dni: str, repo: str, lesson_rel: str) -> str:
    h = hashlib.sha1()
    h.update((dni or "").encode("utf-8"))
    h.update(b"|")
    h.update((repo or "").encode("utf-8"))
    h.update(b"|")
    h.update((lesson_rel or "").encode("utf-8"))
    return h.hexdigest()

# === NUEVO: helpers de path seguro y guardado sin sobrescribir =================
def sanitize_lesson_rel(lesson_rel: str) -> str:
    s = (lesson_rel or "").replace("\\", "/").strip().strip("/")
    parts = [p.strip() for p in s.split("/") if p.strip() not in {"", ".", ".."}]
    return "/".join(parts)

def compute_activity_paths(dni: str, repo: str, lesson_rel: str) -> Tuple[Path, Path, str]:
    """
    Devuelve (new_path, legacy_path, jid). new_path es en subcarpeta; legacy_path es en la raíz.
    """
    jid = stable_id(dni, repo, lesson_rel)
    rel_safe = sanitize_lesson_rel(lesson_rel)
    new_path = (ACTIVIDADES_DIR / rel_safe / f"{jid}.json").resolve()
    legacy_path = (ACTIVIDADES_DIR / f"{jid}.json").resolve()
    return new_path, legacy_path, jid

def save_activity_json_no_overwrite(new_path: Path, legacy_path: Path, data: Dict[str, object]) -> Optional[Path]:
    """
    Guarda atómicamente en new_path, creando carpetas. NO sobrescribe.
    - Si existe legacy_path o new_path, NO guarda y devuelve None.
    - Si no existen, guarda en new_path y devuelve su Path.
    """
    if legacy_path.is_file():
        return None
    if new_path.is_file():
        return None

    new_path.parent.mkdir(parents=True, exist_ok=True)
    tmp = new_path.parent / f".{new_path.name}.tmp"
    tmp.write_text(json.dumps(data, ensure_ascii=False, indent=2), encoding="utf-8")
    tmp.replace(new_path)
    return new_path

# --- main ---------------------------------------------------------------------
def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--include-all", action="store_true", help="Procesa también actividades con Criterios.md ya rellenos")
    ap.add_argument("--timeout", type=int, default=300, help="Timeout (segundos) para la API remota")
    args = ap.parse_args()

    print("[0] API remota configurada")
    print(f"    URL: {API_URL}")

    rows, headers = read_students(CSV_PATH)
    dni_col = detect_dni_column(headers)
    keys_all = repo_keys()

    print(f"=== BATCH to {ACTIVIDADES_DIR} — alumnos={len(rows)} ===")

    for si, row in enumerate(rows, 1):
        dni = (row.get(dni_col) or "").strip() if dni_col else ""
        print(f"\n[Alumno {si}/{len(rows)}] DNI={dni or '(sin dni)'}")

        student = load_student_and_hobbies(dni or "", model=None)
        all_hobbies = list(student.hobbies_used) if student.hobbies_used else []
        must_use_hobbies = [h.strip() for h in all_hobbies[:2] if h.strip()] or ["Juegos de mesa / videojuegos"]

        repos_for_student = detect_repos_for_row(row, keys_all)
        print(f"   Repos detectados: {', '.join(repos_for_student) if repos_for_student else '(ninguno)'}")

        for repo in repos_for_student:
            lessons = read_repo_lessons(repo)
            if not lessons:
                print(f"   Repo {repo}: (sin rutas en .txt)")
                continue

            any_path_for_root = as_abs(lessons[0]) if lessons else ABS_BASE
            repo_root_abs = repo_root_for(repo, fallback_from_specific=any_path_for_root)
            try:
                depth = int(REPO_SCAN_ROOTS.get(repo, {}).get("max_depth", 2))
                tree = get_folder_structure(str(repo_root_abs), max_depth=depth)
                tree_txt = render_tree_ascii(tree, max_lines=TREE_MAX_LINES)
            except Exception:
                tree_txt = f"{repo_root_abs}\n└── (no se pudo leer la estructura)"

            for li, rel in enumerate(lessons, 1):
                folder_abs = as_abs(rel)
                if not folder_abs.is_dir():
                    print(f"      [{li}/{len(lessons)}] {rel} — (no es carpeta)")
                    continue

                already = not needs_generation(folder_abs)
                if already and not args.include_all:
                    print(f"      [{li}/{len(lessons)}] {rel} — Criterios.md ya existe (omitido)")
                    continue

                new_path, legacy_path, jid = compute_activity_paths(dni, repo, rel)
                if new_path.is_file():
                    print(f"      [{li}/{len(lessons)}] {rel} — ya existe (nueva ruta). No se sobrescribe.")
                    continue
                if legacy_path.is_file():
                    print(f"      [{li}/{len(lessons)}] {rel} — ya existe en raíz (legacy). No se duplica ni se mueve.")
                    continue

                # === OVERRIDE manual con 001-actividad.md ============================
                actividad_md = actividad_md_path(folder_abs)
                if actividad_md.is_file():
                    try:
                        md_text = actividad_md.read_text(encoding="utf-8").strip()
                        if md_text:
                            obj = {
                                "id": jid,
                                "dni": dni,
                                "repo": repo,
                                "lesson_rel": rel,
                                "lesson_abs": str(folder_abs),
                                "already_had_criteria": already,
                                "prompt": "(MANUAL OVERRIDE desde 001-actividad.md)",
                                "response_markdown": md_text,
                                "hobbies": all_hobbies,
                                "ts": int(time.time()),
                            }
                            saved_path = save_activity_json_no_overwrite(new_path, legacy_path, obj)
                            if saved_path is None:
                                print(f"         ⏭ No guardado (ya existía).")
                            else:
                                print(f"         ✓ Guardado desde 001-actividad.md: {saved_path}")
                            continue  # saltar IA
                    except Exception as e:
                        print(f"         ⚠ Error leyendo 001-actividad.md: {e}")
                        # si falla, sigue flujo normal (IA)
                # =====================================================================

                print(f"      [{li}/{len(lessons)}] {rel} — generando …")

                # 1) Intento principal: lector del proyecto
                listing, samples = read_folder_listing_and_samples(str(folder_abs))
                # 2) Filtro a 101-Ejercicios + exclusión “Contenidos básicos.md”
                listing, samples, ejercicios_dir = filter_to_101_and_exclude_basicos(folder_abs, listing, samples)

                # 3) Si no hay material tras el filtro, fallback SOLO sobre 101-Ejercicios (sin límites)
                if len(listing) == 0 and len(samples) == 0:
                    listing, samples = fallback_list_and_snippets(ejercicios_dir, max_depth=FALLBACK_DEPTH)
                    listing, samples, _ = filter_to_101_and_exclude_basicos(folder_abs, listing, samples)

                # 4) Construcción del contexto SOLO con samples filtrados
                concepts = extract_concepts_from_files(listing, samples)

                snips = []
                for s in samples[:4]:
                    p = s.get("path", "")
                    t = (s.get("snippet") or "").strip()
                    if p and t:
                        snips.append(f"[{p}]\n{t}")
                lesson_context = "\n\n---\n\n".join(snips) if snips else "Contenido del tema (101-Ejercicios) impartido."

                # 5) Scope SOLO con listing/samples filtrados
                scope_txt, _ = build_scope_block(tree_txt, listing, samples)
                rubric = DEFAULT_RUBRIC.strip()
                base_prompt = build_prompt(lesson_context, rubric, student.hobbies_used, scope_txt)
                prompt = build_personalized_prompt(base_prompt, all_hobbies, must_use_hobbies)

                try:
                    response = call_remote_api(prompt, timeout=args.timeout)
                except Exception as e:
                    print("         ⚠ API remota error:", e)
                    continue

                if looks_like_solution(response):
                    reminder = """
⚠️ RECUERDA (OBLIGATORIO):
- En las actividades, no adelantes contenido de unidades didácticas posteriores a la actual - y si es posible, cíñete a los contenidos de la unidad diáctica actual.
- NO incluyas soluciones ni código final.
- NO muestres ejemplos ejecutables (nada de print/input/def/class).
- Produce ÚNICAMENTE las secciones pedidas del enunciado.
- LA mayoría de actividades son de escribir código - pero algunas no. Si en los archivos de muestra procesados hay unicamente explicaciones pero no codigo, pon actividades de desarrollo estrictamente en base a los contenidos de esa unidad didáctica.
- De la lista de hobbies del alumno, no uses siempre solo el primer hobby. Selecciona uno al azar. Pero solo uno de la lista, no mezcles hobbies.
- Si los materiales que se han trabajado en clase (ejercicios) están en un lenguaje de programación específico, la actividad debe estar en ese mismo lenguaje de programación.
- No incluyas las soluciones, ni tampoco proporciones enunciados tan genericos que no se puedan materializar. Proporciona instrucciones concretas que el alumno pueda resolver en función de los ejercicios vistos en la clase.
"""
                    try:
                        response = call_remote_api(reminder + "\n\n" + prompt, timeout=args.timeout)
                    except Exception as e:
                        print("         ⚠ API remota retry error:", e)
                        continue

                if not contains_any(response, must_use_hobbies):
                    hobbies_reminder = f"""
⚠️ RECUERDA (OBLIGATORIO):
- Debes integrar explícitamente estos hobbies en **Contexto** y en **nombres de variables** del enunciado: {", ".join(must_use_hobbies)}
- Mantén formato y SIN solución.
"""
                    try:
                        response = call_remote_api(hobbies_reminder + "\n\n" + prompt, timeout=args.timeout)
                    except Exception as e:
                        print("         ⚠ API remota retry (hobbies) error:", e)
                        continue

                obj = {
                    "id": jid,
                    "dni": dni,
                    "repo": repo,
                    "lesson_rel": rel,
                    "lesson_abs": str(folder_abs),
                    "already_had_criteria": already,
                    "prompt": prompt,
                    "response_markdown": response,
                    "hobbies": all_hobbies,
                    "ts": int(time.time()),
                }

                saved_path = save_activity_json_no_overwrite(new_path, legacy_path, obj)
                if saved_path is None:
                    if new_path.is_file():
                        print(f"         ⏭ Ya existe ahora en nueva ruta: {new_path}")
                    elif legacy_path.is_file():
                        print(f"         ⏭ Ya existe en legacy: {legacy_path}")
                    else:
                        print(f"         ⏭ No guardado por política no-overwrite.")
                    continue

                print(f"         ✓ Guardado: {saved_path}")

    print(f"\n=== FIN BATCH ===\nCarpeta base: {ACTIVIDADES_DIR}")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())

