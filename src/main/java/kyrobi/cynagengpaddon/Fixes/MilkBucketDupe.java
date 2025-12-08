package kyrobi.cynagengpaddon.Fixes;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Storage.ClaimData;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;
import static org.bukkit.Bukkit.getLogger;

public class MilkBucketDupe implements Listener {
    CynagenGPAddon plugin;

    public MilkBucketDupe(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /*
    When trying to milk a cow in a claimed area, GriefPrevention denies the process.
    However, for some reason, a milk bucket still ends up being in your inventory.
    Additionally, the bucket that was used to get the milk is no correctly removed,
    resulting in an additional bucket being duped.

    Fix: We simply just allow the cow to be milked.
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onCowMilkAllow(PlayerInteractEntityEvent e) {
        // Check if it's a cow being milked
        if (!(e.getRightClicked() instanceof Cow)) return;

        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        // Check if player is holding a bucket
        if (item.getType() != Material.BUCKET) return;

        // If the event was cancelled (by Grief Prevention), uncancel it
        if (e.isCancelled()) {
            e.setCancelled(false);
        }
    }
}
