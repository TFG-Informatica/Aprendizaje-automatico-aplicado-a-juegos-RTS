package ia;

import ai.core.AI;
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
		private int[][] ties;
		private double[][] tie_time;
		private int[][] wins;
		private double[][] win_time;
		private int[][] loses;
		private double[][] lose_time;
		private Semaphore tieSem;
		private Semaphore winSem;
		private Semaphore loseSem;
		
		public Game(int a_ai1_idx, int a_ai2_idx, AI a_ai1, AI a_ai2, boolean a_visualize,
					PhysicalGameState a_pgs, UnitTypeTable a_utt, boolean a_partiallyObservable,
					int a_max_cycles, int a_max_inactive_cycles, int[][] a_ties,
					double[][] a_tie_time, int[][] a_wins, double[][] a_win_time, int[][] a_loses, 
					double[][] a_lose_time, Semaphore a_tieSem, Semaphore a_winSem, Semaphore a_loseSem) {
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
			ties = a_ties;
			tie_time = a_tie_time;
			wins = a_wins;
			win_time = a_win_time;
			loses = a_loses;
			lose_time = a_lose_time;
			tieSem = a_tieSem;
			winSem = a_winSem;
			loseSem = a_loseSem;
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

				boolean gameover = false;
				do {
					// System.gc();
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
				int winner = gs.winner();
				if (winner == -1) {
					try {
						tieSem.acquire();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					ties = ties;
					tie_time = tie_time;
					ties[ai1_idx][ai2_idx]++;
					tie_time[ai1_idx][ai2_idx] += gs.getTime();
					tieSem.release();
				} else if (winner == 0) {
					try {
						winSem.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wins = wins;
					win_time = win_time;
					++wins[ai1_idx][ai2_idx];
					win_time[ai1_idx][ai2_idx] += gs.getTime();
					winSem.release();
				} else if (winner == 1) {
					try {
						loseSem.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					loses = loses;
					lose_time = lose_time;
					++loses[ai1_idx][ai2_idx];
					lose_time[ai1_idx][ai2_idx] += gs.getTime();
					loseSem.release();
				}
			
		}
		
	}
	
	public static int DEBUG = 0;
	public static boolean  WRITE = false;

	public static double[] evaluate(List<AI> bots1, List<AI> bots2, List<PhysicalGameState> maps, UnitTypeTable utt, int iterations,
			int max_cycles, int max_inactive_cycles, boolean visualize, PrintStream out,
			int run_only_those_involving_this_AI, boolean skip_self_play, boolean partiallyObservable,
			String tracePrefix) throws Exception {
		int wins[][] = new int[bots1.size()][bots2.size()];
		int ties[][] = new int[bots1.size()][bots2.size()];
		int loses[][] = new int[bots1.size()][bots2.size()];

		double win_time[][] = new double[bots1.size()][bots2.size()];
		double tie_time[][] = new double[bots1.size()][bots2.size()];
		double lose_time[][] = new double[bots1.size()][bots2.size()];
		
		Semaphore tieSem = new Semaphore(1);
		Semaphore winSem = new Semaphore(1);
		Semaphore loseSem = new Semaphore(1);
		
		ExecutorService executor = Executors.newFixedThreadPool(4);

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
						Runnable g = new Game(ai1_idx, ai2_idx, ai1, ai2, visualize, pgs, utt, partiallyObservable,
								max_cycles, max_inactive_cycles, ties, tie_time, wins, win_time, loses, lose_time,
								tieSem, winSem, loseSem);
						executor.execute(g);
					}
				}
			}
		}
		
		executor.shutdown();  
        while (!executor.isTerminated());
		
		if (WRITE) {
			out.println("Wins: ");
			for (int ai1_idx = 0; ai1_idx < bots1.size(); ai1_idx++) {
				for (int ai2_idx = 0; ai2_idx < bots2.size(); ai2_idx++) {
					out.print(wins[ai1_idx][ai2_idx] + ", ");
				}
				out.println("");
			}
			out.println("Ties: ");
			for (int ai1_idx = 0; ai1_idx < bots1.size(); ai1_idx++) {
				for (int ai2_idx = 0; ai2_idx < bots2.size(); ai2_idx++) {
					out.print(ties[ai1_idx][ai2_idx] + ", ");
				}
				out.println("");
			}
			out.println("Loses: ");
			for (int ai1_idx = 0; ai1_idx < bots1.size(); ai1_idx++) {
				for (int ai2_idx = 0; ai2_idx < bots2.size(); ai2_idx++) {
					out.print(loses[ai1_idx][ai2_idx] + ", ");
				}
				out.println("");
			}
			out.println("Win average time: ");
			for (int ai1_idx = 0; ai1_idx < bots1.size(); ai1_idx++) {
				for (int ai2_idx = 0; ai2_idx < bots2.size(); ai2_idx++) {
					if (wins[ai1_idx][ai2_idx] > 0) {
						out.print((win_time[ai1_idx][ai2_idx] / wins[ai1_idx][ai2_idx]) + ", ");
					} else {
						out.print("-, ");
					}
				}
				out.println("");
			}
			out.println("Tie average time: ");
			for (int ai1_idx = 0; ai1_idx < bots1.size(); ai1_idx++) {
				for (int ai2_idx = 0; ai2_idx < bots2.size(); ai2_idx++) {
					if (ties[ai1_idx][ai2_idx] > 0) {
						out.print((tie_time[ai1_idx][ai2_idx] / ties[ai1_idx][ai2_idx]) + ", ");
					} else {
						out.print("-, ");
					}
				}
				out.println("");
			}
			out.println("Lose average time: ");
			for (int ai1_idx = 0; ai1_idx < bots1.size(); ai1_idx++) {
				for (int ai2_idx = 0; ai2_idx < bots2.size(); ai2_idx++) {
					if (loses[ai1_idx][ai2_idx] > 0) {
						out.print((lose_time[ai1_idx][ai2_idx] / loses[ai1_idx][ai2_idx]) + ", ");
					} else {
						out.print("-, ");
					}
				}
				out.println("");
			}
			out.flush();
		}

		double[] cuenta = new double[bots1.size()];
		
		for (int i = 0; i < bots1.size(); ++i) {
			cuenta[i] = 0;
			for (int j = 0; j < bots2.size(); ++j) {
				cuenta[i] = cuenta[i] + wins[i][j] - loses[i][j];
			}
		}
		
		return cuenta;
		
	}

}
