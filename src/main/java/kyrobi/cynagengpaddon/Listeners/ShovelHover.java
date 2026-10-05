package kyrobi.cynagengpaddon.Listeners;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimInspectionEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShovelHover implements Listener {
    CynagenGPAddon plugin;

    HashMap<String, List<Long>> alreadyVisualized = new HashMap<>();

    public ShovelHover(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerItemHover(PlayerItemHeldEvent e){
        Player player = e.getPlayer();
        int newSlot = e.getNewSlot();
        ItemStack item = player.getInventory().getItem(newSlot);

        if (item == null || item.getType() != Material.GOLDEN_SHOVEL) {
            return;
        }

        String filler = ChatColor.RESET + "" + ChatColor.GRAY + "==============";

        // Inspect claims in already-loaded chunks only. Chunks at view distance
        // are loaded anyway, and claims are chunk-anchored, so async-loading
        // (2*viewDist+1)^2 chunks just to read claim watermarks caused the
        // shovel-switch stall (and a main-thread join()).
        World world = player.getWorld();
        int radius = Math.min(Bukkit.getViewDistance(), 20);
        Chunk center = player.getLocation().getChunk();
        int playerChunkX = center.getX();
        int playerChunkZ = center.getZ();

        Set<Claim> claims = new HashSet<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int chunkX = playerChunkX + x;
                int chunkZ = playerChunkZ + z;
                if (!world.isChunkLoaded(chunkX, chunkZ)) {
                    continue;
                }
                claims.addAll(GriefPrevention.instance.dataStore.getClaims(chunkX, chunkZ));
            }
        }

        ClaimInspectionEvent claimInspectionEvent = new ClaimInspectionEvent(player, null, claims, true);
        Bukkit.getServer().getPluginManager().callEvent(claimInspectionEvent);
        player.sendMessage(filler + "\n \n \n" + ChatColor.GREEN + "View all your claims with " + ChatColor.GOLD + "/claims" + ChatColor.GREEN + "!\n" + ChatColor.GRAY + "(You can also teleport to them!)" + "\n \n \n" + filler);
    }
}
