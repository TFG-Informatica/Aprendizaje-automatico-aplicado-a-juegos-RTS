package bot.io;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import bot.behavior.BarrackBehavior.BarBehType;
import bot.behavior.BaseBehavior.BaseBehType;
import bot.behavior.HeavyBehavior.HeavyBehType;
import bot.behavior.LightBehavior.LightBehType;
import bot.behavior.RangedBehavior.RangedBehType;
import bot.behavior.WorkerBehavior.WorkBehType;
import bot.ia.GeneralScript;
import rts.units.UnitTypeTable;

public class GeneralScriptIO {
	
	public static void store(PrintStream file, GeneralScript bot) {
		List<String> params = bot.getBehaviorTypes();
		for (String s : params)
			file.println(s);
	}
	
	public static GeneralScript load(Scanner file, UnitTypeTable utt) {
		List<String> params = new ArrayList<String>();
		for (int i = 0; i < 6; ++i)
			params.add(file.nextLine());
		return new GeneralScript(utt, -1, -1, BaseBehType.valueOf(params.get(0)), BarBehType.valueOf(params.get(1)),
				WorkBehType.valueOf(params.get(2)), LightBehType.valueOf(params.get(3)), 
				HeavyBehType.valueOf(params.get(4)), RangedBehType.valueOf(params.get(5)));
	}
	
}
