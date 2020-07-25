# Aprendizaje automatico aplicado a juegos RTS

Este proyecto consiste en el desarrollo de un bot capaz de jugar al juego de estrategia en tiempo real simplificado [microRTS](https://github.com/santiontanon/microrts).

Los dos bots que se han desarrollado combinan el uso de comportamientos precodificados para cada tipo de unidad del juego con un algoritmo genético para encontrar la combinación de estos comportamientos más adecuada para el tablero sobre el que se va a jugar.

## Autoría

El  proyecto ha sido realizado por Rubén Ruperto Díaz y Rafael Herrera Troca, estudiantes del Doble Grado en Ingeniería Informática y Matemáticas de la Universidad Complutense de Madrid (UCM).

Forma parte de su Trabajo de Fin de Grado en Ingeniería Informática, dirigido por los profesores Antonio Alejandro Sánchez Ruiz-Granados y Pedro Pablo Gómez Martín.

## Contenido

El proyecto incluye el código de los dos bots implementados, escrito en Java:

 - _SingleStrategy_ es un bot sencillo que juega a microRTS empleando una estrategia fija durante toda la partida. Dicha estrategia está construida a partir de comportamientos básicos precodificados para cada tipo de unidad del juego.
 - _MultiStrategy_ es un bot más complejo desarrollado a partir de SingleStrategy. En lugar de tener una estrategia fija, puede cambiar a medida que avanza la partida. El número de estrategias que se utilizan en cada partida se puede configurar previamente. 

Ambos bots emplean un algoritmo genético para encontrar la mejor combinación de comportamientos para cada fase del juego.

Además del código de los bots, se incluye también el código empleado para probar ambos bots y comprobar su desempeño contra otros jugadores automáticos.

Por último, también están incluidos en el proyecto los _scripts_ de Python utilizados para generar gráficas a partir de los datos almacenados resultantes de los enfrentamientos con otros bots.

## Instalación

Para la instalación del proyecto usando el entorno de desarrollo Eclipse, es necesario descargar también el código de [microRTS](https://github.com/santiontanon/microrts).
Una vez hecho esto, se debe añadir al _build path_ ambos proyectos y la librería _jdom.jar_ incluida en microRTS.

## Detalles del proyecto

La descripción detallada de todo el trabajo, los dos bots desarrollados y los resultados obtenidos se puede encontrar en la [memoria](https://github.com/TFG-Informatica/Aprendizaje-automatico-aplicado-a-juegos-RTS/blob/master/Memoria%20del%20proyecto.pdf) del proyecto.

## Licencia

El código de este proyecto se encuentra sujeto a una [licencia GPL 3.0](https://github.com/TFG-Informatica/Aprendizaje-automatico-aplicado-a-juegos-RTS/blob/master/LICENSE).

La memoria del proyecto se encuentra sujeta a una [licencia CC BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/deed.es).
