package bot.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import ai.RandomBiasedAI;
import ai.abstraction.*;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.AI;
import bot.ia.DFSTreeAI;
import bot.ia.Genetic;
import bot.ia.MultiStageGeneralScript;
import bot.ia.ThreadedTournament;
import bot.io.MultiStageGeneralScriptIO;
import bot.scripts.GeneralScript;
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
import mrtsFixed.bots.WorkerRushPlusPlus;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;

public class MultiFight {

	private static Scanner IN = null;
	private static PrintStream OUT = null;
	private static int iterations = 20;

	public static void main(String[] args) throws Exception {

		OUT = new PrintStream(new FileOutputStream("ResultadosLucha.txt"));
		
		UnitTypeTable utt = new UnitTypeTable();
		GameState gs = new GameState(PhysicalGameState.load("maps/24x24/basesWorkers24x24.xml", utt), utt);
		//Genetic g = new Genetic(25, 5, 5, utt, new AStarPathFinding(), gs, false);
		//g.evolutionaryAlgorithm(30);

		List<AI> bots1 = new ArrayList<AI>();
		for (int i = 0; i < 10; ++i) {
			IN = new Scanner(new File("serial/Bot" + i + ".txt"));
			bots1.add(MultiStageGeneralScriptIO.load(IN, utt));
		}
		
		List<AI> bots2 = Arrays.asList(new EconomyMilitaryRush(utt), new EconomyRush(utt), new EconomyRushBurster(utt),
				new EMRDeterministico(utt), new HeavyDefense(utt), new HeavyRush(utt), new LightDefense(utt),
				new LightRush(utt), new RandomBiasedAI(utt), new RangedDefense(utt), new RangedRush(utt),
				new SimpleEconomyRush(utt), new WorkerDefense(utt), /*new WorkerRush(utt),*/ new WorkerRushPlusPlus(utt));

		/*
		 * OUT.println("Genetico terminado. Resultados:");
		 * System.out.println("Genetico terminado. Resultados:"); for (AI bot :
		 * g.getBestPopulation()) { OUT.println(((GeneralScript) bot).toString());
		 * System.out.println(((GeneralScript) bot).toString()); } OUT.flush();
		 * System.out.flush();
		 */

		double[] resultado = ThreadedTournament.evaluate(bots1, bots2, Arrays.asList(gs.getPhysicalGameState()), utt, iterations,
				3000, 100, false, OUT, -1, false, false, "traces/");

		OUT.println("Competici�n final terminada. Resultados:");
		System.out.println("Competici�n final terminada. Resultados:");
		for (int i = 0; i < resultado.length; ++i) {
			OUT.println(((MultiStageGeneralScript) bots1.get(i)).toString() + " ha ganado " + resultado[i] + " de "
					+ bots2.size()*iterations + " partidas.");
			System.out.println(((MultiStageGeneralScript) bots1.get(i)).toString() + " ha ganado " + resultado[i] + " de "
					+ bots2.size()*iterations + " partidas.");
		}
		
		OUT.close();
	}
}
