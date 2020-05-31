package bot.scripts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;

public class RangedBehavior extends UnitBehavior {

	public enum RangedBehType{LESSHP,LESSPERCHP,KITE,CLOSEST,WAIT,CLOSBUIL};
	private RangedBehType rangedBehType;

	public RangedBehavior(UnitTypeTable a_utt, RangedBehType a_rangedBehType) {
		super(a_utt);
		rangedBehType = a_rangedBehType;
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
	
	public void kiteAttack(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
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
        	kite(gs, u, closestEnemy, pgs);
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
	
	public void kite(GeneralScript gs, Unit u, Unit u2, PhysicalGameState pgs) {
		if (Math.abs(u2.getX() - u.getX())
				+ Math.abs(u2.getY() - u.getY()) < rangedType.attackRange) {
			List<Pair<Integer, Integer>> pos = new ArrayList<Pair<Integer, Integer>>();
			int difX = u.getX() - u2.getX();
			int difY = u.getY() - u2.getY();
			if (difX == 0) {
				pos.add(new Pair<Integer, Integer>(1, 0));
				pos.add(new Pair<Integer, Integer>(-1, 0));
			} else if (difX > 0) {
				pos.add(new Pair<Integer, Integer>(1, 0));
			} else {
				pos.add(new Pair<Integer, Integer>(-1, 0));
			}
			if (difY == 0) {
				pos.add(new Pair<Integer, Integer>(0, 1));
				pos.add(new Pair<Integer, Integer>(0, -1));
			} else if (difY > 0) {
				pos.add(new Pair<Integer, Integer>(0, 1));
			} else {
				pos.add(new Pair<Integer, Integer>(0, -1));
			}

			int r = new Random().nextInt(pos.size());
			Pair<Integer, Integer> newPos = null;
			int N = pos.size() + r;
			boolean[][] free = pgs.getAllFree();
			while (r < N && newPos == null) {
				Pair<Integer, Integer> nPos = new Pair<Integer, Integer>(u.getX() + pos.get(r % pos.size()).m_a,
						u.getY() + pos.get(r % pos.size()).m_b);
				if (nPos.m_a >= 0 && nPos.m_b >= 0 && nPos.m_a < pgs.getWidth() && nPos.m_b < pgs.getHeight()
						&& free[nPos.m_a][nPos.m_b])
					newPos = nPos;
				++r;
			}
			if (newPos != null)
				gs.move(u, newPos.m_a, newPos.m_b);
			else
				gs.attack(u, u2);
		} else
			gs.attack(u, u2);
	}
	
	public RangedBehType getType() {
		return rangedBehType;
	}
	
	public void setType(RangedBehType a_rangedBehType) {
		rangedBehType = a_rangedBehType;
	}

	@Override
	public void behavior(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		switch (rangedBehType) {
		case KITE:
			kiteAttack(gs, u, p, pgs);
			break;
		case LESSHP:
			lessHPAttack(gs, u, p, pgs);
			break;
		case LESSPERCHP:
			lessPercHPAttack(gs, u, p, pgs);
			break;
		case CLOSEST:
			closestAttack(gs, u, p, pgs);
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
