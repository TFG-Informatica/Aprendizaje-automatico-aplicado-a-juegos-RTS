package bot.ia;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import GNS.Droplet;
import ai.RandomBiasedAI;
import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.pathfinding.PathFinding;
import ai.competition.tiamat.Tiamat;
import ai.core.AI;
import ai.core.ParameterSpecification;
import bot.scripts.GeneralScript;
import bot.eval.Time;
import bot.eval.Wins;
import bot.scripts.BarrackBehavior.BarBehType;
import bot.scripts.BaseBehavior.BaseBehType;
import bot.scripts.HeavyBehavior.HeavyBehType;
import bot.scripts.LightBehavior.LightBehType;
import bot.scripts.RangedBehavior.RangedBehType;
import bot.scripts.WorkerBehavior.WorkBehType;
import bot.tournaments.ThreadedTournament;
import mrtsFixed.bots.EMRDeterministico;
import mrtsFixed.bots.EconomyMilitaryRush;
import mrtsFixed.bots.EconomyRush;
import mrtsFixed.bots.EconomyRushBurster;
import mrtsFixed.bots.HeavyDefense;
import mrtsFixed.bots.HeavyRush;
import mrtsFixed.bots.LightDefense;
import mrtsFixed.bots.LightRush;
import mrtsFixed.bots.RangedDefense;
import mrtsFixed.bots.RangedRush;
import mrtsFixed.bots.SimpleEconomyRush;
import mrtsFixed.bots.WorkerDefense;
import mrtsFixed.bots.WorkerRushPlusPlus;
import rts.GameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

public class GeneticV2 {
	
	private PrintStream OUT = null;
	private PrintStream TOUT = null;
	
	private final int TOURNSIZE = 1;
	private final double MUT_CHANCE = 0.3;
	private final int MUT_TIMES = 3;
	private final int MAX_CYCLES = 3000;
	private final int REQ_DIFF = 12;
	private final int NUM_MATCH = 2;
	
	
	private GameState gs;
	private UnitTypeTable utt;
	private List<MultiStageGeneralScript> population;
	private List<MultiStageGeneralScript> bestPopulation;
	private List<AI> rivals;
	private HashMap<MultiStageGeneralScript,Double> allEval;
	private double[] evaluation;
	private int popSize; 
	private int bestSize;
	private boolean visual;
	private int phases;

	public GeneticV2(int a_popSize, int a_bestSize, int a_phases, UnitTypeTable a_utt, 
			GameState a_gs,  boolean a_visual) throws FileNotFoundException {
		
		gs = a_gs;
		utt = a_utt;
		popSize = a_popSize;
		bestSize = a_bestSize;
		phases = a_phases;
		
		visual = a_visual;
		allEval = new HashMap<MultiStageGeneralScript,Double>();
		population = new ArrayList<MultiStageGeneralScript>();
		bestPopulation = new ArrayList<MultiStageGeneralScript>();
		/*rivals = Arrays.asList(new EconomyMilitaryRush(utt), new EconomyRush(utt), new EconomyRushBurster(utt),
				new EMRDeterministico(utt), new HeavyDefense(utt), new HeavyRush(utt), new LightDefense(utt),
				new LightRush(utt), new RandomBiasedAI(utt), new RangedDefense(utt), new RangedRush(utt),
				new SimpleEconomyRush(utt), new WorkerDefense(utt), new WorkerRushPlusPlus(utt));*/
		rivals = Arrays.asList(new Droplet(utt));
		
		OUT = new PrintStream(new FileOutputStream("data/exp2.csv"));
		TOUT = new PrintStream(new FileOutputStream("data/dropletVSself.csv"));
	}
	
	public void getInitialPopulation() {
		Random r = new Random();
		while(population.size() < popSize) {
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
			MultiStageGeneralScript newElem = new MultiStageGeneralScript(scripts);
			if (checkDiff(newElem, population) >= REQ_DIFF)
				population.add(newElem);
		}
	}

	public void select(ArrayList<MultiStageGeneralScript> parents) {
		Random r = new Random();
		for (int i = 0; i < popSize; ++i) {
			int best = -1; double bestEval = -100000;
			for (int j = 0; j < TOURNSIZE; ++j) {
				int a = r.nextInt(popSize);
				if (bestEval < evaluation[a]) {
					bestEval = evaluation[a];
					best = a;
				}
			}
			parents.add((MultiStageGeneralScript) population.get(best));
		}
	}

	/* Cuidado, destruye el array parents. 
	 * Hay que pasarle una copia si se quiere conservar 
	 */
	public void cross(ArrayList<MultiStageGeneralScript> parents, ArrayList<MultiStageGeneralScript> children) {
		Random r = new Random();
		int nPar = parents.size();
		while (parents.size() >= 2 && children.size() < nPar) {
			int p1 = r.nextInt(parents.size());
			MultiStageGeneralScript e1 = parents.remove(p1);
			int p2 = r.nextInt(parents.size());
			MultiStageGeneralScript e2 = parents.remove(p2);
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
			
			children.add(new MultiStageGeneralScript(scripts1));		
			children.add(new MultiStageGeneralScript(scripts2));	
		}
	}
	
	/* Cuidado, modifica el array population. 
	 * Hay que pasarle una copia si se quiere conservar 
	 */
	public void mutate (ArrayList<MultiStageGeneralScript> population) {
		Random r = new Random();
		for (int i = 0; i < population.size(); ++i) {
			List<GeneralScript> scripts = population.get(i).getScripts();
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
						population.get(j).getTimeBudget(), population.get(j).getIterationsBudget(),
						BaseBehType.valueOf(param.get(0)), BarBehType.valueOf(param.get(1)), 
						WorkBehType.valueOf(param.get(2)), LightBehType.valueOf(param.get(3)),
						HeavyBehType.valueOf(param.get(4)), RangedBehType.valueOf(param.get(5))));
			}
			population.set(i, new MultiStageGeneralScript(scripts));
		}
	}
	
	/*public double[] fitness(List<MultiStageGeneralScript> population2) throws Exception {
		double[] evaluation = new double[population2.size()];
		List<AI> unknowns = new ArrayList<AI>();
		for (int i = 0; i < population2.size(); ++i) {
			MultiStageGeneralScript a = population2.get(i);
			if (allEval.containsKey(a))
				evaluation[i] = allEval.get(a);
			else {
				evaluation[i] = -10000000;
				unknowns.add(a);
			}
		}
		
		double[][] tournRes = ThreadedTournament.evaluate(unknowns, rivals, Arrays.asList(gs.getPhysicalGameState()), utt, NUM_MATCH,
				MAX_CYCLES, MAX_CYCLES, visual, new Time(), System.out, -1, false, false, "traces/");
		
		int j = 0;
		for (int i = 0; i < evaluation.length; ++i) {
			if (evaluation[i] == -10000000) {
				evaluation[i] = 0;
				for (double d : tournRes[j])
					evaluation[i] += d;
				allEval.put((MultiStageGeneralScript) unknowns.get(j), evaluation[i]);
				++j;
			}
		}
		
		return evaluation;
	}*/
	
	public double[] fitness(List<MultiStageGeneralScript> population2) throws Exception {
		double[] evaluation = new double[population2.size()];
		List<AI> unknowns = new ArrayList<AI>();
		for (int i = 0; i < population2.size(); ++i) {
			unknowns.add(population2.get(i));
		}
		
		double[][] tournRes = ThreadedTournament.evaluate(unknowns, unknowns, Arrays.asList(gs.getPhysicalGameState()), utt, NUM_MATCH,
				MAX_CYCLES, MAX_CYCLES, visual, new Time(MAX_CYCLES), System.out, -1, false, false, "traces/");
		
		for (int i = 0; i < evaluation.length; ++i) {
			evaluation[i] = 0;
			for (double d : tournRes[i])
				evaluation[i] += d;
		}
		
		return evaluation;
	}
	
	public void evolutionaryAlgorithm(int maxGen) throws Exception {
		int k = 0;
		population = new ArrayList<MultiStageGeneralScript>(); 
		getInitialPopulation();
		evaluation = fitness(population);
		
		while (k < maxGen) {
			storeData(evaluation);
			testDroplet();
			
			ArrayList<MultiStageGeneralScript> bag = new ArrayList<MultiStageGeneralScript>();
			for (MultiStageGeneralScript a : population)
				bag.add(a.clone());
			
			ArrayList<MultiStageGeneralScript> parents = new ArrayList<MultiStageGeneralScript>();
			ArrayList<MultiStageGeneralScript> children = new ArrayList<MultiStageGeneralScript>();
			select(parents);
			cross(parents, children);
			
			for (MultiStageGeneralScript a : children)
				bag.add(a.clone());
			
			for (int i = 0; i < MUT_TIMES; ++i) {
				ArrayList<MultiStageGeneralScript> mutChild = new ArrayList<MultiStageGeneralScript>();
				for (MultiStageGeneralScript a : children)
					mutChild.add(a.clone());
				mutate(mutChild);
				for (MultiStageGeneralScript a : mutChild)
					bag.add(a.clone());
			}
			
			population = new ArrayList<MultiStageGeneralScript>();
			double[] bagEvaluation = fitness(bag);
			for (int i = 0; i < popSize; ++i) {
				int bestInd = 0;
				while (bestInd < bagEvaluation.length && checkDiff(bag.get(bestInd), population) < REQ_DIFF) ++bestInd;
				if (bestInd != bagEvaluation.length) {
					for (int j = bestInd + 1; j < bagEvaluation.length; ++j) {
						if (bagEvaluation[bestInd] < bagEvaluation[j] && checkDiff(bag.get(j), population) >= REQ_DIFF)
							bestInd = j;
					}
				}
				else {
					bestInd = 0;
					for (int j = 1; j < bagEvaluation.length; ++j) {
						if (bagEvaluation[bestInd] < bagEvaluation[j])
							bestInd = j;
					}
				}
				population.add(bag.get(bestInd));
				evaluation[i] = bagEvaluation[bestInd];
				bagEvaluation[bestInd] = -10000000;
			}
			
			++k;
			
			System.out.println("Generación " + k + " de " + maxGen);			
		} 
		
		storeData(evaluation);

		bestPopulation = new ArrayList<MultiStageGeneralScript>();
		for (int i = 0; i < bestSize; ++i) {
			bestPopulation.add(population.get(i));
		}
	}
	
	public int difference(MultiStageGeneralScript a1, MultiStageGeneralScript a2) {
		int diff = 0;
		List<GeneralScript> scripts1 = a1.getScripts();
		List<GeneralScript> scripts2 = a2.getScripts();
		for (int i = 0; i < scripts1.size(); ++i) {
			List<String> script1 = scripts1.get(i).getBehaviorTypes();
			List<String> script2 = scripts2.get(i).getBehaviorTypes();
			for (int j = 0; j < script1.size(); ++j) {
				if (!script1.get(j).equals(script2.get(j)))
					++diff;
			}
		}
		return diff;
	}
	
	private int checkDiff(MultiStageGeneralScript newElem, List<MultiStageGeneralScript> elems) {
		int diffTot = 10000;
		for (MultiStageGeneralScript a : elems) {
			int diff = difference(newElem, a);
			if (diff < diffTot)
				diffTot = diff;
		}
		return diffTot;
	}
	
	public List<AI> getBestPopulation() {
		List<AI> aux = new ArrayList<>();
		for (AI bot : bestPopulation)
			aux.add(bot.clone());
		return aux;
	}
	
	public void storeData(double[] data) {
		OUT.print(data[0]);
		for (int i = 1; i < data.length; ++i)
			OUT.print("," + data[i]);
		OUT.print("\n");
	}
	
	public void testDroplet() throws Exception {
		List<AI> bots = new ArrayList<AI>();
		for (int i = 0; i < population.size(); ++i) {
			bots.add(population.get(i));
		}
		List<AI> drop = new ArrayList<AI>(Arrays.asList(new Droplet(utt)));
		
		double[][] tournRes = ThreadedTournament.evaluate(bots, drop, Arrays.asList(gs.getPhysicalGameState()), utt, NUM_MATCH,
				MAX_CYCLES, MAX_CYCLES, visual, new Time(MAX_CYCLES), System.out, -1, false, false, "traces/");
		
		TOUT.print(tournRes[0][0]);
		for (int i = 1; i < tournRes.length; ++i) {
			TOUT.print("," + tournRes[i][0]);
		}
		TOUT.print("\n");
	}
}