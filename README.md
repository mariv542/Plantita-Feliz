# 🌱 Plantita Feliz

Bienvenido a **Plantita Feliz**, una aplicación diseñada para el cuidado inteligente y automatizado de tus plantas. 🌸💧☀️

---

## 👥 Autores
- **Cristobal Martinez**
- **Hilary Varela**

---

## 🎯 Propósito y problema que resuelve
**Plantita Feliz** busca ofrecer un cuidado más preciso y automatizado de las plantas, pensando en distintos tipos de usuarios:

1. Personas con vidas ocupadas pero amantes de la naturaleza. 🌿
2. Adultos mayores con poca movilidad. 🧓
3. Usuarios que viajan con frecuencia y desean monitoreo remoto. ✈️
4. Empresas agrícolas que necesitan automatizar su producción a gran escala. 🚜
5. Cualquier persona que quiera un cuidado exacto de sus plantas con alertas y datos en tiempo real. 📲

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

---

## ⚠️ Requerimientos
Emulador requerido Pixel 6 Api 34

---

## 🛠️ Guia de instalacion y ejecucion

1. Crear una carpeta en tu escritorio y le asignas el nombre que gustes
2. Abres la carpeta
3. Le das click derecho opciones abrir con gitbash
4. Al abrir la consola escribes el siguiente comando
```   
git clone https://github.com/mariv542/Plantita-Feliz.git
``` 
5. Al darle intro va a descargar el proyecto dentro de la carpeta
6. Abres el editor de android estudio
7. En el editor a la ezquina izquierda sale una opcion de project
8. Buscas abrir proyecto y buscas el que descargaste en la carpeta que creaste y lo seleccionas
9. Despues dejas que cargue el proyecto dura un poco
10. Arriba a la derecha te va a salir la opcion para que escojas el emulador de la aplicacion en android
11. Importante seleccionar un dispositivo Pixel 6 Api 34 (si no se selecciona va a quedar congelado)
12. Despues de seleccionar el emulador requerido
13. Arriba a la derecha del editor sale una flecha a la derecha verde y le das click
14. Con esto el proyecto debe funcionar sin ningun problema.

## Te invitamos a Explorar la aplicacion y ver como evoluciona con el tiempo <3

---
