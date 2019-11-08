package scripts;

import ai.abstraction.AbstractAction;
import ai.abstraction.Train;
import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

enum BaseBehType{ONEWORKER, TWOWORKER, THREEWORKER, RUSHWORKER};

public class BaseBehavior {
	
	private BaseBehType baseBehType;
	
	private UnitTypeTable utt;
	private UnitType workerType;
	private UnitType baseType;
	private UnitType barracksType;
	private UnitType lightType;
	private UnitType heavyType;
	private UnitType rangedType;
	
	
	public BaseBehavior(UnitTypeTable a_utt, BaseBehType a_baseBehType) {
		reset(utt);
		baseBehType = a_baseBehType;
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
	
	public AbstractAction oneWorkerBeh(Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == workerType
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        if (nworkers < 1 && p.getResources() >= workerType.cost) {
            return new Train(u, workerType);
        }
        return null;
	}
	
	public AbstractAction twoWorkerBeh(Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == workerType
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        if (nworkers < 2 && p.getResources() >= workerType.cost) {
            return new Train(u, workerType);
        }
        return null;
	}
	
	public AbstractAction threeWorkerBeh(Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == workerType
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        if (nworkers < 3 && p.getResources() >= workerType.cost) {
            return new Train(u, workerType);
        }
        return null;
	}
	
	public AbstractAction rushWorkerBeh(Unit u, Player p, PhysicalGameState pgs) {
        if (p.getResources() >= workerType.cost) {
            return new Train(u, workerType);
        }
        return null;
	}
	
	public BaseBehType getType() {
		return baseBehType;
	}
	
	public void setType(BaseBehType a_baseBehType) {
		baseBehType = a_baseBehType;
	}
	
}
