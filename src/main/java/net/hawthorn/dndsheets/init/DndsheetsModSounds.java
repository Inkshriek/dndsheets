
package net.hawthorn.dndsheets.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.hawthorn.dndsheets.DndsheetsMod;

public class DndsheetsModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DndsheetsMod.MODID);
	public static final RegistryObject<SoundEvent> DICE = REGISTRY.register("dice", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("dndsheets", "dice")));
}
