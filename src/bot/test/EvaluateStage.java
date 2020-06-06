package bot.test;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.RandomBiasedAI;
import ai.core.AI;
import bot.behavior.BarrackBehavior.BarBehType;
import bot.behavior.BaseBehavior.BaseBehType;
import bot.behavior.HeavyBehavior.HeavyBehType;
import bot.behavior.LightBehavior.LightBehType;
import bot.behavior.RangedBehavior.RangedBehType;
import bot.behavior.WorkerBehavior.WorkBehType;
import bot.eval.Wins;
import bot.ia.GeneralScript;
import bot.ia.MultiStageGeneralScript;
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

public class EvaluateStage {
	
	private static List<AI> allGS = null;
	private static List<List<AI>> bestGS = null;
	private static List<AI> rivals = null;
	private static UnitTypeTable utt = null;
	private static GameState gs = null;
	private static final String map = "maps/24x24/basesWorkers24x24.xml";
	
	public static void main(String args[]) throws Exception{
		
		utt = new UnitTypeTable();
		gs = new GameState(PhysicalGameState.load(map, utt), utt);
		allGS = new ArrayList<AI>();
		bestGS = new ArrayList<List<AI>>();
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
			
 		bestGS.add(new ArrayList<AI>(Arrays.asList(
				new GeneralScript(utt, BaseBehType.THREEWORKER, BarBehType.LIGHT, WorkBehType.HARVESTER,
									LightBehType.LESSPERCHP, HeavyBehType.WAIT, RangedBehType.LESSHP),
				new GeneralScript(utt, BaseBehType.THREEWORKER, BarBehType.LIGHT, WorkBehType.HARVESTER,
						LightBehType.LESSPERCHP, HeavyBehType.LESSPERCHP, RangedBehType.LESSHP),
				new GeneralScript(utt, BaseBehType.THREEWORKER, BarBehType.LIGHT, WorkBehType.HARVESTER,
						LightBehType.LESSPERCHP, HeavyBehType.WAIT, RangedBehType.LESSHP)
				)));

		bestGS.add(new ArrayList<AI>(Arrays.asList(
				new GeneralScript(utt, BaseBehType.RUSHWORKER, BarBehType.LIGHT, WorkBehType.TWOHARVAGGR,
						LightBehType.CLOSBUIL, HeavyBehType.CLOSEST, RangedBehType.CLOSEST),
				new GeneralScript(utt, BaseBehType.RUSHWORKER, BarBehType.LESS, WorkBehType.THREEHARVAGGR,
						LightBehType.CLOSBUIL, HeavyBehType.LESSPERCHP, RangedBehType.WAIT),
				new GeneralScript(utt, BaseBehType.RUSHWORKER, BarBehType.LIGHT, WorkBehType.TWOHARVAGGR,
						LightBehType.CLOSBUIL, HeavyBehType.CLOSBUIL, RangedBehType.LESSHP)
				)));
		
		PrintStream OUT = new PrintStream(new FileOutputStream("data/Final.csv"));
		
		OUT.println("BaseBeh,BarBeh,WorkBeh,LightBeh,HeavyBeh,RangedBeh,Fit01,Fit02,Fit03,Fit04,Fit05,"
				+ "Fit06,Fit07,Fit08,Fit09,Fit10,Fit11,Fit12,Fit13,Fit14,FitTot");
		
		List<AI> us = new ArrayList<AI>();
		
		for (int i = 0; i < bestGS.get(0).size();++i)
			for (AI z : allGS) {
				us.add(new MultiStageGeneralScript(3000, Arrays.asList((GeneralScript)bestGS.get(0).get(i), 
						(GeneralScript)bestGS.get(1).get(i), 
						(GeneralScript)z)));
			}
		
		double res[][] = ThreadedTournament.evaluate(us, rivals, 
						Arrays.asList(gs.getPhysicalGameState()), utt, 1, 3000, 3000, false, new Wins(),
						System.out, -1, false, false, "traces/");		

		int i = 0;
		double[] fitnessTotal = new double[us.size()];
		for (AI bot1 : us) {
			for (String s : ((MultiStageGeneralScript) bot1).getScripts().get(2).getBehaviorTypes())
				OUT.print(s + ",");
			fitnessTotal[i] = 0;
			for (int j = 0; j < res[i].length; ++j) {
				OUT.print(res[i][j] + ",");
				fitnessTotal[i] += res[i][j];
			}
			OUT.print(fitnessTotal[i] + "\n");
			++i;
		}
		
		for (int j = 0; j < 3; ++j) {
			double bestEval = -1;
			int best = -1;
			for (int k = 0; k < fitnessTotal.length; ++k) {
				if (fitnessTotal[k] > bestEval) {
					bestEval = fitnessTotal[k];
					best = k;
				}
			}
			System.out.println(((MultiStageGeneralScript)us.get(best)).toString());
			fitnessTotal[best] = -1;
		}
	}
}
