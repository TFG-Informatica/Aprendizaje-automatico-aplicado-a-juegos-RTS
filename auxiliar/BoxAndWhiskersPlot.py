# -*- coding: utf-8 -*-

# Representa diagramas de bigotes con el fitness de los individuos en
# función de la estrategia que sigue cada uno de los tipos de tropas.

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns


data = pd.read_csv("FILE")

colors = ['#BD190E', '#C6870B', '#71AF06', '#0D8915', '#14A8D5', '#071CD7', '#6E1CDA', '#CD16B4']
sns.set_style("whitegrid")
sns.set_palette(sns.color_palette(colors))

data['FitTot'] = data['Fit15'] // 1000;

for i in range(6):
    plt.figure(figsize=(10,10))    
    if (data.columns.values[i] == 'LightBeh'):
        ax = sns.boxplot(x=data.columns.values[i], y="FitTot", data=data[data.BarBeh=='LIGHT'][(data.WorkBeh=='HARVESTER') 
                                                 | (data.WorkBeh=='ONEHARVAGGR') 
                                                 | (data.WorkBeh=='TWOHARVAGGR') 
                                                 | (data.WorkBeh=='THREEHARVAGGR')], width=.3, linewidth=3)
    elif (data.columns.values[i] == 'HeavyBeh'):
        ax = sns.boxplot(x=data.columns.values[i], y="FitTot", data=data[data.BarBeh=='HEAVY'][(data.WorkBeh=='HARVESTER') 
                                                 | (data.WorkBeh=='ONEHARVAGGR') 
                                                 | (data.WorkBeh=='TWOHARVAGGR') 
                                                 | (data.WorkBeh=='THREEHARVAGGR')], width=.3, linewidth=3)
    elif (data.columns.values[i] == 'RangedBeh'):
        ax = sns.boxplot(x=data.columns.values[i], y="FitTot", data=data[data.BarBeh=='RANGED'][(data.WorkBeh=='HARVESTER') 
                                                 | (data.WorkBeh=='ONEHARVAGGR') 
                                                 | (data.WorkBeh=='TWOHARVAGGR') 
                                                 | (data.WorkBeh=='THREEHARVAGGR')], width=.3, linewidth=3)
    else:
        ax = sns.boxplot(x=data.columns.values[i], y="FitTot", data=data, width=.3, linewidth=3)
    for j,patch in enumerate(ax.artists):
        r, g, b, a = patch.get_facecolor()
        patch.set_facecolor((r, g, b, .5))
        patch.set_edgecolor(colors[j])    
        for k in range(j*6,j*6+6):
            line = ax.lines[k]
            line.set_color(colors[j])
            line.set_mfc(colors[j])
            line.set_mec(colors[j])
    plt.title("Fitness obtenido según la variable " + data.columns.values[i], x=0.5, y=1.025)
    plt.savefig(data.columns.values[i] + "FILE")
    