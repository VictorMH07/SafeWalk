# SafeWalk

Aplicación móvil Android de acompañamiento para trayectos. Permite registrar contactos de confianza, iniciar un trayecto indicando origen y destino, ver el recorrido en un mapa mientras se camina y finalizarlo con el botón **"Llegué bien"**. Los trayectos quedan guardados en un historial.

Proyecto de aula — Desarrollo de aplicaciones para dispositivos móviles
Ingeniería de Sistemas, Universidad de Nariño (Ipiales)
Docente: Martha Nubia Carrillo Obando
Integrante: Victor Manuel Hernandez

## Funcionalidades

- **Perfil de usuario:** nombre, teléfono y correo, con estadísticas de trayectos completados y contactos.
- **Contactos de confianza:** crear, editar, eliminar y marcar favoritos.
- **Crear e iniciar trayecto:** origen, destino y contacto de confianza opcional.
- **Ubicación en tiempo real** durante el trayecto, con solicitud de permiso.
- **Mapa con recorrido:** Google Maps con la ruta recorrida y la posición actual.
- **Finalizar trayecto** con "Llegué bien".
- **Historial** de trayectos con fecha y estado.
- **Persistencia local** con Room: la información se conserva al cerrar la app.
- **Modo claro y oscuro**, incluido el estilo del mapa.

## Tecnologías

| Herramienta                    | Uso                       |
|:-------------------------------|:--------------------------|
| Kotlin                         | Lenguaje de la aplicación |
| Jetpack Compose + Material 3   | Interfaz gráfica          |
| Room (con KSP)                 | Base de datos local       |
| ViewModel + StateFlow          | Estado de las pantallas   |
| Google Play Services Location  | Ubicación del dispositivo |
| Maps Compose (Google Maps SDK) | Mapa y recorrido          |
| Git y GitHub                   | Control de versiones      |

## Estructura del proyecto

```
app/src/main/java/com/example/safewalk/
├── data/
│   ├── local/        Entidades, DAOs y base de datos Room
│   └── location/     Cliente de ubicación y puntos del recorrido
├── ui/
│   ├── components/   Tarjetas, chips, botón "Llegué bien" y mapa
│   ├── home/         Pantalla de inicio y trayecto activo
│   ├── contactos/    Gestión de contactos de confianza
│   ├── perfil/       Perfil de usuario
│   └── theme/        Colores, tipografía y tema
└── MainActivity.kt
```

## Cómo ejecutar el proyecto

1. Clona el repositorio y ábrelo con **Android Studio**.
2. Obtén una clave de API de **Maps SDK for Android** en Google Cloud Console.
3. Agrega la clave al archivo `local.properties` de la raíz (este archivo no se sube al repositorio):
   ```
   MAPS_API_KEY=tu_clave_aqui
   ```
   Sin la clave el proyecto compila, pero el mapa se muestra en blanco.
4. Sincroniza Gradle y ejecuta la app en un emulador con **Google Play / Google APIs** o en un teléfono Android.
5. En el emulador, simula la ubicación desde los controles extendidos (`⋮` > **Location**).

Requisitos: Android 7.0 (API 24) o superior.

## Limitaciones conocidas

- La ubicación se sigue solo mientras la app está abierta en pantalla.
- La app dibuja el recorrido realizado; no calcula ni muestra la ruta hacia el destino.
- El recorrido no se guarda en el historial, solo los datos del trayecto.
- No envía notificaciones ni mensajes a los contactos de confianza.