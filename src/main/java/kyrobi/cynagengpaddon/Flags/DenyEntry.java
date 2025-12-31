package kyrobi.cynagengpaddon.Flags;

import com.earth2me.essentials.spawn.EssentialsSpawn;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Storage.ClaimData;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Set;

import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;

public class DenyEntry implements Listener {

    private CynagenGPAddon plugin;
    private EssentialsSpawn essSpawn;

    public DenyEntry(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.essSpawn = (EssentialsSpawn) Bukkit.getServer().getPluginManager().getPlugin("EssentialsSpawn");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location toLocation = event.getTo();

        // Get the claim at the new location
        Claim toClaim = GriefPrevention.instance.dataStore.getClaimAt(toLocation, false, null);

        // Check if the player is moving from a non-claim area to a claim area
        if (toClaim != null) {
            ClaimData claimData = myDataStore.get(toClaim.getID());
            if(claimData == null){
                return;
            }
            if(claimData.getNoEnterPlayer().contains(event.getPlayer().getUniqueId().toString())){
                event.setCancelled(true);
                player.sendMessage("You cannot enter this claim.");

                Location spawn = essSpawn.getSpawn("default");
                player.teleportAsync(spawn);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(to, false, null);
        if (claim == null) return;

        ClaimData data = myDataStore.get(claim.getID());
        if (data == null) return;

        if (data.getNoEnterPlayer().contains(player.getUniqueId().toString())) {
            event.setCancelled(true);                       // stop the teleport
            player.sendMessage("You cannot enter this claim.");

//            Location spawn = essSpawn.getSpawn("default");
//            player.teleportAsync(spawn);                    // send them to spawn
        }
    }
}
