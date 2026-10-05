package kyrobi.cynagengpaddon.Flags;

import com.earth2me.essentials.spawn.EssentialsSpawn;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Storage.ClaimData;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;

/**
 * Single PlayerMoveEvent handler for the claim-entry flags. Replaces the three
 * per-flag move handlers (DenyEntry, EnterExitMessage, RestrictClaimEntry),
 * which did up to 5 GriefPrevention claim lookups per block moved. This one
 * does exactly one lookup per block and caches the claim the player was last
 * in, so flag checks only run on actual claim boundary crossings.
 */
public class ClaimMovementHandler implements Listener {

    private final CynagenGPAddon plugin;
    private final EssentialsSpawn essSpawn;
    /** Last claim the player was seen in (null = wilderness). */
    private final Map<UUID, Claim> lastClaim = new HashMap<>();

    public ClaimMovementHandler(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.essSpawn = (EssentialsSpawn) Bukkit.getServer().getPluginManager().getPlugin("EssentialsSpawn");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Short-circuit: skip if the player hasn't moved to a new block (e.g. head rotation only)
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Location toLocation = event.getTo();

        // One GP claim lookup per block moved (was up to 5 across three handlers)
        Claim toClaim = GriefPrevention.instance.dataStore.getClaimAt(toLocation, false, null);

        ClaimData toData = toClaim == null ? null : myDataStore.get(toClaim.getID());

        // DenyEntry: while the blacklist contains the player, every step into
        // (or inside) the claim is blocked and they are sent to spawn.
        if (toData != null && toData.getNoEnterPlayer().contains(uuid.toString())) {
            event.setCancelled(true);
            player.sendMessage("The claim owner has blocked you from entering this claim.");
            if (essSpawn != null) {
                player.teleportAsync(essSpawn.getSpawn("default"));
            }
            return;
        }

        // GP's getClaimAt returns the cached Claim instance for a region, so
        // identity compare is reliable here.
        Claim last = lastClaim.get(uuid);
        if (toClaim == last) {
            return;
        }

        if (toClaim != null) {
            // RestrictClaimEntry: crossing into a restricted claim requires trust
            if (toData != null && toData.isRestrictClaim() && isBlocked(player, toClaim)) {
                event.setCancelled(true);
                player.sendMessage("You must be trusted in this claim to enter.");
                return; // stay in the old claim cache-wise
            }

            // EnterExitMessage: farewell/enter text only when coming from wilderness
            if (last == null && toData != null) {
                sendClaimMessage(player, toData.getEnterMessage());
            }
            lastClaim.put(uuid, toClaim);
        } else {
            lastClaim.remove(uuid);
            if (last != null) {
                ClaimData data = myDataStore.get(last.getID());
                if (data != null) {
                    sendClaimMessage(player, data.getExitMessage());
                }
            }
        }
    }

    private void sendClaimMessage(Player player, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        player.sendMessage(ChatColor.GRAY + "Claim Message:");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    private boolean isBlocked(Player player, Claim claim){
        if(player.hasPermission("mod.perks")){
            return false;
        }

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

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastClaim.remove(event.getPlayer().getUniqueId());
    }
}
