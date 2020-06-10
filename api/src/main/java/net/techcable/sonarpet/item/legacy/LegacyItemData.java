package net.techcable.sonarpet.item.legacy;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.techcable.sonarpet.block.BlockData;
import net.techcable.sonarpet.block.BlockDataFactory;
import net.techcable.sonarpet.item.ItemData;
import net.techcable.sonarpet.utils.NmsVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;

@Getter
public class LegacyItemData implements ItemData {
    private final LegacyItemDataFactory factory;
    @NonNull
    private final Material type;
    private final byte rawData;
    @Getter(AccessLevel.NONE) // Doesn't make a defensive copy, which is nessicary as it is mutable
    private final ItemMeta meta;

    /* package */ LegacyItemData(
            LegacyItemDataFactory factory,
            Material type,
            byte rawData,
            ItemMeta meta
    ) {
        NmsVersion.ensureBefore(NmsVersion.v1_13_R2);
        this.factory = Preconditions.checkNotNull(factory);
        this.type = Preconditions.checkNotNull(type, "Null type");
        this.rawData = rawData;
        Preconditions.checkNotNull(meta, "Null metadata");
        this.meta = Bukkit.getItemFactory().asMetaFor(meta, type).clone(); // Not guarnteed to copy ;)
    }

    public LegacyItemData withRawData(int rawData) {
        Preconditions.checkArgument((byte) rawData == rawData, "Raw data doesn't fit into byte: %s", rawData);
        return factory.create(this.type, rawData, this.meta);
    }

    public LegacyItemData withMeta(ItemMeta meta) {
        Preconditions.checkNotNull(meta, "Null metadata");
        return meta.equals(this.meta) ? this : factory.create(getType(), getRawData(), meta);
    }

    public LegacyItemData withPlainMeta() {
        return factory.create(getType(), getRawData(), getMeta());
    }

    public ItemMeta getMeta() {
        return meta.clone();
    }

    @Override
    public String serializeAsString() {
        /*
         * NOTE: Since we're on an old version,
         * we aren't expected to preserve anything beyond material,
         * rawData, lore, and displayName
         */
        return new BasicSerializedItem(
                this.type,
                this.rawData,
                this.getLore(),
                this.getDisplayName().orElse(null)
        ).serializeAsString();
    }

    public ItemStack createStack(int amount) {
        ItemStack stack = new ItemStack(getType(), amount, getRawData());
        stack.setItemMeta(this.meta.clone());
        return stack;
    }

    public MaterialData getLegacyMaterialData() {
        return this.type.getNewData(this.rawData);
    }

    @Nullable
    @Override
    public BlockData getBlockData() {
        if (this.getMeta() instanceof BlockStateMeta) {
            return BlockDataFactory.getInstance().fromState(
                    ((BlockStateMeta) this.getMeta()).getBlockState());
        } else if (this.getType().isBlock()) {
            return BlockDataFactory.getInstance().fromLegacyData(
                    this.getType(),
                    this.rawData
            );
        } else {
            return null;
        }
    }
}