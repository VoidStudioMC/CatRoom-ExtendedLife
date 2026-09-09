package catserver.server;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class WeakWorldSaveManager {
    private static final ObjectArrayFIFOQueue<WorldServer> saveTaskQueue = new ObjectArrayFIFOQueue<>();
    private static final ObjectArrayList<WorldServer> alreadySavedLagWorlds = new ObjectArrayList<>();
    private static long lastSaveTick = MinecraftServer.currentTick;

    public static void saveAllWorlds() {
        alreadySavedLagWorlds.clear();

        if (!saveTaskQueue.isEmpty()) {
            MinecraftServer.LOGGER.warn("[WeakWorldSaveManager] World auto save lag! Remaining count: {}", saveTaskQueue.size());
            WorldServer worldServer;
            while ((worldServer = saveTaskQueue.dequeue()) != null) {
                if (MinecraftServer.getServerInst().worldServerList.contains(worldServer) /* Is unloaded? */) {
                    MinecraftServer.LOGGER.warn("[WeakWorldSaveManager] Saving dimension: {}", worldServer.dimension);
                    try {
                        worldServer.saveAllChunks(true, null);
                    } catch (MinecraftException minecraftexception) {
                        MinecraftServer.LOGGER.warn(minecraftexception.getMessage());
                    }

                    alreadySavedLagWorlds.add(worldServer);
                }
            }
        }

        for (WorldServer worldServer : MinecraftServer.getServerInst().worldServerList) {
            if (worldServer != null) {
                if (alreadySavedLagWorlds.contains(worldServer)) continue;
                saveTaskQueue.enqueue(worldServer);
            }
        }
    }

    public static void onTick() {
        long startTime = System.nanoTime();
        while (!saveTaskQueue.isEmpty()) {
            WorldServer worldServer = saveTaskQueue.dequeue();
            if (worldServer != null && MinecraftServer.getServerInst().worldServerList.contains(worldServer) /* Is unloaded? */) {
                try {
                    worldServer.saveAllChunks(true, null);
                } catch (MinecraftException minecraftexception) {
                    MinecraftServer.LOGGER.warn(minecraftexception.getMessage());
                }
                long estimatedTime = System.nanoTime() - startTime;
                if (estimatedTime > 50000000L /* 50ms */) {
                    break;
                }
            }
        }
        lastSaveTick = MinecraftServer.currentTick;
    }

    public static boolean isNeedTick() {
        return !saveTaskQueue.isEmpty() && MinecraftServer.currentTick - lastSaveTick > 1 /* Idle one tick for working on other things */;
    }
}
