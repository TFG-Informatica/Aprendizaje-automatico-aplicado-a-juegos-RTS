package bot.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import GNS.Droplet;
import ai.RandomBiasedAI;
import ai.competition.tiamat.Tiamat;
import ai.core.AI;
import ai.mcts.naivemcts.NaiveMCTS;
import bot.behavior.BarrackBehavior.BarBehType;
import bot.behavior.BaseBehavior.BaseBehType;
import bot.behavior.HeavyBehavior.HeavyBehType;
import bot.behavior.LightBehavior.LightBehType;
import bot.behavior.RangedBehavior.RangedBehType;
import bot.behavior.WorkerBehavior.WorkBehType;
import bot.eval.Time;
import bot.eval.TimePlusWins;
import bot.eval.Wins;
import bot.ia.GeneralScript;
import bot.ia.MultiStageGeneralScript;
import bot.io.MultiStageGeneralScriptIO;
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
	private static int iterations = 4;

	public static void main(String[] args) throws Exception {

		OUT = new PrintStream(new FileOutputStream("ResultadosLucha.txt"));
		
		UnitTypeTable utt = new UnitTypeTable(UnitTypeTable.VERSION_ORIGINAL_FINETUNED);
		GameState gs = new GameState(PhysicalGameState.load("maps/GardenOfWar64x64.xml", utt), utt);
		//Genetic g = new Genetic(25, 5, 5, utt, new AStarPathFinding(), gs, false);
		//g.evolutionaryAlgorithm(30);

		/*List<AI> bots1 = new ArrayList<AI>(Arrays.asList(
				new GeneralScript(utt, BaseBehType.TWOWORKER, BarBehType.LIGHT, WorkBehType.HARVESTER,
						LightBehType.CLOSEST, HeavyBehType.WAIT, RangedBehType.WAIT),
				new GeneralScript(utt, BaseBehType.TWOWORKER, BarBehType.HEAVY, WorkBehType.HARVESTER,
						LightBehType.CLOSEST, HeavyBehType.CLOSEST, RangedBehType.CLOSEST),
				new GeneralScript(utt, BaseBehType.THREEWORKER, BarBehType.LIGHT, WorkBehType.HARVESTER,
						LightBehType.CLOSEST, HeavyBehType.CLOSBUIL, RangedBehType.CLOSEST),
				new GeneralScript(utt, BaseBehType.THREEWORKER, BarBehType.HEAVY, WorkBehType.HARVESTER,
						LightBehType.CLOSEST, HeavyBehType.CLOSEST, RangedBehType.WAIT)));
		*/
		
		List<AI> bots1 = new ArrayList<AI>();
		for (int i = 0; i < 1; ++i) {
			IN = new Scanner(new File("serial/RandomBot64x64.txt"));
			bots1.add(MultiStageGeneralScriptIO.load(IN, utt));
		}
		
		List<AI> bots2 = Arrays.asList(new WorkerRushPlusPlus(utt), new LightRush(utt), 
										new NaiveMCTS(utt), new Droplet(utt), new Tiamat(utt));

		/*
		 * OUT.println("Genetico terminado. Resultados:");
		 * System.out.println("Genetico terminado. Resultados:"); for (AI bot :
		 * g.getBestPopulation()) { OUT.println(((GeneralScript) bot).toString());
		 * System.out.println(((GeneralScript) bot).toString()); } OUT.flush();
		 * System.out.flush();
		 */

		double[][] res = ThreadedTournament.evaluate(bots1, bots2, Arrays.asList(gs.getPhysicalGameState()), utt, iterations,
				8000, 8000, false, new TimePlusWins(8000), OUT, -1, false, false, "traces/");
		double[] resultado = new double[res.length];
		for (int i = 0; i < resultado.length; ++i) {
			resultado[i] = 0;
			for (int j = 0; j < res[i].length; ++j) {
				resultado[i] += res[i][j];
				System.out.println(res[i][j]);
			}
		}
		
		
		
//		OUT.println("Competición final terminada. Resultados:");
//		System.out.println("Competición final terminada. Resultados:");
//		for (int i = 0; i < resultado.length; ++i) {
//			OUT.println(/*((MultiStageGeneralScript) bots1.get(i)).toString()*/"Bot" + i + " ha ganado " + resultado[i] + " de "
//					+ bots2.size()*iterations + " partidas.");
//			System.out.println(/*((MultiStageGeneralScript) bots1.get(i)).toString()*/"Bot" + i + " ha ganado " + resultado[i] + " de "
//					+ bots2.size()*iterations + " partidas.");
//			System.out.println("Perdió contra:");
//			for (int j = 0; j < res[i].length; ++j) {
//				if (res[i][j] <= 0)
//					System.out.println("Foe" + j);
//			}
//		}
		
		OUT.close();
	}
}
