package run;

import java.util.ArrayList;

import ai.abstraction.pathfinding.AStarPathFinding;
import ia.Genetic;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;
import scripts.GeneralScript;

public class GeneticRun {

	public static void main(String[] args) {		
		
		UnitTypeTable utt = new UnitTypeTable();
		GameState gs = null;
		ArrayList<GeneralScript> result = null;
		try {
			gs = new GameState(PhysicalGameState.load("maps/8x8/OneBaseWorker8x8.xml",utt),utt);
			Genetic g = new Genetic(50, 5, 10, utt, new AStarPathFinding(), gs);
			g.evolutionaryAlgorithm(100);	
			result = g.getBestPopulation();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Resultado:");
		for (GeneralScript scr : result) {
			System.out.println(scr.toString());
		}
	}

}
