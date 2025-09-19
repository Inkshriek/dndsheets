
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.hawthorn.dndsheets.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import net.hawthorn.dndsheets.world.inventory.TestGUIMenu;
import net.hawthorn.dndsheets.world.inventory.RollEditorMenu;
import net.hawthorn.dndsheets.world.inventory.AdvancedRollEditorMenu;
import net.hawthorn.dndsheets.world.inventory.CharacterSheetMenu;
import net.hawthorn.dndsheets.DndsheetsMod;

public class DndsheetsModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, DndsheetsMod.MODID);
	public static final RegistryObject<MenuType<CharacterSheetMenu>> CHARACTER_SHEET = REGISTRY.register("character_sheet", () -> IForgeMenuType.create(CharacterSheetMenu::new));
	public static final RegistryObject<MenuType<RollEditorMenu>> ROLL_EDITOR = REGISTRY.register("roll_editor", () -> IForgeMenuType.create(RollEditorMenu::new));
	public static final RegistryObject<MenuType<TestGUIMenu>> TEST_GUI = REGISTRY.register("test_gui", () -> IForgeMenuType.create(TestGUIMenu::new));
	public static final RegistryObject<MenuType<AdvancedRollEditorMenu>> ADVANCED_ROLL_EDITOR = REGISTRY.register("advanced_roll_editor", () -> IForgeMenuType.create(AdvancedRollEditorMenu::new));
}
