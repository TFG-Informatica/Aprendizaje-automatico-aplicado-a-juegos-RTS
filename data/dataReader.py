# -*- coding: utf-8 -*-

import csv
import numpy as np
import matplotlib.pyplot as plt

with open('exp.csv', 'r') as csvfile:
    so = csv.reader(csvfile, delimiter=',')
    data = []
    for row in so:
        fila = np.array([])
        for elem in row:
            fila = np.append(fila, float(elem))
        data.append(fila)
    data = np.array(data)

best = np.max(data, axis=1)
means = np.mean(data, axis=1)
std = np.std(data, axis=1)

plt.figure(figsize=(10,10))
plt.title('Fitness de cada individuo por generación')
plt.xlabel('Generación')
plt.ylabel('Fitness')
plt.gca().set_ylim([0, 15])
plt.plot(np.array([[n] * np.shape(data)[1] for n in range(len(data))]).ravel(), data.ravel(), '.r')
plt.savefig('Gráficas/FitnessNew.png')
plt.show()


plt.figure(figsize=(10,10))
plt.title('Mayor fitness por generación')
plt.xlabel('Generación')
plt.ylabel('Fitness')
plt.gca().set_ylim([0, 15])
plt.plot(range(len(data)), best, '-r')
plt.savefig('Gráficas/BestNew.png')
plt.show()

plt.figure(figsize=(10,10))
plt.title('Fitness medio por generación')
plt.xlabel('Generación')
plt.ylabel('Fitness')
plt.gca().set_ylim([0, 15])
plt.plot(range(len(data)), means, '-r')
plt.savefig('Gráficas/MeanNew.png')
plt.show()

plt.figure(figsize=(10,10))
plt.title('Desviación típica por generación')
plt.xlabel('Generación')
plt.ylabel('Fitness')
plt.gca().set_ylim([0, 15])
plt.plot(range(len(data)), std, '-r')
plt.savefig('Gráficas/StdNew.png')
plt.show()
