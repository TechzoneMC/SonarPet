package net.techcable.sonarpet.utils;

import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ModernSpawnEggs {
    private ModernSpawnEggs() {}

    @Nonnull
    private static void ensureSupported() {
        if (!ModernSpawnEggs.isSupported()) {
            throw new IllegalStateException(
                    "Can't use modern spawn eggs on " + NmsVersion.current()
            );
        }
    }
    public static boolean isSpawnEgg(Material m) {
        return tryGetSpawnEggType(m) != null;
    }
    public static boolean isSupported() {
        return NmsVersion.current().compareTo(NmsVersion.v1_13_R2) >= 0;
    }
    public static boolean hasSpawnEgg(EntityType e) {
        return tryGetEggMaterial(e) != null;
    }
    @Nonnull
    public static Material getEggMaterial(EntityType t) {
        Material m = tryGetEggMaterial(t);
        if (m == null) {
            throw new IllegalArgumentException("Entity doesn't have spawn egg: " + t);
        }
        return m;
    }
    @Nonnull
    public static EntityType getSpawnEggType(Material m) {
        EntityType entityType = tryGetSpawnEggType(m);
        if (entityType == null) {
            throw new IllegalArgumentException("Material isn't a spawn egg: " + m);
        }
        return entityType;
    }
    private static final String SUFFIX = "_SPAWN_EGG";
    @Nullable
    @SneakyThrows(ReflectiveOperationException.class)
    private static Material tryGetEggMaterial(EntityType entityType) {
        ModernSpawnEggs.ensureSupported();
        Class materialClass = Class.forName("org.bukkit.Material");
        try {
            //noinspection unchecked
            return (Material) Enum.valueOf(materialClass, entityType.name() + SUFFIX);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Nullable
    private static EntityType tryGetSpawnEggType(Material m) {
        ModernSpawnEggs.ensureSupported();
        if (m.name().endsWith(SUFFIX)) {
            String entityName = m.name().substring(
                    0,
                    m.name().length() - SUFFIX.length()
            );
            try {
                return EntityType.valueOf(entityName);
            } catch (IllegalArgumentException ignored) {}
        }
        return null;
    }
}
