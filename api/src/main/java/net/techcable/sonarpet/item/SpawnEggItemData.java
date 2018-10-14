package net.techcable.sonarpet.item;

import com.google.common.base.Preconditions;
import net.techcable.sonarpet.nms.INMS;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class SpawnEggItemData extends ItemData {

    public static final EntityType DEFAULT_TYPE = EntityType.PIG;

    protected SpawnEggItemData(Material m, byte rawData, ItemMeta meta) {
        super(m, rawData, meta);
    }

    public SpawnEggItemData withSpawnedType(EntityType entityType) {
        Preconditions.checkNotNull(entityType, "Null type");
        return getSpawnedType() == entityType ? this : create(entityType, getMeta());
    }

    public abstract EntityType getSpawnedType();

    public static SpawnEggItemData create(EntityType entityType) {
        return create(entityType, Bukkit.getItemFactory().getItemMeta(Material.MONSTER_EGG));
    }

    public static SpawnEggItemData create(EntityType entityType, ItemMeta meta) {
        return INMS.getInstance().createSpawnEggData(entityType, meta);
    }
}
