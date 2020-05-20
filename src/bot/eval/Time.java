package bot.eval;

import rts.GameState;

public class Time implements EvalFunc {

	@Override
	public double evaluate(GameState gs, int player) {
		int time = gs.getTime();
		double mid = Math.sqrt(gs.getPhysicalGameState().getHeight() * gs.getPhysicalGameState().getWidth())*100;
		
		if (gs.winner() != player)
			return 1.0/(2*(1+Math.exp(-3*(time-mid)/mid)));
		else
			return 1.0/(2*(1+Math.exp(3*(time-mid)/mid))) + 0.5;
	}

}
