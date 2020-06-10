package net.techcable.sonarpet.nms;

import net.techcable.sonarpet.utils.NmsVersion;
import org.bukkit.Material;

public interface IModernNMS extends INMS {
    NmsVersion MINIMUM_VERSION = NmsVersion.v1_13_R2;
    Material getMaterialByLegacyId(int id);
    Object getBukkitBlockData(Material m, byte b);
    static IModernNMS getInstance() {
        NmsVersion.ensureAtLeast(MINIMUM_VERSION);
        return (IModernNMS) INMS.getInstance();
    }
}
