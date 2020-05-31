package bot.run;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import ai.RandomBiasedAI;
import ai.core.AI;
import bot.eval.Wins;
import bot.ia.MultiStageGeneralScript;
import bot.scripts.GeneralScript;
import bot.scripts.BarrackBehavior.BarBehType;
import bot.scripts.BaseBehavior.BaseBehType;
import bot.scripts.HeavyBehavior.HeavyBehType;
import bot.scripts.LightBehavior.LightBehType;
import bot.scripts.RangedBehavior.RangedBehType;
import bot.scripts.WorkerBehavior.WorkBehType;
import bot.tournaments.ThreadedTournament;
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
	private static int iterations = 1;

	public static void main(String[] args) throws Exception {

		OUT = new PrintStream(new FileOutputStream("ResultadosLucha.txt"));
		
		UnitTypeTable utt = new UnitTypeTable();
		GameState gs = new GameState(PhysicalGameState.load("maps/24x24/basesWorkers24x24.xml", utt), utt);
		//Genetic g = new Genetic(25, 5, 5, utt, new AStarPathFinding(), gs, false);
		//g.evolutionaryAlgorithm(30);

		List<AI> bots1 = new ArrayList<AI>(Arrays.asList(new MultiStageGeneralScript(Arrays.asList(
				new GeneralScript(utt, BaseBehType.TWOWORKER, BarBehType.RANGED, WorkBehType.HARVESTER,
						LightBehType.WAIT, HeavyBehType.WAIT, RangedBehType.WAIT),
				new GeneralScript(utt, BaseBehType.TWOWORKER, BarBehType.LIGHT, WorkBehType.HARVESTER,
						LightBehType.CLOSEST, HeavyBehType.WAIT, RangedBehType.CLOSEST),
				new GeneralScript(utt, BaseBehType.RUSHWORKER, BarBehType.LIGHT, WorkBehType.ONEHARVAGGR,
						LightBehType.CLOSEST, HeavyBehType.CLOSBUIL, RangedBehType.CLOSEST)))));
		
		/*for (int i = 0; i < 10; ++i) {
			IN = new Scanner(new File("serial/Bot" + i + ".txt"));
			bots1.add(MultiStageGeneralScriptIO.load(IN, utt));
		}*/
		
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

		double[][] res = ThreadedTournament.evaluate(bots1, bots2, Arrays.asList(gs.getPhysicalGameState()), utt, iterations,
				3000, 3000, false, new Wins(), OUT, -1, true, false, "traces/");
		int[] resultado = new int[res.length];
		for (int i = 0; i < resultado.length; ++i) {
			resultado[i] = 0;
			for (int j = 0; j < res[i].length; ++j) {
				resultado[i] += res[i][j];
			}
		}
		
		OUT.println("Competición final terminada. Resultados:");
		System.out.println("Competición final terminada. Resultados:");
		for (int i = 0; i < resultado.length; ++i) {
			OUT.println(/*((MultiStageGeneralScript) bots1.get(i)).toString()*/"Bot" + i + " ha ganado " + resultado[i] + " de "
					+ bots2.size()*iterations + " partidas.");
			System.out.println(/*((MultiStageGeneralScript) bots1.get(i)).toString()*/"Bot" + i + " ha ganado " + resultado[i] + " de "
					+ bots2.size()*iterations + " partidas.");
			System.out.println("Perdió contra:");
			for (int j = 0; j < res[i].length; ++j) {
				if (res[i][j] <= 0)
					System.out.println("Foe" + j);
			}
		}
		
		OUT.close();
	}
}
