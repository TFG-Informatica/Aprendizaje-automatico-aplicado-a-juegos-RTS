package scripts;

import java.util.LinkedList;
import java.util.List;

import ai.abstraction.AbstractAction;
import ai.abstraction.Harvest;
import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

enum WorkBehType{HARVESTER, };

public class WorkerBehavior {
	
	private WorkBehType workBehType;
	
	private UnitTypeTable utt;
	private UnitType workerType;
	private UnitType baseType;
	private UnitType barracksType;
	private UnitType lightType;
	private UnitType heavyType;
	private UnitType rangedType;
	
	private int buildingBase;
	private int buildingBarracks;
	
	private List<Integer> reservedPositions;
	private int resourcesUsed;
	
	
	public WorkerBehavior(UnitTypeTable a_utt, WorkBehType a_workBehType) {
		reset(utt);
		workBehType = a_workBehType;
		resetAtributes();
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
	
	public void harvesterWorker(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nbases = 0;
        int nbarracks = 0;
        int resourcesUsed = 0;

        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == baseType
                    && u2.getPlayer() == p.getID()) {
                nbases++;
            }
            if (u2.getType() == barracksType
                    && u2.getPlayer() == p.getID()) {
                nbarracks++;
            }
        }

        if (nbases == 0 && buildingBase == 0 && 
        	p.getResources() >= baseType.cost + resourcesUsed) {
            // build a base:
            gs.buildIfNotAlreadyBuilding(u,baseType,u.getX(),u.getY(),reservedPositions,p,pgs);
            resourcesUsed += baseType.cost;
        } else if (nbarracks == 0 && buildingBarracks == 0 &&
        	p.getResources() >= barracksType.cost + resourcesUsed) {
            // build a barracks:
            gs.buildIfNotAlreadyBuilding(u,barracksType,u.getX(),u.getY(),reservedPositions,p,pgs);
            resourcesUsed += barracksType.cost;
        } else {
            Unit closestBase = null;
            Unit closestResource = null;
            int closestDistance = 0;
            for (Unit u2 : pgs.getUnits()) {
                if (u2.getType().isResource) {
                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                    if (closestResource == null || d < closestDistance) {
                        closestResource = u2;
                        closestDistance = d;
                    }
                }
            }
            closestDistance = 0;
            for (Unit u2 : pgs.getUnits()) {
                if (u2.getType().isStockpile && u2.getPlayer()==p.getID()) {
                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                    if (closestBase == null || d < closestDistance) {
                        closestBase = u2;
                        closestDistance = d;
                    }
                }
            }
            if (closestResource != null && closestBase != null) {
                AbstractAction aa = gs.getAbstractAction(u);
                if (aa instanceof Harvest) {
                    Harvest h_aa = (Harvest)aa;
                    if (h_aa.getTarget() != closestResource || h_aa.getBase()!=closestBase) 
                    	gs.harvest(u, closestResource, closestBase);
                } else {
                    gs.harvest(u, closestResource, closestBase);
                }
            }
        }
		
	}
	
	public void aggresiveWorker(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nbases = 0;
        int nbarracks = 0;
        int resourcesUsed = 0;

        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == baseType
                    && u2.getPlayer() == p.getID()) {
                nbases++;
            }
            if (u2.getType() == barracksType
                    && u2.getPlayer() == p.getID()) {
                nbarracks++;
            }
        }

        if (nbases == 0 && buildingBase == 0 && 
        	p.getResources() >= baseType.cost + resourcesUsed) {
            // build a base:
            gs.buildIfNotAlreadyBuilding(u,baseType,u.getX(),u.getY(),reservedPositions,p,pgs);
            resourcesUsed += baseType.cost;
        } else if (nbarracks == 0 && buildingBarracks == 0 &&
        	p.getResources() >= barracksType.cost + resourcesUsed) {
            // build a barracks:
            gs.buildIfNotAlreadyBuilding(u,barracksType,u.getX(),u.getY(),reservedPositions,p,pgs);
            resourcesUsed += barracksType.cost;
        } else {
            Unit closestBase = null;
            Unit closestResource = null;
            int closestDistance = 0;
            for (Unit u2 : pgs.getUnits()) {
                if (u2.getType().isResource) {
                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                    if (closestResource == null || d < closestDistance) {
                        closestResource = u2;
                        closestDistance = d;
                    }
                }
            }
            closestDistance = 0;
            for (Unit u2 : pgs.getUnits()) {
                if (u2.getType().isStockpile && u2.getPlayer()==p.getID()) {
                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                    if (closestBase == null || d < closestDistance) {
                        closestBase = u2;
                        closestDistance = d;
                    }
                }
            }
            if (closestResource != null && closestBase != null) {
                AbstractAction aa = gs.getAbstractAction(u);
                if (aa instanceof Harvest) {
                    Harvest h_aa = (Harvest)aa;
                    if (h_aa.getTarget() != closestResource || h_aa.getBase()!=closestBase) 
                    	gs.harvest(u, closestResource, closestBase);
                } else {
                    gs.harvest(u, closestResource, closestBase);
                }
            }
        }
		
	}
	
	public WorkBehType getType() {
		return workBehType;
	}
	
	public void setType(WorkBehType a_workBehType) {
		workBehType = a_workBehType;
	}
	
	public void resetAtributes() {
		buildingBase = 0;
		buildingBarracks = 0;
		reservedPositions = new LinkedList<Integer>();
		resourcesUsed = 0;
	}
	
}
