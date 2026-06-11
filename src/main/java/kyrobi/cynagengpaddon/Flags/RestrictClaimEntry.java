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
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;

import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;

public class RestrictClaimEntry implements Listener {

    private CynagenGPAddon plugin;

    public RestrictClaimEntry(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        Location toLocation = event.getTo();

        Claim toClaim = GriefPrevention.instance.dataStore.getClaimAt(toLocation, false, null);

        if (toClaim != null) {
            ClaimData claimData = myDataStore.get(toClaim.getID());
            if(claimData == null || !claimData.isRestrictClaim()){
                return;
            }

            if(isBlocked(player, toClaim)){
                event.setCancelled(true);
                player.sendMessage("You must be trusted in this claim to enter.");
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
        if (data == null || !data.isRestrictClaim()) return;

        if(isBlocked(player, claim)){
            event.setCancelled(true);
            player.sendMessage("You must be trusted in this claim to enter.");
        }
    }

    private boolean isBlocked(Player player, Claim claim){
        if(player.getUniqueId().equals(claim.getOwnerID())){
            return false;
        }

        ArrayList<String> builders = new ArrayList<>();
        ArrayList<String> containers = new ArrayList<>();
        ArrayList<String> accessors = new ArrayList<>();
        ArrayList<String> managers = new ArrayList<>();
        claim.getPermissions(builders, containers, accessors, managers);

        String uuid = player.getUniqueId().toString();

        if(builders.contains(uuid) || containers.contains(uuid) || accessors.contains(uuid) || managers.contains(uuid)){
            return false;
        }

        return true;
    }
}
