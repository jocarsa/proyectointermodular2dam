// npm install express pug
const express = require('express');
const app = express();

app.set('view engine', 'pug');
app.set('views', './views');

app.get('/', (req, res) => {
  let bitacora = [
    {"titulo":"Titulo 1","contenido":"texto del articulo 1"},
    {"titulo":"Titulo 2","contenido":"texto del articulo 2"},
    {"titulo":"Titulo 3","contenido":"texto del articulo 3"},
    {"titulo":"Titulo 4","contenido":"texto del articulo 4"},
    {"titulo":"Titulo 5","contenido":"texto del articulo 5"},
    {"titulo":"Titulo 6","contenido":"texto del articulo 6"}
  ]
  res.render('bitacora', {mensaje:bitacora});
});

app.listen(5000, () => {
  console.log('Server running on http://localhost:5000');
});
