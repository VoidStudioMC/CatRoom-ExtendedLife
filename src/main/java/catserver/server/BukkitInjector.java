package catserver.server;

import catserver.server.entity.CraftCustomEntity;
import com.cleanroommc.hackery.enums.EnumHackery;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.apache.logging.log4j.Level;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.permissions.DefaultPermissions;

import java.util.Map;

public class BukkitInjector {
    public static boolean initializedBukkit = false;

    public static void injectItemBukkitMaterials() {
        for (Map.Entry<ResourceLocation, Item> entry : ForgeRegistries.ITEMS.getEntries()) {
            ResourceLocation key = entry.getKey();
            Item item = entry.getValue();
            if (!key.getNamespace().equals("minecraft")) {
                String materialName = key.toString().toUpperCase().replaceAll("(:|\\s)", "_").replaceAll("\\W", "");
                int itemId = Item.getIdFromItem(item);
                Material material = Material.addMaterial(EnumHackery.addEnumEntry(Material.class, materialName, new Class[]{int.class, int.class, Material.MaterialType.class}, new Object[]{itemId, item.getItemStackLimit(), Material.MaterialType.MOD_ITEM}));
                if (material != null) {
                    FMLLog.log(Level.DEBUG, "Injected new Forge item material %s with ID %d.", material.name(), material.getId());
                } else {
                    FMLLog.log(Level.INFO, "Inject item failure %s with ID %d.", materialName, itemId);
                }
            }
        }
    }

    public static void injectBlockBukkitMaterials() {
        for (Material material : Material.values()) {
            if (material.getId() < 256)
                Material.addBlockMaterial(material);
        }
        for (Map.Entry<ResourceLocation, Block> entry : ForgeRegistries.BLOCKS.getEntries()) {
            ResourceLocation key = entry.getKey();
            Block block = entry.getValue();
            if (!key.getNamespace().equals("minecraft")) {
                String materialName = key.toString().toUpperCase().replaceAll("(:|\\s)", "_").replaceAll("\\W", "");
                int blockId = Block.getIdFromBlock(block);
                Material material = Material.addBlockMaterial(EnumHackery.addEnumEntry(
                        Material.class,
                        materialName,
                        new Class[]{int.class, Material.MaterialType.class},
                        new Object[]{blockId, Material.MaterialType.MOD_BLOCK})
                );
                if (material != null) {
                    FMLLog.log(Level.DEBUG, "Injected new Forge block material %s with ID %d.", material.name(), material.getId());
                } else {
                    if (blockId < 256) {
                        throw new RuntimeException("Can't inject Forge block material. Registry remap is not support! (level.dat is from the old version or corrupted)");
                    }
                    FMLLog.log(Level.INFO, "Inject block failure %s with ID %d.", materialName, blockId);
                }
            }
        }
    }

    public static void injectBiomes() {
        for1:
        for (Map.Entry<ResourceLocation, net.minecraft.world.biome.Biome> entry : ForgeRegistries.BIOMES.getEntries()) {
            String biomeName = entry.getKey().getPath().toUpperCase(java.util.Locale.ENGLISH);
            for (Biome biome : Biome.values()) {
                if (biome.toString().equals(biomeName)) continue for1;
            }
            EnumHackery.addEnumEntry(Biome.class, biomeName, new Class[]{}, new Object[]{});
        }
    }

    public static void injectEntityType() {
        Map<String, EntityType> NAME_MAP = ReflectionHelper.getPrivateValue(EntityType.class, null, "NAME_MAP");
        Map<Short, EntityType> ID_MAP = ReflectionHelper.getPrivateValue(EntityType.class, null, "ID_MAP");

        for (Map.Entry<String, Class<? extends Entity>> entity : EntityRegistry.entityClassMap.entrySet()) {
            String name = entity.getKey();
            String entityType = name.replace("-", "_").toUpperCase();
            int typeId = GameData.getEntityRegistry().getID(EntityRegistry.getEntry(entity.getValue()));
            EntityType bukkitType = EnumHackery.addEnumEntry(EntityType.class, entityType, new Class[]{String.class, Class.class, Integer.TYPE, Boolean.TYPE}, new Object[]{name, CraftCustomEntity.class, typeId, false});

            NAME_MAP.put(name.toLowerCase(), bukkitType);
            ID_MAP.put((short) typeId, bukkitType);
        }
    }

    public static void registerEnchantments() {
        for (Object enchantment : Enchantment.REGISTRY) {
            org.bukkit.enchantments.Enchantment.registerEnchantment(new org.bukkit.craftbukkit.v1_12_R1.enchantments.CraftEnchantment((Enchantment) enchantment));
        }
        org.bukkit.enchantments.Enchantment.stopAcceptingRegistrations();
    }

    public static void registerPotions() {
        for (Object effect : Potion.REGISTRY) {
            PotionEffectType.registerPotionEffectType(new org.bukkit.craftbukkit.v1_12_R1.potion.CraftPotionEffectType((Potion) effect));
        }
        PotionEffectType.stopAcceptingRegistrations();
    }

    public static void registerBannerPatterns() {
        Map<String, PatternType> PATTERN_MAP = ReflectionHelper.getPrivateValue(PatternType.class, null, "byString");
        for (BannerPattern bannerPattern : BannerPattern.values()) {
            String bannerPatternName = bannerPattern.name();
            String bannerPatterKey = bannerPattern.getHashname();
            if (PatternType.getByIdentifier(bannerPatterKey) == null) {
                PatternType patternType = EnumHackery.addEnumEntry(PatternType.class, bannerPatternName, new Class[]{String.class}, new Object[]{bannerPatterKey});
                if (patternType != null) {
                    PATTERN_MAP.put(bannerPatterKey, patternType);
                }
            }
        }
    }

    public static void registerDefaultPermission(String name, DefaultPermissionLevel level, String desc) {
        PermissionDefault permissionDefault = switch (level) {
            case ALL -> PermissionDefault.TRUE;
            case OP -> PermissionDefault.OP;
            default -> PermissionDefault.FALSE;
        };
        Permission permission = new Permission(name, desc, permissionDefault);
        DefaultPermissions.registerPermission(permission);
    }
}
