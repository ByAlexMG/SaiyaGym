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

## Enlace Anteproyecto

https://sugar-eris-4ce.notion.site/SaiyaGym-8906bcaaf1254997aa0b49cc38a6c4a9?pvs=4

## Enlace CheckPoint

https://sugar-eris-4ce.notion.site/CheckPoint-4538310037164fedb37f11e69b7f933d?pvs=4

Enlace al video: https://youtu.be/HQC_107LtoA

## Enlace Documentación(En Proceso)

https://sugar-eris-4ce.notion.site/SaiyaGym-TFG-7e913d8d54a448119e7341e3a4cff5ed?pvs=4
