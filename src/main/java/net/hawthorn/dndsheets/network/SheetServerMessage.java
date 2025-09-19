
package net.hawthorn.dndsheets.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.hawthorn.dndsheets.DndsheetsMod;
import net.hawthorn.dndsheets.SheetLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SheetServerMessage {
	byte[] data;

	public SheetServerMessage(byte[] data) {
		this.data = data;
	}

	public SheetServerMessage(FriendlyByteBuf buffer) {
		this.data = buffer.readByteArray();
	}

	public static void buffer(SheetServerMessage message, FriendlyByteBuf buffer) {
		buffer.writeByteArray(message.data);
	}

	public static void handler(SheetServerMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			handle(context.getSender(), message.data);
		});
		context.setPacketHandled(true);
	}

	public static void handle(Player entity, byte[] data) {
		String uuid = entity.getStringUUID();
		String json = new String(data);
		JsonObject sheet = JsonParser.parseString(json).getAsJsonObject();
		SheetLoader.saveServer(sheet, uuid);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		DndsheetsMod.addNetworkMessage(SheetServerMessage.class, SheetServerMessage::buffer, SheetServerMessage::new, SheetServerMessage::handler);
	}
}
