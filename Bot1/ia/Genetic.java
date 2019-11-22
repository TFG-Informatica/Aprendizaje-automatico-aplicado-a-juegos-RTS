package ia;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ai.abstraction.pathfinding.PathFinding;
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
	
	private GameState gs;
	private ArrayList<GeneralScript> completeSet;
	private ArrayList<GeneralScript> population;
	private ArrayList<GeneralScript> bestPopulation;
	private int evaluation[];
	private int popSize; 
	private int bestSize;
	private int eliteSize;
	
	public Genetic (int a_popSize, int a_bestSize, int a_eliteSize, UnitTypeTable a_utt, PathFinding a_pf, 
					GameState a_gs) {
		gs = a_gs;
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
		
		population = new ArrayList<GeneralScript>();
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
			newPopulation.add(population.get(best));
		}
	}
	
	public void cross(ArrayList<GeneralScript> newPopulation) {
		Random r = new Random();
		ArrayList<GeneralScript> crossPopulation = new ArrayList<GeneralScript>();
		while (crossPopulation.size() < popSize) {
			int p1 = r.nextInt(popSize);
			int p2 = r.nextInt(popSize);
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

	public void evolutionaryAlgorithm(int maxGen) {
		int k = 0;
		getInitialPopulation();
		while (k < maxGen) {
			ArrayList<GeneralScript> newPopulation = new ArrayList<GeneralScript>();
			// Evaluate
			select(newPopulation);
			cross(newPopulation);
			// Mutate
			// Elite
			++k;
		}
	}
	
	
	
}
