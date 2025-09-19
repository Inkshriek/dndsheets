package net.hawthorn.dndsheets.procedures;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ibm.icu.impl.Pair;
import net.hawthorn.dndsheets.DndsheetsMod;
import net.hawthorn.dndsheets.RollIndex;
import net.hawthorn.dndsheets.SheetLoader;
import net.hawthorn.dndsheets.client.gui.components.RollScrollWidget;
import net.hawthorn.dndsheets.network.SheetServerMessage;
import net.minecraft.client.gui.components.EditBox;

import java.util.HashMap;
import java.util.List;

public class CharacterSheetSaveProcedure {

	/**
	 *
	 * @param guistate
	 * <p>This is the standard save function meant for the CharacterSheetScreen to run.</p>
	 */
	public static void execute(HashMap guistate) {
		if (guistate == null) return;
		if (SheetLoader.getClientSheet() == null ) {
			System.out.println("The client doesn't have a sheet for some reason. Won't be any good saving it like this.");
			return;
		}
		JsonObject sheet = SheetLoader.getClientSheet();
		if (guistate.get("text:charactername") instanceof EditBox _tf) {
			sheet.addProperty("characterName", _tf.getValue());
		}
		if (guistate.get("text:characterclass") instanceof EditBox _tf) {
			sheet.addProperty("characterClass", _tf.getValue());
		}
		if (guistate.get("text:characterrace") instanceof EditBox _tf) {
			sheet.addProperty("characterRace", _tf.getValue());
		}
		if (guistate.get("text:background") instanceof EditBox _tf) {
			sheet.addProperty("background", _tf.getValue());
		}
		if (guistate.get("text:hitpoints") instanceof EditBox _tf) {
			sheet.addProperty("hitPoints", _tf.getValue());
		}
		if (guistate.get("text:hitpoints_max") instanceof EditBox _tf) {
			sheet.addProperty("hitPointsMax", _tf.getValue());
		}
		if (guistate.get("text:hitpoints_temp") instanceof EditBox _tf) {
			sheet.addProperty("hitPointsTemp", _tf.getValue());
		}
		if (guistate.get("text:armorclass") instanceof EditBox _tf) {
			sheet.addProperty("armorClass", _tf.getValue());
		}
		if (guistate.get("text:level") instanceof EditBox _tf) {
			sheet.addProperty("level", _tf.getValue());
		}
		if (guistate.get("text:speed") instanceof EditBox _tf) {
			sheet.addProperty("speed", _tf.getValue());
		}
		if (guistate.get("text:hitdice_types") instanceof EditBox _tf) {
			sheet.addProperty("hitDiceTypes", _tf.getValue());
		}
		if (guistate.get("text:hitdice") instanceof EditBox _tf) {
			sheet.addProperty("hitDice", _tf.getValue());
		}
		if (guistate.get("text:proficiency") instanceof EditBox _tf) {
			sheet.addProperty("proficiencyBonus", _tf.getValue());
		}

		if (guistate.get("text:strength") instanceof EditBox _tf) {
			sheet.addProperty("strength", _tf.getValue());
		}
		if (guistate.get("text:dexterity") instanceof EditBox _tf) {
			sheet.addProperty("dexterity", _tf.getValue());
		}
		if (guistate.get("text:constitution") instanceof EditBox _tf) {
			sheet.addProperty("constitution", _tf.getValue());
		}
		if (guistate.get("text:intelligence") instanceof EditBox _tf) {
			sheet.addProperty("intelligence", _tf.getValue());
		}
		if (guistate.get("text:wisdom") instanceof EditBox _tf) {
			sheet.addProperty("wisdom", _tf.getValue());
		}
		if (guistate.get("text:charisma") instanceof EditBox _tf) {
			sheet.addProperty("charisma", _tf.getValue());
		}

		if (guistate.get("scrolllist:attack_rolls") instanceof RollScrollWidget _tf) {
			String[] names = _tf.getNames();
			for (int i = 0; i < names.length; i++) {
				RollIndex rollIndex = new RollIndex(3, i);
				rollIndex.saveInSheet(sheet, names[i], true);
			}

		}

		byte[] data = sheet.toString().getBytes();
		DndsheetsMod.PACKET_HANDLER.sendToServer(new SheetServerMessage(data));
	}

	/**
	 *
	 * @param expression
	 * @param category
	 * @param index
	 * <p>This is used to save a dice roll expression to its respective category and entry in the JSON file.</p>
	 */
	public static void execute(String expression, int category, int index) {
		if (SheetLoader.getClientSheet() == null) {
			System.out.println("The client doesn't have a sheet for some reason. Won't be any good saving it like this.");
			return;
		}

		if (RollIndex.Category.fromInt(category).isAdvanced()) {
			System.out.println("Tried to make an advanced roll from the character sheet without a sub index to pick from. Not allowed, buddy boy.");
			return;
		}

		JsonObject sheet = SheetLoader.getClientSheet();
		RollIndex rollIndex = new RollIndex(category, index);
		rollIndex.saveInSheet(sheet, expression);

		byte[] data = sheet.toString().getBytes();
		DndsheetsMod.PACKET_HANDLER.sendToServer(new SheetServerMessage(data));
	}

	/**
	 *
	 * @param info
	 * @param category The specific category. This is expected to be an advanced category.
	 * @param index The index of the category's array, which'll pull the roll set.
	 * @param subIndex The sub index, which draws the roll group from the set.
	 * <p>This is used to save a dice roll expression to its respective category and entry in the JSON file.</p>
	 */
	public static void execute(List<Pair<String, String>> info, int category, int index, int subIndex) {
		if (SheetLoader.getClientSheet() == null ) {
			System.out.println("The client doesn't have a sheet for some reason. Won't be any good saving it like this.");
			return;
		}

		if (!RollIndex.Category.fromInt(category).isAdvanced()) {
			System.out.println("Tried to make a basic roll from the character sheet with the wrong method.");
			return;
		}

		JsonObject sheet = SheetLoader.getClientSheet();
		RollIndex rollIndex = new RollIndex(category, index, subIndex);
		rollIndex.saveInSheet(sheet, info);

		byte[] data = sheet.toString().getBytes();
		DndsheetsMod.PACKET_HANDLER.sendToServer(new SheetServerMessage(data));
	}
}
