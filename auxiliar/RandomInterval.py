# -*- coding: utf-8 -*-

# Calcula la cantidad y el porcentaje de individuos con fitness en cada
# intervalo 

import csv
import numpy as np

with open('FILE', 'r') as csvfile:
    so = csv.reader(csvfile, delimiter=',')
    data = []
    for row in so:
        for elem in row:
            data.append(float(elem))
    data = np.array(data)

best = np.max(data)
worst = np.min(data)
mean = np.mean(data)
std = np.std(data)

print("Máximo:", best, "Mínimo:", worst, "Media:", mean, "Desv.tip:", std);

rangos = np.arange(0,29,1);
for i in range(len(rangos)-1):
    print("Entre " + str(rangos[i]) + " y " + str(rangos[i+1]) + ":", np.count_nonzero((data > rangos[i]) & (data <= rangos[i+1])),
          np.count_nonzero((data > rangos[i]) & (data <= rangos[i+1])) / len(data) * 100)