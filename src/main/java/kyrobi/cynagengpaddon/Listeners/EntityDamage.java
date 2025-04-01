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

    static Set<EntityType> protectedMobs = new HashSet<>();

    public EntityDamage(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        protectedMobs.add(EntityType.EVOKER);
        protectedMobs.add(EntityType.ELDER_GUARDIAN);
        protectedMobs.add(EntityType.PIGLIN_BRUTE);
        protectedMobs.add(EntityType.SHULKER);
    }



    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e == null) {
            plugin.getLogger().warning("Received null EntityDamageByEntityEvent");
            return;
        }

        Entity entityBeingDamaged = e.getEntity();
        if (entityBeingDamaged == null) {
            plugin.getLogger().warning("Damaged entity is null");
            return;
        }

        if (!protectedMobs.contains(entityBeingDamaged.getType())) {
            return;
        }

        Player player = null;
        Entity damager = e.getDamager();

        if (damager == null) {
            plugin.getLogger().warning("Damager is null");
            return;
        }

        if (damager instanceof Player) {
            player = (Player) damager;
        }

        if (damager instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player) {
                player = (Player) shooter;
            }
        }

        if (player == null) {
            return;
        }

        if (!entityBeingDamaged.getType().isAlive()) {
            return;
        }

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(entityBeingDamaged.getLocation(), false, null);
        if (claim == null) {
            return;
        }

        ClaimPermission permission = claim.getPermission(player.getName());
        if (permission == null) {
            return;
        }

        if (permission.equals(ClaimPermission.Manage) || permission.equals(ClaimPermission.Build)) {
            return;
        }

        // Prevent damage if player is not the claim owner
        if (!claim.getOwnerID().equals(player.getUniqueId())) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't hurt this protected entity inside a claim.");
        }
    }
}
