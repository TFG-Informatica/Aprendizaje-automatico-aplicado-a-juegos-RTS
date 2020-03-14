package bot.test;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.RandomBiasedAI;
import ai.core.AI;
import bot.ia.ThreadedTournament;
import bot.scripts.GeneralScript;
import bot.scripts.BarrackBehavior.BarBehType;
import bot.scripts.BaseBehavior.BaseBehType;
import bot.scripts.HeavyBehavior.HeavyBehType;
import bot.scripts.LightBehavior.LightBehType;
import bot.scripts.RangedBehavior.RangedBehType;
import bot.scripts.WorkerBehavior.WorkBehType;
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

public class GeneticTests {

	private static List<AI> allGS = null;
	private static List<AI> rivals = null;
	private static UnitTypeTable utt = null;
	private static GameState gs = null;
	private static final String map = "maps/24x24/basesWorkers24x24.xml";
	
	public static void main(String args[]) throws Exception{
		
		utt = new UnitTypeTable();
		gs = new GameState(PhysicalGameState.load(map, utt), utt);
		allGS = new ArrayList<AI>();
		for (BaseBehType baseBehType : BaseBehType.values()) 
		for (BarBehType barBehType : BarBehType.values())
		for (WorkBehType workBehType : WorkBehType.values())
		for (LightBehType lightBehType : LightBehType.values())
		for (HeavyBehType heavyBehType : HeavyBehType.values())
		for (RangedBehType rangedBehType : RangedBehType.values())
			allGS.add(new GeneralScript(utt, baseBehType, barBehType, 
			workBehType, lightBehType, heavyBehType, rangedBehType));
		
		rivals = Arrays.asList(new EconomyMilitaryRush(utt), new EconomyRush(utt), new EconomyRushBurster(utt),
				new EMRDeterministico(utt), new HeavyDefense(utt), new HeavyRush(utt), new LightDefense(utt),
				new LightRush(utt), new RandomBiasedAI(utt), new RangedDefense(utt), new RangedRush(utt),
				new SimpleEconomyRush(utt), new WorkerDefense(utt), new WorkerRushPlusPlus(utt));
		
		PrintStream OUT = new PrintStream(new FileOutputStream("data/GeneticTests.csv"));
		
		OUT.println("BaseBeh,BarBeh,WorkBeh,LightBeh,HeavyBeh,RangedBeh,Fit01,Fit02,Fit03,Fit04,Fit05,"
				+ "Fit06,Fit07,Fit08,Fit09,Fit10,Fit11,Fit12,Fit13,Fit14,FitTot");
		
		int i = 0;
		for(AI bot1 : allGS) {
			for(String s : ((GeneralScript) bot1).getBehaviorTypes())
				OUT.print(s + ",");
			int fitnessTotal = 0;
			for(AI bot2 : rivals) {
				double res = ThreadedTournament.evaluate(Arrays.asList(bot1), Arrays.asList(bot2), 
						Arrays.asList(gs.getPhysicalGameState()), utt, 100, 2000, 100, false, 
						System.out, -1, false, false, "traces/")[0];
				OUT.print(res + ",");
				fitnessTotal += res;
			}
			OUT.print(fitnessTotal + "\n");
			System.out.println(i++);
		}
	}
	
}
