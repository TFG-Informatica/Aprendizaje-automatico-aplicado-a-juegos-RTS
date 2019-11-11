package scripts;

import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

enum BarBehType{LIGHT, HEAVY, RANGED, LESS};

public class BarrackBehavior {
	
	private BarBehType barBehType;
	
	private UnitTypeTable utt;
	private UnitType workerType;
	private UnitType baseType;
	private UnitType barracksType;
	private UnitType lightType;
	private UnitType heavyType;
	private UnitType rangedType;
	
	
	public BarrackBehavior(UnitTypeTable a_utt, BarBehType a_barBehType) {
		reset(utt);
		barBehType = a_barBehType;
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
}
