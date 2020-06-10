package net.techcable.sonarpet.item.legacy;

import com.google.common.base.Preconditions;
import net.techcable.sonarpet.item.StainedClayItemData;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LegacyStainedClayItemData extends LegacyItemData implements StainedClayItemData {

    protected LegacyStainedClayItemData(LegacyItemDataFactory factory, byte rawData, ItemMeta meta) {
        super(factory, Material.STAINED_CLAY, rawData, meta);
    }

    @SuppressWarnings("deprecation")
    public DyeColor getColor() {
        return DyeColor.getByWoolData(getRawData());
    }

    @SuppressWarnings("deprecation")
    public LegacyStainedClayItemData withColor(DyeColor color) {
        return withRawData(Preconditions.checkNotNull(color, "Null color").getWoolData());
    }

    public LegacyStainedClayItemData withRawData(int rawData) {
        return (LegacyStainedClayItemData) super.withRawData(rawData);
    }

}
