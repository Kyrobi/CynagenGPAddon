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
import java.util.concurrent.CompletableFuture;

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

        String filler = ChatColor.RESET + "" + ChatColor.GRAY + "==============";
        if (item != null && item.getType() == Material.GOLDEN_SHOVEL) {


            // Call the asynchronous method and handle the result when it completes
            // Wait for all chunks to be loaded
            // Get the list of futures for all chunks around the player
            List<CompletableFuture<Chunk>> chunkFutures = getChunksAroundPlayer(player, Bukkit.getViewDistance());

            CompletableFuture.allOf(chunkFutures.toArray(new CompletableFuture[0])).thenAccept(v -> {
                Set<Claim> claims = new HashSet<>();

                // Process each loaded chunk
                for (CompletableFuture<Chunk> futureChunk : chunkFutures) {
                    Chunk chunk = futureChunk.join();  // Get the loaded chunk

                    // Fetch claims for each chunk and add them to the set
                    Collection<Claim> chunkClaims = GriefPrevention.instance.dataStore.getClaims(chunk.getX(), chunk.getZ());
                    claims.addAll(chunkClaims);
                }

                // Fire the ClaimInspectionEvent with the found claims
                ClaimInspectionEvent claimInspectionEvent = new ClaimInspectionEvent(e.getPlayer(), null, claims, true);
                Bukkit.getServer().getPluginManager().callEvent(claimInspectionEvent);
                player.sendMessage(filler + "\n \n \n" + ChatColor.GREEN + "View all your claims with " + ChatColor.GOLD + "/claims" + ChatColor.GREEN + "!\n" + ChatColor.GRAY + "(You can also teleport to them!)" + "\n \n \n" + filler);
            });

//            List<Chunk> chunksAroundPlayer = getChunksAroundPlayer(player, Bukkit.getViewDistance());
//            Set<Claim> claims = new HashSet<>();
//
//            for(Chunk chunk: chunksAroundPlayer){
//                Collection<Claim> chunkClaims = GriefPrevention.instance.dataStore.getClaims(chunk.getX(), chunk.getZ());
//                claims.addAll(chunkClaims);
//            }
//
//            ClaimInspectionEvent claimInspectionEvent = new ClaimInspectionEvent(e.getPlayer(), null, claims, true);
//            Bukkit.getServer().getPluginManager().callEvent(claimInspectionEvent);
//            player.sendMessage(filler + "\n \n \n" +ChatColor.GREEN + "View all your claims with " + ChatColor.GOLD + "/claims" + ChatColor.GREEN + "!\n"+ChatColor.GRAY + "(You can also teleport to them!)" + "\n \n \n" + filler);

        }
    }

    public static List<CompletableFuture<Chunk>> getChunksAroundPlayer(Player player, int radius) {
        List<CompletableFuture<Chunk>> chunkFutures = new ArrayList<>();

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
                // Chunk chunk = player.getWorld().getChunkAt(chunkX, chunkZ);
//                player.getWorld().getChunkAtAsync(chunkX, chunkZ).thenAccept(chunk1 -> {
//                   chunks.add(chunk1);
//                });

                // Add each chunk's CompletableFuture to the list
                chunkFutures.add(player.getWorld().getChunkAtAsync(chunkX, chunkZ));
            }
        }

        return chunkFutures;
    }

}
