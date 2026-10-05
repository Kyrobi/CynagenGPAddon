package kyrobi.cynagengpaddon.Features;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Cushion;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class CushionProtection implements Listener {
    private final CynagenGPAddon plugin;

    /*
    Paper 26.3 removes the cushion (EntityRemoveEvent cause=DEATH) BEFORE dispatching
    PrePlayerAttackEntityEvent and ignores that event's cancellation, so the destroy
    can't be prevented. Two-part undo: remember the denied hit for 10s, then
    - respawn the cushion if that exact entity no longer exists (uuid check), and
    - cancel any cushion item drop that appears near it (drop can lag behind the
      removal, which would otherwise dupe the cushion).
     */
    private final Map<PendingKey, Long> pendingDeniedBreaks = new ConcurrentHashMap<>();

    private record PendingKey(String world, int x, int y, int z) {
        static PendingKey of(Location location) {
            return new PendingKey(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }

        boolean isNear(PendingKey other) {
            return world.equals(other.world)
                    && Math.abs(x - other.x) <= 1
                    && Math.abs(y - other.y) <= 1
                    && Math.abs(z - other.z) <= 1;
        }
    }

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
            player.sendMessage(ChatColor.RED + denial);
        }
    }

    // Melee break attempt on a cushion. Fires AFTER the server already removed it.
    @EventHandler
    public void onCushionAttack(PrePlayerAttackEntityEvent event) {
        if (!(event.getAttacked() instanceof Cushion)) {
            return;
        }

        checkBreakAttempt(event, event.getPlayer(), (Cushion) event.getAttacked());
    }

    // Fallback for damage-based breaks (e.g. projectiles, mobs)
    @EventHandler
    public void onCushionDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Cushion)) {
            return;
        }

        Player player = getAttacker(event);
        if (player == null) {
            return;
        }

        checkBreakAttempt(event, player, (Cushion) event.getEntity());
    }

    // Catch the destroyed cushion's self-drop (drops can lag behind the removal)
    @EventHandler
    public void onCushionDrop(ItemSpawnEvent event) {
        if (!(event.getEntity() instanceof Item item)) {
            return;
        }

        ItemStack stack = item.getItemStack();
        if (stack == null || !stack.getType().name().endsWith("_CUSHION")) {
            return;
        }

        PendingKey dropKey = PendingKey.of(event.getLocation());
        for (Map.Entry<PendingKey, Long> pending : pendingDeniedBreaks.entrySet()) {
            if (pending.getValue() > System.currentTimeMillis() && pending.getKey().isNear(dropKey)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    private void checkBreakAttempt(Cancellable event, Player player, Cushion cushion) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(cushion.getLocation(), false, null);
        if (claim == null) {
            return;
        }

        String denial = denialReason(player, claim, (org.bukkit.event.Event) event);
        if (denial == null) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + denial);

        UUID removedId = cushion.getUniqueId();
        Location location = cushion.getLocation().clone();
        DyeColor dye = colorOf(cushion);
        pendingDeniedBreaks.put(PendingKey.of(location), System.currentTimeMillis() + 10_000);
        pruneExpired();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Entity remainder = Bukkit.getEntity(removedId);
            if (remainder != null && remainder.isValid()) {
                return;
            }

            try {
                Entity respawned = location.getWorld().spawnEntity(location, EntityType.CUSHION);
                if (respawned instanceof Colorable && ((Colorable) respawned).getColor() != dye) {
                    ((Colorable) respawned).setColor(dye);
                }
                plugin.getLogger().info("[CushionProtection] Restored a cushion that was broken despite denial");
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("[CushionProtection] Could not respawn cushion: " + e.getMessage());
            }
        }, 1L);
    }

    // Respects ignoreClaims mode, mirrors GriefPrevention's own permission flow
    private String denialReason(Player player, Claim claim, org.bukkit.event.Event event) {
        if (GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).ignoreClaims) {
            return null;
        }

        Supplier<String> denial = claim.checkPermission(player, ClaimPermission.Build, event);
        return (denial == null) ? null : denial.get();
    }

    private DyeColor colorOf(Cushion cushion) {
        return (cushion instanceof Colorable colorable) ? colorable.getColor() : DyeColor.WHITE;
    }

    private void pruneExpired() {
        long now = System.currentTimeMillis();
        for (Iterator<Map.Entry<PendingKey, Long>> it = pendingDeniedBreaks.entrySet().iterator(); it.hasNext(); ) {
            if (it.next().getValue() < now) {
                it.remove();
            }
        }
    }

    private Player getAttacker(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)) {
            return null;
        }

        Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
        if (damager instanceof Player) {
            return (Player) damager;
        }

        if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player) {
            return (Player) ((Projectile) damager).getShooter();
        }

        return null;
    }
}
