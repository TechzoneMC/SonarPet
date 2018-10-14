package net.techcable.sonarpet.item;

public class LegacyMaterialSystem implements MaterialSystem {
    @SuppressWarnings("deprecation") // this is legacy
    @Override
    public String serializeData(ItemData m) {
        byte data = m.getLegacyMaterialData().getData();
        int id = m.getType().getId();
        return id + ":" + data;
    }

    @Override
    public ItemData parseData(String s) {
        String[] parts = s.split(":");
        if (parts.length == 2) {
            try {
                int id = Integer.parseInt(parts[0]);
                byte data = Byte.parseByte(parts[1]);
            } catch (NumberFormatException ignored) {}
        }
        throw new IllegalArgumentException("Invalid item data: " + s);
    }
}
