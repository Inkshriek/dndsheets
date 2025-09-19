/**
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.mcreator.fragmentsintime as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
// https://mvnrepository.com/artifact/com.bernardomg.tabletop/dice
package net.hawthorn.dndsheets;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import com.google.gson.JsonObject;
import io.github.tfriedrichs.dicebot.result.DiceResult;
import io.github.tfriedrichs.dicebot.expression.DiceExpression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DiceManager {

	public DiceManager() {
	}


	public static DiceResult roll(JsonObject sheet, String expression) {
		Logger logger = LogManager.getLogger(DndsheetsMod.MODID);
		logger.log(org.apache.logging.log4j.Level.getLevel("info"), "Initial parse: " + expression);
		expression = expression.toLowerCase();
		try {
			int score, modifier;
			if (sheet.has("strength") && expression.contains("$str")) {
				score = Integer.parseInt(sheet.get("strength").getAsString());
				modifier = (int)Math.floor((double) (score - 10) / 2);
				expression = expression.replace("$str", String.valueOf(modifier));
			}
			if (sheet.has("dexterity") && expression.contains("$dex")) {
				score = Integer.parseInt(sheet.get("dexterity").getAsString());
				modifier = (int)Math.floor((double) (score - 10) / 2);
				expression = expression.replace("$dex", String.valueOf(modifier));
			}
			if (sheet.has("constitution") && expression.contains("$con")) {
				score = Integer.parseInt(sheet.get("constitution").getAsString());
				modifier = (int)Math.floor((double) (score - 10) / 2);
				expression = expression.replace("$con", String.valueOf(modifier));
			}
			if (sheet.has("intelligence") && expression.contains("$int")) {
				score = Integer.parseInt(sheet.get("intelligence").getAsString());
				modifier = (int)Math.floor((double) (score - 10) / 2);
				expression = expression.replace("$int", String.valueOf(modifier));
			}
			if (sheet.has("wisdom") && expression.contains("$wis")) {
				score = Integer.parseInt(sheet.get("wisdom").getAsString());
				modifier = (int)Math.floor((double) (score - 10) / 2);
				expression = expression.replace("$wis", String.valueOf(modifier));
			}
			if (sheet.has("charisma") && expression.contains("$cha")) {
				score = Integer.parseInt(sheet.get("charisma").getAsString());
				modifier = (int)Math.floor((double) (score - 10) / 2);
				expression = expression.replace("$cha", String.valueOf(modifier));
			}
			if (sheet.has("proficiencyBonus") && expression.contains("$prof")) {
				expression = expression.replace("$prof", sheet.get("proficiencyBonus").getAsString());
			}
			if (sheet.has("proficiencyBonus") && expression.contains("$hprof")) {
				modifier = Integer.parseInt(sheet.get("proficiencyBonus").getAsString()) / 2;
				expression = expression.replace("$hprof", String.valueOf(modifier));
			}
			logger.log(org.apache.logging.log4j.Level.getLevel("info"), "Final roll: " + expression);
			DiceExpression ex = DiceExpression.parse(expression);
			DiceResult result = ex.roll();
			return result;
		} catch (Exception e) {
			logger.log(org.apache.logging.log4j.Level.getLevel("info"), "Some roll turned up an error, so it will be ignored.");
			return null;
		}
		
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		
	}

	@Mod.EventBusSubscriber
	private static class ForgeBusEvents {
		@SubscribeEvent
		public static void serverLoad(ServerStartingEvent event) {
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void clientLoad(FMLClientSetupEvent event) {
		}
	}
}
