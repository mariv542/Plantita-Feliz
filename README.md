# 🌱 Plantita Feliz

Bienvenido a **Plantita Feliz**, una aplicación diseñada para el cuidado inteligente y automatizado de tus plantas. 🌸💧☀️

---

## 👥 Autores
- **Cristobal Martinez**
- **Hilary Varela**

---
## Requerimientos
Emulador requerido Pixel 6 Api 34
---

## 🎯 Propósito y problema que resuelve
**Plantita Feliz** busca ofrecer un cuidado más preciso y automatizado de las plantas, pensando en distintos tipos de usuarios:

1. Personas con vidas ocupadas pero amantes de la naturaleza. 🌿
2. Adultos mayores con poca movilidad. 🧓
3. Usuarios que viajan con frecuencia y desean monitoreo remoto. ✈️
4. Empresas agrícolas que necesitan automatizar su producción a gran escala. 🚜
5. Cualquier persona que quiera un cuidado exacto de sus plantas con alertas y datos en tiempo real. 📲

---

## 📱 Pantallas iniciales (Activities)

1. **LoginActivity** → Inicio de sesión y registro de usuarios.
2. **MenuActivity** → Menú principal de navegación.
3. **DashboardActivity** → Pantalla principal con histórico de sensores (cada hora).
4. **UserConfigActivity** → Configuración del perfil del usuario.
5. **AlertsActivity** → Alertas sobre el estado de las plantas.
6. **PlantConfigActivity** → Configuración de parámetros y niveles de riesgo de cada planta.

---

## 🔀 Navegación entre pantallas (Intents y Extras)

- **LoginActivity → MenuActivity** → Se envía información del usuario (userId, nombre).
- **MenuActivity → DashboardActivity** → Se pasa la planta seleccionada (plantId).
- **DashboardActivity → AlertsActivity** → Datos de última lectura (nivelHumedad, temperatura).
- **MenuActivity → UserConfigActivity** → Acceso directo al perfil (sin extras).
- **MenuActivity → PlantConfigActivity** → Se envía el ID de la planta (plantId) + parámetros configurados.

---

## 🛠️ Componentes de Android previstos

- **Activities** → Para cada pantalla.
- **Intents** → Para la navegación y paso de datos.
- **Services** → Procesos en segundo plano (Firebase, sensores).
- **BroadcastReceiver** → Notificaciones de cambios y alertas.
- **Content Provider** → (No previsto en esta fase).

---

## ☁️ Servicios y almacenamiento externo

**Firebase Realtime Database** se usará para:
1. Guardar datos históricos de sensores.
2. Gestionar configuraciones de usuarios y parámetros de plantas.
3. Sincronización en tiempo real para alertas.

---

## 📊 Datos manejados

- Información en **JSON** proveniente de sensores (humedad, temperatura).
- Configuración del usuario y parámetros de cada planta.
- Datos almacenados y recuperados desde **Firebase**.

---

## ⚠️ Riesgos o desafíos iniciales

1. Definir la estructura de capas y organización del proyecto. 🗂️
2. Establecer conexión estable y segura con Firebase. 🔐
3. Integrar y configurar sensores con Arduino. 🤖
4. Coordinar eficientemente las tareas del equipo. 👥

---

## 📆 Hitos de avance (próximas 3 semanas)

1. Organización de la estructura del proyecto Android. 📁
2. Asignación y separación de tareas entre el equipo. 🧩
3. Configuración y conexión inicial con Firebase Realtime Database. 🔗
4. Implementación básica de la navegación entre pantallas principales. 📲

---

## 🚀 Tecnologías utilizadas

- **Android (Java/Kotlin)** 📱
- **Firebase Realtime Database** ☁️
- **Arduino + Sensores** 🌡️💧

---

## 💡 Visión a futuro

En siguientes fases, se planea:
- Expansión con más tipos de sensores (luz, pH, nutrientes).
- Dashboard con gráficas en tiempo real.
- Algoritmos de predicción para necesidades de riego o fertilización.
- Versión para **IoT a gran escala** en agricultura.

---

🌱✨ ¡Con Plantita Feliz, tus plantas siempre estarán bien cuidadas!
