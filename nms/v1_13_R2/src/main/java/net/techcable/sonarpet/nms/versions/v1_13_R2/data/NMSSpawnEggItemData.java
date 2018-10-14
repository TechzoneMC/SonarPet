package net.techcable.sonarpet.nms.versions.v1_13_R2.data;

import java.util.Optional;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_13_R2.*;
import net.techcable.sonarpet.item.SpawnEggItemData;

import net.techcable.sonarpet.utils.ModernSpawnEggs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftMagicNumbers;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;

public class NMSSpawnEggItemData extends SpawnEggItemData {
    public NMSSpawnEggItemData(byte rawData, ItemMeta meta, EntityType type) {
        super(ModernSpawnEggs.getEggMaterial(type), rawData, meta);
    }

    @Override
    public EntityType getSpawnedType() {
        return ModernSpawnEggs.getSpawnEggType(this.getMaterialData().getItemType());
    }


    public static ItemMeta create(Material type, NBTTagCompound tag) {
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
