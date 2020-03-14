# -*- coding: utf-8 -*-


import numpy as np
import pandas as pd
import matplotlib.pyplot as plt


data = pd.read_csv("GeneticTests24x24.csv")

d = {'HARVESTER': data[data.WorkBeh == 'HARVESTER']['FitTot'], 
     'AGGRESSIVE': data[data.WorkBeh == 'AGGRESSIVE']['FitTot']}
df = pd.DataFrame(data=d)
df.boxplot(column=['HARVESTER', 'AGGRESSIVE'], figsize=(10,10))
plt.savefig("Gráficas/BigotesHarvAggr24x24.png")

plt.figure(figsize=(10,10))
plt.scatter(range(len(df)), df['HARVESTER'], marker='o', c='blue', label='Harvester')
plt.scatter(range(len(df)), df['AGGRESSIVE'], marker='o', c='red', label='Aggresive')
plt.title("Fitness total según si son harvester o aggresive")
plt.xlabel("Bot")
plt.ylabel("Fitness")
plt.legend(loc="lower right")
plt.savefig("Gráficas/FitnessHarvAggr24x24.png")
plt.show()