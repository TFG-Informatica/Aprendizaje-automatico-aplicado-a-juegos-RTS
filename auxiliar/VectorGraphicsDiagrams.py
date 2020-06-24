# -*- coding: utf-8 -*-


import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import matplotlib.colors as col


data = pd.read_csv("All8x8.csv")
mapaname ="All8x8"

colors = ['#BD190E', '#C6870B', '#71AF06', '#0D8915', '#14A8D5', '#071CD7', '#6E1CDA', '#CD16B4']
sns.set_style("whitegrid")
sns.set_palette(sns.color_palette(colors))

data['FitTot'] = data['FitTot'] // 1000;

plt.figure(figsize=(15,10))

# ax = sns.boxplot(x='WorkBeh', y="FitTot", data=data,
#                   order=['HARVESTER', 'ONEHARVAGGR', 'TWOHARVAGGR','THREEHARVAGGR',
#                         'AGGRESSIVE', 'ONEHARVNOBAR', 'TWOHARVNOBAR', 'THREEHARVNOBAR'],
#                   width=.3, linewidth=3)

ax = sns.stripplot(x='WorkBeh', y="FitTot", data=data, jitter=0.15,
                  order=['HARVESTER', 'ONEHARVAGGR', 'TWOHARVAGGR','THREEHARVAGGR',
                        'AGGRESSIVE', 'ONEHARVNOBAR', 'TWOHARVNOBAR', 'THREEHARVNOBAR'])

for j,patch in enumerate(ax.artists):
    r, g, b, a = patch.get_facecolor()
    patch.set_facecolor((r, g, b, .5))
    patch.set_edgecolor(colors[j])    
    for k in range(j*6,j*6+6):
        line = ax.lines[k]
        line.set_color(colors[j])
        line.set_mfc(colors[j])
        line.set_mec(colors[j])
        
plt.title("Victorias obtenidas según el comportamiento de los trabajadores", x=0.5, y=1.025)
plt.ylabel('Número de victorias')
plt.xlabel('Comportamiento de los trabajadores')
plt.savefig("Trabajadores"+ mapaname +".pdf",format='pdf')

# Ligeros

plt.figure(figsize=(10,10))
ax = sns.boxplot(x='LightBeh', y="FitTot",
                  data=data[data.BarBeh=='LIGHT'][(data.WorkBeh=='HARVESTER') 
                                                  | (data.WorkBeh=='ONEHARVAGGR') 
                                                  | (data.WorkBeh=='TWOHARVAGGR') 
                                                  | (data.WorkBeh=='THREEHARVAGGR')],              
                  width=.3, linewidth=3)

# ax = sns.stripplot(x='LightBeh', y="FitTot",
#                  data=data[data.BarBeh=='LIGHT'][(data.WorkBeh=='HARVESTER') 
#                                                  | (data.WorkBeh=='ONEHARVAGGR') 
#                                                  | (data.WorkBeh=='TWOHARVAGGR') 
#                                                  | (data.WorkBeh=='THREEHARVAGGR')],              
#                  jitter=0.15)


for j,patch in enumerate(ax.artists):
    r, g, b, a = patch.get_facecolor()
    patch.set_facecolor((r, g, b, .5))
    patch.set_edgecolor(colors[j])    
    for k in range(j*6,j*6+6):
        line = ax.lines[k]
        line.set_color(colors[j])
        line.set_mfc(colors[j])
        line.set_mec(colors[j])
        
plt.title("Victorias obtenidas según el comportamiento de los soldados ligeros", x=0.5, y=1.025)
plt.ylabel('Número de victorias')
plt.xlabel('Comportamiento de los soldados ligeros')
plt.savefig("Ligeros"+ mapaname +".pdf",format='pdf')

# Barracas

plt.figure(figsize=(10,10))
ax = sns.boxplot(x='BarBeh', y="FitTot",
                  data=data[(data.WorkBeh=='HARVESTER') | (data.WorkBeh=='ONEHARVAGGR') 
                  | (data.WorkBeh=='TWOHARVAGGR') | (data.WorkBeh=='THREEHARVAGGR')],              
                  width=.3, linewidth=3)
# ax = sns.stripplot(x='BarBeh', y="FitTot",
#                  data=data[(data.WorkBeh=='HARVESTER') | (data.WorkBeh=='ONEHARVAGGR') 
#                  | (data.WorkBeh=='TWOHARVAGGR') | (data.WorkBeh=='THREEHARVAGGR')],              
#                  jitter=0.15)

for j,patch in enumerate(ax.artists):
    r, g, b, a = patch.get_facecolor()
    patch.set_facecolor((r, g, b, .5))
    patch.set_edgecolor(colors[j])    
    for k in range(j*6,j*6+6):
        line = ax.lines[k]
        line.set_color(colors[j])
        line.set_mfc(colors[j])
        line.set_mec(colors[j])
        
plt.title("Victorias obtenidas según el comportamiento del cuartel", x=0.5, y=1.025)
plt.ylabel('Número de victorias')
plt.xlabel('Comportamiento del cuartel')
plt.savefig("Cuartel"+ mapaname +".pdf",format='pdf')

# Base

plt.figure(figsize=(10,10))
ax = sns.boxplot(x='BaseBeh', y="FitTot",
                  data=data,              
                  width=.3, linewidth=3)
# ax = sns.stripplot(x='BarBeh', y="FitTot",
#                  data=data[(data.WorkBeh=='HARVESTER') | (data.WorkBeh=='ONEHARVAGGR') 
#                  | (data.WorkBeh=='TWOHARVAGGR') | (data.WorkBeh=='THREEHARVAGGR')],              
#                  jitter=0.15)

for j,patch in enumerate(ax.artists):
    r, g, b, a = patch.get_facecolor()
    patch.set_facecolor((r, g, b, .5))
    patch.set_edgecolor(colors[j])    
    for k in range(j*6,j*6+6):
        line = ax.lines[k]
        line.set_color(colors[j])
        line.set_mfc(colors[j])
        line.set_mec(colors[j])
        
plt.title("Victorias obtenidas según el comportamiento de la base", x=0.5, y=1.025)
plt.ylabel('Número de victorias')
plt.xlabel('Comportamiento de la base')
plt.savefig("Base"+ mapaname +".pdf",format='pdf')