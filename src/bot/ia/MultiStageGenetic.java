package bot.ia;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ai.RandomBiasedAI;
import ai.abstraction.WorkerRush;
import ai.core.AI;
import bot.eval.Wins;
import bot.scripts.*;
import bot.scripts.BarrackBehavior.BarBehType;
import bot.scripts.BaseBehavior.BaseBehType;
import bot.scripts.HeavyBehavior.HeavyBehType;
import bot.scripts.LightBehavior.LightBehType;
import bot.scripts.RangedBehavior.RangedBehType;
import bot.scripts.WorkerBehavior.WorkBehType;
import bot.tournaments.ThreadedTournament;
import mrtsFixed.bots.*;
import rts.GameState;
import rts.units.UnitTypeTable;

public class MultiStageGenetic {
	
	private PrintStream OUT = null;
	
	private final int TOURNSIZE = 3;
	private final double MUT_CHANCE = 0.1;
	private final int MAX_CYCLES = 2000;
	
	private GameState gs;
	private UnitTypeTable utt;
	private List<AI> population;
	private List<AI> bestPopulation;
	private List<AI> rivals;
	private double[] evaluation;
	private int popSize; 
	private int bestSize;
	private int eliteSize;
	private boolean visual;
	private int phases;
	
	
	public MultiStageGenetic(int a_popSize, int a_bestSize, int a_eliteSize, int a_phases, UnitTypeTable a_utt, 
					GameState a_gs,  boolean a_visual) throws FileNotFoundException {
		gs = a_gs;
		utt = a_utt;
		popSize = a_popSize;
		bestSize = a_bestSize;
		eliteSize = a_eliteSize;
		phases = a_phases;
		evaluation = new double[popSize];
		visual = a_visual;					
		population = new ArrayList<AI>();
		bestPopulation = new ArrayList<AI>();
		rivals = Arrays.asList(new EconomyMilitaryRush(utt), new EconomyRush(utt), new EconomyRushBurster(utt),
				new EMRDeterministico(utt), new HeavyDefense(utt), new HeavyRush(utt), new LightDefense(utt),
				new LightRush(utt), new RandomBiasedAI(utt), new RangedDefense(utt), new RangedRush(utt),
				new SimpleEconomyRush(utt), new WorkerDefense(utt), /*new WorkerRush(utt),*/ new WorkerRushPlusPlus(utt));
		
		OUT = new PrintStream(new FileOutputStream("data/PruebaNew.csv"));
	}
	
	public void getInitialPopulation() {
		Random r = new Random();
		for (int i = 0; i < popSize; ++i) {
			List<GeneralScript> scripts = new ArrayList<GeneralScript>();
			for (int j = 0; j < phases; ++j) {
				BaseBehType baseBehType = BaseBehType.values()[r.nextInt(BaseBehType.values().length)];
				BarBehType barBehType = BarBehType.values()[r.nextInt(BarBehType.values().length)];
				WorkBehType workBehType = WorkBehType.values()[r.nextInt(WorkBehType.values().length)];
				LightBehType lightBehType = LightBehType.values()[r.nextInt(LightBehType.values().length)];
				HeavyBehType heavyBehType = HeavyBehType.values()[r.nextInt(HeavyBehType.values().length)];
				RangedBehType rangedBehType = RangedBehType.values()[r.nextInt(RangedBehType.values().length)];
				scripts.add(new GeneralScript(utt, baseBehType, barBehType, 
						workBehType, lightBehType, heavyBehType, rangedBehType));
			}
			population.add(new MultiStageGeneralScript(scripts));
		}
	}
	
	public void select(ArrayList<MultiStageGeneralScript> newPopulation) {
		Random r = new Random();
		for (int i = 0; i < popSize - eliteSize; ++i) {
			int best = -1; double bestEval = -100000;
			for (int j = 0; j < TOURNSIZE; ++j) {
				int a = r.nextInt(popSize);
				if (bestEval < evaluation[a]) {
					bestEval = evaluation[a];
					best = a;
				}
			}
			newPopulation.add((MultiStageGeneralScript) population.get(best));
		}
	}
	
	public void cross(ArrayList<MultiStageGeneralScript> newPopulation) {
		Random r = new Random();
		ArrayList<MultiStageGeneralScript> crossPopulation = new ArrayList<MultiStageGeneralScript>();
		while (crossPopulation.size() < newPopulation.size()) {
			int p1 = r.nextInt(newPopulation.size());
			MultiStageGeneralScript e1 = newPopulation.remove(p1);
			int p2 = r.nextInt(newPopulation.size());
			MultiStageGeneralScript e2 = newPopulation.remove(p2);
			int x = r.nextInt(6);
			int y = r.nextInt(phases);
			
			List<GeneralScript> scripts1 = e1.getScripts();
			List<GeneralScript> scripts2 = e2.getScripts();
			
			List<String> param1 = scripts1.get(y).getBehaviorTypes();
			List<String> param2 = scripts2.get(y).getBehaviorTypes();

			List<String> nparam1 = new ArrayList<String>();
			List<String> nparam2 = new ArrayList<String>();

			for (int i = y + 1; i < phases; ++i) {
				GeneralScript aux = scripts1.get(i);
				scripts1.set(i, scripts1.get(i));
				scripts2.set(i, aux);
			}
			
			for (int i = 0; i < 6; ++i) {
				if (i < x) {
					nparam1.add(param1.get(i));
					nparam2.add(param2.get(i));
				} else {
					nparam1.add(param2.get(i));
					nparam2.add(param1.get(i));
				}
			}
			
			scripts1.set(y, new GeneralScript(scripts1.get(y).getUtt(),
					e1.getTimeBudget(), e1.getIterationsBudget(),
					BaseBehType.valueOf(nparam1.get(0)), BarBehType.valueOf(nparam1.get(1)), 
					WorkBehType.valueOf(nparam1.get(2)), LightBehType.valueOf(nparam1.get(3)),
					HeavyBehType.valueOf(nparam1.get(4)), RangedBehType.valueOf(nparam1.get(5))));
			scripts2.set(y, new GeneralScript(scripts2.get(y).getUtt(),
					e1.getTimeBudget(), e1.getIterationsBudget(),
					BaseBehType.valueOf(nparam2.get(0)), BarBehType.valueOf(nparam2.get(1)), 
					WorkBehType.valueOf(nparam2.get(2)), LightBehType.valueOf(nparam2.get(3)),
					HeavyBehType.valueOf(nparam2.get(4)), RangedBehType.valueOf(nparam2.get(5))));
			
			crossPopulation.add(new MultiStageGeneralScript(scripts1));		
			crossPopulation.add(new MultiStageGeneralScript(scripts2));	
		}
		newPopulation = crossPopulation;
	}
	
	public void mutate (ArrayList<MultiStageGeneralScript> newPopulation) {
		Random r = new Random();
		for (int i = 0; i < newPopulation.size(); ++i) {
			List<GeneralScript> scripts = newPopulation.get(i).getScripts();
			for (int j = 0; j < scripts.size(); ++j) {
				List<String> param = scripts.get(j).getBehaviorTypes();
				for (int k = 0; k < param.size(); ++k) {
					if (r.nextDouble() < MUT_CHANCE) {
						switch(k) {
						case 0 : param.set(k, BaseBehType.values()[r.nextInt(BaseBehType.values().length)].toString()); break;
						case 1 : param.set(k, BarBehType.values()[r.nextInt(BarBehType.values().length)].toString()); break;
						case 2 : param.set(k, WorkBehType.values()[r.nextInt(WorkBehType.values().length)].toString()); break;
						case 3 : param.set(k, LightBehType.values()[r.nextInt(LightBehType.values().length)].toString()); break;
						case 4 : param.set(k, HeavyBehType.values()[r.nextInt(HeavyBehType.values().length)].toString()); break;
						case 5 : param.set(k, RangedBehType.values()[r.nextInt(RangedBehType.values().length)].toString()); break;
						}
					}
				}
				scripts.set(j,new GeneralScript(scripts.get(j).getUtt(),
						newPopulation.get(j).getTimeBudget(), newPopulation.get(j).getIterationsBudget(),
						BaseBehType.valueOf(param.get(0)), BarBehType.valueOf(param.get(1)), 
						WorkBehType.valueOf(param.get(2)), LightBehType.valueOf(param.get(3)),
						HeavyBehType.valueOf(param.get(4)), RangedBehType.valueOf(param.get(5))));
			}
			newPopulation.set(i, new MultiStageGeneralScript(scripts));
		}
	}
	
	public void elite(ArrayList<MultiStageGeneralScript> newPopulation) {
		double[] evalCopy = evaluation.clone();
		double bestEval = -100000;
		int best = -1;
		for (int i = 0; i < eliteSize; ++i) {
			for (int j = 0; j < evalCopy.length; ++j) {
				if (evalCopy[j] > bestEval) {
					bestEval = evalCopy[j];
					best = j;
				}
			}
			newPopulation.add((MultiStageGeneralScript) population.get(best));
			evalCopy[best] = bestEval = -100000;
			best = -1;
		}
	}
	
	public void evolutionaryAlgorithm(int maxGen) {
		int k = 0;
		population = new ArrayList<AI>(); 
		getInitialPopulation();
		while (k < maxGen) {
			ArrayList<MultiStageGeneralScript> newPopulation = new ArrayList<MultiStageGeneralScript>();
			double[][] tournRes = new double[population.size()][rivals.size()];
			try {
				tournRes = ThreadedTournament.evaluate(population, rivals, Arrays.asList(gs.getPhysicalGameState()), utt, 1,
						MAX_CYCLES, MAX_CYCLES, visual, new Wins(), System.out, -1, false, false, "traces/");
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (int i = 0; i < tournRes.length; ++i) {
				evaluation[i] = 0;
				for (int j = 0; j < tournRes[i].length; ++j) {
					evaluation[i] = evaluation[i] + tournRes[i][j];
				}
			}
			storeData(evaluation);
			select(newPopulation);
			cross(newPopulation);
			mutate(newPopulation);
			elite(newPopulation);
			++k;
			
			System.out.println("Generación " + k + " de " + maxGen);			
		} 

		List<AI> popAux = new LinkedList<>();
		for (AI bot : population)
			popAux.add(bot.clone());
		double[][] tournRes = new double[population.size()][rivals.size()];
		try {
			tournRes = ThreadedTournament.evaluate(population, rivals, Arrays.asList(gs.getPhysicalGameState()), utt, 1,
					MAX_CYCLES, MAX_CYCLES, visual, new Wins(), System.out, -1, false, false, "traces/");
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < tournRes.length; ++i) {
			evaluation[i] = 0;
			for (int j = 0; j < tournRes[i].length; ++j) {
				evaluation[i] = evaluation[i] + tournRes[i][j];
			}
		}
		storeData(evaluation);

		double[] evalCopy = evaluation.clone();
		bestPopulation = new ArrayList<AI>();
		double bestEval = -100000;
		int best = -1;
		for (int i = 0; i < bestSize; ++i) {
			for (int j = 0; j < evalCopy.length; ++j) {
				if (evalCopy[j] > bestEval) {
					bestEval = evalCopy[j];
					best = j;
				}
			}
			bestPopulation.add((MultiStageGeneralScript) population.get(best));
			evalCopy[best] = bestEval = -100000;
			best = -1;
		}
	}
	
	public List<AI> getBestPopulation() {
		List<AI> aux = new LinkedList<>();
		for (AI bot : bestPopulation)
			aux.add(bot.clone());
		return aux;
	}
	
	public void storeData(double[] data) {
		OUT.print(data[0]);;
		for (int i = 1; i < data.length; ++i)
			OUT.print("," + data[i]);
		OUT.print("\n");
	}
}
