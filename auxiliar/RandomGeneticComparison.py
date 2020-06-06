# -*- coding: utf-8 -*-

# Representa el máximo fitness dado por la elección aleatoria de bots y por
# el algoritmo genético según el número total de bots evaluados.

import csv
import numpy as np
import matplotlib.pyplot as plt

with open('FILE', 'r') as csvfile:
    so = csv.reader(csvfile, delimiter=',')
    gen = []
    for row in so:
        fila = np.array([])
        for elem in row:
            fila = np.append(fila, float(elem))
        gen.append(fila)
    gen = np.array(gen)
    
bestGen = np.max(gen, axis=1)
    
with open('FILE', 'r') as csvfile:
    so = csv.reader(csvfile, delimiter=',')
    data = []
    for row in so:
        fila = np.array([])
        for elem in row:
            fila = np.append(fila, float(elem))
        data.append(fila)
    data = np.array(data)

data = data.ravel()
bestAc = [np.max(data[0:i+1]) for i in range(len(data))]

plt.figure(figsize=(10,10))
plt.title('Mayor fitness')
plt.xlabel('Bots evaluados')
plt.ylabel('Fitness')
plt.plot(np.arange(len(gen))*50+10, bestGen, '-b')
plt.plot(np.arange(len(data)), bestAc, '-r')
plt.savefig('FILE')
plt.show()