package net.techcable.sonarpet.item.legacy;

import net.techcable.sonarpet.item.SpawnEggItemData;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class LegacySpawnEggItemData extends LegacyItemData implements SpawnEggItemData {
    protected LegacySpawnEggItemData(
            LegacyItemDataFactory factory,
            Material m,
            byte rawData,
            ItemMeta meta
    ) {
        super(factory, m, rawData, meta);
    }
}
