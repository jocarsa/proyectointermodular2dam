// npm install express pug
const express = require('express');
const app = express();

app.set('view engine', 'pug');
app.set('views', './views');

app.get('/', (req, res) => {
  res.render('principal', {});
});

app.listen(5000, () => {
  console.log('Server running on http://localhost:5000');
});
