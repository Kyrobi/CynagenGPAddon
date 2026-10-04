package kyrobi.cynagengpaddon.Features;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.ChatColor;
import org.bukkit.entity.Cushion;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPlaceEvent;

import java.util.function.Supplier;

public class CushionProtection implements Listener {
    private final CynagenGPAddon plugin;

    public CushionProtection(final CynagenGPAddon plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // Placing a cushion blocks the support block, so check the claim at that block
    @EventHandler
    public void onCushionPlace(EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Cushion)) {
            return;
        }

        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getBlock().getLocation(), false, null);
        if (claim == null) {
            return;
        }

        String denial = denialReason(player, claim, event);
        if (denial != null) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot place cushions inside a claim you don't have build access to.");
        }
    }

    // Cushions break immediately when attacked, even with 0 damage, so cancel the damage event
    @EventHandler
    public void onCushionDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Cushion)) {
            return;
        }

        Player player = getAttacker(event.getDamager());
        if (player == null) {
            return;
        }

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getEntity().getLocation(), false, null);
        if (claim == null) {
            return;
        }

        String denial = denialReason(player, claim, event);
        if (denial != null) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot break cushions inside a claim you don't have build access to.");
        }
    }

    // Respects ignoreClaims mode, mirrors GriefPrevention's own permission flow
    private String denialReason(Player player, Claim claim, org.bukkit.event.Event event) {
        if (GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).ignoreClaims) {
            return null;
        }

        Supplier<String> denial = claim.checkPermission(player, ClaimPermission.Build, event);
        return (denial == null) ? null : denial.get();
    }

    private Player getAttacker(org.bukkit.entity.Entity damager) {
        if (damager instanceof Player) {
            return (Player) damager;
        }

        if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player) {
            return (Player) ((Projectile) damager).getShooter();
        }

        return null;
    }
}
