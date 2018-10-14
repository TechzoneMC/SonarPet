package net.techcable.sonarpet.nms.versions.v1_11_R1.data;

import java.util.Optional;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_11_R1.EntityTypes;
import net.minecraft.server.v1_11_R1.Item;
import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.MinecraftKey;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.techcable.sonarpet.item.SpawnEggItemData;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftMagicNumbers;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;

public class NMSSpawnEggItemData extends SpawnEggItemData {
    public NMSSpawnEggItemData(byte rawData, ItemMeta meta) {
        this(rawData, meta, SpawnEggItemData.DEFAULT_TYPE);
    }

    public NMSSpawnEggItemData(byte rawData, ItemMeta meta, EntityType type) {
        super(Material.MONSTER_EGG, rawData, createMetaWithEntityType(meta, type));
    }

    @Override
    public EntityType getSpawnedType() {
        return getSpawnEggEntityType(this.getMeta());
    }

    public static EntityType getSpawnEggEntityType(ItemMeta meta) {
        Preconditions.checkNotNull(meta, "Null meta");
        NBTTagCompound tag = getTagFromMeta(Material.MONSTER_EGG, meta);
        Preconditions.checkState(tag != null, "No nbt tag");
        Preconditions.checkState(tag.hasKeyOfType("EntityTag", 10), "No entity tag");
        NBTTagCompound entityTag = tag.getCompound("EntityTag");
        Preconditions.checkState(entityTag.hasKeyOfType("id", 8), "No internal name");
        String internalName = entityTag.getString("id");
        int id = EntityTypes.b.a(EntityTypes.b.get(new MinecraftKey(internalName)));
        EntityType type = EntityType.fromId(id);
        if (type == null)
            throw new IllegalStateException("No entity found with internal name " + internalName + " and id " + id);
        return type;
    }

    public static Optional<EntityType> getSpawnEggEntityTypeIfPresent(ItemMeta meta) {
        try {
            return Optional.of(getSpawnEggEntityType(meta));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Optional.empty();
        }
    }

    public static ItemMeta createMetaWithEntityType(ItemMeta meta, EntityType entityType) {
        NBTTagCompound entityTag = new NBTTagCompound();
        String internalName = EntityTypes.getName(EntityTypes.b.getId(entityType.getTypeId())).b();
        if (internalName == null) throw new AssertionError("Couldn't find internal name for type: " + entityType);
        entityTag.setString("id", internalName);
        NBTTagCompound tag = getTagFromMeta(Material.MONSTER_EGG, Preconditions.checkNotNull(meta, "Null meta"));
        if (tag == null) tag = new NBTTagCompound();
        tag.set("EntityTag", entityTag);
        return createMetaFromTag(Material.MONSTER_EGG, tag);
    }


    public static ItemMeta createMetaFromTag(Material type, NBTTagCompound tag) {
        Item item = CraftMagicNumbers.getItem(Preconditions.checkNotNull(type, "Null type"));
        ItemStack stack = new ItemStack(item);
        stack.setTag(Preconditions.checkNotNull(tag, "Null nbt tag"));
        return CraftItemStack.getItemMeta(stack);
    }

    public static NBTTagCompound getTagFromMeta(Material type, ItemMeta meta) {
        Preconditions.checkNotNull(meta, "Null meta");
        Preconditions.checkNotNull(type, "Null type");
        Preconditions.checkArgument(Bukkit.getItemFactory().isApplicable(meta, type), "Meta %s isn't applicable to %s", meta, type);
        Item item = CraftMagicNumbers.getItem(type);
        ItemStack stack = new ItemStack(item);
        boolean worked = CraftItemStack.setItemMeta(stack, meta);
        if (!worked) throw new RuntimeException("Didn't work");
        return stack.getTag();
    }
}
