package net.techcable.sonarpet.item;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

public interface DyeItemData extends ItemData, ColoredItemData {
    DyeColor getColor();

    DyeItemData withColor(DyeColor color);

    static DyeItemData create(DyeColor color) {
        return create(color, Bukkit.getItemFactory().getItemMeta(Material.INK_SACK));
    }

    static DyeItemData create(DyeColor color, ItemMeta meta) {
        return ItemDataFactory.getInstance().createDye(color, meta);
    }
}
