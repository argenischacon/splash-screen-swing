# splash-screen-swing

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven&logoColor=white)
![FlatLaf](https://img.shields.io/badge/FlatLaf-Swing%20L&F-green)
![SLF4J](https://img.shields.io/badge/SLF4J-Logging-lightgrey)

Una librería ligera y moderna para implementar pantallas de carga (Splash Screens) en aplicaciones Java Swing. Utiliza **FlatLaf** para renderizado de iconos SVG y ofrece una API sencilla basada en `SwingWorker` para gestionar tareas en segundo plano con animaciones fluidas.

## Características

*   **Soporte SVG**: Renderizado de iconos vectoriales de alta calidad gracias a FlatLaf.
*   **Animación Fluida**: Barra de progreso animada automáticamente.
*   **API Sencilla**: Basada en una clase abstracta `SplashWorker` fácil de implementar.
*   **Tiempo Mínimo de Visualización**: Garantiza que el splash screen se muestre el tiempo suficiente para ser legible, incluso si las tareas son muy rápidas.

## Tecnologías

*   **Java 21**
*   **Maven**
*   **FlatLaf** (Look and Feel & Extras)
*   **SLF4J**

## Instalación

Clona el repositorio e instálalo en tu repositorio local de Maven:

```bash
git clone https://github.com/tu-usuario/splash-screen-swing.git
cd splash-screen-swing
mvn clean install
```

Luego, añade la dependencia a tu proyecto:

```xml
<dependency>
    <groupId>com.argenischacon</groupId>
    <artifactId>splash-screen-swing</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Uso

La forma más sencilla de usar la librería es instanciar `SplashScreen` y extender `SplashWorker` para definir tus tareas de inicialización.

### Ejemplo Básico

```java
import com.argenischacon.splash.SplashScreen;
import com.argenischacon.splash.SplashWorker;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        // Configurar el Look and Feel (opcional pero recomendado)
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            // 1. Crear la instancia visual del Splash Screen
            // Puedes pasar la ruta a tu propio icono SVG en el classpath
            SplashScreen splash = new SplashScreen("/icons/mi-logo.svg");

            // 2. Crear el worker para las tareas de fondo
            SplashWorker worker = new SplashWorker(splash) {
                @Override
                protected void performTasks() throws Exception {
                    // Simula tareas de carga (conexión a BD, carga de recursos, etc.)
                    updateProgress(10, "Conectando al servidor...");
                    Thread.sleep(500);

                    updateProgress(50, "Cargando configuración...");
                    Thread.sleep(800);

                    updateProgress(90, "Iniciando interfaz...");
                    Thread.sleep(400);
                }

                @Override
                protected void onFinished() {
                    // 3. Lanzar la ventana principal cuando termine
                    new MainFrame().setVisible(true);
                }
            };

            // Iniciar el proceso
            worker.start();
        });
    }
}
```

### Componentes Principales

*   **`SplashScreen`**: Un `JWindow` que muestra el logo, una barra de progreso y un texto de estado.
*   **`SplashWorker`**: Clase abstracta que maneja el hilo de fondo (`SwingWorker`) y la animación de la barra de progreso. Debes implementar `performTasks()` y opcionalmente `onFinished()`.

## Requisitos

*   JDK 21 o superior.
