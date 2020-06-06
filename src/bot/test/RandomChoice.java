package bot.test;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import GNS.Droplet;
import ai.RandomBiasedAI;
import ai.core.AI;
import bot.behavior.BarrackBehavior.BarBehType;
import bot.behavior.BaseBehavior.BaseBehType;
import bot.behavior.HeavyBehavior.HeavyBehType;
import bot.behavior.LightBehavior.LightBehType;
import bot.behavior.RangedBehavior.RangedBehType;
import bot.behavior.WorkerBehavior.WorkBehType;
import bot.eval.Time;
import bot.eval.TimePlusWins;
import bot.eval.Wins;
import bot.ia.GeneralScript;
import bot.ia.MultiStageGeneralScript;
import bot.io.MultiStageGeneralScriptIO;
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
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;

public class RandomChoice {
	
	private static UnitTypeTable utt = new UnitTypeTable(UnitTypeTable.VERSION_ORIGINAL_FINETUNED);
	private static GameState gs; 
	private final static int MAX_CYCLES = 8000;
	private final static int NUM_MATCHES = 2;
	private static boolean visual = false;
	private static List<AI> rivals = new ArrayList<AI>();
	private static PrintStream OUT;


	public static void main(String[] args) throws Exception {
		
		Scanner sc = new Scanner(System.in);
		int popSize = sc.nextInt(), phases = sc.nextInt();
		sc.close();
		
		gs = new GameState(PhysicalGameState.load("maps/GardenOfWar64x64.xml", utt), utt);
		OUT = new PrintStream(new FileOutputStream("data/Random64x64.csv"));
		
		rivals = Arrays.asList(new EconomyMilitaryRush(utt), new EconomyRush(utt), new EconomyRushBurster(utt),
				new EMRDeterministico(utt), new HeavyDefense(utt), new HeavyRush(utt), new LightDefense(utt),
				new LightRush(utt), new RandomBiasedAI(utt), new RangedDefense(utt), new RangedRush(utt),
				new SimpleEconomyRush(utt), new WorkerDefense(utt), new WorkerRushPlusPlus(utt));
		
		ArrayList<AI> population = new ArrayList<AI>();
		MultiStageGeneralScript best = null;
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
			population.add(new MultiStageGeneralScript(MAX_CYCLES, scripts));
		}
		
		double[] evaluation = fitness(population);
		double bestP = -1000000;
		if (evaluation[0] > bestP) {
			bestP = evaluation[0];
			best = (MultiStageGeneralScript) population.get(0);
		}
		OUT.print(evaluation[0]);
		for(int k = 1; k < population.size(); ++k) {
			if (evaluation[k] > bestP) {
				bestP = evaluation[k];
				best = (MultiStageGeneralScript) population.get(k);
			}
			OUT.print("," + evaluation[k]);
		}
		OUT.print("\n");

		PrintStream ser = new PrintStream(new FileOutputStream("serial/RandomBot64x64.txt"));
		MultiStageGeneralScriptIO.store(ser, best);
		ser.close();
	}
	
	public static double[] fitness(List<AI> population) throws Exception {
		double[] evaluation = new double[population.size()];
		
		double[][] tournRes = ThreadedTournament.evaluate(population, rivals, Arrays.asList(gs.getPhysicalGameState()), utt, NUM_MATCHES,
				MAX_CYCLES, MAX_CYCLES, visual, new Time(MAX_CYCLES), System.out, -1, false, false, "traces/");
		
		for (int i = 0; i < evaluation.length; ++i) {
			evaluation[i] = 0;
			for (double d : tournRes[i])
				evaluation[i] += d;
		}
		return evaluation;
	}


}
