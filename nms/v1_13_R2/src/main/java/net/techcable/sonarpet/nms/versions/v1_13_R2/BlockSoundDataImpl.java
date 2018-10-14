package net.techcable.sonarpet.nms.versions.v1_13_R2;

import lombok.*;

import net.minecraft.server.v1_13_R2.SoundEffectType;
import net.techcable.sonarpet.nms.BlockSoundData;

@RequiredArgsConstructor
public class BlockSoundDataImpl implements BlockSoundData {
    private final SoundEffectType handle;

    @Override
    public float getVolume() {
        // MCP: SoundType.getVolume
        return handle.a();
    }

    @Override
    public float getPitch() {
        // MCP: SoundType.getPitch
        return handle.b();
    }

    @Override
    public boolean equals(Object o) { // We need this!!
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockSoundDataImpl that = (BlockSoundDataImpl) o;

        return handle.equals(that.handle);
    }

    @Override
    public int hashCode() {
        return handle.hashCode();
    }
}

