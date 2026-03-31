package catroom;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CatRoom {
	public static final Map<String, String> forgeCommandPerms = new ConcurrentHashMap<>();

	public static String getForgeCommandPermission(ICommand command) {
		return command instanceof CommandBase ? ((CommandBase) command).permissionNode :
			command.getClass().getName().substring(command.getClass().getName().lastIndexOf('.') + 1).toLowerCase() + "." + command.getName();
	}

	public static void callBukkitWorldLoadEvent(CraftServer server, List<WorldServer> worldServerList) { // fix Nether-API
		for (WorldServer world : worldServerList) {
			server.getPluginManager().callEvent(new WorldLoadEvent(world.getWorld()));
		}
	}

	public static boolean hasPlayerPermission(EntityPlayerMP player, String commandName) { // hmmm, should i move this method to another place?..
		CraftPlayer bukkitPlayer = player.getBukkitEntity();

		return bukkitPlayer.hasPermission("minecraft.command." + commandName) || bukkitPlayer.hasPermission(commandName) ||
			(forgeCommandPerms.containsKey(commandName) && bukkitPlayer.hasPermission(forgeCommandPerms.get(commandName)));
	}
}
