/*
 * This file is part of EchoPet.
 *
 * EchoPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EchoPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EchoPet.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.echopet.compat.api.util.inventory;

import lombok.*;

import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.techcable.sonarpet.item.ItemData;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SpawnEgg;

public class MenuIcon {

    private final int slot;
    @Getter
    private final ItemData itemData;

    public MenuIcon(int slot, ItemData itemData) {
        this.slot = slot;
        this.itemData = itemData;
    }

    public int getSlot() {
        return slot;
    }

    public Material getType() {
        return itemData.getType();
    }

    public String getName() {
        return getItemData().getDisplayName().orElse("");
    }

    public ImmutableList<String> getLore() {
        return getItemData().getLore();
    }

    public ItemMeta getItemMeta() {
        return getItemData().getMeta();
    }

    public ItemStack getIcon(Player viewer) {
        return getItemData().createStack(1);
    }

    public void onClick(Player viewer) {

    }
}