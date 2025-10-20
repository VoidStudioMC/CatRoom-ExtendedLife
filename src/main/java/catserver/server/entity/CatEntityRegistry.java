package catserver.server.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.GameData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CatEntityRegistry<K, V> extends RegistryNamespaced<K, V> {
    private final RegistryNamespaced<K, V> REGISTRY = new RegistryNamespaced<K, V>();

    public void register(int id, K key, V value) {
        REGISTRY.register(id, key, value);
    }

    @Nullable
    public V getObject(@Nullable K name)
    {
        EntityEntry entry = ForgeRegistries.ENTITIES.getValue((ResourceLocation) name);
        return entry == null ? REGISTRY.getObject(name) : (V) entry.getEntityClass();
    }

    @Nullable
    public K getNameForObject(V value)
    {
        EntityEntry entry = EntityRegistry.getEntry((Class<? extends Entity>) value);
        return entry == null ? REGISTRY.getNameForObject(value) : (K) entry.getRegistryName();
    }

    public boolean containsKey(K key)
    {
        return ForgeRegistries.ENTITIES.getValue((ResourceLocation) key) != null || REGISTRY.containsKey(key);
    }

    public int getIDForObject(@Nullable V value)
    {
        EntityEntry entry = EntityRegistry.getEntry((Class<? extends Entity>) value);
        return entry == null ? REGISTRY.getIDForObject(value) : GameData.getEntityRegistry().getID(entry);
    }

    @Nullable
    public V getObjectById(int id)
    {
        EntityEntry entry = GameData.getEntityRegistry().getValue(id);
        return entry == null ? REGISTRY.getObjectById(id) : (V) entry;
    }

    public Iterator<V> iterator()
    {
        List<V> list = new ArrayList<>();
        for (EntityEntry value : GameData.getEntityRegistry().getValues()) list.add((V) value);
        for (V value : REGISTRY) list.add(value);
        return list.iterator();
    }
}
