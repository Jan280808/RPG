package de.jan.rpg.core.command.sub;

import de.jan.rpg.api.item.ItemRarity;
import de.jan.rpg.api.item.combat.Melee;
import de.jan.rpg.api.item.combat.Range;
import de.jan.rpg.api.item.combat.Status;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.command.CoreCommands;
import de.jan.rpg.core.item.combat.CoreCombatManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ItemCommand implements CoreCommands {

    @Override
    public void onCommand(APIImpl api, Player player, String[] args) {
        CoreCombatManager combatManager = api.getCoreItemManager().getCoreCombatManager();

        if(args.length == 2) {
            if(args[1].equalsIgnoreCase("give")) {
                Arrays.stream(Melee.Cooldown.values()).forEach(cooldown -> {
                    Status.Type randomStatus = Status.Type.values()[new Random().nextInt(Status.Type.values().length)];
                    Melee melee = combatManager.createMeleeWeapon(1, "melee.potato", Material.DIAMOND_SWORD, randomStatus, ItemRarity.LEGENDARY, 1, 3, 0.3, 1, 0, cooldown);
                    ItemStack itemStack = melee.getItemStack();
                    player.getInventory().addItem(itemStack);
                });
                Range range = combatManager.createRangeWeapon(1, "melee.potato", Material.BOW, Status.Type.FROST, ItemRarity.LEGENDARY, 5, 8, 0.3, 1, 0, 1, 1);
                ItemStack itemStack = range.getItemStack();
                player.getInventory().addItem(itemStack);
            }
            if(args[1].equalsIgnoreCase("read")) {
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if(itemStack.getType().isAir()) {
                    player.sendMessage("Take a Weapon in your MainHand");
                    return;
                }
                Melee melee = combatManager.getMeleeWeapon(itemStack);
                player.sendMessage("melee: " + melee);
            }
        }
    }

    @Override
    public List<String> subCommands() {
        return List.of("give", "read", "convert", "armor", "readStack");
    }
}
