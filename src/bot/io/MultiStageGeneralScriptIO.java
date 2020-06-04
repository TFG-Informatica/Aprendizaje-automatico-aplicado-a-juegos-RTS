package bot.io;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import bot.ia.MultiStageGeneralScript;
import bot.scripts.GeneralScript;
import rts.units.UnitTypeTable;

public class MultiStageGeneralScriptIO {
	
	public static void store(PrintStream file, MultiStageGeneralScript bot) {
		List<GeneralScript> scripts = bot.getScripts();
		file.println(bot.get_cycles());
		file.println(scripts.size());
		for (int i = 0; i < scripts.size(); ++i)
			GeneralScriptIO.store(file, scripts.get(i));
	}
	
	public static MultiStageGeneralScript load(Scanner file, UnitTypeTable utt) {
		int max_cycles = Integer.parseInt(file.nextLine());
		int numScripts = Integer.parseInt(file.nextLine());
		List<GeneralScript> scripts = new ArrayList<GeneralScript>();
		for (int i = 0; i < numScripts; ++i) 
			scripts.add(GeneralScriptIO.load(file, utt));
		return new MultiStageGeneralScript(max_cycles, scripts);
	}
	
}
