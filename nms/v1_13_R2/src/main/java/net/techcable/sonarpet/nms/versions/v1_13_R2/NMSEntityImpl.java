package net.techcable.sonarpet.nms.versions.v1_13_R2;

import lombok.*;

import com.google.common.collect.ImmutableList;

import net.minecraft.server.v1_13_R2.DamageSource;
import net.minecraft.server.v1_13_R2.Entity;
import net.techcable.sonarpet.nms.INMS;
import net.techcable.sonarpet.nms.NMSEntity;

import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class NMSEntityImpl implements NMSEntity {
    @Getter
    private final Entity handle;

    //
    // Deobfuscated methods
    //

    @Override
    public boolean damageEntity(net.techcable.sonarpet.nms.DamageSource rawSource, float amount) {
        DamageSource damageSource = ((DamageSourceImpl) rawSource).getHandle();
        return getHandle().damageEntity(damageSource, amount);
    }

    @Override
    public org.bukkit.entity.Entity getBukkitEntity() {
        return handle.getBukkitEntity();
    }

    @Override
    public ImmutableList<NMSEntity> getPassengers() {
        return ImmutableList.copyOf(handle.passengers.stream()
                .map(Entity::getBukkitEntity)
                .map(INMS.getInstance()::wrapEntity)
                .toArray(NMSEntity[]::new));
    }

    @Override
    public int hashCode() {
        return handle.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof NMSEntityImpl && ((NMSEntityImpl) obj).handle.equals(this.handle);
    }

    @Override
    public String toString() {
        if (getBukkitEntity() instanceof Player) {
            return "NMSPlayer(" + getBukkitEntity().getName() + ")";
        } else {
            StringBuilder builder = new StringBuilder("NMSEntity(");
            builder.append(getBukkitEntity().getType());
            if (getBukkitEntity().getCustomName() != null) {
                builder.append(":");
                builder.append(getBukkitEntity().getCustomName());
            }
            builder.append(')');
            return builder.toString();
        }
    }
}
