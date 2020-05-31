package bot2.run;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import ai.core.AI;
import bot2.ia.ParamGenetic;
import bot2.ia.ParamMultiStageGeneralScript;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;

public class GeneticRun {
	
	private static PrintStream OUT = null;

	public static void main(String[] args) throws Exception {

		OUT = new PrintStream(new FileOutputStream("data/ResultadosGenetico.txt"));

		UnitTypeTable utt = new UnitTypeTable(UnitTypeTable.VERSION_ORIGINAL_FINETUNED);
		GameState gs = null;
		List<AI> result = null;
		gs = new GameState(PhysicalGameState.load("maps/24x24/basesWorkers24x24.xml", utt), utt);
		ParamGenetic g = new ParamGenetic(20, 5, 3, utt, gs, false);
		g.evolutionaryAlgorithm(100);
		result = g.getBestPopulation();
  
		OUT.println("Resultado:");
		System.out.println("Resultado:");
		for (int i = 0; i < result.size(); ++i) {
			System.out.println(((ParamMultiStageGeneralScript)result.get(i)).toString());
			OUT.println(result.get(i).toString());
			PrintStream ser = new PrintStream(new FileOutputStream("serial/Bot" + i + ".txt"));
		}
		OUT.close();
		
	}

}
