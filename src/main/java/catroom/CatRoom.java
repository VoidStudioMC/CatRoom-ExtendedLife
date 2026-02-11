package catroom;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.world.WorldServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;

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
			server.getPluginManager().callEvent(new org.bukkit.event.world.WorldLoadEvent(world.getWorld()));
		}
	}
}
