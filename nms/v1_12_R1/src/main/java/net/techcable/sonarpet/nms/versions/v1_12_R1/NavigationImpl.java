package net.techcable.sonarpet.nms.versions.v1_12_R1;

import lombok.*;

import net.minecraft.server.v1_12_R1.Navigation;
import net.minecraft.server.v1_12_R1.NavigationAbstract;
import net.techcable.sonarpet.nms.PathEntity;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

@RequiredArgsConstructor
public class NavigationImpl implements net.techcable.sonarpet.nms.Navigation {
    private final NavigationAbstract handle;


    //
    // Breakage likely, check for bugs here
    //

    @Override
    public boolean canEnterDoors() {
        return handle.r().c(); // PathNavigate.getNodeProcessor().getCanEnterDoors()
    }

    @Override
    public void finish() {
        handle.o(); // PathNavigate.noPath
    }

    //
    // Unlikely to break, even across major versions
    // IE: never broken yet ^_^
    //

    @Override
    public PathEntity getPathToLocation(int blockX, int blockY, int blockZ) {
        return new PathEntityImpl(handle.a(blockX, blockY, blockZ));
    }

    @Override
    public PathEntity getPathTo(Entity other) {
        return new PathEntityImpl(handle.a(((CraftEntity) other).getHandle()));
    }

    @Override
    public void navigateTo(PathEntity path, double speed) {
        handle.a(((PathEntityImpl) path).handle, speed);
    }

    @RequiredArgsConstructor
    private static class PathEntityImpl implements PathEntity {
        /* package */ final net.minecraft.server.v1_12_R1.PathEntity handle;
    }
}
