package bot2.ia;

import java.util.ArrayList;
import java.util.List;

import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.AI;
import ai.core.ParameterSpecification;
import bot2.paramScripts.ParamGeneralScript;
import rts.GameState;
import rts.PlayerAction;

public class ParamMultiStageGeneralScript extends AbstractionLayerAI {
	
	int MAX_CYCLES = 3000;
	int stageCycles;
	List<ParamGeneralScript> scripts;

	public ParamMultiStageGeneralScript(List<ParamGeneralScript> a_scripts) {
		super(new AStarPathFinding());
		scripts = a_scripts;
		stageCycles = MAX_CYCLES / scripts.size() + 1; 
	}

	public ParamMultiStageGeneralScript(int timebudget, int cyclesbudget, 
		List<ParamGeneralScript> a_scripts) {
		super(new AStarPathFinding(), timebudget, cyclesbudget);
		scripts = a_scripts;
		stageCycles = MAX_CYCLES / scripts.size() + 1;
	}
	
	public List<ParamGeneralScript> getScripts() {
		List<ParamGeneralScript> scriptsCopy = new ArrayList<ParamGeneralScript>();
		for (ParamGeneralScript gs : scripts) 
			scriptsCopy.add(gs.clone());
		return scriptsCopy;
	}
	
	@Override
	public PlayerAction getAction(int player, GameState gs) throws Exception {
		return scripts.get(gs.getTime() / stageCycles).getAction(player, gs);
	}
	
	@Override
	public ParamMultiStageGeneralScript clone() {
		List<ParamGeneralScript> scriptsCopy = new ArrayList<ParamGeneralScript>();
		for (ParamGeneralScript gs : scripts) 
			scriptsCopy.add(gs.clone());
		return new ParamMultiStageGeneralScript(getTimeBudget(), 
											getIterationsBudget(), scriptsCopy);
	}
	
	@Override
	public void reset() {
		for (ParamGeneralScript gs : scripts) 
			gs.reset();
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
