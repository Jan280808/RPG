package de.jan.rpg.core.command.sub;

import de.jan.rpg.api.component.ComponentSerializer;
import de.jan.rpg.api.item.ItemRarity;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import de.jan.rpg.core.item.CoreItemManager;
import de.jan.rpg.core.item.combat.CoreArmor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class ItemCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        CoreItemManager coreItemManager = api.getCoreItemManager();

        if(args.length == 2) {
            if(args[1].equalsIgnoreCase("give")) {
                coreItemManager.giveItem(player);
            }
            if(args[1].equalsIgnoreCase("read")) {
                coreItemManager.readItemStack(player, player.getInventory().getItemInMainHand());
            }
            if(args[1].equalsIgnoreCase("convert")) {
                coreItemManager.convertItem(player);
            }
            if(args[1].equalsIgnoreCase("armor")) {
                CoreArmor helm = new CoreArmor(1, ComponentSerializer.deserialize("Armor"), Material.TURTLE_HELMET, ItemRarity.LEGENDARY, 5, 100);
                CoreArmor chest = new CoreArmor(1, ComponentSerializer.deserialize("Armor"), Material.CHAINMAIL_CHESTPLATE, ItemRarity.COMMON, 10, 10);
                CoreArmor leggings = new CoreArmor(1, ComponentSerializer.deserialize("Armor"), Material.GOLDEN_LEGGINGS, ItemRarity.UNCOMMON, 5, 0);
                CoreArmor boots = new CoreArmor(1, ComponentSerializer.deserialize("Armor"), Material.DIAMOND_BOOTS, ItemRarity.EPIC, 3, 5);
                player.getInventory().addItem(helm.getItemStack());
                player.getInventory().addItem(chest.getItemStack());
                player.getInventory().addItem(leggings.getItemStack());
                player.getInventory().addItem(boots.getItemStack());
            }
        }
        coreItemManager.giveItem(player);
    }

    @Override
    public List<String> subCommands() {
        return List.of("give", "read", "convert", "armor");
    }
}
