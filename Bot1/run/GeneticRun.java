package run;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import ai.core.AI;
import ia.MultiStageGeneralScript;
import ia.MultiStageGenetic;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;

public class GeneticRun {
	
	private static PrintStream OUT = null;

	public static void main(String[] args) throws Exception {

		OUT = new PrintStream(new FileOutputStream("ResultadosGenetico.txt"));

		UnitTypeTable utt = new UnitTypeTable();
		GameState gs = null;
		List<AI> result = null;
		gs = new GameState(PhysicalGameState.load("maps/24x24/basesWorkers24x24.xml", utt), utt);
		MultiStageGenetic g = new MultiStageGenetic(20, 5, 5, 3, utt, gs, false);
		g.evolutionaryAlgorithm(20);
		result = g.getBestPopulation();

		OUT.println("Resultado:");
		System.out.println("Resultado:");
		for (AI scr : result) {
			System.out.println(((MultiStageGeneralScript)scr).toString());
			OUT.println(scr.toString());
		}
	}

}
