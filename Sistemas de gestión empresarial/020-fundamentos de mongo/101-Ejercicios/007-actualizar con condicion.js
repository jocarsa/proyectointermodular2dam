db.documentos.updateMany(
  	{"nombre":"Jose Vicente"},
    {
        $set:
        {email:"verdadero@correo.com"}
    }
)