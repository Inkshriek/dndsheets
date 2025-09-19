package net.hawthorn.dndsheets.client.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.hawthorn.dndsheets.DndsheetsMod;
import net.hawthorn.dndsheets.SheetLoader;
import net.hawthorn.dndsheets.network.CharacterSheetOpenMessage;
import net.hawthorn.dndsheets.procedures.CharacterSheetSaveProcedure;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import net.hawthorn.dndsheets.world.inventory.RollEditorMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

public class RollEditorScreen extends AbstractContainerScreen<RollEditorMenu> {
	private final static HashMap<String, Object> guistate = RollEditorMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	EditBox rollExpression;
	List<Button> adderButtons = new ArrayList<>();

	public static int workingIndex = 0, workingCategory = 0;

	private static final int X_OFFSET = 30;

	private static final int EXPRESSION_Y = 37;
	private static final int BUTTONS_Y = 82;
	private static final int BUTTONS_SEPARATION = 22;

	public RollEditorScreen(RollEditorMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 200;
		this.imageHeight = 175;
	}

	private static final ResourceLocation texture = new ResourceLocation("dndsheets:textures/screens/roll_editor.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		rollExpression.render(guiGraphics, mouseX, mouseY, partialTicks);
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
			CharacterSheetSaveProcedure.execute(rollExpression.getValue(), workingCategory, workingIndex);
			DndsheetsMod.PACKET_HANDLER.sendToServer(new CharacterSheetOpenMessage(0,0));
			return true;
		}
		if (rollExpression.isFocused())
			return rollExpression.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		rollExpression.tick();
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		final int txtColor = 0xFFFFFF;
		guiGraphics.drawString(this.font, Component.translatable("gui.dndsheets.roll_editor.label_roll_expression"), X_OFFSET, EXPRESSION_Y - 10, 0xFFFFFF, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.dndsheets.roll_editor.label_insert_modifiers"), X_OFFSET, BUTTONS_Y - 10, 0xFFFFFF, false);
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

	@Override
	public void init() {
		super.init();

		JsonObject sheet = SheetLoader.getClientSheet();
		SheetLoader.validateSheet(sheet);

		rollExpression = new EditBox(this.font, this.leftPos + X_OFFSET, this.topPos + EXPRESSION_Y, 135, 18, Component.translatable("gui.dndsheets.roll_editor.rollexpression")) {
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
		rollExpression.setSuggestion(Component.translatable("gui.dndsheets.roll_editor.rollexpression").getString());
		rollExpression.setMaxLength(50);
		guistate.put("text:roll_expression", rollExpression);
		this.addWidget(this.rollExpression);

		String stringCategory = "";
		switch (workingCategory) {
			case 0: stringCategory = "checks"; break;
			case 1: stringCategory = "saves"; break;
			case 2: stringCategory = "skills"; break;
			default: System.out.println("Bad idea."); this.minecraft.player.closeContainer(); break;
		}
		if (sheet.has(stringCategory)) {
			if (sheet.get(stringCategory) != null) {
				JsonArray arr = sheet.get(stringCategory).getAsJsonArray();
				String expression = "";
				if (arr != null && workingIndex < arr.size()) expression = arr.get(workingIndex).getAsString();
				rollExpression.setValue(expression);
			}

		}

		makeAdderButton("button:adder_str", X_OFFSET, BUTTONS_Y, 40, 20, adderButtons, rollExpression, " + $str", "str");
		makeAdderButton("button:adder_dex", X_OFFSET + 47, BUTTONS_Y, 40, 20, adderButtons, rollExpression, " + $dex", "dex");
		makeAdderButton("button:adder_con", X_OFFSET + 94, BUTTONS_Y, 40, 20, adderButtons, rollExpression, " + $con", "con");
		makeAdderButton("button:adder_int", X_OFFSET, BUTTONS_Y + BUTTONS_SEPARATION, 40, 20, adderButtons, rollExpression, " + $int", "int");
		makeAdderButton("button:adder_wis", X_OFFSET + 47, BUTTONS_Y + BUTTONS_SEPARATION, 40, 20, adderButtons, rollExpression, " + $wis", "wis");
		makeAdderButton("button:adder_cha", X_OFFSET + 94, BUTTONS_Y + BUTTONS_SEPARATION, 40, 20, adderButtons, rollExpression, " + $cha", "cha");
		makeAdderButton("button:adder_hprof", X_OFFSET, BUTTONS_Y + BUTTONS_SEPARATION*2, 77, 20, adderButtons, rollExpression, " + $hprof", "hprof");
		makeAdderButton("button:adder_prof", X_OFFSET + 83, BUTTONS_Y + BUTTONS_SEPARATION*2, 51, 20, adderButtons, rollExpression, " + $prof", "prof");
	}
}
