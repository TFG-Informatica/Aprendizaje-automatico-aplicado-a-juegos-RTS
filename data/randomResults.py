# -*- coding: utf-8 -*-

import csv
import numpy as np

with open('random1.csv', 'r') as csvfile:
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

rangos = np.arange(0,15,2);
for i in range(len(rangos)-1):
    print("Entre " + str(rangos[i]) + " y " + str(rangos[i+1]) + ":", np.count_nonzero((data > rangos[i]) & (data <= rangos[i+1])))