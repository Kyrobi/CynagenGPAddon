package kyrobi.cynagengpaddon.Listeners;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Storage.ClaimData;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;

public class EntityDamage implements Listener {
    CynagenGPAddon plugin;

    Set<EntityType> protectedMobs = new HashSet<>();

    public EntityDamage(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        protectedMobs.add(EntityType.EVOKER);
        protectedMobs.add(EntityType.ELDER_GUARDIAN);
        protectedMobs.add(EntityType.PIGLIN_BRUTE);
        protectedMobs.add(EntityType.SHULKER);
    }



    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageByEntityEvent e){
        Entity entityBeingDamaged = e.getEntity();

        if(!protectedMobs.contains(entityBeingDamaged.getType())){
            return;
        }

        Player player = null;
        if(e.getDamager() instanceof Player){
            player = (Player) e.getDamager();
        }

        // Check for projectiles
        if(e.getDamager() instanceof Projectile projectile){
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player) {
               player = ((Player) shooter).getPlayer();
            }
        }

        if(player == null){
            return;
        }

        if (entityBeingDamaged.getType().isAlive()) { // Mobs online

            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(entityBeingDamaged.getLocation(), false, null);
            ClaimPermission permission = claim.getPermission(String.valueOf(player));

            // Ignore if has trust to the claim
            if((permission != null) && (permission.equals(ClaimPermission.Manage) || permission.equals(ClaimPermission.Build))){
                return;
            }

            // Ignore owner
            if(claim.getOwnerID() == player.getUniqueId()){
                return;
            }

            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't hurt this entity inside a claim.");
        }
    }
}
