# SaiyaGym

Autor del proyecto: Alejandro Moreno Garrido

## Descripción
### Resumen General

La aplicación tendrá la función de crear una rutina de ejercicio personalizada para el usuario, la cual dependerá del estado físico actual del mismo y de la meta que quiera alcanzar. Para ello se calculará su porcentaje de grasa corporal y junto a su meta a alcanzar, se creará un plan de ejercicio centrado en el aumento de masa corporal, mantenimiento del mismo o incluso aumentar el gasto calórico mediante ejercicio cardiovasculares de alta intensidad.

### Descripción de Usuario

Usaré la ApiRest de Firebase para almacenar un .json con los categorías de los ejercicios y los ejercicios de cada uno de ellos. Realizaré peticiones a esta API para obtener los ejercicios por parte del usuario, para su posterior almacenamiento en la base de datos de cada usuario.

El usuario podrá registrarse mediante una contraseña y un correo electrónico, el cual podrá usar mas adelante para recuperar la contraseña en caso de que la haya olvidado. Todo esto proceso lo hare mediante Firebase Authentication.

La primera vez que se registre, deberá introducir datos personales suyos como su edad, genero,  altura, peso y porcentaje de grasa al que quiere llegar.

Una vez dentro se le mostrará la vista principal(esta vista se mostrará en caso de que se haya iniciado sesión con antelación) esta vista contendrá 3 fragments (5 fragments en caso de los administradores) para los cuales tendrán una animación personalizada al pasar entre ellos.

La pestaña principal mostrará la tabla de ejercicios que te toca hoy, además al hacer clic en uno de ellos, se desplegará un CardView en el que se mostrarán las repeticiones y las series pertinentes y un video explicativo del mismo(al hacer click en el ejercicio, se expandirá el CardView y después se colapsará mediante unas animaciones personalizadas).

En la pestaña de la izquierda podrá verse un calendario con el grupo muscular a entrenar ese día.

En la ultima pestaña se puede cambiar el correo electrónico, la contraseña, las medidas introducidas para cada usuario y el modo claro oscuro, además de cerrar sesión.

Para controlar los errores, habrá Snackbar que informen del estado de los procesos y de una función que se encargará de guardar los logs en la base de datos(como “INFO” o “ ERROR” dependiendo del origen del mismo.

### Descripción de Administrador

El administrador podrá visualizar 2 opciones extras en la Navigation Bar, las cuales serán “Ejercicios” y “Usuarios”:

- En la vista de Usuarios, el administrador podrá visualizar todos los usuarios que hay registrados y eliminarlos.
- En la vista de Ejercicios, el administrador podrá crear nuevos ejercicios( añadiéndolos a RealTimeDatabase ) y eliminarlos también de la misma manera. En ambos casos se podrá especificar en que categoría se quiere añadir y en que día en especifico.

## Objetivos del Proyecto
Mi objetivo es crear una aplicación para crear rutinas de gimnasio con temática de Dragon Ball para favorecer la inserción de jóvenes fanáticos de dicha saga, al mundo del gimnasio y así poder alcanzar un estilo de vida saludable de una manera entretenida y que sea lo mas amena posible.

## Tecnologias Utilizadas
Para el Front usaré Kotlin, con el cual también realizaré las peticiones a la base de datos y a la API, las cuales estarán alojadas en Firebase.

### IDE

Usaré el IDE de Android Studio, ya que mi proyecto consistirá en una aplicación de móvil y este IDE es el que hemos usado para poder desarrollar apps durante el curso y mis practicas en la empresa.

### Firebase

### **Concurrencia en Firebase**

- **Optimización para Concurrencia**: La base de datos en tiempo real de Firebase está diseñada para manejar altas tasas de concurrencia, permitiendo que múltiples clientes se conecten y realicen operaciones de lectura y escritura simultáneamente. Utiliza un modelo de datos basado en JSON y soporta actualizaciones en tiempo real.
- **Conflictos de Escritura**: Para manejar conflictos de escritura, Firebase utiliza transacciones y reglas de seguridad que permiten definir permisos y restricciones en los datos.
- **Listeners en Tiempo Real**: Los clientes pueden escuchar cambios en tiempo real, lo que significa que las actualizaciones se reflejan casi instantáneamente en todas las instancias conectadas.

### Escalabilidad en Firebase

- **Firestore y Realtime Database**: Ambas bases de datos pueden escalar para manejar grandes volúmenes de tráfico y datos. Firestore, en particular, está diseñada para escalar horizontalmente, lo que significa que puede distribuir la carga de trabajo a través de múltiples servidores para mantener el rendimiento.
- **Storage**: Firebase Storage también está diseñado para escalar automáticamente, permitiendo almacenar y servir grandes cantidades de datos, como imágenes, videos y otros archivos.

### Firebase Authentication

Para Login, Signup y el ForgotPassword, usaré la librería de Firebase llamada “Firebase Authetication”, la cual usa un sistema de verificación mediante JWT(Json Web Token) y se requerirá un correo electrónico y una contraseña.

Tendremos que configurar el tipo de proveedor que vamos a usar para nuestro inicio de sesión

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/31aceda5-deee-448b-823a-832fd8abc7ee/fa18c4f4-e8f8-47ff-8fe0-daeba9716a86/Untitled.png)

### Realtime Database

Para la ApiRest, usaré la librería de Firebase de “Realtime Database”, la cual permite introducir un Json al que se le podrán hacer peticiones de lectura y escritura, dependiendo de los permiso que tenga el usuario que esté realizando las peticiones.

Deberemos configurar las reglas de Realtime Database para permitir que cualquiera pueda leer cualquiera, pero solo editar aquellas personas que tengan el rol de “admin” en su base de datos.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/31aceda5-deee-448b-823a-832fd8abc7ee/aabf8fce-e864-4a97-8ece-efe388c76d62/Untitled.png)

### Firestore Database

Para la base de datos, usaré la librería  Firestore Database, donde alojaré todos los usuarios, los cuales tendrán sus datos personales y los ejercicios que se les han sido asignados por el algoritmo. También usare esta base de datos para almacenar un registro de los Logs de Eventos en mi aplicación.

Tendremos que configurar las reglas de Firestore Database para permitir que los usuarios puedan ver y modificar sus propias bases de datos.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/31aceda5-deee-448b-823a-832fd8abc7ee/89025fe9-f814-4ab4-82bb-d3f4028f518f/Untitled.png)

### Firebase Analytics Dashboard

Usaré la extensión de Firebase llamada “Firebase Analytics Dashboard”, la cual me permite obtener graficas de datos sobre la aplicación, datos que podrían ser útiles a los usuarios.

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/31aceda5-deee-448b-823a-832fd8abc7ee/cfd0ba3d-28b3-4621-ba9c-b0671ed9c2a7/Untitled.png)

Tendremos que sincronizar nuestro proyecto con nuestra cuenta de Firebase, para ello hará falta seguir las instrucciones que nos da Firebase e instalar un archivo en nuestro proyecto llamado “google-service.json” 

> *Nota: Este archivo puede obtenerse descompilando la APK, pero no contiene ninguna información que pueda permitir a nadie alterar datos ni acceder a ninguna información privada.*
>

## Enlace Anteproyecto

https://sugar-eris-4ce.notion.site/SaiyaGym-8906bcaaf1254997aa0b49cc38a6c4a9?pvs=4

## Enlace CheckPoint

https://sugar-eris-4ce.notion.site/CheckPoint-4538310037164fedb37f11e69b7f933d?pvs=4

Enlace al video: https://youtu.be/HQC_107LtoA

## Enlace Documentación(En Proceso)

https://sugar-eris-4ce.notion.site/SaiyaGym-TFG-7e913d8d54a448119e7341e3a4cff5ed?pvs=4
