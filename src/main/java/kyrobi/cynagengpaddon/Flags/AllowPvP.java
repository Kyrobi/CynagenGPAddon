package kyrobi.cynagengpaddon.Flags;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Storage.ClaimData;
import me.ryanhamshire.GriefPrevention.events.PreventPvPEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;

public class AllowPvP implements Listener {

    private CynagenGPAddon plugin;

    public AllowPvP(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreventPvP(PreventPvPEvent e) {
        ClaimData claimData = myDataStore.getOrDefault(e.getClaim().getID(), null);
        if(claimData == null){ return; }

        boolean isPvPAllowed = claimData.isAllowPvP();
        if(isPvPAllowed){
            e.setCancelled(true);
        }
    }
}
