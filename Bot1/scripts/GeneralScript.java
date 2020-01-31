package scripts;

import java.util.ArrayList;
import java.util.List;

import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.ParameterSpecification;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.PlayerAction;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;
import scripts.BarrackBehavior.BarBehType;
import scripts.BaseBehavior.BaseBehType;
import scripts.HeavyBehavior.HeavyBehType;
import scripts.LightBehavior.LightBehType;
import scripts.RangedBehavior.RangedBehType;
import scripts.WorkerBehavior.WorkBehType;

public class GeneralScript extends AbstractionLayerAI {
	
	private BaseBehType baseBehType;
	private BarBehType barBehType;
	private WorkBehType workBehType;
	private LightBehType lightBehType;
	private HeavyBehType heavyBehType;
	private RangedBehType rangeBehType;
	
	private BaseBehavior baseBeh;
	private BarrackBehavior barBeh;
	private WorkerBehavior workBeh;
	private LightBehavior lightBeh;
	private HeavyBehavior heavyBeh;
	private RangedBehavior rangeBeh;
	
	private UnitTypeTable utt;
	private UnitType workerType;
	private UnitType baseType;
	private UnitType barracksType;
	private UnitType lightType;
	private UnitType heavyType;
	private UnitType rangedType;

	public GeneralScript(UnitTypeTable a_utt, PathFinding a_pf, BaseBehType a_baseBeh, BarBehType a_barBeh, 
						WorkBehType a_workBeh, LightBehType a_lightBeh, HeavyBehType a_heavyBeh,
						RangedBehType a_rangeBeh) {
		super(a_pf);
		reset(a_utt);
		baseBehType = a_baseBeh;
		baseBeh = new BaseBehavior(utt, baseBehType);
		barBehType = a_barBeh;
		barBeh = new BarrackBehavior(utt, barBehType);
		workBehType = a_workBeh;
		workBeh = new WorkerBehavior(utt, workBehType);
		lightBehType = a_lightBeh;
		lightBeh = new LightBehavior(utt, lightBehType);
		heavyBehType = a_heavyBeh;
		heavyBeh = new HeavyBehavior(utt, heavyBehType);
		rangeBehType = a_rangeBeh;
		rangeBeh = new RangedBehavior(utt, rangeBehType);
	}
	
	public GeneralScript(UnitTypeTable a_utt, PathFinding a_pf, int timebudget, int cyclesbudget, BaseBehType a_baseBeh, 
						BarBehType a_barBeh, WorkBehType a_workBeh, LightBehType a_lightBeh, 
						HeavyBehType a_heavyBeh, RangedBehType a_rangeBeh) {
        super(a_pf, timebudget, cyclesbudget);
        reset(a_utt);
        baseBehType = a_baseBeh;
		baseBeh = new BaseBehavior(utt, baseBehType);
		barBehType = a_barBeh;
		barBeh = new BarrackBehavior(utt, barBehType);
		workBehType = a_workBeh;
		workBeh = new WorkerBehavior(utt, workBehType);
		lightBehType = a_lightBeh;
		lightBeh = new LightBehavior(utt, lightBehType);
		heavyBehType = a_heavyBeh;
		heavyBeh = new HeavyBehavior(utt, heavyBehType);
		rangeBehType = a_rangeBeh;
		rangeBeh = new RangedBehavior(utt, rangeBehType);
    }
	
	public void reset(UnitTypeTable a_utt)  
    {
        utt = a_utt;
        workerType = utt.getUnitType("Worker");
        baseType = utt.getUnitType("Base");
        barracksType = utt.getUnitType("Barracks");
        lightType = utt.getUnitType("Light");
        heavyType = utt.getUnitType("Heavy");
        rangedType = utt.getUnitType("Ranged");
    }   
	
	@Override
	public PlayerAction getAction(int player, GameState gs) throws Exception {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		Player p = gs.getPlayer(player);
		workBeh.resetAtributes();
		
		for (Unit u : pgs.getUnits()) {
			if (u.getPlayer() == player && gs.getActionAssignment(u) == null) {
				if (u.getType() == baseType)
					baseBeh.behavior(this, u, p, pgs);
				else if (u.getType() == workerType)
					workBeh.behavior(this, u, p, pgs);
				else if (u.getType() == barracksType)
					barBeh.behavior(this, u, p, pgs);
				else if (u.getType() == lightType)
					lightBeh.behavior(this, u, p, pgs);
				else if (u.getType() == heavyType)
					heavyBeh.behavior(this, u, p, pgs);
				else if (u.getType() == rangedType)
					rangeBeh.behavior(this, u, p, pgs);
			}
		}

		// This method simply takes all the unit actions executed so far, and packages
		// them into a PlayerAction
		return translateActions(player, gs);
	}

	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// new AStarPathFinding esta mal pero no se puede arreglar si hacemos hilos
	@Override
	public GeneralScript clone() {
		return new GeneralScript(utt, new AStarPathFinding(), getTimeBudget(), getIterationsBudget(), baseBehType, 
									barBehType,	workBehType, lightBehType, heavyBehType, rangeBehType);
	}
	
	public List<String> getBehaviorTypes() {
		List<String> param = new ArrayList<String>();
		param.add(baseBehType.toString());
		param.add(barBehType.toString());
		param.add(workBehType.toString());
		param.add(lightBehType.toString());
		param.add(heavyBehType.toString());
		param.add(rangeBehType.toString());
		return param;
	}
	
	public UnitTypeTable getUtt() {
		return utt;
	}

	public String toString() {
		List<String> param = this.getBehaviorTypes();
		String res = "";
		for (String s : param) {
			res = res + s + ", ";
		}
		return res;
	}
	
	@Override
	public List<ParameterSpecification> getParameters() {
		// Lista de parametros que de momento no se sabe para que vale.
		return null;
	}

}
