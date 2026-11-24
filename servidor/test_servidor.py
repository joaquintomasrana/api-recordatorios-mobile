import json
import sqlite3
from contextlib import closing
import tempfile
import threading
import unittest
from pathlib import Path
from urllib.request import Request, urlopen

import servidor


class ServicioTest(unittest.TestCase):
    def test_reintentos_y_borrado_conservan_una_sola_clave(self):
        with tempfile.TemporaryDirectory() as temporal:
            servidor.BASE = Path(temporal) / "prueba.db"
            with closing(sqlite3.connect(servidor.BASE)) as conexion:
                conexion.execute("CREATE TABLE recordatorios(clave TEXT PRIMARY KEY, datos TEXT NOT NULL)")
            api = servidor.ThreadingHTTPServer(("127.0.0.1", 0), servidor.Servicio)
            api.modo = "normal"
            hilo = threading.Thread(target=api.serve_forever, daemon=True)
            hilo.start()
            url = f"http://127.0.0.1:{api.server_port}/recordatorios"
            registro = dict(
                clave="misma-clave", titulo="Leer apuntes", descripcion=None,
                categoria="Estudio", prioridad="Alta", completado=False,
                fecha=12345, eliminado=False,
            )
            try:
                for _ in range(3):
                    peticion = Request(
                        url + "/misma-clave", json.dumps(registro).encode(),
                        {"Content-Type": "application/json"}, method="PUT",
                    )
                    with urlopen(peticion) as respuesta:
                        self.assertEqual(200, respuesta.status)
                with urlopen(url) as respuesta:
                    self.assertEqual(1, len(json.load(respuesta)))
                registro["eliminado"] = True
                with urlopen(Request(
                    url + "/misma-clave", json.dumps(registro).encode(),
                    {"Content-Type": "application/json"}, method="PUT",
                )):
                    pass
                with urlopen(url) as respuesta:
                    self.assertTrue(json.load(respuesta)[0]["eliminado"])
                api.modo = "500"
                from urllib.error import HTTPError
                with self.assertRaises(HTTPError) as error:
                    urlopen(url)
                self.assertEqual(500, error.exception.code)
            finally:
                api.shutdown()
                api.server_close()
                hilo.join()


if __name__ == "__main__":
    unittest.main()

