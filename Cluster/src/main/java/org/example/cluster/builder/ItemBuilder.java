package org.example.cluster.builder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    /**
     *  The ItemBuilder offer many function to create &. modify an ItemStack
     */

    private final ItemStack stack;
    private final ItemMeta meta;
    private final Damageable damageable;

    public ItemBuilder(Material material) {
        this.stack = new ItemStack(material);
        this.meta = stack.getItemMeta();
        this.damageable = (Damageable) meta;
    }

    /**
     * set the displayName of an ItemStack
     * */


    public ItemBuilder setDisplayName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    /**
     * creates a list which will be added to the lore
     */

    public ItemBuilder setLore(String... name) {
        List<String> loreList = new ArrayList<>();
        Collections.addAll(loreList, name);
        meta.setLore(loreList);
        return this;
    }

    public ItemBuilder setDurability(int durability) {
        damageable.setDamage(durability);
        return this;
    }


    /**
     * adds a SkullMeta to the ItemStack
     */

    public ItemBuilder setSkull(String skullOwner) {
        if(stack.getType() != Material.PLAYER_HEAD)
            return this;
        SkullMeta skullMeta = (SkullMeta) meta;
        PlayerProfile playerProfile = Bukkit.createPlayerProfile(UUID.randomUUID(), skullOwner);
        ((SkullMeta) meta).setOwnerProfile(playerProfile.clone());
        stack.setItemMeta(skullMeta);
        return this;
    }

    public ItemBuilder modifyItemFlag(ItemFlag itemFlag) {
        meta.addItemFlags(itemFlag);
        return this;
    }

    /**
     * returns an ItemStack
     */

    public ItemStack build() {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }


}
