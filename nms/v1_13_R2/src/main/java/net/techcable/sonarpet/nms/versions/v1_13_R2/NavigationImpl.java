package net.techcable.sonarpet.nms.versions.v1_13_R2;

import lombok.*;

import net.minecraft.server.v1_13_R2.Navigation;
import net.minecraft.server.v1_13_R2.NavigationAbstract;
import net.techcable.sonarpet.nms.PathEntity;

import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;

@RequiredArgsConstructor
public class NavigationImpl implements net.techcable.sonarpet.nms.Navigation {
    private final NavigationAbstract handle;


    //
    // Breakage likely, check for bugs here
    //

    @Override
    public boolean canEnterDoors() {
        return handle.s().c(); // MCP: PathNavigate.getNodeProcessor().getCanEnterDoors()
    }

    @Override
    public void finish() {
        handle.p(); // MCP: PathNavigate.noPath
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
        /* package */ final net.minecraft.server.v1_13_R2.PathEntity handle;
    }
}
