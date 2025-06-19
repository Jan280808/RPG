package de.jan.rpg.core.event;

import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.item.CoreItemManager;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.event.Listener;

public class PlayerChanceArmorEvent implements Listener {

    private final CorePlayerManager corePlayerManager;
    private final CoreItemManager coreItemManager;

    public PlayerChanceArmorEvent(APIImpl api) {
        this.corePlayerManager = api.getCorePlayerManager();
        this.coreItemManager = api.getCoreItemManager();
    }

    /*
    @EventHandler
    public void onChance(PlayerArmorChangeEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        CorePlayer corePlayer = corePlayerManager.getCorePlayer(uuid);
        CoreArmor newArmor = coreItemManager.getCoreArmor(event.getNewItem());
        CoreArmor oldArmor = coreItemManager.getCoreArmor(event.getOldItem());

        Bukkit.getPluginManager().callEvent(new RPGPlayerChanceArmorEvent(corePlayer, oldArmor, newArmor));
    }

    @EventHandler
    public void onChanceArmor(RPGPlayerChanceArmorEvent event) {
        RPGPlayer rpgPlayer = event.getRpgPlayer();
        Armor oldArmor = event.getOldArmor();
        Armor newArmor = event.getNewArmor();

        if(oldArmor != null) {
            rpgPlayer.removeArmor(oldArmor.getArmorValue());
            rpgPlayer.removeMaxLife(oldArmor.getExtraLife());
        }
        if(newArmor != null) {
            rpgPlayer.addArmor(newArmor.getArmorValue());
            rpgPlayer.addMaxLife(newArmor.getExtraLife());
        }
    }

     */
}
