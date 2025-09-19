package net.hawthorn.dndsheets.procedures;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.hawthorn.dndsheets.client.gui.CharacterSheetScreen;
import net.hawthorn.dndsheets.client.gui.components.RollScrollWidget;
import net.minecraft.client.gui.components.EditBox;

import net.hawthorn.dndsheets.SheetLoader;

import java.util.HashMap;

public class CharacterSheetLoadProcedure {

	public static void execute(HashMap guistate, CharacterSheetScreen screen) {
		if (guistate == null)
 {
			System.out.println("well fuck");
			return;
		}
		if (SheetLoader.getClientSheet() == null ) {
			System.out.println("The client doesn't have a sheet for some reason. The GUI will appear fucked up.");
			return;
		}
		JsonObject sheet = SheetLoader.getClientSheet();
		SheetLoader.validateSheet(sheet);
		if (guistate.get("text:charactername") instanceof EditBox _tf && sheet.has("characterName")) {
			String charName = sheet.get("characterName").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:characterclass") instanceof EditBox _tf && sheet.has("characterClass")) {
			String charName = sheet.get("characterClass").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:characterrace") instanceof EditBox _tf && sheet.has("characterRace")) {
			String charName = sheet.get("characterRace").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:background") instanceof EditBox _tf && sheet.has("background")) {
			String charName = sheet.get("background").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:hitpoints") instanceof EditBox _tf && sheet.has("hitPoints")) {
			String charName = sheet.get("hitPoints").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:hitpoints_max") instanceof EditBox _tf && sheet.has("hitPointsMax")) {
			String charName = sheet.get("hitPointsMax").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:hitpoints_temp") instanceof EditBox _tf && sheet.has("hitPointsTemp")) {
			String charName = sheet.get("hitPointsTemp").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:armorclass") instanceof EditBox _tf && sheet.has("armorClass")) {
			String charName = sheet.get("armorClass").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:level") instanceof EditBox _tf && sheet.has("level")) {
			String charName = sheet.get("level").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:speed") instanceof EditBox _tf && sheet.has("speed")) {
			String charName = sheet.get("speed").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:hitdice_types") instanceof EditBox _tf && sheet.has("hitDiceTypes")) {
			String charName = sheet.get("hitDiceTypes").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:hitdice") instanceof EditBox _tf && sheet.has("hitDice")) {
			String charName = sheet.get("hitDice").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:proficiency") instanceof EditBox _tf && sheet.has("proficiencyBonus")) {
			String charName = sheet.get("proficiencyBonus").getAsString();
			_tf.setValue(charName);
		}

		if (guistate.get("text:strength") instanceof EditBox _tf && sheet.has("strength")) {
			String charName = sheet.get("strength").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:dexterity") instanceof EditBox _tf && sheet.has("dexterity")) {
			String charName = sheet.get("dexterity").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:constitution") instanceof EditBox _tf && sheet.has("constitution")) {
			String charName = sheet.get("constitution").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:intelligence") instanceof EditBox _tf && sheet.has("intelligence")) {
			String charName = sheet.get("intelligence").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:wisdom") instanceof EditBox _tf && sheet.has("wisdom")) {
			String charName = sheet.get("wisdom").getAsString();
			_tf.setValue(charName);
		}
		if (guistate.get("text:charisma") instanceof EditBox _tf && sheet.has("charisma")) {
			String charName = sheet.get("charisma").getAsString();
			_tf.setValue(charName);
		}

		if (guistate.get("scrolllist:attack_rolls") instanceof RollScrollWidget _tf && sheet.has("attacks")) {
			JsonArray arr = sheet.getAsJsonArray("attacks");
			for (int i = 0; i < arr.size(); i++) {
				JsonObject rollForm = arr.get(i).getAsJsonObject();
				screen.addToScrollList(_tf, rollForm, 3, i, CharacterSheetScreen.PanelStatus.ATTACKS);
			}

		}
	}
}
