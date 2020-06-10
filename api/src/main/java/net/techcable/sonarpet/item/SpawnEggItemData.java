package net.techcable.sonarpet.item;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;

public interface SpawnEggItemData extends ItemData {

    EntityType DEFAULT_TYPE = EntityType.PIG;

    default SpawnEggItemData withSpawnedType(EntityType entityType) {
        Preconditions.checkNotNull(entityType, "Null type");
        return getSpawnedType() == entityType ? this : create(entityType, getMeta());
    }

    EntityType getSpawnedType();

    static SpawnEggItemData create(EntityType entityType) {
        return create(entityType, Bukkit.getItemFactory().getItemMeta(Material.MONSTER_EGG));
    }

    static SpawnEggItemData create(EntityType entityType, ItemMeta meta) {
        return ItemDataFactory.getInstance().createSpawnEggData(entityType, meta);
    }
}
