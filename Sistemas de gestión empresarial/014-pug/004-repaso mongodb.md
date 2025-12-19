-Orientada a documentos y no tablas
-Tenemos bases de datos
-Pero no tenemos tablas sino colecciones
-La información no se almacena en registros (tuplas) sino en documentos

Y los documentos se almacenan en un plano json

https://www.mongodb.com/

Descargamos:
MongoDB Server - El motor ciego de bases de datos
MongoDB Compass - como Workbench - cliente gráfico de gestión de MongoDB

En ubuntu:;
MongoDB:

curl -fsSL https://pgp.mongodb.com/server-7.0.asc | \
sudo gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg --dearmor


echo "deb [ signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] \
https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | \
sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list

sudo apt update
sudo apt install -y mongodb-org

sudo systemctl start mongod
sudo systemctl enable mongod

Compass:

wget https://downloads.mongodb.com/compass/mongodb-compass_1.43.4_amd64.deb
sudo apt install ./mongodb-compass_1.43.4_amd64.deb

mongodb-compass
