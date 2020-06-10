package net.techcable.sonarpet.particles;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

/**
 * This is used on all modern versions (v1.8.8+)
 *
 * @deprecated Was only used for 1.8.8 support.
 */
@Deprecated
public class BukkitParticleBuilder extends ParticleBuilder {
    public BukkitParticleBuilder(Particle type, float speed, int amount) {
        super(type, speed, amount);
    }

    @Override
    public void show(Player player) {
        Preconditions.checkArgument(player.getWorld().equals(getPosition().getWorld()), "Player world %s doesn't match position world %s", player.getWorld(), getPosition().getWorld());
        getPosition().getWorld().spawnParticle(
                getType().getBukkitParticle(),
                getPosition().getX(),
                getPosition().getY(),
                getPosition().getZ(),
                getType().getAmount(),
                getOffset().getX(),
                getOffset().getY(),
                getOffset().getZ(),
                getType().getSpeed(),
                createData()
        );
    }

    private Object createData() {
        if (hasBlockData()) {
            Preconditions.checkState(getType().getBukkitParticle().getDataType() == MaterialData.class, "Particle type " + getType() + " can't use MaterialData");
            return new MaterialData(getBlockType(), (byte) getBlockMeta());
        } else {
            return null;
        }
    }

}
