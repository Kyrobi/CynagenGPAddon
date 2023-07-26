package kyrobi.cynagengpaddon.Menu;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Trade;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import it.unimi.dsi.fastutil.chars.CharObjectImmutablePair;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Utils;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.helpers.Util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static kyrobi.cynagengpaddon.Menu.ClaimsList.claimsListMenu;
import static kyrobi.cynagengpaddon.Menu.ClaimsMember.claimsMembersMenu;
import static kyrobi.cynagengpaddon.Menu.ClaimsRename.claimsRenamingMenu;
import static kyrobi.cynagengpaddon.Utils.setClaimName;

public class ClaimsOption {

    public static void claimsOptionMenu(Player player, long claimID){

        Essentials ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        ChestGui gui = new ChestGui(6, "Claim Settings");

        OutlinePane background = new OutlinePane(0, 5, 9, 1);
        ItemStack borderBlock = Utils.itemGenerator(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY+"-");
        background.addItem(new GuiItem(borderBlock, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
        }));
        background.setRepeat(true);
        background.setPriority(Pane.Priority.LOWEST);

        gui.addPane(background);

        StaticPane navigation = new StaticPane(0, 0, 9, 6);


        ItemStack backButton = Utils.itemGenerator(Material.RED_WOOL, ChatColor.RED + "Back");
        navigation.addItem(new GuiItem(backButton, event -> {
                // event.getWhoClicked().closeInventory();
            claimsListMenu((Player) event.getWhoClicked());

                event.setCancelled(true);
        }), 4, 5 ); // Indexed 4 to the right, Index 5 down



        /*
        Renaming option
         */
        ArrayList<String> renameButtonLore = new ArrayList<>();
        renameButtonLore.add(ChatColor.GRAY + "Name your claim, yo!");
        ItemStack renameButton = Utils.itemGenerator(Material.NAME_TAG, ChatColor.GREEN + "Rename", renameButtonLore);
        navigation.addItem(new GuiItem(renameButton, event -> {

            claimsRenamingMenu((Player) event.getWhoClicked(), event, claimID);
            event.setCancelled(true);

        }), 4, 2 );

        /*
        Teleport option
         */
        int teleportCost = 800;
        ArrayList<String> teleportButtonLore = new ArrayList<>();
        teleportButtonLore.add(ChatColor.GRAY + "▸ Cost: " + ChatColor.GREEN + "$" + teleportCost);
        teleportButtonLore.add(" ");
        teleportButtonLore.add(ChatColor.GRAY + "Click to teleport to");
        teleportButtonLore.add(ChatColor.GRAY + "your claim.");
        teleportButtonLore.add(" ");
        teleportButtonLore.add(ChatColor.GRAY + "(Not meant as replacement for");
        teleportButtonLore.add(ChatColor.GRAY + "/home, hence the cost.)");
        ItemStack teleportButton = Utils.itemGenerator(Material.ENDER_PEARL, ChatColor.GREEN + "Teleport", teleportButtonLore);
        navigation.addItem(new GuiItem(teleportButton, event -> {

            if(ess.getUser(player.getUniqueId()).getMoney().intValue() >= teleportCost){
                Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);

                Location lesserCorner = claim.getLesserBoundaryCorner();
                Location greaterCorner = claim.getGreaterBoundaryCorner();

                int lesserX = lesserCorner.getBlockX();
                int lesserZ = lesserCorner.getBlockZ();
                int greaterX = greaterCorner.getBlockX();
                int greaterZ = greaterCorner.getBlockZ();

                int middleX = (lesserX + greaterX) / 2;
                int middleZ = (lesserZ + greaterZ) / 2;

                Location teleportLoc = new Location(lesserCorner.getWorld(), middleX, 90, middleZ);

                Bukkit.getScheduler().runTaskAsynchronously(CynagenGPAddon.plugin, () -> {
                    // Didn't know you could do .getHighestBlock async, but gg
                    int safeY = greaterCorner.getWorld().getHighestBlockAt(teleportLoc).getY();
                    teleportLoc.setY(safeY);

                    if(greaterCorner.getWorld().getEnvironment().equals(World.Environment.NETHER) || greaterCorner.getWorld().getEnvironment().equals(World.Environment.THE_END)){
                        player.sendMessage(ChatColor.RED + "For safety reasons, nether\nteleport is disabled.");
                    }

                    ess.getUser(player.getUniqueId()).getAsyncTeleport().teleport(
                            teleportLoc,
                            new Trade(BigDecimal.valueOf(teleportCost), ess),
                            PlayerTeleportEvent.TeleportCause.PLUGIN,
                            new CompletableFuture<Boolean>()
                    );

                });


            } else {
                player.sendMessage(ChatColor.RED + "You do not have enough money to teleport.");
            }

            event.setCancelled(true);

        }), 2, 2 );



        /*
        View members option
         */
        ArrayList<String> membersButtonLore = new ArrayList<>();
        membersButtonLore.add(ChatColor.GRAY + "Manage players in your claim");

        ItemStack membersButton = Utils.itemGenerator(Material.PLAYER_HEAD, ChatColor.GREEN + "Members", membersButtonLore);
        navigation.addItem(new GuiItem(membersButton, event -> {

            claimsMembersMenu((Player) event.getWhoClicked(), claimID);
            event.setCancelled(true);

        }), 6, 2 );

        gui.addPane(navigation);
        gui.show(player);


    }
}
