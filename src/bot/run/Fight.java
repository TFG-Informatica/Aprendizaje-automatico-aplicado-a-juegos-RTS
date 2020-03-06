package bot.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import ai.core.AI;
import bot.ia.Tournament;
import bot.io.MultiStageGeneralScriptIO;
import bot.scripts.GeneralScript;
import bot.scripts.BarrackBehavior.BarBehType;
import bot.scripts.BaseBehavior.BaseBehType;
import bot.scripts.HeavyBehavior.HeavyBehType;
import bot.scripts.LightBehavior.LightBehType;
import bot.scripts.RangedBehavior.RangedBehType;
import bot.scripts.WorkerBehavior.WorkBehType;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;

public class Fight {
	public static void main(String[] args) throws FileNotFoundException {
		
		Scanner bot1file = new Scanner(new File("serial/Bot0.txt"));
		Scanner bot2file = new Scanner(new File("serial/Bot1.txt"));

		UnitTypeTable utt = new UnitTypeTable();
		GameState gs = null;
		List<AI> bots = new ArrayList<AI>();
		bots.add(MultiStageGeneralScriptIO.load(bot1file, utt));
		bots.add(new GeneralScript(utt, -1, -1, BaseBehType.RUSHWORKER, BarBehType.HEAVY,
				WorkBehType.HARVESTER, LightBehType.CLOSEST, HeavyBehType.CLOSEST, RangedBehType.CLOSESTDIRECT));
		
		bot1file.close();
		
		try {
			gs = new GameState(PhysicalGameState.load("maps/24x24/basesWorkers24x24.xml", utt), utt);
			Tournament.evaluate(bots, Arrays.asList(gs.getPhysicalGameState()), utt, 1, 3000, 300, true, System.out, -1,
					true, false, "traces/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
