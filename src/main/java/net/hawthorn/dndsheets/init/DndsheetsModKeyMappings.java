
package net.hawthorn.dndsheets.init;

import net.hawthorn.dndsheets.network.CharacterSheetOpenMessage;
import org.lwjgl.glfw.GLFW;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

import net.hawthorn.dndsheets.DndsheetsMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class DndsheetsModKeyMappings {
	public static final KeyMapping CHARACTER = new KeyMapping("key.dndsheets.character", GLFW.GLFW_KEY_H, "key.categories.dndsheets") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {

				DndsheetsMod.PACKET_HANDLER.sendToServer(new CharacterSheetOpenMessage(0, 0));
				CharacterSheetOpenMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(CHARACTER);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				CHARACTER.consumeClick();
			}
		}
	}
}
