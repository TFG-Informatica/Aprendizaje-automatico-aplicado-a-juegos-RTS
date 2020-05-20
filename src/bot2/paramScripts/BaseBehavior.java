package bot2.paramScripts;

import ai.abstraction.AbstractAction;
import ai.abstraction.Train;
import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitTypeTable;

public class BaseBehavior extends UnitBehavior {

	public enum BaseBehType{ONEWORKER, TWOWORKER, THREEWORKER, RUSHWORKER};
	private BaseBehType baseBehType;	
	
	public BaseBehavior(UnitTypeTable a_utt, BaseBehType a_baseBehType) {
		super(a_utt);
		baseBehType = a_baseBehType;
	}
	
	public void fixWorker(ParamGeneralScript gs, Unit u, Player p, PhysicalGameState pgs, int numWorker) {
		int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType().equals(workerType)
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        if (nworkers < numWorker && p.getResources() >= gs.getResources() + workerType.cost) {
            gs.train(u, workerType);
            gs.useResources(workerType.cost);
        } else {
        	gs.idle(u);
        }
	}
	
	public void rushWorker(ParamGeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
        if (p.getResources() >= gs.getResources() + workerType.cost) {
            gs.train(u, workerType);
            gs.useResources(workerType.cost);
        } else {
        	gs.idle(u);
        }
	}
	
	public BaseBehType getType() {
		return baseBehType;
	}
	
	public void setType(BaseBehType a_baseBehType) {
		baseBehType = a_baseBehType;
	}

	@Override
	public void behavior(ParamGeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		switch (baseBehType) {
		case ONEWORKER:
			this.fixWorker(gs, u, p, pgs, 1);
			break;
		case TWOWORKER:
			this.fixWorker(gs, u, p, pgs, 2);
			break;
		case THREEWORKER:
			this.fixWorker(gs, u, p, pgs, 3);
			break;
		case RUSHWORKER:
			this.rushWorker(gs, u, p, pgs);
			break;
		}
	}
	
}
