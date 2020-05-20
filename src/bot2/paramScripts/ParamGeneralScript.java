package bot2.paramScripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.Move;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.ParameterSpecification;
import bot2.paramScripts.BarrackBehavior.BarBehType;
import bot2.paramScripts.BaseBehavior.BaseBehType;
import bot2.paramScripts.HeavyBehavior.HeavyBehType;
import bot2.paramScripts.LightBehavior.LightBehType;
import bot2.paramScripts.RangedBehavior.RangedBehType;
import bot2.paramScripts.WorkerBehavior.WorkBehType;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;
import util.Pair;

public class ParamGeneralScript extends AbstractionLayerAI {
	
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
	private int waitx;
	private int waity;
	
	private UnitTypeTable utt;
	private UnitType workerType;
	private UnitType baseType;
	private UnitType barracksType;
	private UnitType lightType;
	private UnitType heavyType;
	private UnitType rangedType;
	
	private int resourcesUsed;
	
	private WaitPoint waitPoint;

	public ParamGeneralScript(UnitTypeTable a_utt, BaseBehType a_baseBeh, BarBehType a_barBeh, 
						WorkBehType a_workBeh, LightBehType a_lightBeh, HeavyBehType a_heavyBeh,
						RangedBehType a_rangeBeh, int waitx, int waity) {
		super(new AStarPathFinding());
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
		this.waitx = waitx;
		this.waity = waity;
		waitPoint = new WaitPoint(waitx, waity);
	}
	
	public ParamGeneralScript(UnitTypeTable a_utt, int timebudget, int cyclesbudget, BaseBehType a_baseBeh, 
						BarBehType a_barBeh, WorkBehType a_workBeh, LightBehType a_lightBeh, 
						HeavyBehType a_heavyBeh, RangedBehType a_rangeBeh, int waitx, int waity) {
        super(new AStarPathFinding(), timebudget, cyclesbudget);
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
		this.waitx = waitx;
		this.waity = waity;
		waitPoint = new WaitPoint(waitx, waity);
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
		resourcesUsed = 0;
		workBeh.resetAtributes();
		
		for (Unit u : pgs.getUnits()) {
			if (u.getPlayer() == player) {
				if (u.getType().equals(baseType))
					baseBeh.behavior(this, u, p, pgs);
				else if (u.getType().equals(workerType))
					workBeh.behavior(this, u, p, pgs);
				else if (u.getType().equals(barracksType))
					barBeh.behavior(this, u, p, pgs);
				else if (u.getType().equals(lightType))
					lightBeh.behavior(this, u, p, pgs);
				else if (u.getType().equals(heavyType))
					heavyBeh.behavior(this, u, p, pgs);
				else if (u.getType().equals(rangedType))
					rangeBeh.behavior(this, u, p, pgs);
				int a = 0;
			}
		}

		// This method simply takes all the unit actions executed so far, and packages
		// them into a PlayerAction
		return translateActions(player, gs);
	}
	
	public void useResources(int used) {
		resourcesUsed += used;
	}
	
	public int getResources() {
		return resourcesUsed;
	}
	
	@Override
	public void reset() {
		waitPoint.reset(waitx, waity);
	}
	
	public void wait(Unit u, Player p, PhysicalGameState pgs) {
		Pair<Integer,Integer> wp;
		if (waitPoint.get(u) == null) {
			wp = waitPoint.fetchAndAdv(pgs, u.getX(), u.getY());
			waitPoint.put(u, wp);
		} else {
			wp = waitPoint.get(u);
		}
		move(u, wp.m_a, wp.m_b);
		for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
            	int dx = u2.getX()-u.getX();
                int dy = u2.getY()-u.getY();
                double d = Math.sqrt(dx*dx+dy*dy);
                if (d <= u.getAttackRange()) {
                    attack(u, u2);
                }
            }
        }
	}

	@Override
	public ParamGeneralScript clone() {
		return new ParamGeneralScript(utt, getTimeBudget(), getIterationsBudget(), baseBehType, 
									barBehType,	workBehType, lightBehType, heavyBehType, rangeBehType, waitx, waity);
	}
	
	public List<String> getBehaviorTypes() {
		List<String> param = new ArrayList<String>();
		param.add(baseBehType.toString());
		param.add(barBehType.toString());
		param.add(workBehType.toString());
		param.add(lightBehType.toString());
		param.add(heavyBehType.toString());
		param.add(rangeBehType.toString());
		param.add(new Integer(waitx).toString());
		param.add(new Integer(waity).toString());
		return param;
	}
	
	public UnitTypeTable getUtt() {
		return utt;
	}

	public String toString() {
		List<String> param = this.getBehaviorTypes();
		String res = param.get(0);
		for (int i = 1; i < param.size(); ++i) {
			res += ", " + param.get(i);
		}
		return res;
	}
	
	@Override
	public List<ParameterSpecification> getParameters() {
		// Lista de parametros que de momento no se sabe para que vale.
		return null;
	}

}
