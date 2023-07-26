package kyrobi.cynagengpaddon.Menu;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.Utils;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static kyrobi.cynagengpaddon.Menu.ClaimsMember.claimsMembersMenu;

public class ClaimsTrust {

    enum TRUST_TYPE {
        MANAGER,
        BUILDER,
        CONTAINER,
        ACCESSOR
    }

    public static void showClaimManagers(ArrayList<String> trusts, Player player, TRUST_TYPE trust_type, long claimID){

        List<ItemStack> allMembers = new ArrayList<>();

        for(String i: trusts) {
            UUID playerUUID = UUID.fromString(i);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);

            String formattingPlayerName = ChatColor.RESET + "" + ChatColor.YELLOW + offlinePlayer.getName();
            ItemStack playerIcon = new ItemStack(Material.PLAYER_HEAD);

            SkullMeta skullMeta = (SkullMeta) playerIcon.getItemMeta();
            skullMeta.setOwningPlayer(offlinePlayer);
            playerIcon.setItemMeta(skullMeta);

            ItemMeta itemMeta = playerIcon.getItemMeta();

            // Get OfflinePlayer from UUID
            itemMeta.setDisplayName(formattingPlayerName);

            List<String> lore = new ArrayList<>();
            lore.add(offlinePlayer.getName());
            lore.add(ChatColor.RED + "Click to remove trust ");
            itemMeta.setLore(lore);
            playerIcon.setItemMeta(itemMeta);

            // skull.setItemMeta(itemMeta);

            allMembers.add(
                    playerIcon
            );
        }

        ChestGui gui = null;

        if(trust_type == TRUST_TYPE.MANAGER) { gui = new ChestGui(5, ChatColor.GRAY + "Managers " + ChatColor.GREEN + "/permissiontrust"); }
        if(trust_type == TRUST_TYPE.BUILDER) { gui = new ChestGui(5, ChatColor.GRAY + "Builders " + ChatColor.GREEN + "/trust"); }
        if(trust_type == TRUST_TYPE.CONTAINER) { gui = new ChestGui(5, ChatColor.GRAY + "Container " + ChatColor.GREEN + "/containertrust" ); }
        if(trust_type == TRUST_TYPE.ACCESSOR) { gui = new ChestGui(5, ChatColor.GRAY + "Accessor " + ChatColor.GREEN + "/accesstrust"); }

        PaginatedPane pages = new PaginatedPane(0, 0, 9, 3);
        pages.populateWithItemStacks(allMembers);
        pages.setOnClick(e -> {
            e.setCancelled(true);
            if(e.getCurrentItem() == null){
                return;
            }

            Player playerToRemove = Bukkit.getOfflinePlayer(e.getCurrentItem().getItemMeta().getLore().get(0)).getPlayer();
            Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);
            String playerToRemoveString = playerToRemove.getUniqueId().toString();
            claim.setPermission(playerToRemoveString, ClaimPermission.Inventory);
            claim.dropPermission(playerToRemoveString);
            GriefPrevention.instance.dataStore.saveClaim(claim);
            trusts.remove(playerToRemoveString);
            showClaimManagers(trusts, player, trust_type, claimID);

        });

        gui.addPane(pages);

        /*
        Adds the borders
        */
        OutlinePane background = new OutlinePane(0, 4, 9, 1);
        ItemStack borderBlock = Utils.itemGenerator(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY+"-");
        background.addItem(new GuiItem(borderBlock, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
        }));
        background.setRepeat(true);
        background.setPriority(Pane.Priority.LOWEST);
        gui.addPane(background);

        StaticPane navigation = new StaticPane(0, 0, 9, 5);
        ItemStack backButton = Utils.itemGenerator(Material.RED_WOOL, ChatColor.RED + "Back");
        navigation.addItem(new GuiItem(backButton, event -> {
            event.setCancelled(true);
            claimsMembersMenu(player, claimID);
        }), 4, 4 ); // Indexed 4 to the right, Index 5 down
        gui.addPane(navigation);
        gui.show(player);

    }
}
