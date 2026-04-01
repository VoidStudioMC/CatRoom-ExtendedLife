package catserver.server.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.Validate;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.v1_12_R1.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import java.util.List;

public class ModCustomCommand extends Command {
    private final ICommand vanillaCommand;

    public ModCustomCommand(ICommand command)
    {
        super(command.getName());
        this.vanillaCommand = command;
    }

    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Dummy method
        return false;
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull((Object)sender, "Sender cannot be null");
        Validate.notNull((Object)args, "Arguments cannot be null");
        Validate.notNull((Object)alias, "Alias cannot be null");
        return this.vanillaCommand.getTabCompletions(MinecraftServer.getServerInst(), this.getListener(sender), args, new BlockPos(0, 0, 0));
    }

    private ICommandSender getListener(final CommandSender sender) {
        return switch (sender) {
            case Player s -> ((CraftPlayer) s).getHandle();
            case BlockCommandSender s -> ((CraftBlockCommandSender) s).getTileEntity();
            case CommandMinecart s -> ((CraftMinecartCommand) s).getHandle().getCommandBlockLogic();
            case RemoteConsoleCommandSender ignored -> ((DedicatedServer) MinecraftServer.getServerInst()).rconConsoleSource;
            case ConsoleCommandSender s -> ((CraftServer) s.getServer()).getServer();
            case ProxiedCommandSender s -> ((ProxiedNativeCommandSender) s).getHandle();
            default -> throw new IllegalArgumentException("Cannot make " + sender + " a vanilla command listener");
        };
    }
}