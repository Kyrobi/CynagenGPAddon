package kyrobi.cynagengpaddon.Flags;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Storage.ClaimData;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Set;

import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;

public class DenyEntry implements Listener {

    private CynagenGPAddon plugin;

    public DenyEntry(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
            if(claimData.getNoEnterPlayer().contains(event.getPlayer().getUniqueId().toString())){
                event.setCancelled(true);
                player.sendMessage("You cannot enter this claim.");
            }
        }
    }
}
