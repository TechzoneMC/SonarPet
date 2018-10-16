package net.techcable.sonarpet.block;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class LegacyBlockData implements BlockData {
    private final MaterialData materialData;

    @Override
    public Material getMaterial() {
        return materialData.getItemType();
    }

    @Override
    public String getAsString() {
        return "legacy[" + materialData.getItemType().name() + ":" + materialData.getData() + "]";
    }
}
