package scripts;

import ai.abstraction.AbstractAction;
import ai.abstraction.Train;
import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

enum BaseBehType{ONEWORKER, TWOWORKER, THREEWORKER, RUSHWORKER};

public class BaseBehavior extends UnitBehavior{
	
	private BaseBehType baseBehType;	
	
	public BaseBehavior(UnitTypeTable a_utt, BaseBehType a_baseBehType) {
		super(a_utt);
		baseBehType = a_baseBehType;
	}
	
	public void oneWorker(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == workerType
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        if (nworkers < 1 && p.getResources() >= workerType.cost) {
            gs.train(u, workerType);
        }
	}
	
	public void twoWorker(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == workerType
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        if (nworkers < 2 && p.getResources() >= workerType.cost) {
            gs.train(u, workerType);
        }
	}
	
	public void threeWorker(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == workerType
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        if (nworkers < 3 && p.getResources() >= workerType.cost) {
            gs.train(u, workerType);
        }
	}
	
	public AbstractAction rushWorker(Unit u, Player p, PhysicalGameState pgs) {
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
