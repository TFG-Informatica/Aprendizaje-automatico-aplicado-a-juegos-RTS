package scripts;

import java.util.List;

import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.ParameterSpecification;
import rts.GameState;
import rts.PlayerAction;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

public class GeneralScript extends AbstractionLayerAI {
	
	private BaseBehType baseBehType;
	private BarBehType barBehType;
	private WorkBehType workBehType;
	private LightBehType lightBehType;
	private HeavyBehType heavyBehType;
	private RangeBehType rangeBehType;
	
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
						RangeBehType a_rangeBeh) {
		super(a_pf);
		reset(a_utt);
		baseBehType = a_baseBeh;
		baseBeh = new BaseBehavior(utt, baseBehType);
		barBehType = a_barBeh;
		workBehType = a_workBeh;
		lightBehType = a_lightBeh;
		heavyBehType = a_heavyBeh;
		rangeBehType = a_rangeBeh;
	}
	
	public GeneralScript(UnitTypeTable a_utt, PathFinding a_pf, int timebudget, int cyclesbudget, BaseBehType a_baseBeh, 
						BarBehType a_barBeh, WorkBehType a_workBeh, LightBehType a_lightBeh, 
						HeavyBehType a_heavyBeh, RangeBehType a_rangeBeh) {
        super(a_pf, timebudget, cyclesbudget);
        reset(a_utt);
        baseBeh = a_baseBeh;
		barBeh = a_barBeh;
		workBeh = a_workBeh;
		lightBeh = a_lightBeh;
		heavyBeh = a_heavyBeh;
		rangeBeh = a_rangeBeh;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AI clone() {
		return new GeneralScript(utt, pf, getTimeBudget(), getIterationsBudget(), baseBeh, 
									barBeh,	workBeh, lightBeh, heavyBeh, rangeBeh);
	}

	@Override
	public List<ParameterSpecification> getParameters() {
		// Lista de parametros que de momento no se sabe para que vale.
		return null;
	}

}
