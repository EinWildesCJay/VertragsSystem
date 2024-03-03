package de.EinWildesCJay.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemBuilder {

    ItemStack item;
    ItemMeta meta;

    public ItemBuilder(Material material){
        item = new ItemStack(material);
        meta = item.getItemMeta();
    }

    public ItemBuilder(Material material, Short id){
        item = new ItemStack(material, 1, id);
        meta = item.getItemMeta();
    }

    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(String... strings) {
        meta.setLore(Arrays.asList(strings));
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

}
