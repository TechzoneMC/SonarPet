package net.techcable.sonarpet.item.legacy;

import com.google.common.base.Preconditions;
import net.techcable.sonarpet.item.ColoredItemData;
import net.techcable.sonarpet.item.DyeItemData;
import net.techcable.sonarpet.item.ItemData;
import net.techcable.sonarpet.item.ItemDataFactory;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

public class LegacyDyeItemData extends LegacyItemData implements DyeItemData {

    /* package */ LegacyDyeItemData(LegacyItemDataFactory factory, byte rawData, ItemMeta meta) {
        super(factory, Material.INK_SACK, rawData, meta);
    }

    @SuppressWarnings("deprecation")
    public DyeColor getColor() {
        return DyeColor.getByDyeData(getRawData());
    }

    @SuppressWarnings("deprecation")
    public LegacyDyeItemData withColor(DyeColor color) {
        return withRawData(Preconditions.checkNotNull(color, "Null color").getDyeData());
    }

    public LegacyDyeItemData withRawData(int rawData) {
        return (LegacyDyeItemData) super.withRawData(rawData);
    }
}
