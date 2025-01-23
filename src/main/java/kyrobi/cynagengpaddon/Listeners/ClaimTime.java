package kyrobi.cynagengpaddon.Listeners;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Storage.ClaimData;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimInspectionEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;
import static kyrobi.cynagengpaddon.Utils.longToDate;

public class ClaimTime implements Listener {

    private CynagenGPAddon plugin;

    public ClaimTime(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClaimVisualize(ClaimInspectionEvent e){
        Block block = e.getInspectedBlock();
        if(block == null){
            return;
        }

        Location loc = e.getInspectedBlock().getLocation();
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
        if(claim == null){
            return;
        }

        ClaimData claimData =  myDataStore.get(claim.getID());
        long creationDate;
        if(claimData != null){
            creationDate = claimData.getCreationDate();
        } else {
            creationDate = 0;
        }
        if(creationDate <= 0){
            e.getPlayer().sendMessage("This claim was created before record keeping");
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            e.getPlayer().sendMessage(ChatColor.GRAY + "▸ Creation date: " + ChatColor.WHITE + longToDate(creationDate));
        }, 1);
    }
}
