package bot.ia;

import java.util.ArrayList;
import java.util.List;

import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.AI;
import ai.core.ParameterSpecification;
import bot.scripts.GeneralScript;
import rts.GameState;
import rts.PlayerAction;

public class MultiStageGeneralScript extends AbstractionLayerAI {
	
	int max_cycles;
	int stageCycles;
	List<GeneralScript> scripts;

	public MultiStageGeneralScript(int a_max_cycles, List<GeneralScript> a_scripts) {
		super(new AStarPathFinding());
		max_cycles = a_max_cycles;
		scripts = a_scripts;
		stageCycles = max_cycles / scripts.size() + 1; 
	}

	public MultiStageGeneralScript(int timebudget, int cyclesbudget, int a_max_cycles, 
		List<GeneralScript> a_scripts) {
		super(new AStarPathFinding(), timebudget, cyclesbudget);
		max_cycles = a_max_cycles;
		scripts = a_scripts;
		stageCycles = max_cycles / scripts.size() + 1;
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
	public MultiStageGeneralScript clone() {
		List<GeneralScript> scriptsCopy = new ArrayList<GeneralScript>();
		for (GeneralScript gs : scripts) 
			scriptsCopy.add(gs.clone());
		return new MultiStageGeneralScript(getTimeBudget(), getIterationsBudget(),  max_cycles, scriptsCopy);
	}
	
	@Override
	public void reset() {
		for (GeneralScript gs : scripts) 
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((scripts == null) ? 0 : scripts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultiStageGeneralScript other = (MultiStageGeneralScript) obj;
		if (scripts == null) {
			if (other.scripts != null)
				return false;
		} else if (!scripts.equals(other.scripts))
			return false;
		return true;
	}

	public int get_cycles() {
		return max_cycles;
	}
}
