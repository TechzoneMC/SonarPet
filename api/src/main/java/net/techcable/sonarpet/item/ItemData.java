package net.techcable.sonarpet.item;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.techcable.sonarpet.block.BlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface ItemData {
    Material getType();
    ItemMeta getMeta();

    /**
     * Serialize this ItemData into a string,
     * which can be deserialized by {@link ItemDataFactory#parseFromString(String)}.
     *
     * This is not guarenteed to preserve all the items properties
     * and is only 'best-effort'.
     *
     * @return the serialized string
     */
    String serializeAsString();

    ItemDataFactory getFactory();

    static ItemData create(Material type) {
        return create(type, Bukkit.getItemFactory().getItemMeta(type));
    }

    static ItemData create(Material type, ItemMeta meta) {
        return ItemDataFactory.getInstance().create(type, meta);
    }

    ItemData withMeta(ItemMeta meta);

    ItemData withPlainMeta();

    default ImmutableList<String> getLore() {
        if (getMeta().hasLore()) {
            return ImmutableList.copyOf(getMeta().getLore());
        } else {
            return ImmutableList.of();
        }
    }

    default Optional<String> getDisplayName() {
        return getMeta().hasDisplayName() ? Optional.of(getMeta().getDisplayName()) : Optional.empty();
    }

    default ItemData withDisplayName(@Nullable String name) {
        ItemMeta meta = getMeta(); // Returns a copy ;)
        if (name != null) {
            Preconditions.checkArgument(!name.trim().isEmpty(), "Empty name '%s'", name);
            meta.setDisplayName(name);
        } else {
            meta.setDisplayName(null); // Undocumented behavior of awesomeness
        }
        return withMeta(meta);
    }

    default ItemData withLore(List<String> lore) {
        ItemMeta meta = getMeta(); // Returns a copy ;)
        meta.setLore(ImmutableList.copyOf(lore)); // Copy lore :D
        return withMeta(meta);
    }

    ItemStack createStack(int amount);

    @Nullable
    BlockData getBlockData();
}
