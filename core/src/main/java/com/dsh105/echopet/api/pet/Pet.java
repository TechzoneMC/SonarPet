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

package com.dsh105.echopet.api.pet;

import lombok.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.dsh105.echopet.compat.api.entity.EntityPetType;
import com.dsh105.echopet.compat.api.entity.IEntityNoClipPet;
import com.dsh105.echopet.compat.api.entity.IEntityPet;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.entity.PetData;
import com.dsh105.echopet.compat.api.entity.PetType;
import com.dsh105.echopet.compat.api.event.PetPreSpawnEvent;
import com.dsh105.echopet.compat.api.event.PetTeleportEvent;
import com.dsh105.echopet.compat.api.plugin.EchoPet;
import com.dsh105.echopet.compat.api.util.Lang;
import com.dsh105.echopet.compat.api.util.PetNames;
import com.dsh105.echopet.compat.api.util.StringSimplifier;
import com.google.common.base.Preconditions;

import net.techcable.sonarpet.CancelledSpawnException;
import net.techcable.sonarpet.EntityHookType;
import net.techcable.sonarpet.nms.INMS;
import net.techcable.sonarpet.particles.Particle;
import net.techcable.sonarpet.particles.ParticleBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.*;

public abstract class Pet implements IPet {

    @Nullable
    protected IEntityPet hook;
    @Nonnull
    private final PetType petType;

    @Nonnull
    private final UUID ownerId;
    private Pet rider;
    private String name;
    private ArrayList<PetData> petData = new ArrayList<>();

    private boolean isRider = false;

    public boolean ownerIsMounting = false;
    private boolean ownerRiding = false;
    private boolean isHat = false;
    private boolean destroyed = false;

    // This is hidden behind reflection and ignored by kotlin, so there's no point making the subclasses declare it
    @SneakyThrows(CancelledSpawnException.class)
    public Pet(Player owner)  {
        Objects.requireNonNull(owner, "Null owner");
        this.ownerId = Objects.requireNonNull(owner.getUniqueId());
        this.petType = this.determinePetType();
        this.setPetName(petType.getDefaultName(owner.getName()));
        spawnPet(getOwner(), getPetType().getPrimaryHookType(), false);
    }

    private void spawnPet(Player owner, EntityHookType hookType, boolean forced) throws CancelledSpawnException {
        checkState(this.hook == null, "%s already spawned", this);
        checkState(!this.destroyed, "%s has already been destroyed", this);
        if (!forced) {
            PetPreSpawnEvent spawnEvent = new PetPreSpawnEvent(this, owner.getLocation());
            EchoPet.getPlugin().getServer().getPluginManager().callEvent(spawnEvent);
            if (spawnEvent.isCancelled()) {
                throw new CancelledSpawnException(owner, petType);
            }
        }
        this.hook = EchoPet.getPlugin().getHookRegistry().spawnEntity(this, hookType, owner.getLocation());
        this.applyPetName();
        this.teleportToOwner();
    }

    @SneakyThrows(CancelledSpawnException.class) // May never occur
    protected void switchHookType(Player owner, EntityHookType newHookType) {
        checkState(this.hook != null, "Pet isn't spawned yet!");
        checkState(this.isRegistered(), "%s isn't registered", this);
        if (newHookType == hook.getHookType()) return;
        this.removePet(false);
        assert hook.getBukkitEntity().isDead();
        this.hook = null;
        spawnPet(owner, newHookType, true);
    }

    @Nonnull
    protected PetType determinePetType() {
        Class<? extends Pet> petClass = this.getClass();
        EntityPetType entityPetType = petClass.getAnnotation(EntityPetType.class);
        if (entityPetType == null) {
            throw new IllegalStateException("Pet class " + petClass.getName() + " doesn't have @EntityPetType annotation!");
        }
        return entityPetType.petType();
    }

    @Override
    public boolean isSpawned() {
        return this.hook != null;
    }

    @Nonnull
    @Override
    public IEntityPet getEntityPet() {
        IEntityPet result = this.hook;
        if (result == null) {
            throw new IllegalStateException(
                    "Expected " + this + " to be spawned!"
            );
        }
        return result;
    }

    @Override
    @Nonnull
    public LivingEntity getCraftPet() {
        return this.getEntityPet().getBukkitEntity();
    }

    @Override
    public Location getLocation() {
        return this.getCraftPet().getLocation();
    }

    @Override
    @Nonnull
    public Player getOwner() {
        Player result = Bukkit.getPlayer(ownerId);
        checkState(result != null, "Player not online: %s", ownerId);
        return result;
    }

    @Override
    @Nonnull
    public String getNameOfOwner() {
        return getOwner().getName();
    }

    @Override
    @Nonnull
    public UUID getOwnerUUID() {
        return this.ownerId;
    }

    @Override
    public Object getOwnerIdentification() {
        return ownerId;
    }

    @NotNull
    @Override
    public PetType getPetType() {
        return this.petType;
    }

    @Override
    public boolean isRider() {
        return this.isRider;
    }

    protected void setRider() {
        this.isRider = true;
    }

    @Override
    public boolean isOwnerInMountingProcess() {
        return ownerIsMounting;
    }

    @Override
    public Pet getRider() {
        return this.rider;
    }

    @Override
    public String getPetName() {
        return name;
    }

    @Override
    public String getPetNameWithoutColours() {
        return ChatColor.stripColor(this.getPetName());
    }
    
    @Override
    public String serialisePetName() {
        return getPetName().replace(ChatColor.COLOR_CHAR, '&');
    }

    @Override
    public boolean setPetName(String name) {
        return this.setPetName(name, true);
    }

    @Override
    public boolean setPetName(String name, boolean sendFailMessage) {
        if (PetNames.allow(name, this)) {
            this.name = ChatColor.translateAlternateColorCodes('&', name);
            if (EchoPet.getPlugin().getMainConfig().getBoolean("stripDiacriticsFromNames", true)) {
                this.name = StringSimplifier.stripDiacritics(this.name);
            }
            if (this.name == null || this.name.equalsIgnoreCase("")) {
                this.name = this.petType.getDefaultName(this.getNameOfOwner());
            }
            if (this.isSpawned()) {
                this.applyPetName();
            }
            return true;
        } else {
            if (sendFailMessage) {
                Lang.sendTo(this.getOwner(), Lang.NAME_NOT_ALLOWED.toString().replace("%name%", name));
            }
            return false;
        }
    }

    private void applyPetName() {
        Preconditions.checkState(this.isSpawned(), "Pet isn't spawned: %s", this);
        this.getCraftPet().setCustomName(this.name);
        this.getCraftPet().setCustomNameVisible(EchoPet.getConfig().getBoolean("pets." + this.getPetType().toString().toLowerCase().replace("_", " ") + ".tagVisible", true));
    }

    @Override
    public ArrayList<PetData> getPetData() {
        return this.petData;
    }

    @Override
    public void removeRider() {
        if (rider != null && rider.isSpawned()) {
            INMS.getInstance().mount(rider.getCraftPet(), null);
            rider.removePet(true);
            this.rider = null;
        }
    }

    @Override
    public void removePet(boolean makeSound) {
        if (destroyed) return; // Don't double-free!
        checkState(isSpawned(), "%s isn't currently spawned", this);
        if (makeSound) {
            Particle.CLOUD.show(getLocation());
            Particle.LAVA_SPARK.show(getLocation());
        }
        removeRider();
        this.getEntityPet().remove(makeSound);
        this.hook = null;
        this.destroyed = true;
        // NOTE: Violates encapsulation, but that's the lesser of two evils for now
        EchoPet.getManager().internalOnRemove(this);
    }

    @Override
    public boolean teleportToOwner() {
        Preconditions.checkState(this.getOwner().getLocation() != null, "Owner has null location: %s", this);
        return this.teleport(this.getOwner().getLocation());
    }

    @Override
    public boolean teleport(Location to) {
        if (!this.isSpawned() || this.getEntityPet().isDead()) {
            EchoPet.getManager().saveFileData("autosave", this);
            EchoPet.getSqlManager().saveToDatabase(this, false);
            EchoPet.getManager().removePet(this, false);
            EchoPet.getManager().createPetFromFile("autosave", this.getOwner());
            return false;
        }
        PetTeleportEvent teleportEvent = new PetTeleportEvent(this, this.getLocation(), to);
        EchoPet.getPlugin().getServer().getPluginManager().callEvent(teleportEvent);
        if (teleportEvent.isCancelled()) {
            return false;
        }
        Location l = teleportEvent.getTo();
        if (l.getWorld() == this.getLocation().getWorld()) {
            if (this.getRider() != null) {
                this.getRider().getCraftPet().eject();
                this.getRider().getCraftPet().teleport(l);
            }
            this.getCraftPet().teleport(l);
            if (this.getRider() != null) {
                getCraftPet().eject();
                getCraftPet().addPassenger(this.getRider().getCraftPet());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isOwnerRiding() {
        return this.ownerRiding;
    }

    @Override
    public boolean isHat() {
        return this.isHat;
    }

    @Override
    public void ownerRidePet(boolean flag) {
        if (this.ownerRiding == flag) {
            return;
        }

        this.ownerIsMounting = true;

        if (this.isHat) {
            this.setAsHat(false);
        }

        // Ew...This stuff is UGLY :c

        if (!flag) {
            INMS.getInstance().mount(this.getOwner(), null);
            //((CraftPlayer) this.getOwner()).getHandle().mount(null);
            if (this.getEntityPet() instanceof IEntityNoClipPet) {
                ((IEntityNoClipPet) this.getEntityPet()).noClip(true);
            }
            ownerIsMounting = false;
        } else {
            if (this.getRider() != null) {
                this.getRider().removePet(false);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    INMS.getInstance().mount(getOwner(), getEntityPet().getBukkitEntity());
                    //((CraftPlayer) getOwner()).getHandle().mount(getEntityPet());
                    ownerIsMounting = false;
                    if (getEntityPet() instanceof IEntityNoClipPet) {
                        ((IEntityNoClipPet) getEntityPet()).noClip(false);
                    }
                }
            }.runTaskLater(EchoPet.getPlugin(), 5L);
        }
        this.teleportToOwner();
        this.getEntityPet().resizeBoundingBox(flag);
        this.ownerRiding = flag;
        Particle.PORTAL.show(getLocation());
        Location l = this.getLocation().clone();
        l.setY(l.getY() - 1D);
        ParticleBuilder builder = Particle.BLOCK_DUST.builder();
        builder.setBlockType(l.getBlock().getType());
        builder.setPosition(getLocation());
        builder.show();
    }

    @Override
    public void setAsHat(boolean flag) {
        if (this.isHat == flag) {
            return;
        }
        if (this.ownerRiding) {
            this.ownerRidePet(false);
        }
        this.teleportToOwner();

        //Entity craftPet = ((Entity) this.getCraftPet().getHandle());
        if (!flag) {
            if (this.getRider() != null) {
                //Entity rider = ((Entity) this.getRider().getCraftPet().getHandle());
                //rider.mount(null);
                INMS.getInstance().mount(this.getRider().getEntityPet().getBukkitEntity(), null);

                //craftPet.mount(null);
                INMS.getInstance().mount(this.getEntityPet().getBukkitEntity(), null);

                //rider.mount(craftPet);
                INMS.getInstance().mount(this.getRider().getEntityPet().getBukkitEntity(), this.getEntityPet().getBukkitEntity());
            } else {
                //craftPet.mount(null);
                INMS.getInstance().mount(this.getEntityPet().getBukkitEntity(), null);
            }
        } else {
            if (this.getRider() != null) {
                //Entity rider = ((Entity) this.getRider().getCraftPet().getHandle());
                //rider.mount(null);
                INMS.getInstance().mount(this.getRider().getEntityPet().getBukkitEntity(), null);

                //craftPet.mount(((CraftPlayer) this.getOwner()).getHandle());
                INMS.getInstance().mount(this.getEntityPet().getBukkitEntity(), this.getOwner());

                //this.getCraftPet().setPassenger(this.getRider().getCraftPet());
                INMS.getInstance().mount(this.getRider().getEntityPet().getBukkitEntity(), this.getEntityPet().getBukkitEntity());
            } else {
                //craftPet.mount(((CraftPlayer) this.getOwner()).getHandle());
                INMS.getInstance().mount(this.getEntityPet().getBukkitEntity(), this.getOwner());
            }
        }
        this.getEntityPet().resizeBoundingBox(flag);
        this.isHat = flag;
        Particle.PORTAL.show(getLocation());
        Location l = this.getLocation().clone();
        l.setY(l.getY() - 1D);
        Particle.PORTAL.show(getLocation());
        checkState(this.isRegistered(), "%s is no longer registered!", this);
    }

    @Override
    public Pet createRider(final PetType pt, boolean sendFailMessage) {
        if (pt == PetType.HUMAN) {
            if (sendFailMessage) {
                Lang.sendTo(this.getOwner(), Lang.RIDERS_DISABLED.toString().replace("%type%", petType.toPrettyString()));
            }
            return null;
        }
        if (!EchoPet.getOptions().allowRidersFor(this.getPetType())) {
            if (sendFailMessage) {
                Lang.sendTo(this.getOwner(), Lang.RIDERS_DISABLED.toString().replace("%type%", petType.toPrettyString()));
            }
            return null;
        }
        if (this.isOwnerRiding()) {
            this.ownerRidePet(false);
        }
        if (this.rider != null) {
            this.removeRider();
        }
        if (!petType.isSupported()) {
            if (sendFailMessage) {
                Lang.sendTo(getOwner(), Lang.PET_TYPE_NOT_COMPATIBLE.toString().replace("%type%", petType.toPrettyString()));
            }
            return null;
        }
        IPet newRider;
        try {
            newRider = pt.getNewPetInstance(this.getOwner());
        } catch (CancelledSpawnException e) {
            if (sendFailMessage) {
                e.sendMessage();
            }
            return null;
        }
        EchoPet.getManager().addSecondaryPet(newRider);
        this.rider = (Pet) newRider;
        this.rider.setRider();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isSpawned()) {
                    INMS.getInstance().mount(Pet.this.getRider().getCraftPet(), getCraftPet());
                }
                EchoPet.getSqlManager().saveToDatabase(Pet.this.rider, true);
            }
        }.runTaskLater(EchoPet.getPlugin(), 5L);

        return this.rider;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Pet(");
        Player player = Bukkit.getPlayer(ownerId);
        if (player != null) {
            builder.append("owner=");
            builder.append(player.getName());
            builder.append(", ");
        }
        builder.append("type=");
        builder.append(getPetType().name());
        builder.append(")");
        return builder.toString();
    }

    public boolean isRegistered() {
        return EchoPet.getPlugin().getPetManager().getPets().contains(this);
    }
}
