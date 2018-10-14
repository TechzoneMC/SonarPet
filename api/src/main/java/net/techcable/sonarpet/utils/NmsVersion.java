package net.techcable.sonarpet.utils;

import lombok.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.dsh105.commodus.StringUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//ToDo: Version Fix Here
@Getter
public enum NmsVersion {
    v1_8_R3,
    v1_9_R1,
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R2;

    public static final NmsVersion LATEST, EARLIEST;
    private ImmutableMap<String, Integer> metadata;
    private ImmutableMap<String, String> obfuscatedMethods, obfuscatedFields;
    public int getMetadataId(String name) {
        if (metadata == null) loadData();
        Integer id = metadata.get(name);
        if (id == null) {
            throw new IllegalArgumentException("Metadata " + name + " is unknown for version " + this);
        }
        return id;
    }
    @Nonnull
    public String getObfuscatedMethod(String id) {
        if (obfuscatedMethods == null) loadData();
        String name = obfuscatedMethods.get(id);
        if (name == null) {
            throw new IllegalArgumentException("Obfuscated method " + id + " is unknown for " + this);
        }
        return name;
    }
    @Nullable
    public String tryGetObfuscatedField(String id) {
        if (obfuscatedFields == null) loadData();
        return obfuscatedFields.get(id);

    }
    @Nonnull
    public String getObfuscatedField(String id) {
        String name = tryGetObfuscatedField(id);
        if (name == null) {
            throw new IllegalArgumentException("Obfuscated field " + id + " is unknown for " + this);
        }
        return name;
    }
    @SneakyThrows(IOException.class)
    private void loadData() {
        URL url = getClass().getResource("/versions/" + this + ".json");
        if (url == null) {
            throw new IllegalArgumentException("No version data for " + this);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), Charsets.UTF_8))) {
            JsonObject base = new JsonParser().parse(reader).getAsJsonObject();
            JsonObject metadataObject = base.getAsJsonObject("metadata");
            ImmutableMap.Builder<String, Integer> metadata = ImmutableMap.builder();
            for (Map.Entry<String, JsonElement> entry : metadataObject.entrySet()) {
                metadata.put(entry.getKey(), entry.getValue().getAsInt());
            }
            this.metadata = metadata.build();
            JsonObject obfuscatedMethodsObject = base.getAsJsonObject("obfuscated_methods");
            ImmutableMap.Builder<String, String> obfuscatedMethods = ImmutableMap.builder();
            for (Map.Entry<String, JsonElement> entry : obfuscatedMethodsObject.entrySet()) {
                obfuscatedMethods.put(entry.getKey(), entry.getValue().getAsString());
            }
            this.obfuscatedMethods = obfuscatedMethods.build();
            JsonObject obfuscatedFieldsObject = base.getAsJsonObject("obfuscated_fields");
            ImmutableMap.Builder<String, String> obfuscatedFields = ImmutableMap.builder();
            for (Map.Entry<String, JsonElement> entry : obfuscatedFieldsObject.entrySet()) {
                obfuscatedFields.put(entry.getKey(), entry.getValue().getAsString());
            }
            this.obfuscatedFields = obfuscatedFields.build();
        }
    }

    static {
        LATEST = values()[values().length - 1];
        EARLIEST = values()[0];
    }

    public static NmsVersion parse(String text) {
        switch (text) {
            case "EARLIEST":
            case "LATEST":
            default:
                try {
                    return valueOf(text);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid version: " + text);
                }
        }
    }

    private final int majorVersion = parseMajorVersion(name());
    private static int parseMajorVersion(String s) {
        return Integer.parseInt(s.split("_")[1]);
    }

    public static NmsVersion current() {
        return Versioning.NMS_VERSION;
    }

    public boolean isAtLeast(NmsVersion minimum) {
        return this.compareTo(minimum) >= 0;
    }
    public boolean isBefore(NmsVersion other) {
        return this.compareTo(other) < 0;
    }
    public static void ensureAtLeast(NmsVersion minimum) {
        NmsVersion current = NmsVersion.current();
        if (!current.isAtLeast(minimum)) {
            throw new IllegalStateException("Expected NMS version at least "
                    + minimum
                    + ", but got "
                    + current);
        }
    }
    public static void ensureBefore(NmsVersion maximum) {
        NmsVersion current = NmsVersion.current();
        if (!current.isBefore(maximum)) {
            throw new IllegalStateException("Expected NMS version before "
                    + maximum
                    + ", but got "
                    + current);
        }
    }
}
