package bot.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import GNS.Droplet;
import ai.RandomBiasedAI;
import ai.abstraction.partialobservability.POLightRush;
import ai.core.AI;
import ai.mcts.naivemcts.NaiveMCTS;
import bot.behavior.BarrackBehavior.BarBehType;
import bot.behavior.BaseBehavior.BaseBehType;
import bot.behavior.HeavyBehavior.HeavyBehType;
import bot.behavior.LightBehavior.LightBehType;
import bot.behavior.RangedBehavior.RangedBehType;
import bot.behavior.WorkerBehavior.WorkBehType;
import bot.eval.Time;
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
import mrtsFixed.bots.WorkerRush;
import mrtsFixed.bots.WorkerRushPlusPlus;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;

public class Fight {
	public static void main(String[] args) throws FileNotFoundException {
		
		Scanner bot1file = new Scanner(new File("serial/SelfBot8x80.txt"));
		Scanner bot2file = null;

		UnitTypeTable utt = new UnitTypeTable(UnitTypeTable.VERSION_ORIGINAL_FINETUNED);
		GameState gs = null;
		List<AI> bots = new ArrayList<AI>();
/*		bots = new ArrayList<AI>(Arrays.asList(new EconomyMilitaryRush(utt), new EconomyRush(utt), new EconomyRushBurster(utt),
				new EMRDeterministico(utt), new HeavyDefense(utt), new HeavyRush(utt), new LightDefense(utt),
				new LightRush(utt), new RandomBiasedAI(utt), new RangedDefense(utt), new RangedRush(utt),
				new SimpleEconomyRush(utt), new WorkerDefense(utt), new WorkerRush(utt), new WorkerRushPlusPlus(utt)));
		bots.add(0,new MultiStageGeneralScript(Arrays.asList(new GeneralScript(utt, -1, -1, BaseBehType.TWOWORKER, 
				BarBehType.LIGHT, WorkBehType.HARVESTER, LightBehType.LESSHP, HeavyBehType.WAIT, 
				RangedBehType.WAIT),new GeneralScript(utt, -1, -1, BaseBehType.ONEWORKER, 
				BarBehType.HEAVY, WorkBehType.TWOHARVAGGR, LightBehType.LESSHP, HeavyBehType.WAIT, 
				RangedBehType.CLOSBUIL),new GeneralScript(utt, -1, -1, BaseBehType.ONEWORKER, 
				BarBehType.LESS, WorkBehType.ONEHARVAGGR, LightBehType.CLOSBUIL, HeavyBehType.LESSHP, 
				RangedBehType.CLOSEST))));
	*/
		
		bots.add(MultiStageGeneralScriptIO.load(bot1file, utt)); bots.add(new LightRush(utt));
				/*new ArrayList<AI>(Arrays.asList(new MultiStageGeneralScript(3000, Arrays.asList(
				new GeneralScript(utt, -1, -1, BaseBehType.ONEWORKER, BarBehType.LIGHT, WorkBehType.HARVESTER,
				LightBehType.WAIT, HeavyBehType.WAIT, RangedBehType.WAIT))),
				new MultiStageGeneralScript(3000, Arrays.asList(
				new GeneralScript(utt, -1, -1, BaseBehType.ONEWORKER, BarBehType.LIGHT, WorkBehType.HARVESTER,
				LightBehType.CLOSEST, HeavyBehType.WAIT, RangedBehType.WAIT))),new EconomyMilitaryRush(utt), new EconomyRush(utt), new EconomyRushBurster(utt),
				new EMRDeterministico(utt), new HeavyDefense(utt), new HeavyRush(utt), new LightDefense(utt),
				new LightRush(utt), new RandomBiasedAI(utt), new RangedDefense(utt), new RangedRush(utt),
				new SimpleEconomyRush(utt), new WorkerDefense(utt), new WorkerRushPlusPlus(utt), new Droplet(utt)));*/
		
		try {
			gs = new GameState(PhysicalGameState.load("maps/8x8/basesWorkers8x8.xml", utt), utt);
			ThreadedTournament.evaluate(bots, bots, Arrays.asList(gs.getPhysicalGameState()), utt, 1, 3000, 3000, true, new Time(3000), System.out, 0,
					true, false, "traces/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
