package run;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.RandomBiasedAI;
import ai.abstraction.*;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.AI;
import ia.DFSTreeAI;
import ia.Genetic;
import ia.ThreadedTournament;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;
import scripts.GeneralScript;

public class MultiFight {

	private static PrintStream OUT = null;
	private static int iterations = 10;

	public static void main(String[] args) throws Exception {

		OUT = new PrintStream(new FileOutputStream("ResultadosGenetico.txt"));

		UnitTypeTable utt = new UnitTypeTable();
		GameState gs = new GameState(PhysicalGameState.load("maps/24x24/basesWorkers24x24.xml", utt), utt);
		Genetic g = new Genetic(25, 5, 5, utt, new AStarPathFinding(), gs, false);
		g.evolutionaryAlgorithm(30);

		List<AI> bots1 = new ArrayList<AI>();
		bots1.add(new DFSTreeAI(new AStarPathFinding(), g.getBestPopulation(), new RandomBiasedAI(), 0));
		
		//List<AI> bots2 = g.getBestPopulation();
		List<AI> bots2 = new ArrayList<AI>();
		bots2.add(new WorkerRush(utt));

		OUT.println("Genetico terminado. Resultados:");
		System.out.println("Genetico terminado. Resultados:");
		for (AI bot : g.getBestPopulation()) {
			OUT.println(((GeneralScript) bot).toString());
			System.out.println(((GeneralScript) bot).toString());
		}
		OUT.flush();
		System.out.flush();

		double[] resultado = ThreadedTournament.evaluate(bots1, bots2, Arrays.asList(gs.getPhysicalGameState()), utt, iterations,
				3000, 100, true, OUT, -1, false, false, "traces/");

		OUT.println("Competición final terminada. Resultados:");
		System.out.println("Competición final terminada. Resultados:");
		/*for (int i = 0; i < resultado.length; ++i) {
			OUT.println(((GeneralScript) bots1.get(i)).toString() + " ha ganado " + resultado[i] + " de "
					+ bots2.size()*iterations + " partidas.");
			System.out.println(((GeneralScript) bots1.get(i)).toString() + " ha ganado " + resultado[i] + " de "
					+ bots2.size()*iterations + " partidas.");
		}*/
		OUT.println("El árbol ha ganado " + resultado[0] + " de " + bots2.size()*iterations + " partidas.");
		System.out.println("El árbol ha ganado " + resultado[0] + " de " + bots2.size()*iterations + " partidas.");
		OUT.flush();
		System.out.println();
		
		OUT.close();
	}
}
