# 🎮 Game Vault - Sci-Fi Android Catalog

## 📝 Descripción del Proyecto
Game Vault es una aplicación nativa para Android que explora un catálogo en tiempo real de videojuegos "Free-To-Play". Diseñada bajo una estética Cyberpunk / Sci-Fi de alto contraste, la aplicación demuestra la implementación de arquitecturas escalables, consumo asíncrono de APIs REST y la construcción de interfaces de usuario avanzadas y 100% declarativas.

## 🚀 Arquitectura y Stack Tecnológico
El proyecto está estructurado bajo el patrón de diseño **MVVM (Model-View-ViewModel)**, garantizando la separación de responsabilidades entre la capa de red, la gestión del estado y la interfaz visual.

*   **Lenguaje:** Kotlin
*   **UI Framework:** Jetpack Compose (100% Declarativo)
*   **Arquitectura:** MVVM
*   **Networking:** Retrofit2 + Gson (Consumo de *Free-To-Play Games API*)
*   **Carga de Imágenes:** Coil (Carga asíncrona mediante URLs y corrutinas)
*   **Navegación:** Navigation Compose (`NavHost`, `NavController`)

## 📱 Vistas y Características
*   **Splash Screen (Cyber Interface):** Pantalla de inicialización de sistema inmersiva con bordes biselados (`CutCornerShape`), gradientes de luces de neón (`Brush`) y navegación automática gestionada mediante *Coroutines*.
*   **Home (Launcher):** Interfaz principal dinámica que responde a los estados de carga del `ViewModel`. Implementa listas horizontales (`LazyRow`) para destacar juegos en un carrusel, *Chips* interactivos para filtrar por categoría (Shooter, MMORPG, etc.) y una grilla optimizada de 2 columnas para el catálogo general. Incluye una *Bottom Navigation Bar* personalizada.
*   **Detail Screen (Ficha Técnica):** Vista expandida del juego seleccionado que renderiza los datos en tiempo real pasando el ID a través de la navegación. Presenta un póster inmersivo, un dashboard con estadísticas del título y un *Floating Action Button* estilizado con gradientes de cian a magenta.

## ⚙️ Instalación y Uso

Para ejecutar este proyecto en tu entorno local, asegúrate de tener instalado **Android Studio** y sigue estos pasos:

1. **Clonar el repositorio:**
   Abre tu terminal y ejecuta el siguiente comando:
   ```bash
   git clone [https://github.com/SamuPV05/Game-Vault.git](https://github.com/SamuPV05/Game-Vault.git)
   ---
**Desarrollado por:**
Samuel Pérez Valencia

👨‍💻 Samuel Pérez Valencia  
📚 *Desarrollo de Software - Universidad Católica Luis Amigó*
