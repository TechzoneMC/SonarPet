package net.techcable.sonarpet.item;

import net.techcable.sonarpet.utils.NmsVersion;

/**
 * Defines a system for interacting between `org.bukkit.Material`.
 *
 * This is necessary for the new and radical changes in 1.13.
 */
public interface MaterialSystem {
    String serializeData(ItemData m);

    ItemData parseData(String s);

    static MaterialSystem getInstance() {
        if (NmsVersion.current().isAtLeast(NmsVersion.v1_13_R2)) {
            return new ModernMaterialSystem();
        } else {
            return new LegacyMaterialSystem();
        }
    }
}
