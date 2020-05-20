package bot.eval;

import rts.GameState;

public interface EvalFunc {

	double evaluate(GameState gs, int player);
	
}
