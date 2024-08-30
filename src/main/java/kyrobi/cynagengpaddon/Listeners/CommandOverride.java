package kyrobi.cynagengpaddon.Listeners;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class CommandOverride implements Listener {

    private CynagenGPAddon plugin;

    public CommandOverride(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreCommand(PlayerCommandPreprocessEvent e) {
        String[] args = e.getMessage().split(" ");

        if (args[0].equalsIgnoreCase("/claim")) {
            e.setMessage("/cynagengpaddon:claims");
        }

        else if(args[0].equalsIgnoreCase("/claimslist")){
            e.setMessage("/cynagengpaddon:claims");
        }

        else if(args[0].equalsIgnoreCase("/listclaims")){
            e.setMessage("/cynagengpaddon:claims");
        }
    }
}