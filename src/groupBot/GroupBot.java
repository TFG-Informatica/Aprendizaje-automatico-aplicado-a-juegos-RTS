package groupBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.Train;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.ResourceUsage;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;

public class GroupBot extends AbstractionLayerAI {
	UnitTypeTable m_utt = null;
	private int nextgroupid = 0;
	private HashMap<Unit,Integer> assignment = null;
	private HashMap<Integer,Group> groups = null;
	private boolean firstturn = true;

	// This is the default constructor that microRTS will call:
	public GroupBot(UnitTypeTable a_utt) {
		super(new AStarPathFinding());
		reset(a_utt);
		m_utt=a_utt;
		assignment=new HashMap<Unit,Integer>();
		groups=new HashMap<Integer,Group>();
	}

	// This will be called by microRTS when it wants to create new instances of this
	// bot (e.g., to play multiple games).
	public AI clone() {
		return new GroupBot(m_utt);
	}

	// This will be called once at the beginning of each new game:
	public void reset() {
		super.reset();
		groups.clear();
		assignment.clear();
		nextgroupid=0;
		firstturn =true;
	}
	
	private void initializeAssignment(int player, PhysicalGameState pgs) {
		Group unnasigned = new Group();
		for (Unit u : pgs.getUnits()) {
			if (u.getPlayer() == player) {
				unnasigned.add(u);
				assignment.put(u, 0);
			}
		}
		groups.put(0,unnasigned);
	}

	// Called by microRTS at each game cycle.
	// Returns the action the bot wants to execute.
	public PlayerAction getAction(int player, GameState gs) {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		
		if(firstturn) {
			//Si es el primer ciclo, crea un grupo con las unidades iniciales del tablero
			firstturn=false;
			initializeAssignment(player,pgs);
		}
		else {
			//Elimina unidades muertas en el ciclo anterior
			updateUnits(player,pgs);
		}
		
		//Asignación de acciones a cada unidad
		PlayerAction pa = new PlayerAction();
		
		for (Unit u : pgs.getUnits()) {

			if (u.getPlayer() == player && gs.getActionAssignment(u) == null) {

				if (u.getType() == m_utt.getUnitType("Base")) {

					pa.addUnitAction(u, new UnitAction(UnitAction.TYPE_PRODUCE, UnitAction.DIRECTION_DOWN,
							m_utt.getUnitType("Worker")));

				} else if (u.getType() == m_utt.getUnitType("Worker")) {

					pa.addUnitAction(u, new UnitAction(UnitAction.TYPE_MOVE, UnitAction.DIRECTION_LEFT));

				} else {

					pa.addUnitAction(u, new UnitAction(UnitAction.TYPE_NONE, 10));

				}
			}

		}
		return pa;
	}
	
	private void updateUnits(int player, PhysicalGameState pgs) {
		groups.clear();
		for (Unit u : pgs.getUnits()) {
			if (u.getPlayer() == player) {
				Integer i = assignment.get(u);
				if(i!=null) {
					//groups.add(i.intValue(), u);
				}

			}
		}

	}

	public int getNextId() {
		return ++nextgroupid;
	}
	
	// This will be called by the microRTS GUI to get the
	// list of parameters that this bot wants exposed
	// in the GUI.
	public List<ParameterSpecification> getParameters() {
		return new ArrayList<>();
	}
}
