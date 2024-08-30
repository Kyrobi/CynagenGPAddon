package kyrobi.cynagengpaddon.Flags;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Storage.ClaimData;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;

public class EnterExitMessage implements Listener {

    private CynagenGPAddon plugin;

    public EnterExitMessage(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location fromLocation = event.getFrom();
        Location toLocation = event.getTo();

        Claim fromClaim = GriefPrevention.instance.dataStore.getClaimAt(fromLocation, false, null);
        Claim toClaim = GriefPrevention.instance.dataStore.getClaimAt(toLocation, false, null);

        // When a player enters a claim
        if(toClaim != null && fromClaim == null){
            ClaimData claimData = myDataStore.get(toClaim.getID());
            if(claimData == null){
                return;
            }
            String enterMessage = claimData.getEnterMessage();

            if(enterMessage.equals("")){
                return;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', enterMessage));
        }


        // When a player leaves a claim
        if(fromClaim != null && toClaim == null){
            ClaimData claimData = myDataStore.get(fromClaim.getID());
            if(claimData == null){
                return;
            }
            String exitMessage = claimData.getExitMessage();

            if(exitMessage.equals("")){
                return;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', exitMessage));
        }
    }
}
