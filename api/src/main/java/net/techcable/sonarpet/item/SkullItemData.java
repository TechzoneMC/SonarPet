package net.techcable.sonarpet.item;

import java.util.Optional;
import java.util.UUID;

import com.google.common.base.Preconditions;

import net.techcable.sonarpet.utils.PlayerProfile;
import net.techcable.sonarpet.utils.ProfileUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public interface SkullItemData extends ItemData {
    Optional<PlayerProfile> getProfile();

    SkullItemData withOwner(UUID owner);

    SkullItemData withOwner(String ownerName);

    SkullItemData withOwner(PlayerProfile profile);

    Optional<UUID> getOwner();

    Optional<String> getOwnerName();

    boolean hasOwner();

    static SkullItemData createHuman() {
        return create(SkullType.HUMAN_SKULL);
    }

    static SkullItemData createHuman(PlayerProfile owner) {
        return createHuman(owner, Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM));
    }

    static SkullItemData createHuman(PlayerProfile owner, ItemMeta rawMeta) {
        return ItemDataFactory.getInstance().createHumanSkull(owner, rawMeta);
    }


    static SkullItemData create(SkullType type) {
        return create(type, Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM));
    }

    static SkullItemData create(SkullType type, ItemMeta meta) {
        return ItemDataFactory.getInstance().createSkull(type, meta);
    }

    SkullType getSkullType();

    ItemData withSkullType(SkullType type);

    @Override
    SkullMeta getMeta();

    enum SkullType {
        SKELETON_SKULL,
        WITHER_SKELETON_SKULL,
        ZOMBIE_SKULL,
        HUMAN_SKULL,
        CREEPER_HEAD;

        public static SkullType getById(int id) {
            SkullType[] values = values();
            if (id < 0) {
                throw new IllegalArgumentException("Negative id: " + id);
            } else if (id >= values.length) {
                throw new IllegalArgumentException("Invalid id: " + id);
            } else {
                return values[id];
            }
        }

        public byte getId() {
            return (byte) ordinal();
        }
    }
}
