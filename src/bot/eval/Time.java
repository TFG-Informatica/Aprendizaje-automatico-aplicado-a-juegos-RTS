package bot.eval;

import rts.GameState;

public class Time implements EvalFunc {

	int mid;
	int CURV = 5;
	double LIM = 0.45;
	
	public Time (int max) {
		mid = max / 2;
	}
	
	@Override
	public double evaluate(GameState gs, int player) {
		int time = gs.getTime();
		
		if (gs.winner() != player)
			return LIM / (1 + Math.exp(-CURV*(time-mid)/mid));
		else
			return LIM / (1 + Math.exp(CURV*(time-mid)/mid)) + (1-LIM);
	}

}
