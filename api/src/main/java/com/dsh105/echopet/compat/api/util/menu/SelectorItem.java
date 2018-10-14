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

package com.dsh105.echopet.compat.api.util.menu;

import lombok.*;

import com.google.common.base.Preconditions;

import net.techcable.sonarpet.item.ItemData;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;


public enum SelectorItem {

    SELECTOR(Material.BONE, 1, ChatColor.GREEN + "Pets", ""),
    TOGGLE(Material.BONE, 1, ChatColor.YELLOW + "Toggle Pet", "toggle"),
    CALL(Material.ENDER_PEARL, 1, ChatColor.YELLOW + "Call Pet", "call"),
    RIDE(Material.CARROT_STICK, 1, ChatColor.YELLOW + "Ride Pet", "ride"),
    HAT(Material.IRON_HELMET, 1, ChatColor.YELLOW + "Hat Pet", "hat"),
    NAME(Material.NAME_TAG, 1, ChatColor.YELLOW + "Name Your Pet", "name"),
    MENU(Material.WORKBENCH, 1, ChatColor.YELLOW + "Open PetMenu", "menu"),
    CLOSE(Material.BOOK, 1, ChatColor.YELLOW + "Close", "select");

    private final String command;
    @Getter
    private final ItemData itemData;
    private final int amount;

    SelectorItem(Material type, int amount, String name, String command) {
        this(ItemData.create(type), amount, name, command);
    }

    SelectorItem(ItemData itemData, int amount, String name, String command) {
        Preconditions.checkNotNull(itemData, "Null item data");
        itemData = itemData.withDisplayName(Preconditions.checkNotNull(name, "Null name"));
        this.itemData = itemData;
        this.command = "pet " + Preconditions.checkNotNull(command, "Null command");
        this.amount = amount;
    }


    public String getCommand() {
        return command;
    }

    public Material getType() {
        return getItemData().getType();
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return getItemData().getDisplayName().get();
    }

    public ItemStack getItem() {
        return getItem(this.amount);
    }

    public ItemStack getItem(int amount) {
        return getItemData().createStack(amount);
    }
}