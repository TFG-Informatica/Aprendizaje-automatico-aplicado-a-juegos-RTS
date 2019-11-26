package run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.AI;
import ia.ThreadedTournament;
import ia.Tournament;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;
import scripts.GeneralScript;
import scripts.BarrackBehavior.BarBehType;
import scripts.BaseBehavior.BaseBehType;
import scripts.HeavyBehavior.HeavyBehType;
import scripts.LightBehavior.LightBehType;
import scripts.RangedBehavior.RangedBehType;
import scripts.WorkerBehavior.WorkBehType;

public class Fight {
public static void main(String[] args) {		
		
		UnitTypeTable utt = new UnitTypeTable();
		GameState gs = null;
		List<AI> bots = new ArrayList<AI>();
		bots.add(new GeneralScript(utt, new AStarPathFinding(), -1, -1,
				BaseBehType.THREEWORKER, BarBehType.HEAVY, WorkBehType.HARVESTER, 
				LightBehType.LESSPERCHP, HeavyBehType.LESSPERCHP, RangedBehType.LESSHP));
		bots.add(new GeneralScript(utt, new AStarPathFinding(), -1, -1,
				BaseBehType.THREEWORKER, BarBehType.RANGED, WorkBehType.HARVESTER, 
				LightBehType.LESSHP, HeavyBehType.CLOSEST, RangedBehType.LESSHP));
		try {
			gs = new GameState(PhysicalGameState.load("maps/8x8/OneBaseWorker8x8.xml",utt),utt);
			ThreadedTournament.evaluate(bots, Arrays.asList(gs.getPhysicalGameState()), utt, 1,
					3000, 300, true, System.out, -1, false, false, "");	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
