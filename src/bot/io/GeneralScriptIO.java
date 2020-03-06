package bot.io;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import bot.scripts.GeneralScript;
import bot.scripts.BarrackBehavior.BarBehType;
import bot.scripts.BaseBehavior.BaseBehType;
import bot.scripts.HeavyBehavior.HeavyBehType;
import bot.scripts.LightBehavior.LightBehType;
import bot.scripts.RangedBehavior.RangedBehType;
import bot.scripts.WorkerBehavior.WorkBehType;
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
