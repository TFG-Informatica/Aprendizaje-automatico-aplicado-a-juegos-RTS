package scripts;

import ai.abstraction.AbstractAction;
import ai.abstraction.Train;
import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitTypeTable;

public class BaseBehavior extends UnitBehavior{
	
	public enum BaseBehType{ONEWORKER, TWOWORKER, THREEWORKER, RUSHWORKER};
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

	@Override
	public void behavior(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		switch (baseBehType) {
		case ONEWORKER:
			this.oneWorker(gs, u, p, pgs);
			break;
		case TWOWORKER:
			this.twoWorker(gs, u, p, pgs);
			break;
		case THREEWORKER:
			this.threeWorker(gs, u, p, pgs);
			break;
		case RUSHWORKER:
			this.rushWorker(u, p, pgs);
			break;
		}
	}
	
}
