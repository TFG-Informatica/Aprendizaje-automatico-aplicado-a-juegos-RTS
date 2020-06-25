# Aprendizaje automatico aplicado a juegos RTS

Este proyecto consiste en el desarrollo de un bot capaz de jugar al juego de estrategia en tiempo real simplificado microRTS. 

Ha sido realizado por Rubén Ruperto Díaz y Rafael Herrera Troca, estudiantes del Doble Grado en Ingeniería Informática y Matemáticas de la Universidad Complutense de Madrid (UCM), como parte de su Trabajo de Fin de Grado en Ingeniería Informática, dirigido por los profesores Antonio Alejandro Sánchez Ruiz-Granados y Pedro Pablo Gómez Martín.

El proyecto incluye el código de los bots implementados:
 - SingleStrategy juega a microRTS empleando una estrategia fija durante toda la partida, construida a partir de comportamientos básicos para cada tipo de unidad del juego.
 - MultiStrategy cambia de estrategia a medida que avanza la partida, pudiéndose configurar el número de estrategias que se quiere utilizar.
Asimismo, se incluye también el código empleado para probar ambos bots y comprobar su desempeño contra otros jugadores automáticos.

Para instalarlo usando el entorno de desarrollo Eclipse, es necesario descargar también el código de [microRTS](https://github.com/santiontanon/microrts) y añadir al _build path_ ambos proyectos y la librería _jdom.jar_ de microRTS.

Durante el proyecto también se ha redactado una [memoria](https://github.com/TFG-Informatica/Aprendizaje-automatico-aplicado-a-juegos-RTS) en la que se describe detalladamente todo el trabajo, los dos bots desarrollados y los resultados obtenidos.

