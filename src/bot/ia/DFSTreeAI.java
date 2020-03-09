package bot.ia;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.ParameterSpecification;
import ai.evaluation.*;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import gui.PhysicalGameStateJFrame;
import gui.PhysicalGameStatePanel;
import rts.GameState;
import rts.PlayerAction;

public class DFSTreeAI extends AbstractionLayerAI{
	
	private final int MAX_DEPTH = 10;
	private List<AI> availStrat;
	private AI oponent;
	private int player;
	
	public DFSTreeAI(PathFinding a_pf, List<AI> a_availStrat, AI a_oponent, int a_player) {
		super(a_pf);
		availStrat = a_availStrat;
		oponent = a_oponent;
		player = a_player;
	}
	
	public DFSTreeAI(PathFinding a_pf, int a_timebudget, int a_cyclesbudget, List<AI> a_availStrat, AI a_oponent, int a_player) {
		super(a_pf, a_timebudget, a_cyclesbudget);
		availStrat = a_availStrat;
		oponent = a_oponent;
	}

	@Override
	public PlayerAction getAction(int player, GameState gs) throws Exception {
		//PhysicalGameStateJFrame panel = PhysicalGameStatePanel.newVisualizer(gs, 320, 320, false, new SimpleEvaluationFunction(), PhysicalGameStatePanel.COLORSCHEME_BLACK);
		return root(gs);		
	}
	
	public PlayerAction root(GameState gs) throws Exception {
		
		PlayerAction pa = availStrat.get(0).getAction(player, gs);
		GameState gs2 = gs.cloneIssue(pa);
		PlayerAction oa = oponent.getAction(1-player, gs2);
		GameState gs3 = gs2.cloneIssue(oa);
		
		float maxPoints = node(gs3,1);
		PlayerAction best = pa;
		for (int i = 1; i < availStrat.size(); ++i) {
			//jugar con estrategia i un turno y el del oponente
			pa = availStrat.get(i).getAction(player, gs);
			gs2 = gs.cloneIssue(pa);
			oa = oponent.getAction(1-player, gs2);
			gs3 = gs2.cloneIssue(oa);
			
			float points = node(gs3,1);
			if (maxPoints < points) {
				maxPoints = points;
				best = pa;
			}
		}
		return best;
	}
	
	public float node(GameState gs, int depth) throws Exception {	
		if (depth == MAX_DEPTH) {
			return new SimpleSqrtEvaluationFunction3().evaluate(player, 1-player, gs);
		} else {
			PlayerAction pa = oponent.getAction(player, gs);
			GameState gs2 = gs.cloneIssue(pa);
			pa = oponent.getAction(1-player, gs2);
			GameState gs3 = gs2.cloneIssue(pa);
			float maxPoints = node(gs3, depth + 1);
			//panel.paint(new Graphics());
			/*for (int i = 1; i < availStrat.size(); ++i) {
				//jugar con estrategia i un turno y el del oponente
				pa = availStrat.get(i).getAction(player, gs);
				gs2 = gs.cloneIssue(pa);
				pa = oponent.getAction(1-player, gs2);
				gs3 = gs2.cloneIssue(pa);
				
				float points = node(gs3, depth + 1);
				maxPoints = (points > maxPoints) ? points : maxPoints;
			}*/
			return maxPoints;
		}
	}

	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// new AStarPathFinding esta mal pero no se puede arreglar si hacemos hilos
	@Override
	public AI clone() {
		List<AI> navailStrat = new ArrayList<AI>();
		for (int i = 0; i < availStrat.size(); ++i) {
			navailStrat.add(availStrat.get(i).clone());
		}
		return new DFSTreeAI(new AStarPathFinding(), getTimeBudget(), getIterationsBudget(), navailStrat, oponent.clone(), player);
	}

	@Override
	public List<ParameterSpecification> getParameters() {
		// Lista de parametros que de momento no se sabe para que vale.
		return null;
	}
	
}
