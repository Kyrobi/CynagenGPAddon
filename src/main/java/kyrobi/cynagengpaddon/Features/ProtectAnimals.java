package kyrobi.cynagengpaddon.Features;

import com.earth2me.essentials.spawn.EssentialsSpawn;
import com.google.common.util.concurrent.Service;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class ProtectAnimals implements Listener {
    private CynagenGPAddon plugin;

    public ProtectAnimals(final CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damagedEntity = event.getEntity();

        if(!(damagedEntity instanceof Tameable)) {
            return;
        }

        Entity damager = event.getDamager();

        Tameable tameableEntity = (Tameable) damagedEntity;

        // If damage is being done to a tamed mob
        if (tameableEntity.isTamed() || tameableEntity.isCustomNameVisible()) {

            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(tameableEntity.getLocation(), false, null);

            // If the animal isn't inside a claim, don't protect it
            if(claim == null){ return; }

            // If the player is doing damage to the animal that's inside a claim
            if(damager instanceof Player){
                Player currentPlayer = ((Player) damager).getPlayer();

                // If the player is ignoring claims, don't do anything
                boolean isIgnoringClaim = GriefPrevention.instance.dataStore.getPlayerData(currentPlayer.getUniqueId()).ignoreClaims;
                if(isIgnoringClaim){
                    return;
                }

                // Allow the claim owner to attack the pets
                if(claim.getOwnerID().equals(currentPlayer.getUniqueId())){
                    return;
                }

                ClaimPermission permission = claim.getPermission(String.valueOf(currentPlayer));
                if((permission != null) && (permission.equals(ClaimPermission.Manage) || permission.equals(ClaimPermission.Build))){
                    return;
                }

                event.setCancelled(true);
                currentPlayer.sendMessage(ChatColor.RED + "You cannot attack pets that are inside a claim.");

            }
        }


    }
}
