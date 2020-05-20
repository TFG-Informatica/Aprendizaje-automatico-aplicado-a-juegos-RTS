package bot.scripts;

import java.util.ArrayList;

import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;

public class LightBehavior extends UnitBehavior {

	public enum LightBehType{LESSHP,LESSPERCHP,CLOSEST,WAIT,CLOSBUIL};
	private LightBehType lightBehType;
	
	public LightBehavior(UnitTypeTable a_utt, LightBehType a_lightBehType) {
		super(a_utt);
		lightBehType = a_lightBehType;
	}
	
	public void lessHPAttack(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		Unit lowestHPEnemy = null;
        int lowestHP = 0;
        Unit closest = null;
        int closestDistance = -1;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
            	int dx = u2.getX()-u.getX();
                int dy = u2.getY()-u.getY();
            	int d = Math.abs(dx) + Math.abs(dy);
	            if (closest == null || d < closestDistance) {
	            	closest = u2;
	                closestDistance = d;
            	}
                double r = Math.sqrt(dx*dx+dy*dy);
                if (r <= u.getAttackRange()) {
                    int hp = u2.getHitPoints();
                    if (lowestHPEnemy == null || hp < lowestHP) {
                    	lowestHPEnemy = u2;
                    	lowestHP = hp;
                    }
                }
            }
        }
        if (lowestHPEnemy != null) {
            gs.attack(u, lowestHPEnemy);
        } else {
        	gs.attack(u, closest);
        }
	}
	
	public void lessPercHPAttack(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		Unit lowestHPEnemy = null;
        double lowestHP = 0;
        Unit closest = null;
        int closestDistance = -1;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
            	int dx = u2.getX()-u.getX();
                int dy = u2.getY()-u.getY();
            	int d = Math.abs(dx) + Math.abs(dy);
	            if (closest == null || d < closestDistance) {
	            	closest = u2;
	                closestDistance = d;
            	}
                double r = Math.sqrt(dx*dx+dy*dy);
                if (r <= u.getAttackRange()) {
                    int hp = u2.getHitPoints() / u2.getMaxHitPoints();
                    if (lowestHPEnemy == null || hp < lowestHP) {
                    	lowestHPEnemy = u2;
                    	lowestHP = hp;
                    }
                }
            }
        }
        if (lowestHPEnemy != null) {
            gs.attack(u, lowestHPEnemy);
        } else {
        	gs.attack(u, closest);
        }
	}
	
	public void closestAttack(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
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
	
	public void wait(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		gs.wait(u, p, pgs);
	}
	
	public void closestToBuilding(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		Unit closestEnemy = null;
		int closestDistance = 0;
		
		ArrayList<Pair<Integer,Integer>> buildingPos = new ArrayList<Pair<Integer,Integer>>();
		for (Unit b : pgs.getUnits()) {
			if (b.getPlayer() >= 0 && b.getPlayer() == p.getID() && 
			   (b.getType().equals(barracksType) || b.getType().equals(baseType)))
				buildingPos.add(new Pair<Integer,Integer>(b.getX(), b.getY()));
		}
		
		for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
            	for (Pair<Integer,Integer> b : buildingPos) {
	                int d = Math.abs(u2.getX() - b.m_a) + Math.abs(u2.getY() - b.m_b);
	                if (closestEnemy == null || d < closestDistance) {
	                    closestEnemy = u2;
	                    closestDistance = d;
	                }
            	}
            }
        }
        if (closestEnemy != null) {
            gs.attack(u, closestEnemy);
        }
	}
	
	public LightBehType getType() {
		return lightBehType;
	}
	
	public void setType(LightBehType a_lightBehType) {
		lightBehType = a_lightBehType;
	}

	@Override
	public void behavior(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		switch (lightBehType) {
		case CLOSEST:
			closestAttack(gs, u, p, pgs);
			break;
		case LESSHP:
			lessHPAttack(gs, u, p, pgs);
			break;
		case LESSPERCHP:
			lessPercHPAttack(gs, u, p, pgs);
			break;
		case WAIT:
			wait(gs, u, p, pgs);
			break;
		case CLOSBUIL:
			closestToBuilding(gs, u, p, pgs);
			break;
		}
	}
	
}
