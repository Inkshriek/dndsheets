package net.hawthorn.dndsheets.procedures;

import net.minecraft.client.gui.components.EditBox;

import java.util.HashMap;

public class TestProcedure {
	public static void execute(HashMap guistate) {
		if (guistate == null)
			return;
		if (guistate.get("text:textFieldName") instanceof EditBox _tf)
			_tf.setValue("Text");
	}
}
