package net.techcable.sonarpet.block;

import org.bukkit.Material;

/**
 * A facade around the modern `BlockData` interface,
 * in order to ensure backwards compatibility.
 */
public interface BlockData {
    Material getMaterial();
    String getAsString();
    static BlockData createBlockData(Material m) {
        return BlockDataFactory.getInstance().createBlockData(m);
    }
    static BlockData parseBlockData(String s) {
        return BlockDataFactory.getInstance().parseBlockData(s);
    }

    /**
     * Parse the sepcified block data 'leniently',
     * making a decent attempt to convert from legacy data.
     *
     * @param s the data to parse
     * @throws IllegalArgumentException if the data is invalid
     * @return the resulting block data
     */
    static BlockData parseLeniently(String s) {
        return BlockDataFactory.getInstance().parseLeniently(s);
    }
    static BlockData fromLegacyData(String materialName, byte data) {
        return BlockDataFactory.getInstance().fromLegacyData(materialName, data);
    }
    static BlockData fromLegacyData(Material m, byte data) {
        return BlockDataFactory.getInstance().fromLegacyData(m, data);
    }
}
