package kyrobi.cynagengpaddon.commands;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.UUID;

public class TransferAllClaims implements CommandExecutor {

    public TransferAllClaims(CynagenGPAddon cynagenGPAddon) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!sender.hasPermission("admin.perks")){
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Check if the correct number of arguments are provided
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /transferallclaims <from> <to>");
            return true;
        }

        String fromPlayerName = args[0];
        String toPlayerName = args[1];

        // Get the OfflinePlayer objects
        OfflinePlayer fromPlayer = Bukkit.getOfflinePlayer(fromPlayerName);
        OfflinePlayer toPlayer = Bukkit.getOfflinePlayer(toPlayerName);

        // Check if the players have ever joined the server
        if (!fromPlayer.hasPlayedBefore()) {
            sender.sendMessage("§cError: Player '" + fromPlayerName + "' has never joined the server.");
            return true;
        }

        if (!toPlayer.hasPlayedBefore()) {
            sender.sendMessage("§cError: Player '" + toPlayerName + "' has never joined the server.");
            return true;
        }

        // Get UUIDs
        UUID fromUUID = fromPlayer.getUniqueId();
        UUID toUUID = toPlayer.getUniqueId();

        // Get all claims from the 'from' player
        Collection<Claim> claims = GriefPrevention.instance.dataStore.getClaims();
        int transferredCount = 0;

        for (Claim claim : claims) {
            // Check if this claim belongs to the 'from' player
            if (claim.ownerID != null && claim.ownerID.equals(fromUUID)) {
                // Transfer ownership to the 'to' player
                GriefPrevention.instance.dataStore.changeClaimOwner(claim, toUUID);
                transferredCount++;
            }
        }

        // Send feedback to the command sender
        if (transferredCount > 0) {
            sender.sendMessage("§aSuccessfully transferred " + transferredCount +
                    " claim(s) from " + fromPlayerName + " to " + toPlayerName + ".");
        } else {
            sender.sendMessage("§eNo claims found for player " + fromPlayerName + ".");
        }

        return true;
    }
}
