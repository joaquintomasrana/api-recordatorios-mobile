"""Servicio de prueba de la Actividad 3. Usa Python sin paquetes adicionales."""
import argparse
import json
import sqlite3
from contextlib import closing
import time
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path

BASE = Path(__file__).with_name("recordatorios.sqlite3")


class Servicio(BaseHTTPRequestHandler):
    def responder(self, codigo, datos):
        contenido = json.dumps(datos, ensure_ascii=False).encode()
        self.send_response(codigo)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(contenido)))
        self.end_headers()
        self.wfile.write(contenido)

    def comprobar_modo(self):
        if self.server.modo == "timeout":
            time.sleep(15)
        if self.server.modo in ("400", "500"):
            self.responder(int(self.server.modo), {"error": "Fallo simulado"})
            return False
        return True

    def do_GET(self):
        if self.path != "/recordatorios":
            return self.responder(404, {"error": "Ruta inexistente"})
        if not self.comprobar_modo():
            return
        with closing(sqlite3.connect(BASE)) as conexion:
            filas = conexion.execute("SELECT datos FROM recordatorios ORDER BY clave").fetchall()
        self.responder(200, [json.loads(fila[0]) for fila in filas])

    def do_PUT(self):
        if not self.path.startswith("/recordatorios/"):
            return self.responder(404, {"error": "Ruta inexistente"})
        if not self.comprobar_modo():
            return
        try:
            cantidad = int(self.headers.get("Content-Length", 0))
            if not 0 < cantidad <= 16384:
                raise ValueError()
            dato = json.loads(self.rfile.read(cantidad))
            clave = self.path.split("/")[-1]
            valido = (
                dato["clave"] == clave and bool(clave)
                and isinstance(dato["titulo"], str) and len(dato["titulo"].strip()) >= 4
                and (dato.get("descripcion") is None or
                     isinstance(dato["descripcion"], str) and len(dato["descripcion"]) <= 200)
                and dato["categoria"] in ("Estudio", "Personal")
                and dato["prioridad"] in ("Alta", "Media", "Baja")
                and isinstance(dato["completado"], bool)
                and isinstance(dato["eliminado"], bool)
                and isinstance(dato["fecha"], int) and dato["fecha"] > 0
            )
            if not valido:
                raise ValueError()
        except (ValueError, KeyError, TypeError):
            return self.responder(400, {"error": "Recordatorio inválido"})
        with closing(sqlite3.connect(BASE)) as conexion:
            conexion.execute(
                "INSERT INTO recordatorios(clave, datos) VALUES (?, ?) "
                "ON CONFLICT(clave) DO UPDATE SET datos = excluded.datos",
                (clave, json.dumps(dato)),
            )
            conexion.commit()
        self.responder(200, dato)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--puerto", type=int, default=8080)
    parser.add_argument("--modo", choices=["normal", "400", "500", "timeout"], default="normal")
    opciones = parser.parse_args()
    with closing(sqlite3.connect(BASE)) as conexion:
        conexion.execute("CREATE TABLE IF NOT EXISTS recordatorios (clave TEXT PRIMARY KEY, datos TEXT NOT NULL)")
    servidor = ThreadingHTTPServer(("0.0.0.0", opciones.puerto), Servicio)
    servidor.modo = opciones.modo
    print(f"Servicio disponible en el puerto {opciones.puerto}, modo {opciones.modo}")
    try:
        servidor.serve_forever()
    except KeyboardInterrupt:
        servidor.server_close()


if __name__ == "__main__":
    main()

