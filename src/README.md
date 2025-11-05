# 🌡️ Buscador de Temperaturas

Aplicación Java desarrollada bajo el **paradigma de programación funcional**, que permite cargar y procesar registros de temperatura diarios de distintas ciudades de Colombia a partir de un archivo CSV.  
Genera gráficas comparativas y estadísticas de temperaturas promedio, además de identificar la ciudad más y menos calurosa en una fecha específica.

---

## 🧰 Tecnologías y librerías usadas

- ☕ **Java SE 17+**
- 🖥️ **Swing** (para la interfaz gráfica)
- 📊 **JFreeChart** (para generar las gráficas de barras)
- 📅 **JCalendar (DateChooser)** (para seleccionar rangos de fechas)
- 📂 **Programación funcional con Streams y Lambdas**

---

## 🧩 Estructura del proyecto

Buscador_Temperatura/
┣ lib/
┃ ┣ jcalendar-1.4.jar
┃ ┣ jcommon-1.0.24.jar
┃ ┗ jfreechart-1.5.4.jar
┣ src/
┃ ┣ datos/
┃ ┃ ┗ Temperaturas.csv
┃ ┣ entidades/
┃ ┃ ┗ RegistroTemperatura.java
┃ ┣ servicios/
┃ ┃ ┗ TemperaturaServicio.java
┃ ┣ ui/
┃ ┃ ┣ FrmTemperaturas.java
┃ ┃ ┗ BarChartUtil.java
┃ ┗ App.java
┗ .gitignore

---

## ⚙️ Instrucciones de ejecución

### 🔧 Compilar:
```bash
javac -cp "lib/*:src" -d out src/entidades/*.java src/servicios/*.java src/ui/*.java src/App.java

### ▶️ Ejecutar:
 java -cp "lib/*:out" App

💡 En Windows usa ; en lugar de : en el classpath.

## ✨ Funcionalidades principales

- 📂 **Carga automática** de los datos desde el archivo `src/datos/Temperaturas.csv`.
- 🏙️ **Selección de ciudad**: permite filtrar por una ciudad específica o mostrar todas.
- 📅 **Rango de fechas**: el usuario puede elegir el intervalo de tiempo a analizar.
- 📊 **Gráfica de barras**: muestra el **promedio de temperatura por ciudad** dentro del rango seleccionado.
- 🌡️ **Análisis estadístico**: identifica la **ciudad más calurosa** y la **menos calurosa** para una fecha específica.
- 🧮 **Procesamiento funcional**: los datos se manejan mediante **Streams**, **Lambdas** y **colecciones inmutables**.
