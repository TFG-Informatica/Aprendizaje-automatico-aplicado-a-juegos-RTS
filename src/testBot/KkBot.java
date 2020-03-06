package testBot;

import java.util.ArrayList;
import java.util.List;

import ai.abstraction.Train;
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

public class KkBot extends AIWithComputationBudget {
	UnitTypeTable m_utt = null;
	private int fase = 0;

	// This is the default constructor that microRTS will call:
	public KkBot(UnitTypeTable utt) {
		super(-1, -1);
		m_utt = utt;
	}

	// This will be called by microRTS when it wants to create new instances of this
	// bot (e.g., to play multiple games).
	public AI clone() {
		return new KkBot(m_utt);
	}

	// This will be called once at the beginning of each new game:
	public void reset() {
	}

	// Called by microRTS at each game cycle.
	// Returns the action the bot wants to execute.
	public PlayerAction getAction(int player, GameState gs) {
		PlayerAction pa = new PlayerAction();
		PhysicalGameState pgs = gs.getPhysicalGameState();

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

	// This will be called by the microRTS GUI to get the
	// list of parameters that this bot wants exposed
	// in the GUI.
	public List<ParameterSpecification> getParameters() {
		return new ArrayList<>();
	}
}
