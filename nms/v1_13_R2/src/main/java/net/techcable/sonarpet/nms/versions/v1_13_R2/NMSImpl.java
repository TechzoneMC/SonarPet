package net.techcable.sonarpet.nms.versions.v1_13_R2;

import net.minecraft.server.v1_13_R2.*;
import net.minecraft.server.v1_13_R2.DamageSource;
import net.techcable.pineapple.reflection.PineappleField;
import net.techcable.sonarpet.item.SpawnEggItemData;
import net.techcable.sonarpet.nms.*;
import net.techcable.sonarpet.nms.versions.v1_13_R2.data.NMSSpawnEggItemData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.meta.ItemMeta;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class NMSImpl implements INMS {
    @Override
    public SpawnEggItemData createSpawnEggData(EntityType entityType, ItemMeta meta) {
        checkNotNull(entityType, "Null entity type");
        checkNotNull(meta, "Null item meta");
        return new NMSSpawnEggItemData((byte) 0, meta, entityType);
    }

    @Override
    public void mount(Entity bukkitRider, Entity bukkitVehicle) {
        net.minecraft.server.v1_13_R2.Entity rider = ((CraftEntity) bukkitRider).getHandle();
        if (bukkitVehicle == null) {
            net.minecraft.server.v1_13_R2.Entity vehicle = rider.getVehicle(); // This is how you *really* get the vehicle :/
            if (rider instanceof DismountingBlocked) {
                ((DismountingBlocked) rider).reallyStopRiding();
            }
            rider.stopRiding();
            if (vehicle != null) {
                Packet packet = new PacketPlayOutMount(vehicle);
                for (EntityHuman human : rider.world.players) {
                    ((EntityPlayer) human).playerConnection.sendPacket(packet);
                }
            }
        } else {
            checkArgument(bukkitRider.getWorld().equals(bukkitVehicle.getWorld()), "Rider is in world %s, while vehicle is in world %s", bukkitRider.getWorld().getName(), bukkitVehicle.getWorld().getName());
            net.minecraft.server.v1_13_R2.Entity vehicle = ((CraftEntity) bukkitVehicle).getHandle();
            rider.a(vehicle, true); // !! Obfuscated !!
            Packet packet = new PacketPlayOutMount(vehicle);
            for (EntityHuman human : rider.world.players) {
                ((EntityPlayer) human).playerConnection.sendPacket(packet);
            }
        }
    }

    @Override
    public boolean spawnEntity(NMSInsentientEntity wrapper, Location l) {
        EntityLiving entity = ((NMSEntityInsentientImpl) wrapper).getHandle();
        entity.spawnIn(((CraftWorld) l.getWorld()).getHandle());
        ((LivingEntity) entity.getBukkitEntity()).setCollidable(false);
        entity.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
        if (!l.getChunk().isLoaded()) {
            l.getChunk().load();
        }
        return entity.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public net.techcable.sonarpet.nms.DamageSource mobAttackDamageSource(LivingEntity entity) {
        return new DamageSourceImpl(DamageSource.mobAttack(((CraftLivingEntity) entity).getHandle()));
    }

    @Override
    public net.techcable.sonarpet.nms.DamageSource wrapDamageSource(Object handle) {
        return new DamageSourceImpl((DamageSource) handle);
    }

    @Override
    public NMSEntity wrapEntity(Entity entity) {
        net.minecraft.server.v1_13_R2.Entity handle = ((CraftEntity) entity).getHandle();
        if (handle instanceof EntityPlayer) {
            return new NMSPlayerImpl((EntityPlayer) handle);
        } else if (handle instanceof EntityHorseAbstract) {
            return new NMSEntityHorseImpl((EntityHorseAbstract) handle);
        } else if (handle instanceof EntityInsentient) {
            return new NMSEntityInsentientImpl((EntityInsentient) handle);
        } else if (handle instanceof EntityLiving) {
            return new NMSLivingEntityImpl((EntityLiving) handle);
        } else {
            return new NMSEntityImpl(handle);
        }
    }

    private PineappleField<Block, SoundEffectType> STEP_SOUND_FIELD = PineappleField.create(Block.class, "stepSound", SoundEffectType.class);
    @Override
    @SuppressWarnings("deprecation") // I know about ur stupid magic value warning mom
    public BlockSoundData getBlockSoundData(Material material) {
        Block block = Block.getByCombinedId(material.getId()).getBlock();
        return new BlockSoundDataImpl(STEP_SOUND_FIELD.get(block));
    }

    @Override
    @SuppressWarnings("deprecation") // I know about ur stupid magic value warning mom
    public boolean isLiquid(Material block) {
        return Block.getByCombinedId(block.getId()).getMaterial().isLiquid();
    }

    @Override
    public EntityRegistry createDefaultEntityRegistry() {
        return new NMSEntityRegistry();
    }

    @Override
    public NMSSound getNmsSound(Sound bukkitSound) {
        return new NMSSoundImpl(NMSSoundImplKt.getSoundEffect(bukkitSound));
    }
}
