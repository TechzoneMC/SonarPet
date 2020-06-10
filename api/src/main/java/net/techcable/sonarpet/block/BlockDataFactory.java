package net.techcable.sonarpet.block;

import net.techcable.sonarpet.utils.NmsVersion;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

public interface BlockDataFactory {
    BlockData createBlockData(Material m);
    BlockData createBlockData(Material m, String data);
    BlockData parseBlockData(String s);

    static BlockDataFactory getInstance() {
        if (NmsVersion.current().isAtLeast(NmsVersion.v1_13_R2)) {
            return new ModernBlockDataFactory();
        } else {
            return new LegacyBlockDataFactory();
        }
    }
    BlockData parseLeniently(String s);
    BlockData fromLegacyData(int id, byte data);
    BlockData fromLegacyData(String materialName, byte data);
    BlockData fromLegacyData(Material m, byte data);
}
