package net.hawthorn.dndsheets.procedures;

import com.ibm.icu.impl.Pair;
import net.hawthorn.dndsheets.DndsheetsMod;
import net.hawthorn.dndsheets.RollIndex;
import net.hawthorn.dndsheets.SheetLoader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;

import net.hawthorn.dndsheets.DiceManager;
import io.github.tfriedrichs.dicebot.result.DiceResult;
import io.github.tfriedrichs.dicebot.result.DiceResultPrettyPrinter;


import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RollAnnouncerProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, JsonObject sheet, CommandContext<CommandSourceStack> arguments) {
		if (!world.isClientSide() && world.getServer() != null)
			world.getServer().getPlayerList().broadcastSystemMessage(Component.literal((new Object() {
				public String getMessage() {
					try {
						DiceResult result = DiceManager.roll(sheet, MessageArgument.getMessage(arguments, "expression").getString());
						if (result == null) return "A roll didn't work. Make sure the roll expression is written properly.";
						else {
							String print = new DiceResultPrettyPrinter().prettyPrint(result);
							return (sheet.has("characterName") ? sheet.get("characterName").getAsString() + " rolled a " + print : "Someone rolled a " + print);
						}
					} catch (CommandSyntaxException ignored) {
						System.out.println("we goofed");
						return "oopsie";
					}
				}
			}).getMessage()), false);
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("dndsheets:dice")), SoundSource.NEUTRAL, 1, 1);
			}
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, String uuid, int category, int index, int subIndex) {
		Logger logger = LogManager.getLogger(DndsheetsMod.MODID);
		logger.log(org.apache.logging.log4j.Level.getLevel("info"), "Attempting to make a roll announcement.");
		if (!world.isClientSide() && world.getServer() != null)
			world.getServer().getPlayerList().broadcastSystemMessage(Component.literal((new Object() {
				public String getMessage() {
					JsonObject sheet = SheetLoader.getServerSheet(uuid);
					RollIndex roll = new RollIndex(category, index, subIndex);
					List<String> expressions = roll.findExpressionsInSheet(sheet);
					List<String> contexts = roll.findContextsInSheet(sheet);
					List<String> resultRolls = new ArrayList<>();
                    for (String expression : expressions) {
                        DiceResult output = DiceManager.roll(sheet, expression);
                        if (output == null) {
                            logger.log(org.apache.logging.log4j.Level.getLevel("info"), "Got a null.");
                            continue;
                        }
                        resultRolls.add(new DiceResultPrettyPrinter().prettyPrint(output));
                    }

					if (resultRolls.isEmpty()) return "A roll didn't work. Make sure ability scores are set and that the expression is correct.";
					String print = (sheet.has("characterName") ? sheet.get("characterName").getAsString() + " rolled a " : "Someone rolled a ");

					boolean first = true;
					for (int i = 0; i < resultRolls.size(); i++) {
						String context = "";
						if (i < contexts.size()) {
							context = contexts.get(i);
						}
						String result = resultRolls.get(i);
						if (!first) {
							print += " and ";
						}
						print += result + " (" + context + ")";
						first = false;
					}
					return print;
				}
			}).getMessage()), false);
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("dndsheets:dice")), SoundSource.NEUTRAL, 1, 1);
			}
		}

	}

}
