
package net.hawthorn.dndsheets.command;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.common.util.FakePlayerFactory;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.Commands;
import net.hawthorn.dndsheets.SheetLoader;
import net.hawthorn.dndsheets.procedures.RollAnnouncerProcedure;

import com.google.gson.JsonObject;

@Mod.EventBusSubscriber
public class RollCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("roll")
			.then(Commands.argument("expression", MessageArgument.message()).executes(arguments -> {
				Level world = arguments.getSource().getUnsidedLevel();
				double x = arguments.getSource().getPosition().x();
				double y = arguments.getSource().getPosition().y();
				double z = arguments.getSource().getPosition().z();
				Entity entity = arguments.getSource().getEntity();
				if (entity == null && world instanceof ServerLevel _servLevel)
					entity = FakePlayerFactory.getMinecraft(_servLevel);
				Direction direction = Direction.DOWN;
				if (entity != null)
					direction = entity.getDirection();
				String uuid = entity.getStringUUID();

				JsonObject sheet = SheetLoader.getServerSheet(uuid);
				RollAnnouncerProcedure.execute(world, x, y, z, sheet, arguments);
				return 0;
			})));
				
		event.getDispatcher().register(Commands.literal("r")
			.then(Commands.argument("expression", MessageArgument.message()).executes(arguments -> {
				Level world = arguments.getSource().getUnsidedLevel();
				double x = arguments.getSource().getPosition().x();
				double y = arguments.getSource().getPosition().y();
				double z = arguments.getSource().getPosition().z();
				Entity entity = arguments.getSource().getEntity();
				if (entity == null && world instanceof ServerLevel _servLevel)
					entity = FakePlayerFactory.getMinecraft(_servLevel);
				Direction direction = Direction.DOWN;
				if (entity != null)
					direction = entity.getDirection();
				String uuid = entity.getStringUUID();

				JsonObject sheet = SheetLoader.getServerSheet(uuid);
				RollAnnouncerProcedure.execute(world, x, y, z, sheet, arguments);
				return 0;
			})));

	}
}
