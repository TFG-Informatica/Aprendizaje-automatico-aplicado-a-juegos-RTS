package ia;

import java.util.ArrayList;
import java.util.Random;

import ai.abstraction.pathfinding.PathFinding;
import rts.units.UnitTypeTable;
import scripts.*;
import scripts.BarrackBehavior.BarBehType;
import scripts.BaseBehavior.BaseBehType;
import scripts.HeavyBehavior.HeavyBehType;
import scripts.LightBehavior.LightBehType;
import scripts.RangedBehavior.RangedBehType;
import scripts.WorkerBehavior.WorkBehType;

public class Genetic {
	
	private ArrayList<GeneralScript> completeSet;
	private ArrayList<GeneralScript> population;
	private int popSize; 
	
	public Genetic (int a_popSize, UnitTypeTable a_utt, PathFinding a_pf) {
		popSize = a_popSize;
		completeSet = new ArrayList<GeneralScript>();
		
		for (BaseBehType baseBehType : BaseBehType.values()) 
			for (BarBehType barBehType : BarBehType.values())
				for (WorkBehType workBehType : WorkBehType.values())
					for (LightBehType lightBehType : LightBehType.values())
						for (HeavyBehType heavyBehType : HeavyBehType.values())
							for (RangedBehType rangedBehType : RangedBehType.values())
								completeSet.add(new GeneralScript(a_utt, a_pf, baseBehType, barBehType, 
								workBehType, lightBehType, heavyBehType, rangedBehType));
				
	}
	
	public void getInitialPopulation() {
		Random r = new Random();
		for(int i=0;i<popSize;++i) {		
			population.add(completeSet.get(r.nextInt(completeSet.size())));
		}
	}
	
	public void evolutionaryAlgorithm() {
		
	}
	
	
	
}
