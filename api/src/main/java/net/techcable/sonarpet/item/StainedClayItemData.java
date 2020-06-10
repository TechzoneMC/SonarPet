package net.techcable.sonarpet.item;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

public interface StainedClayItemData extends ItemData, ColoredItemData {

    DyeColor getColor();

    StainedClayItemData withColor(DyeColor color);

    static StainedClayItemData create(DyeColor color) {
        return create(color, Bukkit.getItemFactory().getItemMeta(Material.STAINED_CLAY));
    }

    static StainedClayItemData create(DyeColor color, ItemMeta meta) {
        return ItemDataFactory.getInstance().createStainedClay(color, meta);
    }
}
