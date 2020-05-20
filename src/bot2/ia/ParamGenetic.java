package bot2.ia;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ai.RandomBiasedAI;
import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.ParameterSpecification;
import bot.scripts.GeneralScript;
import bot.eval.Time;
import bot.eval.Wins;
import bot2.paramScripts.BarrackBehavior.BarBehType;
import bot2.paramScripts.BaseBehavior.BaseBehType;
import bot2.paramScripts.HeavyBehavior.HeavyBehType;
import bot2.paramScripts.LightBehavior.LightBehType;
import bot2.paramScripts.RangedBehavior.RangedBehType;
import bot2.paramScripts.WorkerBehavior.WorkBehType;
import bot.tournaments.ThreadedTournament;
import bot2.paramScripts.ParamGeneralScript;
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

public class ParamGenetic {
	
	private PrintStream OUT = null;
	
	private final int TOURNSIZE = 3;
	private final double MUT_CHANCE = 0.1;
	private final int MUT_TIMES = 3;
	private final int MAX_CYCLES = 3000;
	
	private GameState gs;
	private UnitTypeTable utt;
	private List<ParamMultiStageGeneralScript> population;
	private List<ParamMultiStageGeneralScript> bestPopulation;
	private List<AI> rivals;
	private HashMap<ParamMultiStageGeneralScript,Double> allEval;
	private double[] evaluation;
	private int popSize; 
	private int bestSize;
	private boolean visual;
	private int phases;
	private int width;
	private int height;

	public ParamGenetic(int a_popSize, int a_bestSize, int a_phases, UnitTypeTable a_utt, 
			GameState a_gs,  boolean a_visual) throws FileNotFoundException {
		
		gs = a_gs;
		utt = a_utt;
		popSize = a_popSize;
		bestSize = a_bestSize;
		phases = a_phases;
		
		visual = a_visual;
		allEval = new HashMap<ParamMultiStageGeneralScript,Double>();
		population = new ArrayList<ParamMultiStageGeneralScript>();
		bestPopulation = new ArrayList<ParamMultiStageGeneralScript>();
		rivals = Arrays.asList(new EconomyMilitaryRush(utt), new EconomyRush(utt), new EconomyRushBurster(utt),
				new EMRDeterministico(utt), new HeavyDefense(utt), new HeavyRush(utt), new LightDefense(utt),
				new LightRush(utt), new RandomBiasedAI(utt), new RangedDefense(utt), new RangedRush(utt),
				new SimpleEconomyRush(utt), new WorkerDefense(utt), /*new WorkerRush(utt),*/ new WorkerRushPlusPlus(utt));
		
		OUT = new PrintStream(new FileOutputStream("data/exp.csv"));
	}
	
	public void getInitialPopulation() {
		Random r = new Random();
		width = gs.getPhysicalGameState().getWidth();
		height = gs.getPhysicalGameState().getHeight();
		
		for (int i = 0; i < popSize; ++i) {
			List<ParamGeneralScript> scripts = new ArrayList<ParamGeneralScript>();
			for (int j = 0; j < phases; ++j) {
				BaseBehType baseBehType = BaseBehType.values()[r.nextInt(BaseBehType.values().length)];
				BarBehType barBehType = BarBehType.values()[r.nextInt(BarBehType.values().length)];
				WorkBehType workBehType = WorkBehType.values()[r.nextInt(WorkBehType.values().length)];
				LightBehType lightBehType = LightBehType.values()[r.nextInt(LightBehType.values().length)];
				HeavyBehType heavyBehType = HeavyBehType.values()[r.nextInt(HeavyBehType.values().length)];
				RangedBehType rangedBehType = RangedBehType.values()[r.nextInt(RangedBehType.values().length)];

				scripts.add(new ParamGeneralScript(utt, baseBehType, barBehType, 
						workBehType, lightBehType, heavyBehType, rangedBehType, r.nextInt(width), r.nextInt(height)));
			}
			population.add(new ParamMultiStageGeneralScript(scripts));
		}
	}
	
	public void select(ArrayList<ParamMultiStageGeneralScript> parents) {
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
			parents.add((ParamMultiStageGeneralScript) population.get(best));
		}
	}

	/* Cuidado, destruye el array parents. 
	 * Hay que pasarle una copia si se quiere conservar 
	 */
	public void cross(ArrayList<ParamMultiStageGeneralScript> parents, ArrayList<ParamMultiStageGeneralScript> children) {
		Random r = new Random();
		while (children.size() < parents.size()) {
			int p1 = r.nextInt(parents.size());
			ParamMultiStageGeneralScript e1 = parents.remove(p1);
			int p2 = r.nextInt(parents.size());
			ParamMultiStageGeneralScript e2 = parents.remove(p2);	
			int y = r.nextInt(phases);
			
			List<ParamGeneralScript> scripts1 = e1.getScripts();
			List<ParamGeneralScript> scripts2 = e2.getScripts();
			
			List<String> param1 = scripts1.get(y).getBehaviorTypes();
			List<String> param2 = scripts2.get(y).getBehaviorTypes();

			List<String> nparam1 = new ArrayList<String>();
			List<String> nparam2 = new ArrayList<String>();
			
			int x = r.nextInt(param1.size());			

			for (int i = y + 1; i < phases; ++i) {
				ParamGeneralScript aux = scripts1.get(i);
				scripts1.set(i, scripts1.get(i));
				scripts2.set(i, aux);
			}
			
			for (int i = 0; i < param1.size(); ++i) {
				if (i < x) {
					nparam1.add(param1.get(i));
					nparam2.add(param2.get(i));
				} else {
					nparam1.add(param2.get(i));
					nparam2.add(param1.get(i));
				}
			}
			
			scripts1.set(y, new ParamGeneralScript(scripts1.get(y).getUtt(),
					e1.getTimeBudget(), e1.getIterationsBudget(),
					BaseBehType.valueOf(nparam1.get(0)), BarBehType.valueOf(nparam1.get(1)), 
					WorkBehType.valueOf(nparam1.get(2)), LightBehType.valueOf(nparam1.get(3)),
					HeavyBehType.valueOf(nparam1.get(4)), RangedBehType.valueOf(nparam1.get(5)),
					Integer.parseInt(nparam1.get(6)),Integer.parseInt(nparam1.get(7))));
			scripts2.set(y, new ParamGeneralScript(scripts2.get(y).getUtt(),
					e1.getTimeBudget(), e1.getIterationsBudget(),
					BaseBehType.valueOf(nparam2.get(0)), BarBehType.valueOf(nparam2.get(1)), 
					WorkBehType.valueOf(nparam2.get(2)), LightBehType.valueOf(nparam2.get(3)),
					HeavyBehType.valueOf(nparam2.get(4)), RangedBehType.valueOf(nparam2.get(5)),
					Integer.parseInt(nparam2.get(6)),Integer.parseInt(nparam2.get(7))));
			
			children.add(new ParamMultiStageGeneralScript(scripts1));		
			children.add(new ParamMultiStageGeneralScript(scripts2));	
		}
	}
	
	/* Cuidado, modifica el array population. 
	 * Hay que pasarle una copia si se quiere conservar 
	 */
	public void mutate (ArrayList<ParamMultiStageGeneralScript> population) {
		Random r = new Random();
		for (int i = 0; i < population.size(); ++i) {
			List<ParamGeneralScript> scripts = population.get(i).getScripts();
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
						case 6 : param.set(k, new Integer(r.nextInt(width)).toString()); break;
						case 7 : param.set(k, new Integer(r.nextInt(height)).toString()); break;
						}
					}
				}
				scripts.set(j, new ParamGeneralScript(scripts.get(j).getUtt(),
						population.get(j).getTimeBudget(), population.get(j).getIterationsBudget(),
						BaseBehType.valueOf(param.get(0)), BarBehType.valueOf(param.get(1)), 
						WorkBehType.valueOf(param.get(2)), LightBehType.valueOf(param.get(3)),
						HeavyBehType.valueOf(param.get(4)), RangedBehType.valueOf(param.get(5)),
						Integer.parseInt(param.get(6)),Integer.parseInt(param.get(7))));
			}
			population.set(i, new ParamMultiStageGeneralScript(scripts));
		}
	}
	
	public double[] fitness(List<ParamMultiStageGeneralScript> population2) throws Exception {
		double[] evaluation = new double[population2.size()];
		List<AI> unknowns = new ArrayList<AI>();
		for (int i = 0; i < population2.size(); ++i) {
			ParamMultiStageGeneralScript a = population2.get(i);
			if (allEval.containsKey(a))
				evaluation[i] = allEval.get(a);
			else {
				evaluation[i] = -10000000;
				unknowns.add(a);
			}
		}
		
		double[][] tournRes = ThreadedTournament.evaluate(unknowns, rivals, Arrays.asList(gs.getPhysicalGameState()), utt, 1,
				MAX_CYCLES, MAX_CYCLES, visual, new Time(), System.out, -1, false, false, "traces/");
		
		int j = 0;
		for (int i = 0; i < evaluation.length; ++i) {
			if (evaluation[i] == -10000000) {
				evaluation[i] = 0;
				for (double d : tournRes[j])
					evaluation[i] += d;
				allEval.put((ParamMultiStageGeneralScript) unknowns.get(j), evaluation[i]);
				++j;
			}
		}
		
		return evaluation;
	}
	
	public void evolutionaryAlgorithm(int maxGen) throws Exception {
		int k = 0;
		population = new ArrayList<ParamMultiStageGeneralScript>(); 
		getInitialPopulation();
		evaluation = fitness(population);
		
		while (k < maxGen) {
			storeData(evaluation);
			
			ArrayList<ParamMultiStageGeneralScript> bag = new ArrayList<ParamMultiStageGeneralScript>();
			for (ParamMultiStageGeneralScript a : population)
				bag.add(a.clone());
			
			ArrayList<ParamMultiStageGeneralScript> parents = new ArrayList<ParamMultiStageGeneralScript>();
			ArrayList<ParamMultiStageGeneralScript> children = new ArrayList<ParamMultiStageGeneralScript>();
			select(parents);
			cross(parents, children);
			
			for (ParamMultiStageGeneralScript a : children)
				bag.add(a.clone());
			
			for (int i = 0; i < MUT_TIMES; ++i) {
				ArrayList<ParamMultiStageGeneralScript> mutChild = new ArrayList<ParamMultiStageGeneralScript>();
				for (ParamMultiStageGeneralScript a : children)
					mutChild.add(a.clone());
				mutate(mutChild);
				for (ParamMultiStageGeneralScript a : mutChild)
					bag.add(a.clone());
			}
			
			population = new ArrayList<ParamMultiStageGeneralScript>();
			double[] bagEvaluation = fitness(bag);
			for (int i = 0; i < popSize; ++i) {
				int bestInd = 0;
				for (int j = 1; j < bagEvaluation.length; ++j) {
					if (bagEvaluation[bestInd] < bagEvaluation[j])
						bestInd = j;
				}
				population.add(bag.get(bestInd));
				evaluation[i] = bagEvaluation[bestInd];
				bagEvaluation[bestInd] = -10000000;
			}
			
			++k;
			
			System.out.println("Generación " + k + " de " + maxGen);			
		} 
		
		storeData(evaluation);

		bestPopulation = new ArrayList<ParamMultiStageGeneralScript>();
		for (int i = 0; i < bestSize; ++i) {
			bestPopulation.add(population.get(i));
		}
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
}