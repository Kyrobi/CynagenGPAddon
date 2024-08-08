package kyrobi.cynagengpaddon.Listeners;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimInspectionEvent;
import me.ryanhamshire.GriefPrevention.util.BoundingBox;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.ryanhamshire.GriefPrevention.DataStore.getChunkHash;

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

        String filler = ChatColor.RESET + "" + ChatColor.GREEN + "------------";
        if (item != null && item.getType() == Material.GOLDEN_SHOVEL) {

            List<Chunk> chunksAroundPlayer = getChunksAroundPlayer(player, Bukkit.getViewDistance());
            Set<Claim> claims = new HashSet<>();

            for(Chunk chunk: chunksAroundPlayer){
                Collection<Claim> chunkClaims = GriefPrevention.instance.dataStore.getClaims(chunk.getX(), chunk.getZ());
                claims.addAll(chunkClaims);
            }

            ClaimInspectionEvent claimInspectionEvent = new ClaimInspectionEvent(e.getPlayer(), null, claims, true);
            Bukkit.getServer().getPluginManager().callEvent(claimInspectionEvent);

        }
    }

    public static List<Chunk> getChunksAroundPlayer(Player player, int radius) {
        List<Chunk> chunks = new ArrayList<>();

        // Get the player's current location
        Location playerLocation = player.getLocation();

        // Get the chunk coordinates for the player's location
        int playerChunkX = playerLocation.getChunk().getX();
        int playerChunkZ = playerLocation.getChunk().getZ();

        // Iterate over the chunks within the specified radius
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int chunkX = playerChunkX + x;
                int chunkZ = playerChunkZ + z;

                // Get the chunk at the specified coordinates
                Chunk chunk = player.getWorld().getChunkAt(chunkX, chunkZ);

                // Add the chunk to the list
                chunks.add(chunk);
            }
        }

        // Return the list of chunks
        return chunks;
    }

}
