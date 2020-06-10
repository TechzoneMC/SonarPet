package net.techcable.sonarpet.item.legacy;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import javax.annotation.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class BasicSerializedItem {
    @NonNull
    private final Material type;
    private final byte rawData;
    private final ImmutableList<String> lore;
    @Nullable
    private final String displayName;

    private JsonObject serialize() {
        JsonObject result = new JsonObject();
        result.addProperty("type", this.type.name());
        result.addProperty("rawData", this.rawData);
        if (!this.lore.isEmpty()) {
            JsonArray lore = new JsonArray();
            for (String element : this.lore) {
                lore.add(element);
            }
            result.add("lore", lore);
        }
        if (this.displayName != null) {
            result.addProperty("displayName", this.displayName);
        }
        return result;
    }
    public String serializeAsString() {
        return "legacy(" + this.serialize() + ")";
    }
    public static boolean isLegacyItem(String s) {
        return s.startsWith("legacy(");
    }

    public static BasicSerializedItem deserializeFromString(String s) {
        if (s.startsWith("legacy(") && s.endsWith(")")) {
            String inner = s.substring("legacy(".length(), s.length() - 1);
            try {
                return new Gson().fromJson(inner, BasicSerializedItem.class);
            } catch (JsonSyntaxException e) {
                throw new IllegalArgumentException(
                        "Invalid legacy item: " + s,
                        e
                );
            }
        } else {
            throw new IllegalArgumentException("Not a legacy item: " + s);
        }

    }
}
