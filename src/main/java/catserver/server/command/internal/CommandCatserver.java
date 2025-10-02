package catserver.server.command.internal;

import catserver.server.CatServer;
import catserver.server.utils.ItemStackUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.spigotmc.SneakyThrow;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public class CommandCatserver extends Command {
    public CommandCatserver(String name) {
        super(name);
        this.description = "CatServer related commands";
        this.usageMessage = "/catserver worlds|reload|reloadall|dumpitem|dumplisteners";
        setPermission("catserver.command.catserver");
    }

    private static final MethodHandle EVENT_TYPES_HANDLE;

    static {
        try {
            final Field eventTypesField = HandlerList.class.getDeclaredField("EVENT_TYPES");
            eventTypesField.setAccessible(true);
            EVENT_TYPES_HANDLE = MethodHandles.lookup().unreflectGetter(eventTypesField);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "worlds" -> {
                sender.sendMessage(formatStringLength("Dim", 8) + " " + formatStringLength("Name", 8) + " " + formatStringLength("Type", 8));
                for (Integer dimension : DimensionManager.getStaticDimensionIDs()) {
                    World world = DimensionManager.getWorld(dimension, false);
                    String name = (world != null ? world.getWorld().getName() : "(Unload)");
                    String type = DimensionManager.getProviderType(dimension).toString();
                    sender.sendMessage(formatStringLength(String.valueOf(dimension), 8) + " " + formatStringLength(name, 8) + " " + formatStringLength(type, 8));
                }
            }
            case "reload" -> {
                CatServer.getConfig().loadConfig();
                sender.sendMessage(ChatColor.GREEN + "Configuration reload complete.");
            }
            case "reloadall" -> {
                CatServer.getConfig().loadConfig();
                ((CraftServer) Bukkit.getServer()).reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Configuration reload complete.");
            }
            case "dumpitem" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                    return true;
                }
                var itemInHand = ((CraftPlayer) player).getHandle().getHeldItemMainhand();
                if (itemInHand.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "You are not holding any item.");
                    return true;
                }
                sender.sendMessage(ItemStackUtils.formatItemStackToPrettyString(itemInHand));
                TextComponent message = new TextComponent("[Click to insert give command]");
                message.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ItemStackUtils.itemStackToGiveCommand(itemInHand)));
                sender.spigot().sendMessage(message);
            }
            case "dumplisteners" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /catserver dumplisteners tofile");
                    return true;
                }
                if (args[1].equals("tofile")) {
                    this.dumpToFile(sender);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /catserver dumplisteners tofile");
                }
            }
        }

        return true;
    }

    private static String formatStringLength(String str, int size) {
        int formatLength = size - str.length();
        for (int i = 0; i < formatLength; i++) {
            str += " ";
        }
        return str;
    }

    private void dumpToFile(final CommandSender sender) {
        final File file = new File("debug/listeners-"
                + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss").format(LocalDateTime.now()) + ".txt");
        file.getParentFile().mkdirs();
        try (final PrintWriter writer = new PrintWriter(file)) {
            for (final String eventClass : eventClassNames()) {
                final HandlerList handlers;
                try {
                    handlers = (HandlerList) findClass(eventClass).getMethod("getHandlerList").invoke(null);
                } catch (final ReflectiveOperationException e) {
                    continue;
                }
                if (handlers.getRegisteredListeners().length != 0) {
                    writer.println(eventClass);
                }
                for (final RegisteredListener registeredListener : handlers.getRegisteredListeners()) {
                    writer.println(" - " + registeredListener);
                }
            }
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        sender.sendMessage(ChatColor.GREEN + "Dumped listeners to " + file);
    }

    @SuppressWarnings("unchecked")
    private static Set<String> eventClassNames() {
        try {
            return (Set<String>) EVENT_TYPES_HANDLE.invokeExact();
        } catch (final Throwable e) {
            SneakyThrow.sneaky(e);
            return Collections.emptySet(); // Unreachable
        }
    }

    private static Class<?> findClass(final String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException ignore) {
            for (final Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
                if (!plugin.isEnabled()) {
                    continue;
                }

                try {
                    return Class.forName(className, false, plugin.getClass().getClassLoader());
                } catch (final ClassNotFoundException ignore0) {
                }
            }
        }
        throw new ClassNotFoundException(className);
    }
}
