package net.techcable.sonarpet.particles

import com.dsh105.commodus.GeometryUtil
import net.techcable.sonarpet.utils.NmsVersion.*
import net.techcable.sonarpet.utils.Versioning
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*

@Deprecated(message = "Only needed for 1.8.8 compat.....")
abstract class ParticleBuilder protected constructor(
        var type: Particle,
        var speed: Float = 0f,
        var amount: Int = 0
): Cloneable {
    private var _blockType: Material? = null
    private var _blockMeta: Int = 0

    var blockType: Material?
        get() = _blockType
        set(value) {
            val oldValue = this._blockType
            this._blockType = value
            if (oldValue == null && value != null) {
                this._blockMeta = 0
            }
        }
    var blockMeta: Int
        get() {
            checkNotNull(blockType) { "No block data present!" }
            return _blockMeta
        }
        set(value) {
            checkNotNull(blockType) { "No block data present!" }
            this._blockMeta = value
        }

    fun hasBlockData() = blockType != null
    var offset = Vector.getRandom()

    fun setOffset(x: Double, y: Double, z: Double) {
        this.offset = Vector(x, y, z)
    }
    fun randomOffset(random: Random) = setOffset(random.nextDouble(), random.nextDouble(), random.nextDouble())
    fun setPosition(world: World, x: Double, y: Double, z: Double) {
        this.position = Location(world, x, y, z)
    }

    abstract fun show(player: Player)

    fun show() {
        for (player in GeometryUtil.getNearbyPlayers(this.position, 50)) {
            this.show(player)
        }
    }

    private var _position: Location? = null
    var position: Location
        get() = checkNotNull(_position) { "Position not set!" }.clone() // Defensive copy
        set(value) {
            requireNotNull(value.world)
            _position = value.clone() // Defensive copy
        }

    override fun clone() = super.clone() as ParticleBuilder

    companion object {
        @JvmStatic
        fun create(type: Particle, speed: Float, amount: Int): ParticleBuilder {
            return when (Versioning.NMS_VERSION!!) {
                v1_12_R1, v1_11_R1, v1_10_R1, v1_9_R1, v1_9_R2, v1_13_R2 -> {
                    BukkitParticleBuilder(type, speed, amount)
                }
                 -> v18ParticleBuilder(type, speed, amount)
            }
        }
    }
}
