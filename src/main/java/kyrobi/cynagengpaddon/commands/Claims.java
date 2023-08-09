package kyrobi.cynagengpaddon.commands;

import kyrobi.cynagengpaddon.Menu.ClaimsList;
import kyrobi.cynagengpaddon.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;

import static kyrobi.cynagengpaddon.Menu.ClaimsList.claimsListMenu;
import static kyrobi.cynagengpaddon.Menu.ClaimsOption.claimsOptionMenu;
import static kyrobi.cynagengpaddon.Utils.claimsNameCache;
import static kyrobi.cynagengpaddon.Utils.setClaimName;

public class Claims implements CommandExecutor {

    private CynagenGPAddon plugin;
    Plugin griefPreventionPlugin = Bukkit.getServer().getPluginManager().getPlugin("GriefPrevention");
    public static HashMap<String, ClaimsList.Sort> userSortType = new HashMap<>();

    public Claims(final CynagenGPAddon plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Player player = (Player) commandSender;

//        if(!player.getName().equals("Kyrobi")){
//            player.sendMessage(ChatColor.RED + "Command is temporarily disabled - Kyrobi is testing");
//            return false;
//        }

        claimsListMenu(player, userSortType.getOrDefault(player.getName(), ClaimsList.Sort.CLAIM_ID));

        return false;
    }

}
