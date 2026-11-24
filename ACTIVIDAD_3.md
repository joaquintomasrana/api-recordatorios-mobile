# Actividad 3 Aplicación de recordatorios

La aplicación conserva los recordatorios en Room y la configuración en Preferences DataStore. Se puede crear, consultar, editar, completar y eliminar sin depender del servidor. La primera ejecución presenta una lista vacía para que los datos que se prueben sean los ingresados por el usuario.

## Ejecutar

1. Abrir el proyecto con Android Studio y sincronizar Gradle.
2. Usar el SDK 37 y un emulador con API 24 o superior.
3. Iniciar el servicio con `python servidor/servidor.py`. Requiere Python 3, sin paquetes adicionales.
4. Ejecutar la configuración `app` de Android Studio.
5. Crear un registro y tocar Sincronizar. La app también intenta sincronizar al abrirse y cuando Android informa que vuelve la conectividad.

La URL predeterminada es `http://10.0.2.2:8080/`. Esa dirección permite que el emulador llegue al servicio de la computadora. Para un teléfono físico, poner la IP local de la computadora y el puerto 8080 en la propiedad `API_BASE_URL`, siguiendo `configuracion.example.properties`. Ambos equipos deben estar en la misma red y el firewall debe permitir el puerto. La URL debe terminar en barra. El HTTP sin cifrar está habilitado para este servicio de prueba local. Para un servidor público se debe configurar HTTPS.

Para compilar desde una terminal con JDK disponible:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

## Evolución y organización

Antes los datos estaban en una lista que se perdía al cerrar. Ahora Room guarda los registros en `recordatorios.db`. El DAO devuelve Flow, por lo que una modificación local actualiza las pantallas sin recargar manualmente listas.

```text
Compose → ViewModel → RepositorioLocal → DAO → Room
                            ↓                ↓ Flow
                       Retrofit           ViewModel → Compose
                            ↓
                      Servicio Python
```

Los modelos de la interfaz están en model. RecordatorioEntity representa la tabla y RecordatorioDto representa el JSON remoto. Las funciones aModelo, aDto y aEntidad hacen las transformaciones. Las pantallas reciben modelos y eventos mediante sus ViewModel.

## Preferencias

DataStore tiene una sola instancia y guarda tres opciones pequeñas. El tema oscuro empieza desactivado, mostrar completados empieza activado y el orden inicial es Fecha. Ajustes permite modificarlas. El tema se aplica en MainActivity y el repositorio combina el flujo de preferencias con el de Room para ordenar y filtrar.

Los cambios se escriben con funciones suspendidas desde PreferenciasViewModel. Ante un error recuperable de lectura se usan los valores predeterminados. Un orden desconocido se interpreta como Fecha. Un fallo de escritura se muestra en Ajustes. DataStore no guarda los recordatorios.

## Persistencia y sincronización

Cada registro tiene un ID local autogenerado y una clave UUID que también identifica su copia remota. Crear o editar escribe primero en Room y marca el registro pendiente. Eliminar conserva una marca de borrado que se enviará más tarde y oculta inmediatamente el registro del listado.

La sincronización envía los pendientes con PUT a una ruta que contiene la clave. El servidor inserta o actualiza por esa clave única. Si la respuesta se pierde después de guardar, repetir el PUT modifica el mismo registro. Un Mutex impide sincronizaciones simultáneas dentro de la app. Al confirmar se comprueba la revisión local para evitar marcar como sincronizada una edición realizada mientras se esperaba la respuesta.

Después del envío se consultan los registros remotos y se incorporan a Room dentro de una transacción. Una modificación local pendiente tiene prioridad frente a esa descarga. Los borrados conservan su marca en ambos lados para evitar que una descarga posterior los vuelva a mostrar.

El estado de cada registro indica Pendiente de sincronización, Sincronizado o Error de sincronización. Los fallos conservan los datos. El listado muestra mensajes diferentes para errores HTTP 4xx, HTTP 5xx, timeout y problemas de conexión. Una IOException significa que no se pudo contactar con el servicio, que puede deberse a la red o a que el servidor esté detenido.

Este servicio es de práctica para un dispositivo, sin usuarios ni autenticación. No incluye sincronización programada con la app cerrada. Al volver a abrir se reintenta. La coordinación entre varios dispositivos que editen el mismo registro a la vez requeriría un protocolo adicional de resolución de conflictos.

## Recorridos para las evidencias

| Prueba | Pasos y resultado esperado |
|---|---|
| Persistencia | Crear dos registros, cerrar completamente la app y abrirla. Ambos deben seguir en el listado. |
| Preferencias | Activar tema oscuro, ocultar completados y elegir Título. Cerrar y abrir. Deben mantenerse las tres opciones. |
| Actualización | Editar un título desde el detalle y volver. El listado debe reflejarlo. |
| Estado | Cambiar a completado y comprobar el filtro de Ajustes. |
| Eliminación | Eliminar desde el detalle, cerrar y abrir. El registro debe seguir oculto. |
| Sin conexión | Detener el servidor, crear, editar y eliminar. Room debe conservar las operaciones. |
| Recuperación | Iniciar el servidor, tocar Sincronizar y comprobar el estado Sincronizado. |
| Duplicados | Tocar Sincronizar varias veces. GET /recordatorios debe tener una sola entrada por clave. |
| HTTP 400 | Iniciar con `--modo 400`. El listado debe informar solicitud inválida. |
| HTTP 500 | Iniciar con `--modo 500`. Debe informar error del servidor. |
| Timeout | Iniciar con `--modo timeout`. Debe informar que el servicio tardó demasiado. |
| Orden inválido | La prueba de preferencias comprueba que una opción desconocida produce Fecha. |

## Resultados de las pruebas automáticas

Verificado el 22 de noviembre de 2026:

- Compilación del APK debug y de las pruebas Android correcta.
- Cuatro pruebas unitarias JVM aprobadas, incluyendo las conversiones entre entidad, DTO y modelo, el rechazo de datos remotos inválidos y el orden predeterminado.
- Cinco pruebas en el emulador API 37 aprobadas. Incluyen cierre y reapertura del archivo Room, actualización y borrado, lectura de preferencias desde otra instancia, funcionamiento sin servidor, pérdida de respuesta después de guardar, reintento sin duplicados y una edición local mientras se sincroniza.
- Una prueba HTTP del servicio Python aprobada. Repite el PUT tres veces con la misma clave, comprueba que existe una sola fila y verifica la marca de borrado y el error HTTP 500.
- El APK final se instaló y abrió correctamente en el emulador, con arranque en frío.

Para repetir las pruebas:

```powershell
python -m unittest discover -s servidor -v
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest --no-configuration-cache
```

La última orden requiere un emulador o dispositivo conectado. Las pruebas de Android usan una base separada de prueba y un servicio falso para controlar los fallos.

Registrar las capturas y los resultados realmente observados al hacer estos recorridos en el emulador. La compilación y las pruebas automáticas no sustituyen las evidencias visuales ni demuestran un cierre y reapertura del proceso.
