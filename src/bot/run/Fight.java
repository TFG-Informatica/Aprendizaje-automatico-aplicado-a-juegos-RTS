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
import bot.ia.MultiStageGeneralScript;
import bot.io.MultiStageGeneralScriptIO;
import bot.scripts.GeneralScript;
import bot.scripts.BarrackBehavior.BarBehType;
import bot.scripts.BaseBehavior.BaseBehType;
import bot.scripts.HeavyBehavior.HeavyBehType;
import bot.scripts.LightBehavior.LightBehType;
import bot.scripts.RangedBehavior.RangedBehType;
import bot.scripts.WorkerBehavior.WorkBehType;
import bot.tournaments.Tournament;
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
		
		Scanner bot1file = new Scanner(new File("serial/Bot0.txt"));
		Scanner bot2file = new Scanner(new File("serial/Bot1.txt"));

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
		
		bots = new ArrayList<AI>(Arrays.asList(new MultiStageGeneralScript(Arrays.asList(
				new GeneralScript(utt, -1, -1, BaseBehType.ONEWORKER, BarBehType.RANGED, WorkBehType.THREEHARVAGGR,
				LightBehType.WAIT, HeavyBehType.WAIT, RangedBehType.WAIT)))));
		
		try {
			gs = new GameState(PhysicalGameState.load("maps/24x24/basesWorkers24x24.xml", utt), utt);
			Tournament.evaluate(bots, Arrays.asList(gs.getPhysicalGameState()), utt, 1, 3000, 3000, true, System.out, 0,
					false, false, "traces/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
