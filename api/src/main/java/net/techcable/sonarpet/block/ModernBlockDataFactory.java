package net.techcable.sonarpet.block;

import lombok.SneakyThrows;
import net.techcable.pineapple.reflection.Reflection;
import net.techcable.sonarpet.nms.IModernNMS;
import net.techcable.sonarpet.nms.INMS;
import net.techcable.sonarpet.utils.NmsVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.BlockState;

import java.lang.invoke.MethodHandle;

public class ModernBlockDataFactory implements BlockDataFactory {
    /* package */ ModernBlockDataFactory() {
        NmsVersion.ensureAtLeast(NmsVersion.v1_13_R2);
    }

    @Override
    @SneakyThrows
    public BlockData createBlockData(Material m) {
        return new ModernBlockData(CREATE_BLOCK_DATA_MATERIAL.invoke(m));
    }

    @Override
    @SneakyThrows
    public BlockData createBlockData(Material m, String data) {
        return new ModernBlockData(CREATE_BLOCK_DATA_MATERIAL_STRING.invoke(m, data));
    }

    @Override
    @SneakyThrows
    public BlockData parseBlockData(String s) {
        return new ModernBlockData(CREATE_BLOCK_DATA_STRING.invoke(s));
    }


    @Override
    @SneakyThrows
    public BlockData fromState(BlockState state) {
        return new ModernBlockData(BLOCK_STATE_GET_DATA.invoke(state));
    }

    @Override
    public BlockData parseLeniently(String s) {
        if (LegacyBlockDataFactory.isLegacyData(s)) {
            ParsedLegacyData parsed = LegacyBlockDataFactory.parseLegacyData(s);
            return fromLegacyData(parsed.getType(), parsed.getData());
        } else {
            return parseBlockData(s);
        }
    }

    @Override
    public BlockData fromLegacyData(int id, byte data) {
        Material m = IModernNMS.getInstance().getMaterialByLegacyId(id);
        return fromLegacyData(m, data);
    }

    @Override
    public BlockData fromLegacyData(String materialName, byte data) {
        Material m = Material.getMaterial("LEGACY_" + materialName);
        if (m == null) {
            throw new IllegalArgumentException("Invalid material name: " + materialName);
        }
        return fromLegacyData(m, data);
    }

    @Override
    public BlockData fromLegacyData(Material m, byte data) {
        return new ModernBlockData(IModernNMS.getInstance().getBukkitBlockData(m, data));
    }

    private static final MethodHandle CREATE_BLOCK_DATA_STRING = Reflection.getMethod(
            Server.class,
            "createBlockData",
            String.class
    );

    private static final MethodHandle CREATE_BLOCK_DATA_MATERIAL = Reflection.getMethod(
            Server.class,
            "createBlockData",
            Material.class
    );

    private static final MethodHandle CREATE_BLOCK_DATA_MATERIAL_STRING = Reflection.getMethod(
            Server.class,
            "createBlockData",
            Material.class,
            String.class
    );

    private static final MethodHandle BLOCK_STATE_GET_DATA = Reflection.getMethod(
            BlockState.class,
            "getBlockData"
    );
}
