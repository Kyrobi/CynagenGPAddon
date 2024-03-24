package kyrobi.cynagengpaddon.Menu;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Trade;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Menu.ClaimOptions.ClaimsTrust;
import kyrobi.cynagengpaddon.Utils;
import me.ryanhamshire.*;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static kyrobi.cynagengpaddon.Menu.ClaimOptions.ClaimsFlags.showClaimFlags;
import static kyrobi.cynagengpaddon.Menu.ClaimsList.claimsListMenu;
import static kyrobi.cynagengpaddon.Menu.ClaimOptions.ClaimsMember.claimsMembersMenu;
import static kyrobi.cynagengpaddon.Menu.ClaimOptions.ClaimsRename.claimsRenamingMenu;
import static kyrobi.cynagengpaddon.commands.Claims.userSortType;

public class ClaimsOption {
    static int normalTeleportPrice = 800;

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
            event.setCancelled(true);
            claimsListMenu((Player) event.getWhoClicked(), userSortType.get(player.getName()));

        }), 4, 5 ); // Indexed 4 to the right, Index 5 down



        /*
        Renaming option
         */
        ArrayList<String> renameButtonLore = new ArrayList<>();
        renameButtonLore.add(ChatColor.GRAY + "Name your claim, yo!");
        ItemStack renameButton = Utils.itemGenerator(Material.NAME_TAG, ChatColor.GREEN + "Rename", renameButtonLore);
        navigation.addItem(new GuiItem(renameButton, event -> {

            event.setCancelled(true);
            claimsRenamingMenu((Player) event.getWhoClicked(), event, claimID);

        }), 4, 2 );

        /*
        Teleport option
         */
        int teleportCost = getTeleportCost(player);
        ArrayList<String> teleportButtonLore = getTeleportDiscountLore(player);
        ItemStack teleportButton = Utils.itemGenerator(Material.ENDER_PEARL, ChatColor.GREEN + "Teleport", teleportButtonLore);
        navigation.addItem(new GuiItem(teleportButton, event -> {
            event.setCancelled(true);
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

                CynagenGPAddon plugin = JavaPlugin.getPlugin(CynagenGPAddon.class);
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    // Didn't know you could do .getHighestBlock async, but gg
                    int safeY = greaterCorner.getWorld().getHighestBlockAt(teleportLoc).getY();
                    teleportLoc.setY(safeY);

                    if(greaterCorner.getWorld().getEnvironment().equals(World.Environment.NETHER)){
                        player.sendMessage(ChatColor.RED + "For safety reasons, nether\nteleport is disabled.");
                        return;
                    }

                    if(greaterCorner.getWorld().getEnvironment().equals(World.Environment.THE_END)){
                        if(safeY <= 0 || (greaterCorner.getWorld().getHighestBlockAt(teleportLoc).getType() == Material.AIR)){
                            player.sendMessage(ChatColor.RED + "Can't teleport. Area is over void.");
                            return;
                        }
                    }

                    ess.getUser(player.getUniqueId()).getAsyncTeleport().teleport(
                            teleportLoc,
                            new Trade(BigDecimal.valueOf(teleportCost), ess),
                            PlayerTeleportEvent.TeleportCause.PLUGIN,
                            new CompletableFuture<>()
                    );

                });


            } else {
                player.sendMessage(ChatColor.RED + "You do not have enough money to teleport.");
            }

        }), 2, 2 );



        /*
        View members option
         */
        ArrayList<String> membersButtonLore = new ArrayList<>();
        membersButtonLore.add(ChatColor.GRAY + "Manage players in your claim");

        ItemStack membersButton = Utils.itemGenerator(Material.PLAYER_HEAD, ChatColor.GREEN + "Members", membersButtonLore);
        navigation.addItem(new GuiItem(membersButton, event -> {

            event.setCancelled(true);
            claimsMembersMenu((Player) event.getWhoClicked(), claimID);

        }), 6, 2 );


        /*
        Flags option
         */
        ArrayList<String> flagsButtonLore = new ArrayList<>();
        flagsButtonLore.add(ChatColor.GRAY + "▸ Flags are optional settings that");
        flagsButtonLore.add(ChatColor.GRAY + "▸ modify how a claim works. It lets");
        flagsButtonLore.add(ChatColor.GRAY + "▸ you customize claims by setting");
        flagsButtonLore.add(ChatColor.GRAY + "▸ extra options such as allowing PvP");
        flagsButtonLore.add(ChatColor.GRAY + "▸ or welcome messages.");
        ItemStack flagsButton = Utils.itemGenerator(Material.OAK_SIGN, ChatColor.GREEN + "Flags", flagsButtonLore);
        navigation.addItem(new GuiItem(flagsButton, event -> {

            event.setCancelled(true);
            showClaimFlags((Player) event.getWhoClicked(), claimID);

        }), 4, 3 );

        /*
        Delete claim option
         */
        ArrayList<String> deleteClaimButtonLore = new ArrayList<>();
        deleteClaimButtonLore.add(ChatColor.RED + "WARNING: THIS WILL DELETE YOUR CLAIM!");
        deleteClaimButtonLore.add(" ");
        deleteClaimButtonLore.add(ChatColor.RED + "THIS ACTION CANNOT BE UNDONE");
        ItemStack deleteClaimButton = Utils.itemGenerator(Material.BARRIER, ChatColor.RED + "DELETE CLAIM ! ! !", deleteClaimButtonLore);
        navigation.addItem(new GuiItem(deleteClaimButton, event -> {
            event.setCancelled(true);
            confirmClaimDelete(player, claimID);
        }), 4, 0 );

        gui.addPane(navigation);
        gui.show(player);

    }

    public static void confirmClaimDelete(Player player, long claimID){
        ChestGui gui = new ChestGui(6, "Cofnifrm Deleet");
        StaticPane navigation = new StaticPane(0, 0, 9, 6);

        ItemStack setClaimLeaveMessageButton = Utils.itemGenerator(Material.RED_WOOL, ChatColor.RED + "CONFIRM DELETE");
        navigation.addItem(new GuiItem(setClaimLeaveMessageButton, event -> {
            event.setCancelled(true);

            Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);
            GriefPrevention griefPrevention = GriefPrevention.instance;
            griefPrevention.dataStore.deleteClaim(claim);
            player.sendMessage(ChatColor.GREEN + "Claim deleted.");
            player.closeInventory();

        }), 4, 2 );

        gui.addPane(navigation);
        gui.show(player);

    }

    private static int getTeleportCost(Player player){
        double VIPPlusDiscountPercentage = 0.2;
        double boosterDiscountPercentage = 0.4;
        double teleportDiscountPackagePercentage = 0.6;
        double teleportAndBoosterDiscountPackagePercentage = 0.75;

        if(player.hasPermission("perks.claimTeleportDiscount") && (player.hasPermission("booster.perks"))){
            return (int) (normalTeleportPrice * (1 - teleportAndBoosterDiscountPackagePercentage));
        }

        else if(player.hasPermission("perks.claimTeleportDiscount")){
            return (int) (normalTeleportPrice * (1 - teleportDiscountPackagePercentage));
        }

        else if(player.hasPermission("booster.perks")){
            return (int) (normalTeleportPrice * (1 - boosterDiscountPercentage));
        }

        else if(player.hasPermission("vipplus.perks")){
            return (int) (normalTeleportPrice * (1 - VIPPlusDiscountPercentage));
        }


        return normalTeleportPrice;
    }

    private static ArrayList<String> getTeleportDiscountLore(Player player){
        ArrayList<String> teleportButtonLore = new ArrayList<>();
        teleportButtonLore.add(ChatColor.GRAY + "Cost: " + ChatColor.GREEN + "$" + normalTeleportPrice +" " + ChatColor.GRAY + "(0% VIP+ Discount)");
        teleportButtonLore.add(" ");
        teleportButtonLore.add(ChatColor.GRAY + "▸ Click to teleport to");
        teleportButtonLore.add(ChatColor.GRAY + "▸ your claim.");
        teleportButtonLore.add(" ");
        teleportButtonLore.add(ChatColor.GRAY + "(Not meant as replacement for");
        teleportButtonLore.add(ChatColor.GRAY + "/home, hence the cost.)");

        if(player.hasPermission("vipplus.perks") || player.hasPermission("booster.perks") || player.hasPermission("perks.claimTeleportDiscount")){
            teleportButtonLore.set(0,
                    ChatColor.GRAY + "▸ Cost: " + ChatColor.RED + ChatColor.STRIKETHROUGH + "$" + normalTeleportPrice + ChatColor.RESET + ChatColor.GREEN + " $" + getTeleportCost(player));
        }

        if(player.hasPermission("perks.claimTeleportDiscount") && player.hasPermission("booster.perks")){
            teleportButtonLore.set(1, teleportButtonLore.get(1) + " " + ChatColor.GRAY + "(75% Teleport + Booster Discount)");
        }

        else if(player.hasPermission("perks.claimTeleportDiscount")){
            teleportButtonLore.set(0, teleportButtonLore.get(0) + " " + ChatColor.GRAY + "(60% Teleport Discount)");
        }

        else if(player.hasPermission("booster.perks")){
            teleportButtonLore.set(0, teleportButtonLore.get(0) + " " + ChatColor.GRAY + "(40% Booster Discount)");
        }

        else if(player.hasPermission("vipplus.perks")){
            teleportButtonLore.set(0, teleportButtonLore.get(0) + " " + ChatColor.GRAY + "(20% VIP+ Discount)");
        }

        return teleportButtonLore;
    }
}
