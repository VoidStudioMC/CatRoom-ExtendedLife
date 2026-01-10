package catroom;

import net.minecraft.world.WorldServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;

import java.util.List;

public class CatRoom {
	public static void callBukkitWorldLoadEvent(CraftServer server, List<WorldServer> worldServerList) { // fix Nether-API
		for (WorldServer world : worldServerList) {
			server.getPluginManager().callEvent(new org.bukkit.event.world.WorldLoadEvent(world.getWorld()));
		}
	}
}
