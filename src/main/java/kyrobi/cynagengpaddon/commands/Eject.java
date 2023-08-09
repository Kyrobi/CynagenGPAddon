package kyrobi.cynagengpaddon.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.earth2me.essentials.spawn.EssentialsSpawn;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class Eject implements CommandExecutor {

    private CynagenGPAddon plugin;
    EssentialsSpawn essSpawn;

    public Eject(final CynagenGPAddon plugin){
        this.plugin = plugin;
        this.essSpawn = (EssentialsSpawn) Bukkit.getServer().getPluginManager().getPlugin("EssentialsSpawn");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Player player = (Player) commandSender;
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        if(args.length == 0){
            player.sendMessage(ChatColor.RED + "Usage: /eject <player>");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);


        if(target == null){
            player.sendMessage(ChatColor.RED + "The player you are trying to eject is not online.");
            return false;
        }

        if(target.hasPermission("mod.perks")){
            player.sendMessage(ChatColor.RED + "You cannot eject a staff.");
            return false;
        }

        if(target.getName().equals(player.getName())){
            player.sendMessage(ChatColor.RED + "You cannot eject yourself.");
            return false;
        }

        // Get the player's current location
        Location currentPlayerLocation = player.getLocation();

        // Check if the player is inside a claim
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(currentPlayerLocation, false, null);
        Location spawn = essSpawn.getSpawn("default");


        // Checks if target is inside the claim
        if (claim != null && claim.contains(target.getLocation(), true, false)) {
            if(claim.getOwnerName().equals(player.getName())){

                ClaimPermission trust = claim.getPermission(String.valueOf(target.getUniqueId()));
                if( (trust != null) && (trust.equals(ClaimPermission.Manage) || trust.equals(ClaimPermission.Build))){
                    player.sendMessage(ChatColor.RED + "You cannot kick a player with trust in your claim.");
                    return false;
                }

                target.teleportAsync(spawn);
                player.sendMessage(ChatColor.GREEN + "Ejected " + ChatColor.GRAY + target.getName() + ChatColor.GREEN + " out of your claim.\nThey have been sent to spawn.");
            } else {
                player.sendMessage(ChatColor.RED + "You need to own this claim to eject people.");
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "You need to be in the same claim as the person\nyou're trying to eject.");

        }

        return false;
    }

}
