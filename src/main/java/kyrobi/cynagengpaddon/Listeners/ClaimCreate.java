package kyrobi.cynagengpaddon.Listeners;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
    }
}
