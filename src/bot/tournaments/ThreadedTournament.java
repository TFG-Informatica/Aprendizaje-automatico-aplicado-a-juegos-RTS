package bot.tournaments;

import ai.core.AI;
import bot.eval.EvalFunc;
import gui.PhysicalGameStateJFrame;
import gui.PhysicalGameStatePanel;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import rts.GameState;
import rts.PartiallyObservableGameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.Trace;
import rts.TraceEntry;
import rts.units.UnitTypeTable;

public class ThreadedTournament {

	private static class Game implements Runnable {
		
		private int ai1_idx;
		private int ai2_idx;
		private AI ai1;
		private AI ai2;
		private boolean visualize;
		private PhysicalGameState pgs;
		private UnitTypeTable utt;
		private boolean partiallyObservable;
		private int max_cycles;
		private long max_inactive_cycles;
		private double[][] punct; 
		private Semaphore punctSem;
		private EvalFunc eval;
		private int player;
		
		public Game(int a_ai1_idx, int a_ai2_idx, AI a_ai1, AI a_ai2, boolean a_visualize,
					PhysicalGameState a_pgs, UnitTypeTable a_utt, boolean a_partiallyObservable,
					int a_max_cycles, int a_max_inactive_cycles, double[][] a_punct, Semaphore a_punctSem,
					EvalFunc a_eval, int a_player) {
			ai1_idx = a_ai1_idx;
			ai2_idx = a_ai2_idx;
			ai1 = a_ai1;
			ai2 = a_ai2;
			visualize = a_visualize;
			pgs = a_pgs;
			utt = a_utt;
			partiallyObservable = a_partiallyObservable;
			max_cycles = a_max_cycles;
			max_inactive_cycles = a_max_inactive_cycles;
			punct = a_punct;
			punctSem = a_punctSem;
			eval = a_eval;
			player = a_player;
		}
		
		public void run() {
				long lastTimeActionIssued = 0;

				ai1.reset();
				ai2.reset();
				GameState gs = new GameState(pgs.clone(), utt);

				Trace trace = new Trace(utt);
				TraceEntry te = new TraceEntry(gs.getPhysicalGameState().clone(), gs.getTime());
				trace.addEntry(te);

				PhysicalGameStateJFrame w = null;
				if (visualize)
					w = PhysicalGameStatePanel.newVisualizer(gs, 600, 600, partiallyObservable);

				try {
					ai1.preGameAnalysis(gs,0);
					ai2.preGameAnalysis(gs,0);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				
				boolean gameover = false;
				do {
					if (visualize) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					PlayerAction pa1 = null, pa2 = null;
					if (partiallyObservable) {
						try {						
							pa1 = ai1.getAction(0, new PartiallyObservableGameState(gs, 0));
							pa2 = ai2.getAction(1, new PartiallyObservableGameState(gs, 1));
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						try {
							pa1 = ai1.getAction(0, gs);
							pa2 = ai2.getAction(1, gs);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (!pa1.isEmpty() || !pa2.isEmpty()) {
						te = new TraceEntry(gs.getPhysicalGameState().clone(), gs.getTime());
						te.addPlayerAction(pa1.clone());
						te.addPlayerAction(pa2.clone());
						trace.addEntry(te);
					}

					if (gs.issueSafe(pa1))
						lastTimeActionIssued = gs.getTime();
					if (gs.issueSafe(pa2))
						lastTimeActionIssued = gs.getTime();

					gameover = gs.cycle();
					if (w != null) {
						w.setStateCloning(gs);
						w.repaint();
						try {
							Thread.sleep(1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} while (!gameover && (gs.getTime() < max_cycles)
						&& (gs.getTime() - lastTimeActionIssued < max_inactive_cycles));

				te = new TraceEntry(gs.getPhysicalGameState().clone(), gs.getTime());
				trace.addEntry(te);

				if (w != null)
					w.dispose();
				
				try {
					punctSem.acquire();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				punct = punct;
				punct[ai1_idx][ai2_idx] += eval.evaluate(gs, player);
				punctSem.release();			
		}
		
	}
	
	public static int DEBUG = 0;
	public static boolean  WRITE = false;

	public static double[][] evaluate(List<AI> bots1, List<AI> bots2, List<PhysicalGameState> maps, UnitTypeTable utt, int iterations,
			int max_cycles, int max_inactive_cycles, boolean visualize, EvalFunc eval, PrintStream out,
			int run_only_those_involving_this_AI, boolean skip_self_play, boolean partiallyObservable,
			String tracePrefix) throws Exception {

		double[][] punct = new double[bots1.size()][bots2.size()];
		
		Semaphore punctSem = new Semaphore(1);
		
		int numThreads = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool((visualize)?1:numThreads);

		for (int ai1_idx = 0; ai1_idx < bots1.size(); ai1_idx++) {
			for (int ai2_idx = 0; ai2_idx < bots2.size(); ai2_idx++) {
				if (run_only_those_involving_this_AI != -1 && ai1_idx != run_only_those_involving_this_AI
						&& ai2_idx != run_only_those_involving_this_AI)
					continue;
				if (skip_self_play && ai1_idx == ai2_idx)
					continue;
				
				for (PhysicalGameState pgs : maps) {
					for (int i = 0; i < iterations; i++) {
						AI ai1 = bots1.get(ai1_idx).clone();
						AI ai2 = bots2.get(ai2_idx).clone();
						if (i % 2 == 1) {
							Runnable g = new Game(ai1_idx, ai2_idx, ai1, ai2, visualize, pgs, utt, partiallyObservable,
								max_cycles, max_inactive_cycles, punct, punctSem, eval, 0);
							executor.execute(g);
						} else {
							Runnable g = new Game(ai1_idx, ai2_idx, ai2, ai1, visualize, pgs, utt, partiallyObservable,
									max_cycles, max_inactive_cycles, punct, punctSem, eval, 1);
							executor.execute(g);
							
						}
					}
				}
			}
		}
		
		executor.shutdown();  
        while (!executor.isTerminated());

		return punct;
		
	}

}
