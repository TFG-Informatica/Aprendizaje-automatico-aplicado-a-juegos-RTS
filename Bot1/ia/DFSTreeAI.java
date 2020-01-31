package ia;

import java.util.List;

import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.ParameterSpecification;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import rts.GameState;
import rts.PlayerAction;

public class DFSTreeAI extends AbstractionLayerAI{
	
	private final int MAX_DEPTH = 3;
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
		
		
		
	}
	
	public float node(GameState gs, int depth) {	
		if (depth == MAX_DEPTH) {
			return new SimpleSqrtEvaluationFunction3().evaluate(player, 1-player, gs);
		} else {
			int maxPoints;
			for (int i = 0; i < availStrat.size(); ++i) {
				// jugar con stategia i
				int points = node(gs3, depth + 1);
				maxPoints = (points > maxPoints) ? points : maxPoints;
			}
			return maxPoints;
		}
	}

	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// new AStarPathFinding esta mal pero no se puede arreglar si hacemos hilos
	@Override
	public AI clone() {
		return new DFSTreeAI(new AStarPathFinding(), getTimeBudget(), getIterationsBudget(), availStrat, oponent.clone(), player);
	}

	@Override
	public List<ParameterSpecification> getParameters() {
		// Lista de parametros que de momento no se sabe para que vale.
		return null;
	}
	
}
