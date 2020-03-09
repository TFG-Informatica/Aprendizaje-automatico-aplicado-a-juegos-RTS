package bot.scripts;

import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

public abstract class UnitBehavior {

	protected UnitTypeTable utt;
	protected UnitType workerType;
	protected UnitType baseType;
	protected UnitType barracksType;
	protected UnitType lightType;
	protected UnitType heavyType;
	protected UnitType rangedType;
	
	public UnitBehavior(UnitTypeTable a_utt) {
		reset(a_utt);
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
	
	public abstract void behavior(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs);
	
}
