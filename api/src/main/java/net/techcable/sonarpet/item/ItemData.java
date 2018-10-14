package net.techcable.sonarpet.item;

import lombok.*;

import java.util.List;
import java.util.Optional;

import net.techcable.sonarpet.item.ModernMaterialSystem;
import net.techcable.sonarpet.nms.INMS;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.techcable.sonarpet.utils.ModernSpawnEggs;
import net.techcable.sonarpet.utils.NmsVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.MonsterEggs;

@Getter
public class ItemData {
    @NonNull
    private final Material type;
    private final byte rawData;
    @Getter(AccessLevel.NONE) // Doesn't make a defensive copy, which is nessicary as it is mutable
    private final ItemMeta meta;

    protected ItemData(Material type, byte rawData, ItemMeta meta) {
        this.type = Preconditions.checkNotNull(type, "Null type");
        this.rawData = rawData;
        Preconditions.checkNotNull(meta, "Null metadata");
        this.meta = Bukkit.getItemFactory().asMetaFor(meta, type).clone(); // Not guarnteed to copy ;)
    }

    public static ItemData create(MaterialData materialData) {
        Preconditions.checkNotNull(materialData, "Null material data");
        return create(materialData.getItemType(), materialData.getData());
    }


    public static ItemData create(MaterialData materialData, ItemMeta meta) {
        Preconditions.checkNotNull(materialData, "Null material data");
        return create(materialData.getItemType(), materialData.getData(), meta);
    }

    public static ItemData create(Material type) {
        return create(type, 0);
    }

    public static ItemData create(Material type, ItemMeta meta) {
        return create(type, 0, meta);
    }

    public static ItemData create(Material type, int rawData) {
        Preconditions.checkNotNull(type, "Null type");
        return create(type, rawData, Bukkit.getItemFactory().getItemMeta(type));
    }

    public static ItemData create(Material type, int rawData, ItemMeta meta) {
        Preconditions.checkNotNull(type, "Null type");
        Preconditions.checkArgument((byte) rawData == rawData, "Raw data doesn't fit in byte: %s", rawData);
        Preconditions.checkNotNull(meta, "Null item meta");
        if (ModernSpawnEggs.isSupported()) {
            if (ModernSpawnEggs.isSpawnEgg(type)) {
                return INMS.getInstance().createSpawnEggData(type, (byte) rawData, meta);
            }
        } else {
            // We're on an older version, where there is only one type of monster egg
            if (type == Material.MONSTER_EGG) {
                return INMS.getInstance().createSpawnEggData(type, (byte) rawData, meta);
            }
        }
        /*
         * NOTE: I'm keeping Material.MONSTER_EGG out of the switch
         * since it's deprecated on 1.13.
         * It'll now be conditional on whether we have 'modern' monster egg support
         * Furthermore we have to switch on the raw string,
         * since bukkit does some crazy madness with the ordinal.
         */
        switch (type.name()) {
            case "INK_SACK":
                return new DyeItemData((byte) rawData, meta);
            case "SKULL_ITEM":
                return new SkullItemData((byte) rawData, meta);
            case "STAINED_CLAY":
                return new StainedClayItemData((byte) rawData, meta);
            case "WOOL":
                return new StainedClayItemData((byte) rawData, meta);
            default:
                return new ItemData(type, (byte) rawData, meta);
        }
    }

    public ItemData withRawData(int rawData) {
        Preconditions.checkArgument((byte) rawData == rawData, "Raw data doesn't fit into byte: %s", rawData);
        return ItemData.create(this.type, rawData, this.meta);
    }

    public ItemData withMeta(ItemMeta meta) {
        Preconditions.checkNotNull(meta, "Null metadata");
        return meta.equals(this.meta) ? this : create(getType(), getRawData(), meta);
    }

    public ItemData withPlainMeta() {
        return create(getType(), getRawData());
    }

    public ItemMeta getMeta() {
        return meta.clone();
    }

    public ImmutableList<String> getLore() {
        if (meta.hasLore()) {
            return ImmutableList.copyOf(meta.getLore());
        } else {
            return ImmutableList.of();
        }
    }

    public Optional<String> getDisplayName() {
        return meta.hasDisplayName() ? Optional.of(meta.getDisplayName()) : Optional.empty();
    }

    public ItemData withDisplayName(Optional<String> name) {
        Preconditions.checkNotNull(name, "Null optional");
        ItemMeta meta = getMeta(); // Returns a copy ;)
        if (name.isPresent()) {
            Preconditions.checkArgument(!name.get().trim().isEmpty(), "Empty name '%s'", name.get());
            meta.setDisplayName(name.get());
        } else {
            meta.setDisplayName(null); // Undocumented behavior of awesomeness
        }
        return withMeta(meta);
    }

    public ItemData withDisplayName(String name) {
        return withDisplayName(Optional.of(Preconditions.checkNotNull(name, "Null name")));
    }

    public ItemData withLore(List<String> lore) {
        ItemMeta meta = getMeta(); // Returns a copy ;)
        meta.setLore(ImmutableList.copyOf(lore)); // Copy lore :D
        return withMeta(meta);
    }

    public ItemStack createStack(int amount) {
        ItemStack stack = new ItemStack(getType(), amount, getRawData());
        stack.setItemMeta(this.meta.clone());
        return stack;
    }

    public MaterialData getLegacyMaterialData() {
        NmsVersion.ensureBefore(NmsVersion.v1_13_R2);
        return this.type.getNewData(this.rawData);
    }

    public Object getModernBlockData() {
        NmsVersion.ensureAtLeast(NmsVersion.v1_13_R2);
        if (this.meta instanceof BlockStateMeta) {
            return ModernMaterialSystem.getDataFromState(
                    ((BlockStateMeta) this.meta).getBlockState()
            );
        } else {
            return ModernMaterialSystem.createDefaultBlockData(this.type);
        }
    }
}
