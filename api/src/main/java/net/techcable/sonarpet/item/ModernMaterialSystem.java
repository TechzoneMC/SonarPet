package net.techcable.sonarpet.item;

import com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import net.techcable.pineapple.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.invoke.MethodHandle;
import java.util.Objects;

public class ModernMaterialSystem implements MaterialSystem {
    @Override
    @SneakyThrows
    public String serializeData(ItemData m) {
        if (m.getType().isBlock()) {
            // Give them the whole block data
            return (String) BLOCK_DATA_GET_AS_STRING.invoke(m.getModernBlockData());
        } else {
            // Otherwise we just give them the material without any data
            // This strips all information, but the bukkit API has no real alternative
            // NOTE: Must use reflection in order to bypass the 1.13 compat hacks
            return (String) MATERIAL_NAME.invoke(m.getType());
        }
    }

    @Override
    @SneakyThrows
    public ItemData parseData(String s) {
        Preconditions.checkNotNull(s, "Null data");
        {
            Material m = (Material) MATERIAL_GET_MATERIAL.invokeExact(s);
            if (m != null) {
                // First we try just the material, in case it's an item
                return ItemData.create(m);
            }
        }
        Object data = SERVER_CREATE_BLOCK_DATA.invoke(Bukkit.getServer(), s);
        Material m = (Material) BLOCK_DATA_GET_MATERIAL.invoke(data);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(m);
        if (meta instanceof BlockStateMeta) {
            // TODO: I think this should always be the case
            // Really, we need to just make ItemData fully version-dependent on modern versions
            BlockState state = ((BlockStateMeta) meta).getBlockState();
            BLOCK_STATE_SET_DATA.invoke(state, data);
        }
        return ItemData.create(m, meta);
    }

    private static final Class<?> BLOCK_DATA_CLASS = Objects.requireNonNull(
            Reflection.getClass("org.bukkit.block.data.BlockData"));
    private static final MethodHandle SERVER_CREATE_BLOCK_DATA = Reflection.getMethod(
            Server.class,
            "createBlockData",
            String.class
    );
    private static final MethodHandle BLOCK_DATA_GET_AS_STRING = Reflection.getMethod(
            BLOCK_DATA_CLASS,
            "getAsString"
    );
    private static final MethodHandle BLOCK_DATA_GET_MATERIAL = Reflection.getMethod(
            BLOCK_DATA_CLASS,
            "getMaterial"
    );
    private static final MethodHandle MATERIAL_CREATE_BLOCK_DATA = Reflection.getMethod(
            Material.class,
            "createBlockData"
    );

    private static final MethodHandle MATERIAL_NAME = Reflection.getMethod(
            Enum.class,
            "name"
    );
    private static final MethodHandle MATERIAL_GET_MATERIAL = Reflection.getMethod(
            Material.class,
            "getMaterial",
            String.class
    );
    private static final MethodHandle BLOCK_STATE_GET_DATA = Reflection.getMethod(
            BlockState.class,
            "getBlockData"
    );
    private static final MethodHandle BLOCK_STATE_SET_DATA = Reflection.getMethod(
            BlockState.class,
            "setBlockData",
            BLOCK_DATA_CLASS
    );

    @SneakyThrows
    public static Object createDefaultBlockData(Material m) {
        return MATERIAL_CREATE_BLOCK_DATA.invoke(m);
    }
    @SneakyThrows
    public static Object getDataFromState(BlockState state) {
        return BLOCK_STATE_GET_DATA.invoke(state);
    }
}
