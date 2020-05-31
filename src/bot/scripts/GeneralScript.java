package bot.scripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ai.abstraction.AbstractAction;
import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.Build;
import ai.abstraction.Move;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.ParameterSpecification;
import bot.scripts.BarrackBehavior.BarBehType;
import bot.scripts.BaseBehavior.BaseBehType;
import bot.scripts.HeavyBehavior.HeavyBehType;
import bot.scripts.LightBehavior.LightBehType;
import bot.scripts.RangedBehavior.RangedBehType;
import bot.scripts.WorkerBehavior.WorkBehType;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;
import util.Pair;

public class GeneralScript extends AbstractionLayerAI {
	
	private BaseBehType baseBehType;
	private BarBehType barBehType;
	private WorkBehType workBehType;
	private LightBehType lightBehType;
	private HeavyBehType heavyBehType;
	private RangedBehType rangeBehType;
	
	private BaseBehavior baseBeh;
	private BarrackBehavior barBeh;
	private WorkerBehavior workBeh;
	private LightBehavior lightBeh;
	private HeavyBehavior heavyBeh;
	private RangedBehavior rangeBeh;
	
	private UnitTypeTable utt;
	private UnitType workerType;
	private UnitType baseType;
	private UnitType barracksType;
	private UnitType lightType;
	private UnitType heavyType;
	private UnitType rangedType;
	
	private int resourcesUsed;
	
	private WaitPoint waitPoint;
	
	private class WaitPoint {
		
		private int waitX;
		private int waitY;
		private int dir;
		private int despl;
		private int max;
		private boolean aumen;
		private HashMap<Unit, Pair<Integer,Integer>> waitPositions;
		
		public WaitPoint(){
			waitX = -1;
			waitY = -1;
			dir = 0;
			despl = 0;
			max = 1;
			aumen = false;
			waitPositions = new HashMap<Unit, Pair<Integer,Integer>>();
		}
		
		public Pair<Integer,Integer> get(Unit u) {
			if (waitPositions.containsKey(u))
				return waitPositions.get(u);
			else
				return null;
		}
		
		public void put(Unit u, Pair<Integer,Integer> pos) {
			waitPositions.put(u, pos);
		}
		
		public Pair<Integer,Integer> fetchAndAdv(PhysicalGameState pgs, int x, int y) {
			boolean[][] free = pgs.getAllFree();
			
			if(waitX == -1 && waitY == -1) {
				waitX = x; waitY = y;
				if(x < pgs.getWidth() / 2) {
					for (int i = 5; i > 0; --i) {
						if (waitX + i < free.length && free[waitX + i][waitY]) {
							waitX += i; break;
						} else if (waitY + i < free[waitX].length && free[waitX][waitY + i]) {
							waitY += i; break;
						} else if (waitX - i >= 0 && free[waitX - i][waitY]) {
							waitX -= i; break;
						} else if (waitY - i >= 0 && free[waitX][waitY - i]) {
							waitY -= i; break;
						} 
					}
				} else {
					for (int i = 5; i > 0; --i) {
						if (waitX - i >= 0 && free[waitX - i][waitY]) {
							waitX -= i; break;
						} else if (waitY - i >= 0 && free[waitX][waitY - i]) {
							waitY -= i; break;
						} else if (waitX + i < free.length && free[waitX + i][waitY]) {
							waitX += i; break;
						} else if (waitY + i < free[waitX].length && free[waitX][waitY + i]) {
							waitY += i; break;
						} 					}
				}
			}
			
			Pair<Integer,Integer> result = new Pair<Integer,Integer>(waitX, waitY);
			
			boolean found = false;
			while(!found) {
				switch(dir) {
				case 0:
					++waitX; break;
				case 1:
					++waitY; break;
				case 2:
					--waitX; break;
				case 3:
					--waitY; break;
				}
				++despl;
				if (despl == max) {
					dir = (dir + 1) % 4;
					if (aumen)
						++max;
					aumen = !aumen;
					despl = 0;
				}
				if (waitX >= 0 && waitY >= 0 && 
					waitX < free.length && waitY < free[waitX].length && 
					free[waitX][waitY]) {
					found = true;
				} 
			}
			
			return result;
		}
		
		public void reset() {
			waitX = -1;
			waitY = -1;
			dir = 0;
			despl = 0;
			max = 1;
			aumen = false;
			waitPositions = new HashMap<Unit, Pair<Integer,Integer>>();
		}
	}
	

	public GeneralScript(UnitTypeTable a_utt, BaseBehType a_baseBeh, BarBehType a_barBeh, 
						WorkBehType a_workBeh, LightBehType a_lightBeh, HeavyBehType a_heavyBeh,
						RangedBehType a_rangeBeh) {
		super(new AStarPathFinding());
		reset(a_utt);
		baseBehType = a_baseBeh;
		baseBeh = new BaseBehavior(utt, baseBehType);
		barBehType = a_barBeh;
		barBeh = new BarrackBehavior(utt, barBehType);
		workBehType = a_workBeh;
		workBeh = new WorkerBehavior(utt, workBehType);
		lightBehType = a_lightBeh;
		lightBeh = new LightBehavior(utt, lightBehType);
		heavyBehType = a_heavyBeh;
		heavyBeh = new HeavyBehavior(utt, heavyBehType);
		rangeBehType = a_rangeBeh;
		rangeBeh = new RangedBehavior(utt, rangeBehType);
		waitPoint = new WaitPoint();
	}
	
	public GeneralScript(UnitTypeTable a_utt, int timebudget, int cyclesbudget, BaseBehType a_baseBeh, 
						BarBehType a_barBeh, WorkBehType a_workBeh, LightBehType a_lightBeh, 
						HeavyBehType a_heavyBeh, RangedBehType a_rangeBeh) {
        super(new AStarPathFinding(), timebudget, cyclesbudget);
        reset(a_utt);
        baseBehType = a_baseBeh;
		baseBeh = new BaseBehavior(utt, baseBehType);
		barBehType = a_barBeh;
		barBeh = new BarrackBehavior(utt, barBehType);
		workBehType = a_workBeh;
		workBeh = new WorkerBehavior(utt, workBehType);
		lightBehType = a_lightBeh;
		lightBeh = new LightBehavior(utt, lightBehType);
		heavyBehType = a_heavyBeh;
		heavyBeh = new HeavyBehavior(utt, heavyBehType);
		rangeBehType = a_rangeBeh;
		rangeBeh = new RangedBehavior(utt, rangeBehType);
		waitPoint = new WaitPoint();
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
	
	@Override
	public PlayerAction getAction(int player, GameState gs) throws Exception {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		Player p = gs.getPlayer(player);
		resourcesUsed = 0;
		workBeh.resetAtributes();
		
		for (Unit u : pgs.getUnits()) {
			if (u.getPlayer() == player) {
				if (u.getType().equals(baseType))
					baseBeh.behavior(this, u, p, pgs);
				else if (u.getType().equals(workerType))
					workBeh.behavior(this, u, p, pgs);
				else if (u.getType().equals(barracksType))
					barBeh.behavior(this, u, p, pgs);
				else if (u.getType().equals(lightType))
					lightBeh.behavior(this, u, p, pgs);
				else if (u.getType().equals(heavyType))
					heavyBeh.behavior(this, u, p, pgs);
				else if (u.getType().equals(rangedType))
					rangeBeh.behavior(this, u, p, pgs);
				int a = 0;
			}
		}

		// This method simply takes all the unit actions executed so far, and packages
		// them into a PlayerAction
		return translateActions(player, gs);
	}
	
	public void useResources(int used) {
		resourcesUsed += used;
	}
	
	public int getResources() {
		return resourcesUsed;
	}
	
	@Override
	public void reset() {
		waitPoint.reset();
	}
	
	public void wait(Unit u, Player p, PhysicalGameState pgs) {
		Pair<Integer,Integer> wp;
		if (waitPoint.get(u) == null) {
			wp = waitPoint.fetchAndAdv(pgs, u.getX(), u.getY());
			waitPoint.put(u, wp);
		} else {
			wp = waitPoint.get(u);
		}
		move(u, wp.m_a, wp.m_b);
		for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
            	int dx = u2.getX()-u.getX();
                int dy = u2.getY()-u.getY();
                double d = Math.sqrt(dx*dx+dy*dy);
                if (d <= u.getAttackRange() + 2) {
                    attack(u, u2);
                }
            }
        }
	}
	
	@Override
	public GeneralScript clone() {
		return new GeneralScript(utt, getTimeBudget(), getIterationsBudget(), baseBehType, 
									barBehType,	workBehType, lightBehType, heavyBehType, rangeBehType);
	}
	
	public List<String> getBehaviorTypes() {
		List<String> param = new ArrayList<String>();
		param.add(baseBehType.toString());
		param.add(barBehType.toString());
		param.add(workBehType.toString());
		param.add(lightBehType.toString());
		param.add(heavyBehType.toString());
		param.add(rangeBehType.toString());
		return param;
	}
	
	public UnitTypeTable getUtt() {
		return utt;
	}

	public String toString() {
		List<String> param = this.getBehaviorTypes();
		String res = param.get(0);
		for (int i = 1; i < param.size(); ++i) {
			res += ", " + param.get(i);
		}
		return res;
	}
	
	@Override
	public List<ParameterSpecification> getParameters() {
		// Lista de parametros que de momento no se sabe para que vale.
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + barBehType.ordinal();
		result = prime * result + baseBehType.ordinal();
		result = prime * result + heavyBehType.ordinal();
		result = prime * result + lightBehType.ordinal();
		result = prime * result + rangeBehType.ordinal();
		result = prime * result + workBehType.ordinal();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneralScript other = (GeneralScript) obj;
		if (barBehType != other.barBehType)
			return false;
		if (baseBehType != other.baseBehType)
			return false;
		if (heavyBehType != other.heavyBehType)
			return false;
		if (lightBehType != other.lightBehType)
			return false;
		if (rangeBehType != other.rangeBehType)
			return false;
		if (workBehType != other.workBehType)
			return false;
		return true;
	}

	
}
