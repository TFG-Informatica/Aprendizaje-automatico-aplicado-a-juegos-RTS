package bot.scripts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ai.abstraction.AbstractAction;
import ai.abstraction.Harvest;
import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;

public class WorkerBehavior extends UnitBehavior {

	public enum WorkBehType{HARVESTER, AGGRESSIVE, ONEHARVAGGR, TWOHARVAGGR, THREEHARVAGGR, ONEHARVNOBAR, TWOHARVNOBAR, THREEHARVNOBAR};
	private WorkBehType workBehType;
	
	private ArrayList<Pair<Integer,Integer>> obst;
	private int buildingBase;
	private int buildingBarracks;
	private int previousBases;
	private int previousBarracks;
	
	private List<Integer> reservedPositions;
	
	public WorkerBehavior(UnitTypeTable a_utt, WorkBehType a_workBehType) {
		super(a_utt);
		workBehType = a_workBehType;
		resetAtributes();
	}
	
	public void harvesterWorker(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		int nbases = 0;
        int nbarracks = 0;
        obst = new ArrayList<Pair<Integer, Integer>>();

        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType().equals(baseType)
                    && u2.getPlayer() == p.getID()) {
                nbases++;
                obst.add(new Pair<Integer,Integer>(u2.getX(),u2.getY()));
            }
            if (u2.getType().equals(barracksType)
                    && u2.getPlayer() == p.getID()) {
                nbarracks++;
                obst.add(new Pair<Integer,Integer>(u2.getX(),u2.getY()));
            }
            if (u2.getType().isResource) {
            	obst.add(new Pair<Integer,Integer>(u2.getX(),u2.getY()));
            }
        }
        
        if (nbases > previousBases) buildingBase -= (nbases - previousBases);
        if (nbarracks > previousBarracks) buildingBarracks -= (nbarracks - previousBarracks);

        if (nbases == 0 && buildingBase == 0 && 
        	p.getResources() >= baseType.cost + gs.getResources()) {
            // build a base:
            gs.buildIfNotAlreadyBuilding(u,baseType,u.getX(),u.getY(),reservedPositions,p,pgs);
            ++buildingBase;
            gs.useResources(baseType.cost);
        } else if (nbarracks == 0 && buildingBarracks == 0 &&
        	p.getResources() >= barracksType.cost + gs.getResources()) {
            // look for a good spot:
        	Pair<Integer, Integer> pos = getGoodCoords(new Pair<Integer,Integer>(u.getX(), u.getY()), pgs.getWidth(), pgs.getHeight());
        	// build a barracks:
            gs.buildIfNotAlreadyBuilding(u,barracksType,pos.m_a+1,pos.m_b+1,reservedPositions,p,pgs);
            ++buildingBarracks;
            gs.useResources(barracksType.cost);
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
            } else {
            	Unit closestEnemy = null;
                closestDistance = 0;
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
		previousBases = nbases;
		previousBarracks = nbarracks;
	}

	public void mixedWorker(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs, int harv) {
		int nbases = 0;
		int nbarracks = 0;
		int harvesting = 0;
        obst = new ArrayList<Pair<Integer, Integer>>();


        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType().equals(baseType)
                    && u2.getPlayer() == p.getID()) {
                ++nbases;
                obst.add(new Pair<Integer,Integer>(u2.getX(),u2.getY()));
            }
            if (u2.getType().equals(barracksType)
                    && u2.getPlayer() == p.getID()) {
                nbarracks++;
                obst.add(new Pair<Integer,Integer>(u2.getX(),u2.getY()));
            }
            if (u2.getType().equals(workerType) && !u2.equals(u)
            		&& u2.getPlayer() == p.getID() && gs.getAbstractAction(u2) instanceof Harvest) {
            	++harvesting;
            }
            if (u2.getType().isResource) {
            	obst.add(new Pair<Integer,Integer>(u2.getX(),u2.getY()));
            }
        }
        
        if (nbases > previousBases) buildingBase -= (nbases - previousBases);
        if (nbarracks > previousBarracks) buildingBarracks -= (nbarracks - previousBarracks);
        if (nbases == 0 && buildingBase == 0 && 
        		p.getResources() >= baseType.cost + gs.getResources()) {
            // build a base:
            gs.buildIfNotAlreadyBuilding(u,baseType,u.getX(),u.getY(),reservedPositions,p,pgs);
            ++buildingBase;
            gs.useResources(baseType.cost);
        } else if (nbarracks == 0 && buildingBarracks == 0 &&
        	p.getResources() >= barracksType.cost + gs.getResources()) {
        	// look for a good spot:
        	Pair<Integer, Integer> pos = getGoodCoords(new Pair<Integer,Integer>(u.getX(), u.getY()), pgs.getWidth(), pgs.getHeight());
        	// build a barracks:
            gs.buildIfNotAlreadyBuilding(u,barracksType,pos.m_a+1,pos.m_b+1,reservedPositions,p,pgs);
            ++buildingBarracks;
            gs.useResources(barracksType.cost);
        } else if ((nbases == 0 && buildingBase == 0) || (nbarracks == 0 && buildingBarracks == 0) || harvesting < harv) {
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
            } else {
            	Unit closestEnemy = null;
                closestDistance = 0;
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

	public void aggressiveWorker(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs, int harv) {
		int nbases = 0, harvesting = 0;

        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType().equals(baseType)
                    && u2.getPlayer() == p.getID()) {
                ++nbases;
            }
            if (u2.getType().equals(workerType) && !u2.equals(u)
            		&& u2.getPlayer() == p.getID() && gs.getAbstractAction(u2) instanceof Harvest) {
            	++harvesting;
            }
        }
        
        if (nbases > previousBases) buildingBase -= (nbases - previousBases);

        if (nbases == 0 && buildingBase == 0 && 
        		p.getResources() >= baseType.cost + gs.getResources()) {
            // build a base:
            gs.buildIfNotAlreadyBuilding(u,baseType,u.getX(),u.getY(),reservedPositions,p,pgs);
            ++buildingBase;
            gs.useResources(baseType.cost);
        } else if ((nbases == 0 && buildingBase == 0) || harvesting < harv) {
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
            } else {
            	Unit closestEnemy = null;
                closestDistance = 0;
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
	
	private Pair<Integer, Integer> getGoodCoords(Pair<Integer, Integer> me, int dimX, int dimY) {
		int[] movX = {1, 1, 0, -1, -1, -1, 0, 1};
		int[] movY = {0, 1, 1, 1, 0, -1, -1, -1};
		
		for (int i = 2; i > 0; --i) {
			for (int j = 0; j < movX.length; ++j) {
				Pair<Integer,Integer> c = new Pair<Integer,Integer>(me.m_a + i*movX[j], me.m_b + i*movY[j]);
				if (c.m_a >= 0 && c.m_a < dimX && c.m_b >= 0 && c.m_b < dimY) {
					boolean good = true;
					for (Pair<Integer,Integer> a : obst) {
						if (Math.abs(a.m_a - c.m_a) <= 1 && Math.abs(a.m_b - c.m_b) <= 1) {
							good = false;
							break;
						}
					}
					if (good)
						return c;
				}
			}
		}
		return me;
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
	}

	@Override
	public void behavior(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		switch (workBehType) {
		case AGGRESSIVE:
			aggressiveWorker(gs, u, p, pgs, 0);
			break;
		case ONEHARVNOBAR:
			aggressiveWorker(gs, u, p, pgs, 1);
			break;
		case TWOHARVNOBAR:
			aggressiveWorker(gs, u, p, pgs, 2);
			break;
		case THREEHARVNOBAR:
			aggressiveWorker(gs, u, p, pgs, 3);
			break;
		case HARVESTER:
			harvesterWorker(gs, u, p, pgs);
			break;
		case ONEHARVAGGR:
			mixedWorker(gs, u, p, pgs, 1);
			break;
		case TWOHARVAGGR:
			mixedWorker(gs, u, p, pgs, 2);
			break;
		case THREEHARVAGGR:
			mixedWorker(gs, u, p, pgs, 3);
			break;
		}
		
	}
	
}
