package net.techcable.sonarpet.item.legacy;

import com.google.common.base.Preconditions;
import net.techcable.sonarpet.item.*;
import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.PlayerProfile;
import net.techcable.sonarpet.utils.Versioning;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.SpawnEgg;

public class LegacyItemDataFactory implements ItemDataFactory {
    public LegacyItemDataFactory() {
        NmsVersion.ensureBefore(NmsVersion.v1_13_R2);
    }

    @Override
    public ItemData parseFromString(String s) {
        BasicSerializedItem data = BasicSerializedItem.deserializeFromString(s);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(data.getType());
        if (!data.getLore().isEmpty()) {
            meta.setLore(data.getLore());
        }
        if (data.getDisplayName() != null) {
            meta.setDisplayName(data.getDisplayName());
        }
        return create(data.getType(), data.getRawData(), meta);
    }

    LegacyItemData create(Material type, int rawData, ItemMeta meta) {
        Preconditions.checkNotNull(type, "Null type");
        Preconditions.checkArgument((byte) rawData == rawData, "Raw data doesn't fit in byte: %s", rawData);
        Preconditions.checkNotNull(meta, "Null item meta");
        switch (type) {
            case INK_SACK:
                return new LegacyDyeItemData(this, (byte) rawData, meta);
            case SKULL_ITEM:
                return new LegacySkullItemData(this, (byte) rawData, meta);
            case STAINED_CLAY:
                return new LegacyStainedClayItemData(this, (byte) rawData, meta);
            case WOOL:
                return new LegacyWoolItemData(this, (byte) rawData, meta);
            case MONSTER_EGG:
                // We expect this to be overridden, so we leave a hook
                return (LegacyItemData) createSpawnEggData((byte) rawData, meta);
            default:
                return new LegacyItemData(this, type, (byte) rawData, meta);
        }
    }

    private SpawnEggItemData createSpawnEggData(byte rawData, ItemMeta meta) {
        EntityType entityType = new SpawnEgg(rawData).getSpawnedType();
        if (entityType == null) entityType = SpawnEggItemData.DEFAULT_TYPE;
        return createSpawnEggData(entityType, meta); // Convert raw data to entity type
    }

    @Override
    public ItemData create(Material type, ItemMeta meta) {
        return create(type, 0, meta);
    }

    @Override
    public LegacyDyeItemData createDye(DyeColor color, ItemMeta meta) {
        byte data = Preconditions.checkNotNull(color, "Null color").getDyeData();
        return new LegacyDyeItemData(this, data, meta);
    }

    @Override
    public StainedClayItemData createStainedClay(DyeColor color, ItemMeta meta) {
        byte data = Preconditions.checkNotNull(color, "Null color").getWoolData();
        return new LegacyStainedClayItemData(this, data, it);
    }

    @Override
    public WoolItemData createWool(DyeColor color, ItemMeta meta) {
        byte data = Preconditions.checkNotNull(color, "Null color").getWoolData();
        return new LegacyWoolItemData(this, data, it);
    }

    @Override
    public LegacySkullItemData createHumanSkull(PlayerProfile owner, ItemMeta rawMeta) {
        Preconditions.checkNotNull(rawMeta, "Null meta");
        Preconditions.checkNotNull(owner, "Null owner");
        SkullMeta meta;
        if (rawMeta instanceof SkullMeta) {
            meta = (SkullMeta) rawMeta;
        } else {
            meta = (SkullMeta) Bukkit.getItemFactory().asMetaFor(rawMeta, Material.SKULL_ITEM);
        }
        meta = meta.clone(); // Don't modify their junk
        meta.setOwner(owner.getName());
        return createSkull(SkullItemData.SkullType.HUMAN_SKULL, meta);
    }

    @Override
    public LegacySkullItemData createSkull(SkullItemData.SkullType type, ItemMeta rawData) {
        return new LegacySkullItemData(
                this,
                Preconditions.checkNotNull(type, "Null skull type").getId(),
                rawData
        );
    }

    @Override
    public SpawnEggItemData createSpawnEggData(EntityType entityType, ItemMeta meta) {
        Preconditions.checkNotNull(meta, "Null meta");
        Preconditions.checkNotNull(entityType, "Null entity type");
        return new LegacySpawnEggItemData(this, Material.LEGACY_MONSTER_EGG, new SpawnEgg(entityType).getData(), meta) {
            @Override
            @SuppressWarnings("depreciation") // Bukkit is okay on versions less than 1.9, and we've already checked above
            public EntityType getSpawnedType() {
                return ((SpawnEgg) getLegacyMaterialData()).getSpawnedType();
            }
        };
    }
}
