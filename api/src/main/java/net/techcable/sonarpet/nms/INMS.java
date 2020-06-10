/*
 * This file is part of EchoPet.
 *
 * EchoPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EchoPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EchoPet.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.techcable.sonarpet.nms;

import com.dsh105.echopet.compat.api.plugin.IEchoPetPlugin;
import com.google.common.base.Preconditions;
import net.techcable.pineapple.reflection.Reflection;
import net.techcable.sonarpet.item.ItemDataFactory;
import net.techcable.sonarpet.item.legacy.LegacyItemDataFactory;
import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static net.techcable.sonarpet.utils.Versioning.NMS_VERSION;
import static net.techcable.sonarpet.utils.Versioning.NMS_VERSION_STRING;

@SuppressWarnings("deprecation")
public interface INMS {
    default ItemDataFactory getItemDataFactory() {
        NmsVersion.ensureBefore(NmsVersion.v1_13_R2);
        return new LegacyItemDataFactory();
    }

    boolean spawnEntity(NMSInsentientEntity entity, Location location);

    default void mount(Entity rider, Entity vehicle) {
        Preconditions.checkNotNull(rider, "Null rider");
        if (vehicle != null) {
            vehicle.setPassenger(rider);
        } else {
            rider.leaveVehicle();
        }
    }

    static boolean isSupported() {
        return Helper.instance != null;
    }

    static INMS getInstance() {
        if (isSupported()) {
            return Helper.instance;
        } else {
            throw new UnsupportedOperationException("Unsupported version");
        }
    }

    DamageSource mobAttackDamageSource(LivingEntity entity);

    DamageSource wrapDamageSource(Object handle);

    NMSEntity wrapEntity(Entity entity);

    BlockSoundData getBlockSoundData(Material material);

    boolean isLiquid(Material block);

    /**
     * Get the default entity registry for this nms version.
     *
     * @return the default registry
     * @deprecated use {@link IEchoPetPlugin#getEntityRegistry()}
     */
    @Deprecated()
    EntityRegistry createDefaultEntityRegistry();

    NMSSound getNmsSound(Sound bukkitSound);
}

/**
 * A helper for NMS getInstance()
 */
class Helper {

    public static final INMS instance;

    static {
        MethodHandle constructor;
        Class<?> implClass = Reflection.getClass("net.techcable.sonarpet.nms.versions." + NMS_VERSION + ".NMSImpl");
        if (implClass == null) {
            instance = null;
        } else {
            try {
                constructor = MethodHandles.publicLookup().findConstructor(implClass, MethodType.methodType(void.class));
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new AssertionError("Unable to invoke constructor", e);
            }
            try {
                instance = constructor == null ? null : (INMS) constructor.invoke();
            } catch (Throwable t) {
                throw new AssertionError("NMS constructor threw exception", t);
            }
        }
        if (NmsVersion.current().isAtLeast(IModernNMS.MINIMUM_VERSION)
                && !(instance instanceof IModernNMS)) {
            throw new RuntimeException("NMSImpl for " + NMS_VERSION_STRING + " should implement IModernNMS");
        }
    }
}
