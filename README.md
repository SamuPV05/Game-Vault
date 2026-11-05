# 🎮 Game Vault 2.0 — Cyberpunk Gaming Ecosystem

## 📝 Descripción del Proyecto
**Game Vault** es un ecosistema interactivo nativo para Android diseñado bajo una estética **Cyberpunk / Sci-Fi** de alto contraste. Nacida como un catálogo de consulta, la aplicación ha evolucionado hacia una plataforma integral para gamers que combina descubrimiento de juegos en tiempo real, radar de ofertas 100% gratuitas, gestión personal de colección (*Backlog*), minijuegos arcade competitivos y un sistema de progresión con niveles e insignias.

---

## 🚀 Arquitectura y Stack Tecnológico
El proyecto sigue estrictamente el patrón **MVVM (Model-View-ViewModel)** con **Unidirectional Data Flow (UDF)** mediante `StateFlow` y estados inmutables, garantizando modularidad, resiliencia ante fallos de red y cero crashes en tiempo de ejecución.

* **Lenguaje:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3 + Diseño Cyberpunk personalizado)
* **Arquitectura:** MVVM + Clean Repository Pattern + UDF (`StateFlow` / `collectAsState`)
* **Networking:** Retrofit 2 + Gson (Consumo multi-API asíncrono con corrutinas)
  * *FreeToGame API* (Catálogo general Free-To-Play)
  * *RAWG Video Games Database API* (Metacritic Duel & Mystery Cover)
  * *GamerPower API* (Loot Radar: Giveaways y juegos 100% gratis en Steam, Epic Games y GOG)
* **Persistencia Local:** `SharedPreferences` + serialización JSON (`DuelPreferences`, `VaultPreferences`)
* **Carga de Imágenes:** Coil (`AsyncImage`)
* **Navegación:** Navigation Compose (`NavHost` con barra inferior de 5 módulos)

---

## 🌐 Los 5 Pilares del Ecosistema

### 1. 🏛️ Catálogo Inteligente (`HomeScreen` & `DetailScreen`)
* **Buscador Cyberpunk en Tiempo Real:** Filtra instantáneamente por título, género, estudio desarrollador o plataforma.
* **Filtros Dinámicos y Carrusel Featured:** Navegación por categorías (*Shooter, MMORPG, Strategy, Racing, Sports, Fighting*) y acceso rápido a tu bóveda.
* **Ficha Técnica Interactiva:** Póster inmersivo, metadatos del título, clasificación directa en el Backlog, calificación por estrellas y botón **`INITIATE`** conectado al navegador oficial del juego.

### 2. 🎁 Loot Radar (`LootScreen`)
* **Escáner de Juegos 100% Gratis:** Rastrea en tiempo real promociones temporales *Free-to-Keep* y DLCs gratuitos en **Steam, Epic Games Store, GOG y PC**.
* **Calculadora de Botín:** Muestra el valor total en dólares (`$ USD`) disponible para ahorrar en el momento.
* **Recompensas de XP:** Reclamar botines abre el enlace oficial de la tienda y otorga **+30 XP** al perfil del jugador.

### 3. 🕹️ Arcade Vault (`ArcadeScreen`)
Hub de minijuegos con vibración háptica (`LocalHapticFeedback`), animaciones reactivas y récords persistentes:
* **Metacritic Duel (`DuelScreen`):** Desafío *Higher or Lower* donde debes predecir si el juego retador tiene un Metascore más alto o más bajo que el campeón actual. Incluye animación *shake* en fallos, sistema de rangos dinámicos (*Rookie Glitch, Street Runner, Netrunner Pro, Overclock Master, Cyber Legend*) y opción para compartir tu récord en redes sociales.
* **Mystery Cover (`CoverQuizViewModel`):** Adivina el videojuego oculto tras una captura distorsionada ópticamente (`Modifier.blur`). Cuenta con 3 vidas y el sistema de riesgo/recompensa **"ENFOCAR LENTE"**: adivina con máxima distorsión para ganar **+3 pts** o aclara la imagen por menos puntos.

### 4. 📦 The Backlog Matrix (`BacklogScreen`)
* **Gestor Personal por Estados:** Clasifica tus juegos en 🟡 **`EN COLA`**, 🔵 **`EN CURSO`**, 🟢 **`COMPLETADOS`** o 🔴 **`ABANDONADOS`**.
* **Calificación Personal:** Evalúa tus juegos de 1 a 5 estrellas (`2..10 pts`) directamente desde la tarjeta o su ficha técnica.
* **Ruleta "DECIDE POR MÍ" (`HackRandomDialog`):** ¿No sabes a qué jugar hoy? El escáner selecciona aleatoriamente un título pendiente de tu bóveda y te permite ponerlo **`EN CURSO`** con un toque.

### 5. 👤 Netrunner Profile (`ProfileScreen`)
* **Motor de Progresión y XP:** Cada acción en la app suma experiencia real (guardar juegos, completar títulos, calificar, acertar en el Arcade o reclamar juegos gratis).
* **Clases Operativas (Avatares):** Selección persistente entre 4 identidades Cyberpunk (*GHOST_RUNNER, CHROME_RONIN, ZERO_COOL, NEON_VALKYRIE*).
* **Telemetría y Logros:** Estadísticas globales (mejor racha, total de aciertos, género favorito automático) y **6 insignias desbloqueables** con barras de progreso.

---

## 📂 Estructura de Paquetes

```text
com.example.gamevault
├── data/
│   ├── CatalogRepository.kt    # Repositorio de catálogo FreeToGame
│   ├── DuelRepository.kt       # Repositorio RAWG API + Mazo de respaldo Metacritic
│   ├── LootRepository.kt       # Repositorio GamerPower API (Giveaways)
│   ├── DuelPreferences.kt      # Persistencia de récords, XP y perfil Netrunner
│   └── VaultPreferences.kt     # Persistencia JSON del Backlog Matrix y calificaciones
├── model/
│   ├── BacklogModels.kt        # Estados y entradas del Backlog Matrix
│   ├── GameDuelModels.kt       # DTOs de RAWG, rangos Cyberpunk y modelos de duelo
│   ├── LootModels.kt           # DTOs y modelos de dominio para Loot Radar
│   └── ProfileModels.kt        # Avatares, logros y resumen de perfil
├── viewmodel/
│   ├── DuelViewModel.kt        # Lógica y StateFlow de Metacritic Duel
│   ├── CoverQuizViewModel.kt   # Lógica y StateFlow de Mystery Cover
│   └── LootViewModel.kt        # Lógica y StateFlow de Loot Radar
├── ui/
│   ├── components/
│   │   └── CyberComponents.kt  # Paleta Cyberpunk, BottomNav de 5 pestañas y widgets
│   ├── SplashScreen.kt         # Pantalla de inicio animada
│   ├── HomeScreen.kt           # Catálogo principal con buscador y filtros
│   ├── DetailScreen.kt         # Ficha técnica + panel Backlog Matrix
│   ├── LootScreen.kt           # Radar de juegos gratis y ofertas
│   ├── ArcadeScreen.kt         # Hub de minijuegos (Metacritic Duel + Mystery Cover)
│   ├── DuelScreen.kt           # Interfaz de Metacritic Duel
│   ├── BacklogScreen.kt        # Gestor de colección + Ruleta "Decide por mí"
│   └── ProfileScreen.kt        # Perfil Netrunner, nivel, XP y logros
├── Game.kt                     # Modelo base del catálogo
├── GameApiService.kt           # Cliente Retrofit para FreeToGame
├── GameViewModel.kt            # ViewModel principal del catálogo, bóveda y perfil
└── MainActivity.kt             # Punto de entrada y NavHost central
```

---

## ⚙️ Instalación y Uso

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/SamuPV05/Game-Vault.git
   ```
2. **Abrir en Android Studio:**
   Importa la carpeta del proyecto y espera a que finalice la sincronización de Gradle.
3. **(Opcional) Configurar RAWG API Key:**
   El modo Arcade funciona de inmediato gracias a su mazo integrado de respaldo. Si deseas consultar nuevos lotes en vivo desde RAWG, coloca tu clave en `DuelRepository.kt` (`RAWG_API_KEY`).
4. **Ejecutar:**
   Presiona **Run ▶** (`Shift + F10`) sobre un emulador o dispositivo físico con Android 7.0 (API 24) o superior.

---

**Desarrollado por:**  
👨‍💻 **Samuel Pérez Valencia**  
📚 *Desarrollo de Software - Universidad Católica Luis Amigó*
