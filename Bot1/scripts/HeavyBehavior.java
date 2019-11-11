package scripts;

import rts.PhysicalGameState;
import rts.Player;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

enum HeavyBehType{LESSHP,LESSPERCHP,CLOSEST};

public class HeavyBehavior {
	
	private HeavyBehType heavyBehType;
	
	private UnitTypeTable utt;
	private UnitType workerType;
	private UnitType baseType;
	private UnitType barracksType;
	private UnitType lightType;
	private UnitType heavyType;
	private UnitType rangedType;
	
	
	public HeavyBehavior(UnitTypeTable a_utt, HeavyBehType a_heavyBehType) {
		reset(utt);
		heavyBehType = a_heavyBehType;
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
	
}
