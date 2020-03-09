package GroupBot;

import java.util.HashSet;
import java.util.List;

import rts.units.Unit;

public class Group {

	private HashSet<Unit> grupo;
	private int id;
	private GroupBot bot;
	
	public Group() {
		grupo=new HashSet<Unit>();
		id=bot.getNextId();
	}
	
	public Group(HashSet<Unit> lista) {
		grupo=lista;
		id=bot.getNextId();
	}
	
	public void add(Unit u) {
		grupo.add(u);
	}
	public int getId() {
		return id;
	}
	
}
