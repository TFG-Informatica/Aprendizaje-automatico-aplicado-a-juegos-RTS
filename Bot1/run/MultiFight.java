package run;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.AI;
import ia.Genetic;
import ia.ThreadedTournament;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;
import scripts.GeneralScript;

public class MultiFight {

	private static PrintStream OUT = null;
	private static int iterations = 1;

	public static void main(String[] args) throws Exception {

		OUT = new PrintStream(new FileOutputStream("ResultadosGenetico.txt"));

		UnitTypeTable utt = new UnitTypeTable();
		GameState gs = new GameState(PhysicalGameState.load("maps/24x24/basesWorkers24x24.xml", utt), utt);
		Genetic g = new Genetic(10, 5, 5, utt, new AStarPathFinding(), gs, false);
		g.evolutionaryAlgorithm(100);

		List<AI> bots1 = g.getBestPopulation();
		List<AI> bots2 = g.getCompleteSet();

		OUT.println("Genetico terminado. Resultados:");
		System.out.println("Genetico terminado. Resultados:");
		for (AI bot : bots1) {
			OUT.println(((GeneralScript) bot).toString());
			System.out.println(((GeneralScript) bot).toString());
		}
		OUT.flush();
		System.out.flush();

		int[] resultado = ThreadedTournament.evaluate(bots1, bots2, Arrays.asList(gs.getPhysicalGameState()), utt, iterations,
				2000, 100, false, OUT, -1, false, false, "traces/");

		OUT.println("Competición final terminada. Resultados:");
		System.out.println("Competición final terminada. Resultados:");
		for (int i = 0; i < resultado.length; ++i) {
			OUT.println(((GeneralScript) bots1.get(i)).toString() + " ha ganado " + resultado[i] + " de "
					+ bots2.size()*iterations + " partidas.");
			System.out.println(((GeneralScript) bots1.get(i)).toString() + " ha ganado " + resultado[i] + " de "
					+ bots2.size()*iterations + " partidas.");
		}
		OUT.flush();
		System.out.println();
		
		OUT.close();
	}
}
