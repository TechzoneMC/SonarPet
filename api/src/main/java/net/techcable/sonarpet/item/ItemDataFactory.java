package net.techcable.sonarpet.item;

import net.techcable.sonarpet.nms.INMS;
import net.techcable.sonarpet.utils.ModernSpawnEggs;
import net.techcable.sonarpet.utils.PlayerProfile;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.SpawnEgg;

public interface ItemDataFactory {
    ItemData create(Material type, ItemMeta meta);

    DyeItemData createDye(DyeColor color, ItemMeta meta);
    StainedClayItemData createStainedClay(DyeColor color, ItemMeta meta);
    WoolItemData createWool(DyeColor color, ItemMeta meta);

    /**
     * Parse an `ItemData` from its serialized string form.
     * This is the deserializer counterpart to {@link ItemData#serializeAsString()}
     *
     * @param s the serialized string to parse
     * @throws IllegalArgumentException if the data is invalid
     * @return the deserialized data
     */
    ItemData parseFromString(String s);

    static ItemDataFactory getInstance() {
        return INMS.getInstance().getItemDataFactory();
    }

    SkullItemData createHumanSkull(PlayerProfile owner, ItemMeta rawData);
    SkullItemData createSkull(SkullItemData.SkullType skullType, ItemMeta rawData);
    SpawnEggItemData createSpawnEggData(EntityType entityType, ItemMeta meta);
}
