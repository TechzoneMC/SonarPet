package net.techcable.sonarpet.nms.entity.type

import com.dsh105.echopet.compat.api.entity.IPet
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityParrotPet
import net.techcable.sonarpet.EntityHook
import net.techcable.sonarpet.EntityHookType
import net.techcable.sonarpet.nms.NMSInsentientEntity
import net.techcable.sonarpet.nms.entity.EntityInsentientPet
import org.bukkit.entity.Parrot

@EntityHook(EntityHookType.PARROT)
class EntityParrotPet internal constructor(
        pet: IPet,
        entity: NMSInsentientEntity,
        hookType: EntityHookType
) : EntityInsentientPet(pet, entity, hookType), IEntityParrotPet {
    override fun initiateEntityPet() {
        super.initiateEntityPet()
        // If we're sitting, we can't move
        (this.bukkitEntity as Parrot).isSitting = false
    }
}