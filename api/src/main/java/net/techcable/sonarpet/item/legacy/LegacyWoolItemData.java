package net.techcable.sonarpet.item.legacy;

import com.google.common.base.Preconditions;
import net.techcable.sonarpet.item.ColoredItemData;
import net.techcable.sonarpet.item.ItemData;
import net.techcable.sonarpet.item.WoolItemData;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

public class LegacyWoolItemData extends LegacyItemData implements WoolItemData, ColoredItemData {

    protected LegacyWoolItemData(LegacyItemDataFactory factory, byte rawData, ItemMeta meta) {
        super(factory, Material.WOOL, rawData, meta);
    }

    @SuppressWarnings("deprecation")
    public DyeColor getColor() {
        return DyeColor.getByWoolData(getRawData());
    }

    @SuppressWarnings("deprecation")
    public LegacyWoolItemData withColor(DyeColor color) {
        return withRawData(Preconditions.checkNotNull(color, "Null color").getWoolData());
    }

    public LegacyWoolItemData withRawData(int rawData) {
        return (LegacyWoolItemData) super.withRawData(rawData);
    }
}
