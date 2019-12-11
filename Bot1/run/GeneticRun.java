package run;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.AI;
import ia.Genetic;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;
import scripts.GeneralScript;

public class GeneticRun {
	
	private static PrintStream OUT = null;

	public static void main(String[] args) throws Exception {

		OUT = new PrintStream(new FileOutputStream("ResultadosGenetico.txt"));

		UnitTypeTable utt = new UnitTypeTable();
		GameState gs = null;
		List<AI> result = null;
		gs = new GameState(PhysicalGameState.load("maps/24x24/basesWorkers24x24.xml", utt), utt);
		Genetic g = new Genetic(100, 5, 10, utt, new AStarPathFinding(), gs, false);
		g.evolutionaryAlgorithm(100);
		result = g.getBestPopulation();

		OUT.println("Resultado:");
		System.out.println("Resultado:");
		for (AI scr : result) {
			System.out.println(((GeneralScript)scr).toString());
			OUT.println(scr.toString());
		}
	}

}
