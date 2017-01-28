package net.techcable.sonarpet;

import java.util.Arrays;
import java.util.EnumMap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import org.bukkit.Sound;

/**
 * A version independent enumeration of sounds.
 */
public enum SafeSound {
    BLAZE_AMBIENT("ENTITY_BLAZE_AMBIENT", "BLAZE_BREATH"),
    BLAZE_DEATH("ENTITY_BLAZE_DEATH", "BLAZE_DEATH"),
    CHICKEN_AMBIENT("ENTITY_CHICKEN_AMBIENT", "CHICKEN_IDLE"),
    CHICKEN_DEATH("ENTITY_CHICKEN_DEATH", "CHICKEN_HURT"),
    CHICKEN_STEP("ENTITY_CHICKEN_STEP", "CHICKEN_WALK"),
    COW_AMBIENT("ENTITY_COW_AMBIENT", "COW_IDLE"),
    COW_DEATH("ENTITY_COW_DEATH", "COW_HURT"),
    COW_STEP("ENTITY_COW_STEP", "COW_WALK"),
    DONKEY_AMBIENT("ENTITY_DONKEY_AMBIENT", "DONKEY_IDLE"),
    DONKEY_ANGRY("ENTITY_DONKEY_ANGRY", "DONKEY_ANGRY"),
    DONKEY_DEATH("ENTITY_DONKEY_DEATH", "DONKEY_DEATH"),
    ENDERMITE_STEP("ENTITY_ENDERMITE_STEP", "SILVERFISH_WALK"), // SILVERFISH_WALK is the cloest we have on 1.8.8 :(
    GHAST_AMBIENT("ENTITY_GHAST_AMBIENT", "GHAST_MOAN"),
    GHAST_DEATH("ENTITY_GHAST_DEATH", "GHAST_DEATH"),
    HORSE_AMBIENT("ENTITY_HORSE_AMBIENT", "HORSE_IDLE"),
    HORSE_ANGRY("ENTITY_HORSE_ANGRY", "HORSE_ANGRY"),
    HORSE_BREATHE("ENTITY_HORSE_BREATHE", "HORSE_BREATHE"),
    HORSE_GALLOP("ENTITY_HORSE_GALLOP", "HORSE_GALLOP"),
    HORSE_JUMP("ENTITY_HORSE_JUMP", "HORSE_JUMP"),
    HORSE_STEP_WOOD("ENTITY_HORSE_STEP_WOOD", "HORSE_WOOD"),
    HORSE_STEP("ENTITY_HORSE_STEP", "HORSE_SOFT"),
    IRONGOLEM_ATTACK("ENTITY_IRONGOLEM_ATTACK", "IRONGOLEM_DEATH"),
    IRONGOLEM_DEATH("ENTITY_IRONGOLEM_DEATH", "IRONGOLEM_DEATH"),
    IRONGOLEM_STEP("ENTITY_IRONGOLEM_STEP", "IRONGOLEM_WALK"),
    PIG_AMBIENT("ENTITY_PIG_AMBIENT", "PIG_IDLE"),
    PIG_DEATH("ENTITY_PIG_DEATH", "PIG_DEATH"),
    PIG_STEP("ENTITY_PIG_STEP", "PIG_WALK"),
    SHEEP_STEP("ENTITY_SHEEP_STEP", "SHEEP_WALK"),
    SILVERFISH_AMBIENT("ENTITY_SILVERFISH_AMBIENT", "SILVERFISH_IDLE"),
    SILVERFISH_DEATH("ENTITY_SILVERFISH_DEATH", "SILVERFISH_KILL"),
    SILVERFISH_STEP("ENTITY_SILVERFISH_DEATH", "SILVERFISH_WALK"),
    SKELETON_AMBIENT("ENTITY_SKELETON_AMBIENT", "SKELETON_IDLE"),
    SKELETON_DEATH("ENTITY_SKELETON_DEATH", "SKELETON_DEATH"),
    SKELETON_HORSE_AMBIENT("ENTITY_SKELETON_HORSE_AMBIENT", "HORSE_SKELETON_IDLE"),
    SKELETON_HORSE_DEATH("ENTITY_SKELETON_HORSE_DEATH", "HORSE_SKELETON_DEATH"),
    SKELETON_STEP("ENTITY_SKELETON_STEP", "SKELETON_WALK"),
    SPIDER_AMBIENT("ENTITY_SPIDER_AMBIENT", "SPIDER_IDLE"),
    SPIDER_DEATH("ENTITY_SPIDER_DEATH", "SPIDER_DEATH"),
    SPIDER_STEP("ENTITY_SPIDER_STEP", "SPIDER_IDLE"),
    WITHER_AMBIENT("ENTITY_WITHER_AMBIENT", "WITHER_IDLE"),
    WITHER_DEATH("ENTITY_WITHER_DEATH", "WITHER_DEATH"),
    WOLF_SHAKE("ENTITY_WOLF_SHAKE", "WOLF_SHAKE"),
    VILLAGER_AMBIENT("ENTITY_VILLAGER_AMBIENT", "VILLAGER_IDLE"),
    VILLAGER_DEATH("ENTITY_VILLAGER_DEATH", "VILLAGER_DEATH"),
    ZOMBIE_AMBIENT("ENTITY_ZOMBIE_AMBIENT", "ZOMBIE_IDLE"),
    ZOMBIE_DEATH("ENTITY_ZOMBIE_DEATH", "ZOMBIE_DEATH"),
    ZOMBIE_HORSE_DEATH("ENTITY_ZOMBIE_HORSE_DEATH", "HORSE_ZOMBIE_DEATH"),
    ZOMBIE_STEP("ENTITY_ZOMBIE_STEP", "ZOMBIE_WALK");

    private final Sound sound;

    /* private */ SafeSound(String... names) {
        Sound sound = null;
        for (String name : names) {
            try {
                if (sound != null) {
                    Sound.valueOf(name);
                    throw new IllegalArgumentException("Sounds named " + name + " and " + sound.name() + " were both found.");
                } else {
                    sound = Sound.valueOf(name);
                }
            } catch (IllegalArgumentException ignroed) {}
        }
        if (sound == null) throw new IllegalArgumentException("No sounds named " + Arrays.toString(names) + " are found!");
        this.sound = sound;
    }

    public Sound getBukkitSound() {
        return sound;
    }
}
