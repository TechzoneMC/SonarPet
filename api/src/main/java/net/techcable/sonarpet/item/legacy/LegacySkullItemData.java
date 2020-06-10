package net.techcable.sonarpet.item.legacy;

import com.google.common.base.Preconditions;
import net.techcable.sonarpet.item.ItemData;
import net.techcable.sonarpet.item.ItemDataFactory;
import net.techcable.sonarpet.item.SkullItemData;
import net.techcable.sonarpet.utils.PlayerProfile;
import net.techcable.sonarpet.utils.ProfileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Optional;
import java.util.UUID;

public class LegacySkullItemData extends LegacyItemData implements SkullItemData {
    private final PlayerProfile profile;

    public Optional<PlayerProfile> getProfile() {
        if (getSkullType() == SkullType.HUMAN_SKULL && getMeta().hasOwner()) {
            String ownerName = getMeta().getOwner();
            Preconditions.checkState(profile.getName().equals(ownerName), "Profile %s doesn't match owner %s", profile, ownerName);
            return Optional.of(profile);
        } else {
            Preconditions.checkState(profile == null, "Profile %s is present but meta has no owner", profile);
            return Optional.empty();
        }
    }


    /* package */ LegacySkullItemData(LegacyItemDataFactory factory, byte rawData, ItemMeta meta) {
        this(factory, rawData, meta, ((SkullMeta) meta).hasOwner() ? ProfileUtils.lookupOptimistically(((SkullMeta) meta).getOwner()) : null);
    }

    /* package */ LegacySkullItemData(LegacyItemDataFactory factory, byte rawData, ItemMeta meta, PlayerProfile profile) {
        super(factory, Material.SKULL_ITEM, rawData, meta);
        Preconditions.checkArgument(profile == null || profile.getName().equals(((SkullMeta) meta).getOwner()));
        this.profile = profile;
    }

    public LegacySkullItemData withOwner(UUID owner) {
        Preconditions.checkNotNull(owner, "Null owner uuid");
        if (getProfile().isPresent() && getProfile().get().getId().equals(owner)) {
            return this;
        } else {
            return withOwner(ProfileUtils.lookupOptimistically(owner));
        }
    }

    public LegacySkullItemData withOwner(String ownerName) {
        Preconditions.checkNotNull(ownerName, "Null owner name");
        if (getProfile().isPresent() && getProfile().get().getName().equals(ownerName)) {
            return this;
        } else {
            return withOwner(ProfileUtils.lookupOptimistically(ownerName));
        }
    }

    public LegacySkullItemData withOwner(PlayerProfile profile) {
        return profile.equals(this.profile) ? this : getFactory().createHumanSkull(profile, this.getMeta());
    }

    public Optional<UUID> getOwner() {
        return getProfile().map(PlayerProfile::getId);
    }


    public Optional<String> getOwnerName() {
        return getProfile().map(PlayerProfile::getName);
    }

    public boolean hasOwner() {
        return getProfile().isPresent();
    }

    public SkullType getSkullType() {
        int data = getRawData();
        SkullType[] values = SkullType.values();
        if (data < 0) {
            throw new IllegalStateException("Can't get skull type from negative data: " + data);
        } else if (data >= values.length) {
            throw new IllegalStateException("Can't get skull type from too large data: " + data);
        } else {
            return values[data];
        }
    }

    public ItemData withSkullType(SkullType type) {
        return withRawData(Preconditions.checkNotNull(type, "Null type").ordinal());
    }

    @Override
    public SkullMeta getMeta() {
        return (SkullMeta) super.getMeta();
    }
}
