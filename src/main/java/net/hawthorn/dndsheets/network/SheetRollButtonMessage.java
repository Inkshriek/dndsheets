
package net.hawthorn.dndsheets.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.hawthorn.dndsheets.DndsheetsMod;
import net.hawthorn.dndsheets.RollIndex;
import net.hawthorn.dndsheets.SheetLoader;
import net.hawthorn.dndsheets.procedures.RollAnnouncerProcedure;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SheetRollButtonMessage {
	int category, index, subIndex, x, y, z;

	public SheetRollButtonMessage(int category, int index, int subIndex, int x, int y, int z) {
		this.category = category;
		this.index = index;
		this.subIndex = subIndex;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SheetRollButtonMessage(FriendlyByteBuf buffer) {
		this.category = buffer.readInt();
		this.index = buffer.readInt();
		this.subIndex = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public static void buffer(SheetRollButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.category);
		buffer.writeInt(message.index);
		buffer.writeInt(message.subIndex);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handler(SheetRollButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			handle(context.getSender(), message.category, message.index, message.subIndex, message.x, message.y, message.z);
		});
		context.setPacketHandled(true);
	}

	public static void handle(Player entity, int category, int index, int subIndex, int x, int y, int z) {
		Level world = entity.level();
		String uuid = entity.getStringUUID();
		// security measure to prevent arbitrary chunk generation
		Logger logger = LogManager.getLogger(DndsheetsMod.MODID);
		if (!world.hasChunkAt(entity.blockPosition())) {
			logger.log(org.apache.logging.log4j.Level.getLevel("info"), "Couldn't make a roll, the player's coordinates are somewhere without a chunk.");
			return;
		}
		if (SheetLoader.getServerSheet(uuid) == null) {
			logger.log(org.apache.logging.log4j.Level.getLevel("info"), "Couldn't make a roll, unable to find player's sheet on the server.");
			return;
		}
		try {
			RollAnnouncerProcedure.execute(world, x, y, z, uuid, category, index, subIndex);
		}
		catch(Exception e) {
			logger.log(org.apache.logging.log4j.Level.getLevel("severe"), e);
		}

	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		DndsheetsMod.addNetworkMessage(SheetRollButtonMessage.class, SheetRollButtonMessage::buffer, SheetRollButtonMessage::new, SheetRollButtonMessage::handler);
	}
}
