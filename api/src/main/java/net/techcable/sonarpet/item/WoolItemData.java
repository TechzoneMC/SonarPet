package net.techcable.sonarpet.item;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

public interface WoolItemData extends ItemData, ColoredItemData {

    DyeColor getColor();

    WoolItemData withColor(DyeColor color);

    static WoolItemData create(DyeColor color) {
        return create(color, Bukkit.getItemFactory().getItemMeta(Material.WOOL));
    }

    static WoolItemData create(DyeColor color, ItemMeta meta) {
        return ItemDataFactory.getInstance().createWool(color, meta);
    }
}
