package ia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import rts.GameState;
import rts.units.UnitTypeTable;
import scripts.*;
import scripts.BarrackBehavior.BarBehType;
import scripts.BaseBehavior.BaseBehType;
import scripts.HeavyBehavior.HeavyBehType;
import scripts.LightBehavior.LightBehType;
import scripts.RangedBehavior.RangedBehType;
import scripts.WorkerBehavior.WorkBehType;

public class Genetic {
	
	private final int TOURNSIZE = 10;
	private final double MUT_CHANCE = 0.1;
	
	private GameState gs;
	private UnitTypeTable utt;
	private ArrayList<GeneralScript> completeSet;
	private List<AI> population;
	private ArrayList<GeneralScript> bestPopulation;
	private int evaluation[];
	private int popSize; 
	private int bestSize;
	private int eliteSize;
	
	
	public Genetic (int a_popSize, int a_bestSize, int a_eliteSize, UnitTypeTable a_utt, PathFinding a_pf, 
					GameState a_gs) {
		gs = a_gs;
		utt = a_utt;
		popSize = a_popSize;
		bestSize = a_bestSize;
		eliteSize = a_eliteSize;
		evaluation = new int[popSize];
		completeSet = new ArrayList<GeneralScript>();
		
		for (BaseBehType baseBehType : BaseBehType.values()) 
			for (BarBehType barBehType : BarBehType.values())
				for (WorkBehType workBehType : WorkBehType.values())
					for (LightBehType lightBehType : LightBehType.values())
						for (HeavyBehType heavyBehType : HeavyBehType.values())
							for (RangedBehType rangedBehType : RangedBehType.values())
								completeSet.add(new GeneralScript(a_utt, a_pf, baseBehType, barBehType, 
								workBehType, lightBehType, heavyBehType, rangedBehType));
		
		population = new ArrayList<AI>();
		bestPopulation = new ArrayList<GeneralScript>();

	}

	public void getInitialPopulation() {
		Random r = new Random();
		for (int i = 0; i < popSize; ++i) {
			population.add(completeSet.get(r.nextInt(completeSet.size())));
		}
	}
	
	public void select(ArrayList<GeneralScript> newPopulation) {
		Random r = new Random();
		for (int i = 0; i < popSize - eliteSize; ++i) {
			int best = -1; int bestEval = -100000;
			for (int j = 0; j < TOURNSIZE; ++j) {
				int a = r.nextInt(popSize);
				if (bestEval < evaluation[a]) {
					bestEval = evaluation[a];
					best = a;
				}
			}
			newPopulation.add((GeneralScript) population.get(best));
		}
	}
	
	public void cross(ArrayList<GeneralScript> newPopulation) {
		Random r = new Random();
		ArrayList<GeneralScript> crossPopulation = new ArrayList<GeneralScript>();
		while (crossPopulation.size() < newPopulation.size()) {
			int p1 = r.nextInt(newPopulation.size());
			int p2 = r.nextInt(newPopulation.size());
			int x = r.nextInt(6);
			
			List<String> param1 = newPopulation.get(p1).getBehaviorTypes();
			List<String> param2 = newPopulation.get(p2).getBehaviorTypes();
			
			List<String> nparam1 = new ArrayList<String>();
			List<String> nparam2 = new ArrayList<String>();
			
			for (int i = 0; i < 6; ++i) {
				if (i < x) {
					nparam1.add(param1.get(i));
					nparam2.add(param2.get(i));
				} else {
					nparam1.add(param2.get(i));
					nparam2.add(param1.get(i));
				}
			}
			
			crossPopulation.add(new GeneralScript(newPopulation.get(p1).getUtt(), newPopulation.get(p1).getPathFinding(),
					newPopulation.get(p1).getTimeBudget(), newPopulation.get(p1).getIterationsBudget(),
					BaseBehType.valueOf(nparam1.get(0)), BarBehType.valueOf(nparam1.get(1)), 
					WorkBehType.valueOf(nparam1.get(2)), LightBehType.valueOf(nparam1.get(3)),
					HeavyBehType.valueOf(nparam1.get(4)), RangedBehType.valueOf(nparam1.get(5))));		
			crossPopulation.add(new GeneralScript(newPopulation.get(p1).getUtt(), newPopulation.get(p1).getPathFinding(),
					newPopulation.get(p1).getTimeBudget(), newPopulation.get(p1).getIterationsBudget(),
					BaseBehType.valueOf(nparam2.get(0)), BarBehType.valueOf(nparam2.get(1)), 
					WorkBehType.valueOf(nparam2.get(2)), LightBehType.valueOf(nparam2.get(3)),
					HeavyBehType.valueOf(nparam2.get(4)), RangedBehType.valueOf(nparam2.get(5))));	
		}
		newPopulation = crossPopulation;
	}
	
	public void mutate (ArrayList<GeneralScript> newPopulation) {
		Random r = new Random();
		for (int j = 0; j < newPopulation.size(); ++j) {
			List<String> param = newPopulation.get(j).getBehaviorTypes();
			for (int i = 0; i < param.size(); ++i) {
				if (r.nextDouble() < MUT_CHANCE) {
					switch(i) {
					case 0 : param.set(i, BaseBehType.values()[r.nextInt(BaseBehType.values().length)].toString()); break;
					case 1 : param.set(i, BarBehType.values()[r.nextInt(BarBehType.values().length)].toString()); break;
					case 2 : param.set(i, WorkBehType.values()[r.nextInt(WorkBehType.values().length)].toString()); break;
					case 3 : param.set(i, LightBehType.values()[r.nextInt(LightBehType.values().length)].toString()); break;
					case 4 : param.set(i, HeavyBehType.values()[r.nextInt(HeavyBehType.values().length)].toString()); break;
					case 5 : param.set(i, RangedBehType.values()[r.nextInt(RangedBehType.values().length)].toString()); break;
					}
				}
			}
			newPopulation.set(j,new GeneralScript(newPopulation.get(j).getUtt(), newPopulation.get(j).getPathFinding(),
					newPopulation.get(j).getTimeBudget(), newPopulation.get(j).getIterationsBudget(),
					BaseBehType.valueOf(param.get(0)), BarBehType.valueOf(param.get(1)), 
					WorkBehType.valueOf(param.get(2)), LightBehType.valueOf(param.get(3)),
					HeavyBehType.valueOf(param.get(4)), RangedBehType.valueOf(param.get(5))));
		}
	}
	
	public void elite(ArrayList<GeneralScript> newPopulation) {
		int[] evalCopy = evaluation.clone();
		int bestEval = -100000, best = -1;
		for (int i = 0; i < eliteSize; ++i) {
			for (int j = 0; j < evalCopy.length; ++j) {
				if (evalCopy[j] > bestEval) {
					bestEval = evalCopy[j];
					best = j;
				}
			}
			newPopulation.add((GeneralScript) population.get(best));
			evalCopy[best] = bestEval = -100000;
			best = -1;
		}
	}

	public void evolutionaryAlgorithm(int maxGen) {
		int k = 0;
		getInitialPopulation();
		while (k < maxGen) {
			ArrayList<GeneralScript> newPopulation = new ArrayList<GeneralScript>();
			try {
				evaluation = Tournament.evaluate(population, Arrays.asList(gs.getPhysicalGameState()), utt, 1,
						3000, 300, false, System.out, -1, false, false, "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			select(newPopulation);
			cross(newPopulation);
			mutate(newPopulation);
			elite(newPopulation);
			++k;
		}
		try {
			evaluation = Tournament.evaluate(population, Arrays.asList(gs.getPhysicalGameState()), utt, 1,
					3000, 300, false, System.out, -1, false, false, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		bestPopulation = new ArrayList<GeneralScript>();
		int[] evalCopy = evaluation.clone();
		int bestEval = -100000, best = -1;
		for (int i = 0; i < bestSize; ++i) {
			for (int j = 0; j < evalCopy.length; ++j) {
				if (evalCopy[j] > bestEval) {
					bestEval = evalCopy[j];
					best = j;
				}
			}
			bestPopulation.add((GeneralScript) population.get(best));
			evalCopy[best] = bestEval = -100000;
			best = -1;
		}
	}
	
	public ArrayList<GeneralScript> getBestPopulation() {
		return (ArrayList<GeneralScript>) bestPopulation.clone();
	}
	
}
