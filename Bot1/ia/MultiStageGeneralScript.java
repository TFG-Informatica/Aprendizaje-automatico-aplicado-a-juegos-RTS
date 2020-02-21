package ia;

import java.util.ArrayList;
import java.util.List;

import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.AI;
import ai.core.ParameterSpecification;
import rts.GameState;
import rts.PlayerAction;
import scripts.GeneralScript;

public class MultiStageGeneralScript extends AbstractionLayerAI {
	
	int MAX_CYCLES = 2000;
	int stageCycles;
	List<GeneralScript> scripts;

	public MultiStageGeneralScript(List<GeneralScript> a_scripts) {
		super(new AStarPathFinding());
		scripts = a_scripts;
		stageCycles = MAX_CYCLES / scripts.size() + 1; 
	}

	public MultiStageGeneralScript(int timebudget, int cyclesbudget, 
		List<GeneralScript> a_scripts) {
		super(new AStarPathFinding(), timebudget, cyclesbudget);
		scripts = a_scripts;
		stageCycles = MAX_CYCLES / scripts.size() + 1;
	}
	
	public List<GeneralScript> getScripts() {
		List<GeneralScript> scriptsCopy = new ArrayList<GeneralScript>();
		for (GeneralScript gs : scripts) 
			scriptsCopy.add(gs.clone());
		return scriptsCopy;
	}
	
	@Override
	public PlayerAction getAction(int player, GameState gs) throws Exception {
		return scripts.get(gs.getTime() / stageCycles).getAction(player, gs);
	}
	
	@Override
	public AI clone() {
		List<GeneralScript> scriptsCopy = new ArrayList<GeneralScript>();
		for (GeneralScript gs : scripts) 
			scriptsCopy.add(gs.clone());
		return new MultiStageGeneralScript(getTimeBudget(), 
											getIterationsBudget(), scriptsCopy);
	}
	
	@Override
	public String toString() {
		String s = "<" + scripts.get(0).toString();
		for (int i = 1; i < scripts.size(); ++i) {
			s += " | " + scripts.get(i).toString();
		}
		s += ">";
		return s;
	}

	@Override
	public List<ParameterSpecification> getParameters() {
		// Lista de parametros que de momento no se sabe para que vale.
		return null;
	}

}
