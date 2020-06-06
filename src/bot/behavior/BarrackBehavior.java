package bot.behavior;

import bot.ia.GeneralScript;
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
            if (u2.getType().equals(workerType)
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        int reserved = (nworkers > 0) ? 0 : workerType.cost;
        if (p.getResources() >= lightType.cost + gs.getResources() + reserved) {
            gs.train(u, lightType);
            gs.useResources(lightType.cost);
        } else {
        	gs.idle(u);
        }
	}
	
	public void trainHeavy(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType().equals(workerType)
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        int reserved = (nworkers > 0) ? 0 : workerType.cost;
        if (p.getResources() >= heavyType.cost + gs.getResources() + reserved) {
            gs.train(u, heavyType);
            gs.useResources(heavyType.cost);
        } else {
        	gs.idle(u);
        }
	}
	
	public void trainRanged(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType().equals(workerType)
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        int reserved = (nworkers > 0) ? 0 : workerType.cost;
        if (p.getResources() >= rangedType.cost + gs.getResources() + reserved) {
            gs.train(u, rangedType);
            gs.useResources(rangedType.cost);
        } else {
        	gs.idle(u);
        }
	}
	
	public void trainLess(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nworkers = 0, nlights = 0, nheavys = 0, nrangeds = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType().equals(workerType)
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            } else if (u2.getType().equals(lightType)
                    && u2.getPlayer() == p.getID()) {
                nlights++;
            } else if (u2.getType().equals(heavyType)
                    && u2.getPlayer() == p.getID()) {
                nheavys++;
            } else if (u2.getType().equals(rangedType)
                    && u2.getPlayer() == p.getID()) {
                nrangeds++;
            }
        }
        int reserved = (nworkers > 0) ? 0 : workerType.cost;
        if (nlights <= nheavys && nlights <= nrangeds && p.getResources() >= lightType.cost + gs.getResources() + reserved) {
        	gs.train(u, lightType);
        	gs.useResources(lightType.cost);
        } else if (nheavys <= nlights && nheavys <= nrangeds && p.getResources() >= heavyType.cost + gs.getResources() + reserved) {
        	gs.train(u, heavyType);
        	gs.useResources(heavyType.cost);
        } else if (p.getResources() >= rangedType.cost + gs.getResources() + reserved) {
        	gs.train(u, rangedType);
        	gs.useResources(rangedType.cost);
        } else {
        	gs.idle(u);
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
