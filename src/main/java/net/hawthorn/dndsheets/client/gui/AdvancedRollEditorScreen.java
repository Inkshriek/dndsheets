package net.hawthorn.dndsheets.client.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.systems.RenderSystem;
import net.hawthorn.dndsheets.DndsheetsMod;
import net.hawthorn.dndsheets.RollIndex;
import net.hawthorn.dndsheets.SheetLoader;
import net.hawthorn.dndsheets.network.CharacterSheetOpenMessage;
import net.hawthorn.dndsheets.procedures.CharacterSheetSaveProcedure;
import net.hawthorn.dndsheets.world.inventory.AdvancedRollEditorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdvancedRollEditorScreen extends AbstractContainerScreen<AdvancedRollEditorMenu> {
	private final static HashMap<String, Object> guistate = AdvancedRollEditorMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	EditBox rollExpression1, rollExpression2, rollContext1, rollContext2;
	List<Button> adderButtons = new ArrayList<>();

	private static final int FIRSTROLL_X = 15;
	private static final int SECONDROLL_X = 170;
	private static final int SEPARATION = 36;

	public static int workingIndex = 0, workingCategory = 0, workingSubIndex = 0;

	public AdvancedRollEditorScreen(AdvancedRollEditorMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 325;
		this.imageHeight = 200;
	}

	private static final ResourceLocation texture = new ResourceLocation("dndsheets:textures/screens/advanced_roll_editor.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		rollExpression1.render(guiGraphics, mouseX, mouseY, partialTicks);
		rollExpression2.render(guiGraphics, mouseX, mouseY, partialTicks);
		rollContext1.render(guiGraphics, mouseX, mouseY, partialTicks);
		rollContext2.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			List<Pair<String, String>> data = new ArrayList<>();
			data.add(Pair.of(rollContext1.getValue(), rollExpression1.getValue()));
			data.add(Pair.of(rollContext2.getValue(), rollExpression2.getValue()));
			CharacterSheetSaveProcedure.execute(data, workingCategory, workingIndex, workingSubIndex);
			DndsheetsMod.PACKET_HANDLER.sendToServer(new CharacterSheetOpenMessage(0,0));
			return true;
		}
		if (rollExpression1.isFocused())
			return rollExpression1.keyPressed(key, b, c);
		if (rollExpression2.isFocused())
			return rollExpression2.keyPressed(key, b, c);
		if (rollContext1.isFocused())
			return rollContext1.keyPressed(key, b, c);
		if (rollContext2.isFocused())
			return rollContext2.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		rollExpression1.tick();
		rollExpression2.tick();
		rollContext1.tick();
		rollContext2.tick();
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.dndsheets.roll_editor.label_roll_editor_1"), FIRSTROLL_X, 10, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dndsheets.roll_editor.label_roll_editor_2"), SECONDROLL_X, 10, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dndsheets.roll_editor.label_roll_context"), FIRSTROLL_X, SEPARATION - 10, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dndsheets.roll_editor.label_roll_expression"), FIRSTROLL_X, SEPARATION*2 - 10, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dndsheets.roll_editor.label_insert_modifiers"), FIRSTROLL_X, SEPARATION*3 - 10, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dndsheets.roll_editor.label_roll_context"), SECONDROLL_X, SEPARATION - 10, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dndsheets.roll_editor.label_roll_expression"), SECONDROLL_X, SEPARATION*2 - 10, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dndsheets.roll_editor.label_insert_modifiers"), SECONDROLL_X, SEPARATION*3 - 10, -12829636, false);
	}

	@Override
	public void onClose() {
		super.onClose();
	}

	/**
	 * <p>This makes a Button which adds to an EditBox with a given substring once clicked.</p>
	 * @param guistateKey
	 * @param x
	 * @param y
	 * @param sizeX
	 * @param sizeY
	 * @param buttonList
	 * @param editBox
	 * @param subStringToAdd
	 */
	private void makeAdderButton(String guistateKey, int x, int y, int sizeX, int sizeY, List<Button> buttonList, EditBox editBox, String subStringToAdd, String type) {
		Button btn = Button.builder(Component.translatable("gui.dndsheets.roll_editor.button_" + type), e -> {
			if (editBox != null) {
				editBox.setValue(editBox.getValue() + subStringToAdd);
			}
		}).bounds(this.leftPos + x, this.topPos + y, sizeX, sizeY).build();
		guistate.put(guistateKey, btn);
		this.addRenderableWidget(btn);
		buttonList.add(btn);
	}

	private void initRollEditor() {
		JsonObject sheet = SheetLoader.getClientSheet();
		SheetLoader.validateSheet(sheet);

		rollExpression1 = new EditBox(this.font, this.leftPos + FIRSTROLL_X, this.topPos + SEPARATION*2, 118, 18, Component.translatable("gui.dndsheets.roll_editor.rollexpression")) {
			@Override
			public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollexpression").getString());
				else
					setSuggestion(null);
			}

			@Override
			public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollexpression").getString());
				else
					setSuggestion(null);
			}
		};
		rollExpression1.setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollexpression").getString());
		rollExpression1.setMaxLength(50);
		guistate.put("text:roll_expression", rollExpression1);
		this.addWidget(this.rollExpression1);

		rollExpression2 = new EditBox(this.font, this.leftPos + SECONDROLL_X, this.topPos + SEPARATION*2, 118, 18, Component.translatable("gui.dndsheets.roll_editor.rollexpression")) {
			@Override
			public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollexpression2").getString());
				else
					setSuggestion(null);
			}

			@Override
			public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollexpression2").getString());
				else
					setSuggestion(null);
			}
		};
		rollExpression2.setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollexpression2").getString());
		rollExpression2.setMaxLength(50);
		guistate.put("text:roll_expression_2", rollExpression2);
		this.addWidget(this.rollExpression2);

		rollContext1 = new EditBox(this.font, this.leftPos + FIRSTROLL_X, this.topPos + SEPARATION, 118, 18, Component.translatable("gui.dndsheets.roll_editor.rollexpression")) {
			@Override
			public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollcontext").getString());
				else
					setSuggestion(null);
			}

			@Override
			public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollcontext").getString());
				else
					setSuggestion(null);
			}
		};
		rollContext1.setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollcontext").getString());
		rollContext1.setMaxLength(50);
		guistate.put("text:roll_context", rollContext1);
		this.addWidget(this.rollContext1);

		rollContext2 = new EditBox(this.font, this.leftPos + SECONDROLL_X, this.topPos + SEPARATION, 118, 18, Component.translatable("gui.dndsheets.roll_editor.rollexpression")) {
			@Override
			public void insertText(String text) {
				super.insertText(text);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollcontext2").getString());
				else
					setSuggestion(null);
			}

			@Override
			public void moveCursorTo(int pos) {
				super.moveCursorTo(pos);
				if (getValue().isEmpty())
					setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollcontext2").getString());
				else
					setSuggestion(null);
			}
		};
		rollContext2.setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollcontext2").getString());
		rollContext2.setMaxLength(50);
		guistate.put("text:roll_context_2", rollContext2);
		this.addWidget(this.rollContext2);

		RollIndex.Category category = RollIndex.Category.fromInt(workingCategory);
		String stringCategory = category.toString();

		if (!category.isAdvanced()) {
			System.out.println("You're using Advanced Roll Editor on something that isn't advanced, buddy.");
			this.minecraft.player.closeContainer();

		}

		try {
			if (sheet.has(stringCategory)) {
				JsonArray arr = sheet.get(stringCategory).getAsJsonArray();
				JsonObject rollForm = arr.get(workingIndex).getAsJsonObject();
				JsonArray rollSet = rollForm.getAsJsonArray("rolls");
				JsonArray rollGroup = rollSet.get(workingSubIndex).getAsJsonArray();

				JsonObject rollInstance1 = rollGroup.get(0).getAsJsonObject();
				JsonObject rollInstance2 = rollGroup.get(1).getAsJsonObject();

				rollExpression1.setValue(rollInstance1.get("expression").getAsString());
				rollContext1.setValue(rollInstance1.get("context").getAsString());
				rollExpression2.setValue(rollInstance2.get("expression").getAsString());
				rollContext2.setValue(rollInstance2.get("context").getAsString());
			}
		}
		catch(Exception e){
			System.out.println("Couldn't pull values for the advanced roll, so the fields will be blank.");
		}


		makeAdderButton("button:adder_str", FIRSTROLL_X, SEPARATION*3, 40, 20, adderButtons, rollExpression1, " + $str", "str");
		makeAdderButton("button:adder_dex", FIRSTROLL_X + 45, SEPARATION*3, 40, 20, adderButtons, rollExpression1, " + $dex", "dex");
		makeAdderButton("button:adder_con", FIRSTROLL_X + 90, SEPARATION*3, 40, 20, adderButtons, rollExpression1, " + $con", "con");
		makeAdderButton("button:adder_int", FIRSTROLL_X, SEPARATION*3 + 27, 40, 20, adderButtons, rollExpression1, " + $int", "int");
		makeAdderButton("button:adder_wis", FIRSTROLL_X + 45, SEPARATION*3 + 27, 40, 20, adderButtons, rollExpression1, " + $wis", "wis");
		makeAdderButton("button:adder_cha", FIRSTROLL_X + 90, SEPARATION*3 + 27, 40, 20, adderButtons, rollExpression1, " + $cha", "cha");
		makeAdderButton("button:adder_hprof", FIRSTROLL_X, SEPARATION*3 + 54, 77, 20, adderButtons, rollExpression1, " + $hprof", "hprof");
		makeAdderButton("button:adder_prof", FIRSTROLL_X + 90, SEPARATION*3 + 54, 51, 20, adderButtons, rollExpression1, " + $prof", "prof");

		makeAdderButton("button:adder_str_2", SECONDROLL_X, SEPARATION*3, 40, 20, adderButtons, rollExpression2, " + $str", "str");
		makeAdderButton("button:adder_dex_2", SECONDROLL_X + 45, SEPARATION*3, 40, 20, adderButtons, rollExpression2, " + $dex", "dex");
		makeAdderButton("button:adder_con_2", SECONDROLL_X + 90, SEPARATION*3, 40, 20, adderButtons, rollExpression2, " + $con", "con");
		makeAdderButton("button:adder_int_2", SECONDROLL_X, SEPARATION*3 + 27, 40, 20, adderButtons, rollExpression2, " + $int", "int");
		makeAdderButton("button:adder_wis_2", SECONDROLL_X + 45, SEPARATION*3 + 27, 40, 20, adderButtons, rollExpression2, " + $wis", "wis");
		makeAdderButton("button:adder_cha_2", SECONDROLL_X + 90, SEPARATION*3 + 27, 40, 20, adderButtons, rollExpression2, " + $cha", "cha");
		makeAdderButton("button:adder_hprof_2", SECONDROLL_X, SEPARATION*3 + 54, 77, 20, adderButtons, rollExpression2, " + $hprof", "hprof");
		makeAdderButton("button:adder_prof_2", SECONDROLL_X + 90, SEPARATION*3 + 54, 51, 20, adderButtons, rollExpression2, " + $prof", "prof");
	}

	@Override
	public void init() {
		super.init();


		initRollEditor();
	}
}
