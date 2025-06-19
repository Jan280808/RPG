package de.jan.rpg.core.event;

import de.jan.rpg.api.item.combat.Weapon;
import de.jan.rpg.api.item.combat.data.WeaponType;
import de.jan.rpg.core.APIImpl;
import de.jan.rpg.core.item.combat.CoreCombatManager;
import de.jan.rpg.core.item.combat.RangeWeapon;
import de.jan.rpg.core.player.CorePlayerManager;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PlayerWeaponEvent implements Listener {

    private final CorePlayerManager corePlayerManager;
    private final CoreCombatManager coreCombatManager;

    public PlayerWeaponEvent(APIImpl api) {
        this.corePlayerManager = api.getCorePlayerManager();
        this.coreCombatManager = api.getCoreItemManager().getCoreCombatManager();
    }

    @EventHandler
    public void onInteractRangeWeapon(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if(itemStack == null) return;
        if(itemStack.getType().isAir()) return;

        Player player = event.getPlayer();
        if(player.hasCooldown(itemStack.getType())) return;

        Weapon weapon = coreCombatManager.getWeapon(itemStack);
        if(weapon == null) return;

        if(!weapon.getWeaponType().equals(WeaponType.RANGE)) return;
        RangeWeapon rangeWeapon = (RangeWeapon) weapon;


    }

    @EventHandler
    public void onProjectile(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if(event.getHitBlock() == null) return;

        if(projectile.getShooter() instanceof  Player) {
            projectile.remove();
        }
    }

    @EventHandler
    public void onInteractMageWeapon(PlayerInteractEvent event) {

    }
}
