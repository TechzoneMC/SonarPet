package net.techcable.sonarpet.block;

import com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import net.techcable.pineapple.reflection.Reflection;
import net.techcable.sonarpet.utils.NmsVersion;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegacyBlockDataFactory implements BlockDataFactory {
    /* package */ LegacyBlockDataFactory() {
        NmsVersion.ensureBefore(NmsVersion.v1_13_R2);
    }

    @Override
    public BlockData createBlockData(Material m) {
        return new LegacyBlockData(m.getNewData((byte) 0));
    }

    @Override
    public BlockData createBlockData(Material m, String data) {
        Preconditions.checkNotNull(m, "Null material");
        Preconditions.checkNotNull(data, "Null data");
        ParsedLegacyData parsed = parseLegacyData(data);
        Preconditions.checkArgument(
                parsed.getType() == m,
                "Expected type %s for data %s",
                m, data
        );
        return new LegacyBlockData(parsed.toLegacyMaterialData());
    }

    @Override
    public BlockData parseBlockData(String s) {
        return new LegacyBlockData(parseLegacyData(s).toLegacyMaterialData());
    }

    @Override
    public BlockData fromState(BlockState state) {
        return new LegacyBlockData(state.getData());
    }

    @Override
    public BlockData parseLeniently(String s) {
        // we do **not* handle data from future versions, so nothing special to do
        return parseBlockData(s);
    }

    @Override
    public BlockData fromLegacyData(int id, byte data) {
        Material material = Material.getMaterial(id);
        if (material == null) {
            throw new IllegalArgumentException("Invalid material id: " + id);
        }
        return fromLegacyData(material, data);
    }

    @Override
    public BlockData fromLegacyData(String materialName, byte data) {
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("Invalid material name: " + materialName);
        }
        return fromLegacyData(material, data);
    }

    @Override
    public BlockData fromLegacyData(Material m, byte data) {
        return new LegacyBlockData(m.getNewData(data));
    }

    private static final Pattern LEGACY_DATA_PATTERN = Pattern
            .compile("legacy\\[(\\w+)(?::(\\d+))?]");
    public static boolean isLegacyData(String data) {
        return LEGACY_DATA_PATTERN.matcher(data).matches();
    }
    public static ParsedLegacyData parseLegacyData(String data) {
        Preconditions.checkNotNull(data, "Null data");
        Matcher matcher = LEGACY_DATA_PATTERN.matcher(data);
        String materialName = matcher.group(1);
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException(
                    "Invalid material " + materialName +
                            " for legacy data: " + data
            );
        }
        String legacyDataStr = matcher.group(2);
        final byte legacyData;
        if (legacyDataStr != null) {
            try {
                legacyData = Byte.parseByte(legacyDataStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Invalid legacyData " + legacyDataStr +
                                " for legeacy data: " + data,
                        e
                );
            }
        } else {
            legacyData = 0;
        }
        return new ParsedLegacyData(material, legacyData);
    }
}
