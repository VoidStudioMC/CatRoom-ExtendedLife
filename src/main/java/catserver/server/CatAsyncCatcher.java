package catserver.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.v1_12_R1.util.Waitable;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class CatAsyncCatcher {
    public static boolean enabled = CatServer.getConfig().enableAsyncCatcher;

    public static boolean isMainThread() {
        return Thread.currentThread() == MinecraftServer.getServerInst().primaryThread;
    }

    public static void catchOp(String reason) {
        if (enabled && !isMainThread()) {
            throw new IllegalStateException( "Asynchronous " + reason + "!" );
        }
    }

    public static boolean checkAsync(String reason) {
        if (CatAsyncCatcher.enabled && !isMainThread()) {
            if (!CatServer.getConfig().disableAsyncCatchWarn) {
                CatServer.log.warn("A Mod/Plugin try to async " + reason + ", it will be executed safely on the main server thread until return!");
                CatServer.log.warn("Please check the stacktrace in debug.log and report the author.");
            }
            CatServer.log.debug("Try to async " + reason, new Throwable());
            return true;
        }
/*        if (CatServer.getConfig().disableAsyncCatcher) {
            if (!CatServer.getConfig().disableAsyncCatchWarn && !isMainThread()) {
                CatServer.log.warn("A Mod/Plugin try to async " + reason + ", async catcher is disabled!");
                CatServer.log.warn("Please check the stacktrace in debug.log and report the author.");
                CatServer.log.debug("Try to async " + reason, new Throwable());
            }
        }*/ // CREF: i don't understand why this is necessary
        return false;
    }

    public static void ensureExecuteOnPrimaryThread(Runnable runnable) {
        ensureExecuteOnPrimaryThread(() -> { runnable.run(); return null; });
    }

    public static <T> T ensureExecuteOnPrimaryThread(Supplier<T> runnable) {
        Waitable<T> waitable = new Waitable<T>() {
            @Override
            protected T evaluate() {
                return runnable.get();
            }
        };
        MinecraftServer.getServerInst().processQueue.add(waitable);
        try {
            return waitable.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkAndPostPrimaryThread(String reason, Runnable runnable) {
        if (checkAsync(reason)) {
            MinecraftServer.getServerInst().processQueue.add(runnable);
            return true;
        }
        return false;
    }

    public static Chunk asyncLoadChunkCaught(World world, AnvilChunkLoader loader, ChunkProviderServer provider, int x, int z) {
        if (ForgeChunkManager.asyncChunkLoading) {
            Waitable<Chunk> waitable = new Waitable<Chunk>() {
                @Override
                protected Chunk evaluate() {
                    return provider.getChunkIfLoaded(x, z);
                }
            };

            ChunkIOExecutor.queueChunkLoad(world, loader, provider, x, z, waitable);

            try {
                return waitable.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else {
            return ensureExecuteOnPrimaryThread(() -> ChunkIOExecutor.syncChunkLoad(world, loader, provider, x, z));
        }
    }
}
