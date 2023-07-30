package kyrobi.cynagengpaddon.Menu.ClaimOptions;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Trade;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Utils;
import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.flags.FlagDef_AllowPvP;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static kyrobi.cynagengpaddon.Menu.ClaimOptions.ClaimsMember.claimsMembersMenu;
import static kyrobi.cynagengpaddon.Menu.ClaimOptions.ClaimsRename.claimsRenamingMenu;
import static kyrobi.cynagengpaddon.Menu.ClaimOptions.FlagsPage.NoPlayerEnter.claimsNoPlayerEnterOption;
import static kyrobi.cynagengpaddon.Menu.ClaimsList.claimsListMenu;
import static kyrobi.cynagengpaddon.Menu.ClaimsOption.claimsOptionMenu;

public class ClaimsFlags {
    /*
    NOTES
    unSetFlag(Claim, Flag, boolean newFlag)
    If newFlag is true, the flag will be kept but set to false.
    If newFlag is false, the flag will be completely removed/unset from the claim.

    SetFlag(Claim, Flag, boolean newFlag)
    If true - Any existing flag value will be overridden
    If false - The flag will only be set if not already existing
     */

    public static void showClaimFlags(Player player, long claimID){

        Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);
        FlagManager manager = GPFlags.getInstance().getFlagManager();


        ChestGui gui = new ChestGui(6, "Claim Flags");

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
            event.setCancelled(true);
            claimsOptionMenu((Player) event.getWhoClicked(), claimID);
        }), 4, 5 ); // Indexed 4 to the right, Index 5 down



        /*
        PvP Flags
         */
        ArrayList<String> pvpButtonLore = new ArrayList<>();
        pvpButtonLore.add(ChatColor.GRAY + "▸ Toggle PvP in your claim ");
        pvpButtonLore.add("");
        boolean pvpEnabled;
        if(manager.getFlag(claim, "AllowPvP") != null){
            pvpEnabled = manager.getFlag(claim, "AllowPvP").getSet();
        } else {
            pvpEnabled = false;
        }

        if(pvpEnabled){
            pvpButtonLore.add(ChatColor.GRAY + "▸ Status: " + ChatColor.GREEN + " PvP Enabled");
        } else {
            pvpButtonLore.add(ChatColor.GRAY + "▸ Status: " + ChatColor.RED + " PvP Disabled");
        }

        ItemStack pvpButton = Utils.itemGenerator(Material.DIAMOND_SWORD, ChatColor.GREEN + "PvP", pvpButtonLore);
        navigation.addItem(new GuiItem(pvpButton, event -> {

            event.setCancelled(true);
            FlagDefinition flag = manager.getFlagDefinitionByName("AllowPvP");

            if(!pvpEnabled){
                manager.setFlag(claim, flag, true, true, "asd");
            } else {
                manager.setFlag(claim, flag, false, true, "asd");
                // manager.unSetFlag(claim, flag, false);
            }

            manager.save();
            showClaimFlags(player, claimID);

        }), 2, 2 );


        /*
        NoEnterPlayer
         */
        ArrayList<String> noEnterPlayerButtonLore = new ArrayList<>();
        noEnterPlayerButtonLore.add(ChatColor.GRAY + "Block certain players from your claim");
        ItemStack membersButton = Utils.itemGenerator(Material.OAK_DOOR, ChatColor.GREEN + "Blacklisted Members", noEnterPlayerButtonLore);
        navigation.addItem(new GuiItem(membersButton, event -> {
            event.setCancelled(true);
            claimsNoPlayerEnterOption((Player) event.getWhoClicked(), event ,claimID);

        }), 4, 2 );


//        /*
//        Flags option
//         */
//        ArrayList<String> flagsButtonLore = new ArrayList<>();
//        flagsButtonLore.add(ChatColor.GRAY + "▸ Flags are optional settings that");
//        ItemStack flagsButton = Utils.itemGenerator(Material.OAK_SIGN, ChatColor.GREEN + "Flags", flagsButtonLore);
//        navigation.addItem(new GuiItem(flagsButton, event -> {
//            event.setCancelled(true);
//            claimsRenamingMenu((Player) event.getWhoClicked(), event, claimID);
//
//        }), 6, 2);



        gui.addPane(navigation);
        gui.show(player);

    }
}