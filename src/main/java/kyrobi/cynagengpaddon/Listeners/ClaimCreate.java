package kyrobi.cynagengpaddon.Listeners;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Storage.ClaimData;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;
import static kyrobi.cynagengpaddon.Utils.setClaimDate;

public class ClaimCreate implements Listener {

    private CynagenGPAddon plugin;

    public ClaimCreate(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClaimCreate(ClaimCreatedEvent e){
        setClaimDate(e.getClaim().getID());

        if(!myDataStore.containsKey(e.getClaim().getID())){
            ClaimData claimData = new ClaimData(e.getClaim().getID(), (Player) e.getCreator());
            myDataStore.put(e.getClaim().getID(), claimData);
        }
    }
}
