/**
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.mcreator.fragmentsintime as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package net.hawthorn.dndsheets;

import com.google.gson.*;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.hawthorn.dndsheets.network.SheetClientMessage;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SheetLoader {

	public static final Path GAME_DIR = FMLPaths.GAMEDIR.get();
	public static final Path SHEETS_DIR = GAME_DIR.resolve("charactersheets");
	public static HashMap<String, JsonObject> sheets = new HashMap<String, JsonObject>(); //A list of all loaded character sheets.
	private static JsonObject current = null; //Currently active character sheet. Important for populating GUIs when they're opened and knowing which to save to.

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(new SheetLoader());
	}

	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		//There isn't much of a need to do anything here. From here, we can expect the client to be handed the character sheet associated with it through ClientSheetMessage.
		//After that, the client can update the server on its sheet through ServerSheetMessage.
		//The sheets need to be kept on the server in the first place for the /roll command and roll buttons to work (since those send serverwide messages).

	}

	@SubscribeEvent
	public void clientJoinedServer(EntityJoinLevelEvent event) {
		//This needs to do two things:
		//1. When a player joins, it should check for their sheet and then give them a packet with it.
		//2. If the player doesn't have one on the server, it'll make one with their UUID first and THEN give the packet.
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) return;

		UUID uuid = entity.getUUID();
		String uuidString = uuid.toString();
		load();
		if (SheetLoader.getServerSheet(uuidString) == null) {
			makeNew("New Sheet", uuidString);
		};

		try {
			Supplier<ServerPlayer> serverPlayer = () -> entity.getServer().getPlayerList().getPlayer(uuid);
			byte[] data = SheetLoader.getServerSheet(uuidString).toString().getBytes();
			DndsheetsMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(serverPlayer), new SheetClientMessage(data));
			System.out.println("are you winning son");
		}
		catch(Exception e) {
			System.out.println(e.toString());
		}

	}

	@SubscribeEvent
	public static void serverLoad(FMLDedicatedServerSetupEvent event) {
		//This sets up the SheetLoader on the server side via loading all the sheets saved there.
		load();
	}

	public static JsonObject getClientSheet() {
		if (current == null) {
			System.out.println("Client sheet returned null. Are you sure you're not calling this from the server side?");
		}
		return current;
	}

	public static JsonObject getServerSheet(String uuid) {
		if (sheets.containsKey(uuid)) {
			return sheets.get(uuid);
		}
		else {
			System.out.println("Server character sheet retrieval failed. Make sure the UUID is correct and that you're not calling this from the client.");
			return null;
		}
	}

	//Save the given sheet into a JSON file, making a new one if it doesn't exist, and updates the "sheets" HashMap.
	public static void saveServer(JsonObject sheet, String uuid) {
		sheets.put(uuid, sheet);
		
		Path file = SHEETS_DIR.resolve(uuid + ".json").toAbsolutePath();
		
		try {
			Files.createDirectories(SHEETS_DIR);
			boolean overwritten = Files.deleteIfExists(file);
			Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
			String prettyJson = prettyGson.toJson(sheet);
			
			try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
				out.write(prettyJson.getBytes());
			}
		} catch (IOException e) { 
			//we fucked up
		}
	}

	//Loads each JSON file under the "charactersheets" folder in the Minecraft instance into JSON objects, filling the "sheets" HashMap.
	private static void load() {
		sheets = new HashMap<String, JsonObject>();
		ArrayList<Path> files = new ArrayList<Path>();
		
		try {
			Files.createDirectories(SHEETS_DIR);
			try (Stream<Path> paths = Files.walk(SHEETS_DIR)) {
			    paths.filter(f -> !Files.isDirectory(f) && f.getFileName().toString().endsWith(".json"))
			    .forEach(path -> {
			    	if (Files.isDirectory(path))
						return;

					files.add(path);
			    });
			} 
		} catch (IOException e) { 
			//we fucked up
		}

		try {
			for (Path path : files) {
				InputStream in = Files.newInputStream(path, StandardOpenOption.READ);
				Scanner s = new Scanner(in).useDelimiter("\\A");
				String result = s.hasNext() ? s.next() : "";
				JsonObject json = JsonParser.parseString(result).getAsJsonObject();
				sheets.put(path.getFileName().normalize().toString().replace(".json",""), json);
			}
		} catch (IOException e) { 
			//we fucked up
		}
	}

	/**
	 * <p>This method validates a character sheet JsonObject. This essentially makes sure it has the expected properties and fixes it if it doesn't.</p>
	 * @param sheet
	 */
	public static void validateSheet(JsonObject sheet) {

		//Checking basics
		if (!sheet.has("characterName")) sheet.addProperty("characterName", "John Doe");
		if (!sheet.has("strength")) sheet.addProperty("strength", "10");
		if (!sheet.has("dexterity")) sheet.addProperty("dexterity", "10");
		if (!sheet.has("constitution")) sheet.addProperty("constitution", "10");
		if (!sheet.has("intelligence")) sheet.addProperty("intelligence", "10");
		if (!sheet.has("wisdom")) sheet.addProperty("wisdom", "10");
		if (!sheet.has("charisma")) sheet.addProperty("charisma", "10");
		if (!sheet.has("proficiencyBonus")) sheet.addProperty("proficiencyBonus", "2");

		//Checking roll expressions
		if (!sheet.has("checks")) {
			JsonArray checks = new JsonArray();
			checks.add("1d20 + $str");
			checks.add("1d20 + $dex");
			checks.add("1d20 + $con");
			checks.add("1d20 + $int");
			checks.add("1d20 + $wis");
			checks.add("1d20 + $cha");
			sheet.add("checks", checks);
		}
		if (!sheet.has("saves")) {
			JsonArray saves = new JsonArray();
			saves.add("1d20 + $str");
			saves.add("1d20 + $dex");
			saves.add("1d20 + $con");
			saves.add("1d20 + $int");
			saves.add("1d20 + $wis");
			saves.add("1d20 + $cha");
			sheet.add("saves", saves);
		}
		if (!sheet.has("skills")) {
			JsonArray skills = new JsonArray();
			skills.add("1d20 + $str");
			skills.add("1d20 + $dex");
			skills.add("1d20 + $dex");
			skills.add("1d20 + $dex");
			skills.add("1d20 + $int");
			skills.add("1d20 + $int");
			skills.add("1d20 + $int");
			skills.add("1d20 + $int");
			skills.add("1d20 + $int");
			skills.add("1d20 + $wis");
			skills.add("1d20 + $wis");
			skills.add("1d20 + $wis");
			skills.add("1d20 + $wis");
			skills.add("1d20 + $wis");
			skills.add("1d20 + $cha");
			skills.add("1d20 + $cha");
			skills.add("1d20 + $cha");
			skills.add("1d20 + $cha");
			sheet.add("skills", skills);
		}
		if (!sheet.has("attacks")) {
			JsonArray attacks = new JsonArray();
			sheet.add("attacks", attacks);
		}
	}

	//Makes a new sheet, adds it to the "sheets" HashMap, and then calls Save() to make a file from it.
	public static void makeNew(String characterName, String uuid) {
		JsonObject newSheet = new JsonObject();
		newSheet.addProperty("characterName", characterName);
		newSheet.addProperty("strength", "10");
		newSheet.addProperty("dexterity", "10");
		newSheet.addProperty("constitution", "10");
		newSheet.addProperty("intelligence", "10");
		newSheet.addProperty("wisdom", "10");
		newSheet.addProperty("charisma", "10");
		newSheet.addProperty("proficiencyBonus", "2");

		JsonArray checks = new JsonArray();
		checks.add("1d20 + $str");
		checks.add("1d20 + $dex");
		checks.add("1d20 + $con");
		checks.add("1d20 + $int");
		checks.add("1d20 + $wis");
		checks.add("1d20 + $cha");

		JsonArray saves = new JsonArray();
		saves.add("1d20 + $str");
		saves.add("1d20 + $dex");
		saves.add("1d20 + $con");
		saves.add("1d20 + $int");
		saves.add("1d20 + $wis");
		saves.add("1d20 + $cha");

		JsonArray skills = new JsonArray();
		skills.add("1d20 + $str");
		skills.add("1d20 + $dex");
		skills.add("1d20 + $dex");
		skills.add("1d20 + $dex");
		skills.add("1d20 + $int");
		skills.add("1d20 + $int");
		skills.add("1d20 + $int");
		skills.add("1d20 + $int");
		skills.add("1d20 + $int");
		skills.add("1d20 + $wis");
		skills.add("1d20 + $wis");
		skills.add("1d20 + $wis");
		skills.add("1d20 + $wis");
		skills.add("1d20 + $wis");
		skills.add("1d20 + $cha");
		skills.add("1d20 + $cha");
		skills.add("1d20 + $cha");
		skills.add("1d20 + $cha");

		JsonArray attacks = new JsonArray();

		newSheet.add("checks", checks);
		newSheet.add("saves", saves);
		newSheet.add("skills", skills);
		newSheet.add("attacks", attacks);

		saveServer(newSheet, uuid);
	}

	//Sets a new "current" sheet from the "sheets" HashSet. Ideally some GUI letting you choose from the loaded list will call this.
	public static void setClient(JsonObject sheet) {
		current = sheet;
		//lol it's really that simple
	}



}
