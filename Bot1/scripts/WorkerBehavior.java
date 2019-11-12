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

enum WorkBehType{HARVESTER, AGGRESSIVE};

public class WorkerBehavior extends UnitBehavior{
	
	private WorkBehType workBehType;
	
	private int buildingBase;
	private int buildingBarracks;
	
	private List<Integer> reservedPositions;
	private int resourcesUsed;
	
	public WorkerBehavior(UnitTypeTable a_utt, WorkBehType a_workBehType) {
		super(a_utt);
		workBehType = a_workBehType;
		resetAtributes();
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
	
	public void aggressiveWorker(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nbases = 0;
		int resourcesUsed = 0;

        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == baseType
                    && u2.getPlayer() == p.getID()) {
                nbases++;
            }
        }

        if (nbases == 0 && buildingBase == 0 && 
        	p.getResources() >= baseType.cost + resourcesUsed) {
            // build a base:
            gs.buildIfNotAlreadyBuilding(u,baseType,u.getX(),u.getY(),reservedPositions,p,pgs);
            resourcesUsed += baseType.cost;
        } else if (nbases == 0 && buildingBarracks == 0) {
            // harvest resources for building a base:
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
        } else {
            Unit closestEnemy = null;
            int closestDistance = 0;
            for (Unit u2 : pgs.getUnits()) {
                if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                    if (closestEnemy == null || d < closestDistance) {
                        closestEnemy = u2;
                        closestDistance = d;
                    }
                }
            }
            if (closestEnemy != null) {
                gs.attack(u, closestEnemy);
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

	@Override
	public void behavior(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		switch (workBehType) {
		case AGGRESSIVE:
			aggressiveWorker(gs, u, p, pgs);
			break;
		case HARVESTER:
			harvesterWorker(gs, u, p, pgs);
			break;
		}
		
	}
	
}
