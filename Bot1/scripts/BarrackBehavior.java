package scripts;

import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitTypeTable;

public class BarrackBehavior extends UnitBehavior{
	
	public enum BarBehType{LIGHT, HEAVY, RANGED, LESS};
	private BarBehType barBehType;	
	
	public BarrackBehavior(UnitTypeTable a_utt, BarBehType a_barBehType) {
		super(a_utt);
		barBehType = a_barBehType;
	}
	
	public void trainLight(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == workerType
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        if (nworkers > 0 && p.getResources() >= lightType.cost) {
            gs.train(u, lightType);
        }
	}
	
	public void trainHeavy(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == workerType
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        if (nworkers > 0 && p.getResources() >= heavyType.cost) {
            gs.train(u, heavyType);
        }
	}
	
	public void trainRanged(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == workerType
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        if (nworkers > 0 && p.getResources() >= rangedType.cost) {
            gs.train(u, rangedType);
        }
	}
	
	public void trainLess(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0, nlights = 0, nheavys = 0, nrangeds = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == workerType
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            } else if (u2.getType() == lightType
                    && u2.getPlayer() == p.getID()) {
                nlights++;
            } else if (u2.getType() == heavyType
                    && u2.getPlayer() == p.getID()) {
                nheavys++;
            } else if (u2.getType() == rangedType
                    && u2.getPlayer() == p.getID()) {
                nrangeds++;
            }
        }
        if (nworkers > 0) {
			if (nlights <= nheavys && nlights <= nrangeds && p.getResources() >= lightType.cost)
				gs.train(u, lightType);
			else if (nheavys <= nlights && nheavys <= nrangeds && p.getResources() >= heavyType.cost)
				gs.train(u, heavyType);
			else if (p.getResources() >= rangedType.cost)
				gs.train(u, rangedType);
		}
	}
	
	public BarBehType getType() {
		return barBehType;
	}
	
	public void setType(BarBehType a_barBehType) {
		barBehType = a_barBehType;
	}

	@Override
	public void behavior(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		switch(barBehType) {
		case HEAVY:
			trainHeavy(gs, u, p, pgs);
			break;
		case LESS:
			trainLess(gs, u, p, pgs);
			break;
		case LIGHT:
			trainLight(gs, u, p, pgs);
			break;
		case RANGED:
			trainRanged(gs, u, p, pgs);
			break;
		default:
			break;
		
		}
		
	}
}
