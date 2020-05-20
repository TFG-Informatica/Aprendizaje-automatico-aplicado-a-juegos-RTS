package bot.test;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import ai.RandomBiasedAI;
import ai.core.AI;
import bot.eval.Time;
import bot.eval.Wins;
import bot.ia.MultiStageGeneralScript;
import bot.scripts.GeneralScript;
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
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;

public class RandomChoiceTest {
	
	private static UnitTypeTable utt = new UnitTypeTable();
	private static GameState gs; 
	private static int MAX_CYCLES = 3000;
	private static boolean visual = false;
	private static List<AI> rivals = new ArrayList<AI>();
	private static PrintStream OUT;


	public static void main(String[] args) throws Exception {
		
		Scanner sc = new Scanner(System.in);
		int popSize = sc.nextInt(), phases = sc.nextInt();
		sc.close();
		
		gs = new GameState(PhysicalGameState.load("maps/24x24/basesWorkers24x24.xml", utt), utt);
		OUT = new PrintStream(new FileOutputStream("data/random1.csv"));
		
		rivals = Arrays.asList(new EconomyMilitaryRush(utt), new EconomyRush(utt), new EconomyRushBurster(utt),
				new EMRDeterministico(utt), new HeavyDefense(utt), new HeavyRush(utt), new LightDefense(utt),
				new LightRush(utt), new RandomBiasedAI(utt), new RangedDefense(utt), new RangedRush(utt),
				new SimpleEconomyRush(utt), new WorkerDefense(utt), /*new WorkerRush(utt),*/ new WorkerRushPlusPlus(utt));
		
		ArrayList<AI> population = new ArrayList<AI>();
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
		
		double[] evaluation = fitness(population);
		storeData(evaluation);		
	}
	
	public static double[] fitness(List<AI> population) throws Exception {
		double[] evaluation = new double[population.size()];
		
		double[][] tournRes = ThreadedTournament.evaluate(population, population, Arrays.asList(gs.getPhysicalGameState()), utt, 1,
				MAX_CYCLES, MAX_CYCLES, visual, new Wins(), System.out, -1, false, false, "traces/");
		
		for (int i = 0; i < evaluation.length; ++i) {
			evaluation[i] = 0;
			for (double d : tournRes[i])
				evaluation[i] += d;
		}
		return evaluation;
	}
	
	public static void storeData(double[] data) {
		OUT.print(data[0]);
		for (int i = 1; i < data.length; ++i)
			OUT.print("," + data[i]);
		OUT.print("\n");
	}


}
