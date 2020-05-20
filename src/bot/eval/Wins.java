package bot.eval;

import rts.GameState;

public class Wins implements EvalFunc {

	@Override
	public double evaluate(GameState gs, int player) {
		if (gs.winner() == player)
			return 1;
		else
			return 0;
	}

}
