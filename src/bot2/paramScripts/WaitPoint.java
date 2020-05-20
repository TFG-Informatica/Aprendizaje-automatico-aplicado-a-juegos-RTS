package bot2.paramScripts;

import java.util.HashMap;


import rts.PhysicalGameState;
import rts.units.Unit;
import util.Pair;

public class WaitPoint {
	
	private int waitX;
	private int waitY;
	private int dir;
	private int despl;
	private int max;
	private boolean aumen;
	private HashMap<Unit, Pair<Integer,Integer>> waitPositions;
	
	
	public WaitPoint(int x, int y){
		waitX = x;
		waitY = y;
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
	
	
	public void reset(int x, int y) {
		waitX = x;
		waitY = y;
		dir = 0;
		despl = 0;
		max = 1;
		aumen = false;
		waitPositions = new HashMap<Unit, Pair<Integer,Integer>>();
	}
}
