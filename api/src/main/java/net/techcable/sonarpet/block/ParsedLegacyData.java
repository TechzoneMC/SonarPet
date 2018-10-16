package net.techcable.sonarpet.block;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

@RequiredArgsConstructor
@Getter
/* package */ class ParsedLegacyData {
    @NonNull
    private final Material type;
    private final byte data;

    public MaterialData toLegacyMaterialData() {
        return type.getNewData(this.data);
    }
}
