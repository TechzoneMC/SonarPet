package net.techcable.sonarpet.block;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.techcable.pineapple.reflection.Reflection;
import org.bukkit.Material;

import java.lang.invoke.MethodHandle;
import java.util.Objects;

public final class ModernBlockData implements BlockData {
    private final Object handle;
    /* package */ ModernBlockData(Object handle) {
        Preconditions.checkNotNull(handle);
        this.handle = BLOCK_DATA_CLASS.cast(handle);
    }

    @Override
    @SneakyThrows
    public Material getMaterial() {
        return (Material) GET_MATERIAL.invoke(handle);
    }

    @Override
    @SneakyThrows
    public String getAsString() {
        return (String) GET_AS_STRING.invoke(this.handle);
    }

    private static final Class<?> BLOCK_DATA_CLASS = Objects.requireNonNull(
            Reflection.getClass("org.bukkit.block.data.BlockData"));
    private static final MethodHandle GET_MATERIAL = Reflection.getMethod(
            BLOCK_DATA_CLASS,
            "getMaterial"
    );
    private static final MethodHandle GET_AS_STRING = Reflection.getMethod(
            BLOCK_DATA_CLASS,
            "getString"
    );
}
