# splash-screen-swing

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven&logoColor=white)
![FlatLaf](https://img.shields.io/badge/FlatLaf-Swing%20L&F-green)
![SLF4J](https://img.shields.io/badge/SLF4J-Logging-lightgrey)

Una librería ligera y moderna para implementar pantallas de carga (Splash Screens) en aplicaciones Java Swing. Utiliza **FlatLaf** para renderizado de iconos SVG y ofrece una API fluida (Builder Pattern) basada en `SwingWorker` para gestionar tareas en segundo plano con animaciones fluidas.

## Características

*   **Soporte SVG**: Renderizado de iconos vectoriales de alta calidad gracias a FlatLaf.
*   **Animación Fluida**: Barra de progreso animada automáticamente.
*   **API Fluida**: Configuración sencilla y legible mediante un patrón Builder para definir tareas y callbacks.
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

La forma más sencilla de usar la librería es instanciar `SplashScreen` y utilizar el `SplashWorker` con su builder para definir tus tareas de inicialización.

### Ejemplo Básico

```java
import com.argenischacon.splash.SplashScreen;
import com.argenischacon.splash.SplashWorker;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class MainApp {
    public static void main(String[] args) {
        // Configurar el Look and Feel (opcional pero recomendado)
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            // 1. Crear la instancia visual del Splash Screen
            // Puedes pasar la ruta a tu propio icono SVG en el classpath
            SplashScreen splash = new SplashScreen("/icons/mi-logo.svg");

            // 2. Configurar y construir el SplashWorker usando la API fluida
            SplashWorker worker = SplashWorker.create(splash)
                    .addTask("Conectando al servidor...", () -> Thread.sleep(500))
                    .addTask("Cargando configuración...", () -> Thread.sleep(800))
                    .addTask("Iniciando interfaz...", () -> Thread.sleep(400))
                    .onError(e -> {
                        // Manejo de errores
                        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                    })
                    .onFinished(() -> {
                        // 3. Lanzar la ventana principal cuando termine
                        // new MainFrame().setVisible(true);
                        System.out.println("Aplicación iniciada");
                    })
                    .build();

            // 4. Iniciar el proceso
            worker.start();
        });
    }
}
```

### Componentes Principales

*   **`SplashScreen`**: Un `JWindow` que muestra el logo, una barra de progreso y un texto de estado.
*   **`SplashWorker`**: Orquestador que maneja las tareas en segundo plano y la animación. Se configura mediante `SplashWorker.create(splash)...build()`, permitiendo agregar tareas secuenciales y definir callbacks de éxito o error.

## Requisitos

*   JDK 21 o superior.
