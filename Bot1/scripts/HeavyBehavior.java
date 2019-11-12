package scripts;

import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitTypeTable;

public class HeavyBehavior extends UnitBehavior{
	
	public enum HeavyBehType{LESSHP,LESSPERCHP,CLOSEST};
	private HeavyBehType heavyBehType;
	
	public HeavyBehavior(UnitTypeTable a_utt, HeavyBehType a_heavyBehType) {
		super(a_utt);
		heavyBehType = a_heavyBehType;
	}
	
	public void lessHPAttack(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		Unit lowestHPEnemy = null;
        int lowestHP = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
                int d = u2.getHitPoints();
                if (lowestHPEnemy == null || d < lowestHP) {
                	lowestHPEnemy = u2;
                	lowestHP = d;
                }
            }
        }
        if (lowestHPEnemy != null) {
            gs.attack(u, lowestHPEnemy);
        }
	}
	
	public void lessPercHPAttack(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		Unit lowestHPEnemy = null;
        float lowestHP = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
                float d = u2.getHitPoints() / u2.getMaxHitPoints();
                if (lowestHPEnemy == null || d < lowestHP) {
                	lowestHPEnemy = u2;
                	lowestHP = d;
                }
            }
        }
        if (lowestHPEnemy != null) {
            gs.attack(u, lowestHPEnemy);
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
	
	
	public HeavyBehType getType() {
		return heavyBehType;
	}
	
	public void setType(HeavyBehType a_heavyBehType) {
		heavyBehType = a_heavyBehType;
	}

	@Override
	public void behavior(GeneralScript gs, Unit u, Player p, PhysicalGameState pgs) {
		switch (heavyBehType) {
		case CLOSEST:
			closestAttack(gs, u, p, pgs);
			break;
		case LESSHP:
			lessHPAttack(gs, u, p, pgs);
			break;
		case LESSPERCHP:
			lessPercHPAttack(gs, u, p, pgs);
			break;
		}		
	}
	
}
