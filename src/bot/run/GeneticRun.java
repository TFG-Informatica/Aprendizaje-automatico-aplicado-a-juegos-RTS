package bot.run;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.List;

import ai.core.AI;
import bot.ia.MultiStageGeneralScript;
import bot.ia.MultiStageGenetic;
import bot.io.MultiStageGeneralScriptIO;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;

public class GeneticRun {
	
	private static PrintStream OUT = null;

	public static void main(String[] args) throws Exception {

		OUT = new PrintStream(new FileOutputStream("data/ResultadosGenetico.txt"));

		UnitTypeTable utt = new UnitTypeTable();
		GameState gs = null;
		List<AI> result = null;
		gs = new GameState(PhysicalGameState.load("maps/24x24/basesWorkers24x24.xml", utt), utt);
		MultiStageGenetic g = new MultiStageGenetic(25, 10, 10, 3, utt, gs, false);
		g.evolutionaryAlgorithm(10);
		result = g.getBestPopulation();

		OUT.println("Resultado:");
		System.out.println("Resultado:");
		for (int i = 0; i < result.size(); ++i) {
			System.out.println(((MultiStageGeneralScript)result.get(i)).toString());
			OUT.println(result.get(i).toString());
			PrintStream ser = new PrintStream(new FileOutputStream("serial/Bot" + i + ".txt"));
			MultiStageGeneralScriptIO.store(ser, (MultiStageGeneralScript) result.get(i));
			ser.close();
		}
		OUT.close();
		
	}

}
