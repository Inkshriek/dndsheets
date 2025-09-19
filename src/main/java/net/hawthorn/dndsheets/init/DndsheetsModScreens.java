
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.hawthorn.dndsheets.init;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;

import net.hawthorn.dndsheets.client.gui.TestGUIScreen;
import net.hawthorn.dndsheets.client.gui.RollEditorScreen;
import net.hawthorn.dndsheets.client.gui.AdvancedRollEditorScreen;
import net.hawthorn.dndsheets.client.gui.CharacterSheetScreen;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DndsheetsModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(DndsheetsModMenus.ADVANCED_ROLL_EDITOR.get(), AdvancedRollEditorScreen::new);
			MenuScreens.register(DndsheetsModMenus.CHARACTER_SHEET.get(), CharacterSheetScreen::new);
			MenuScreens.register(DndsheetsModMenus.ROLL_EDITOR.get(), RollEditorScreen::new);
			MenuScreens.register(DndsheetsModMenus.TEST_GUI.get(), TestGUIScreen::new);
		});
	}
}
