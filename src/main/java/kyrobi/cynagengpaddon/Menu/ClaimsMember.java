package kyrobi.cynagengpaddon.Menu;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.Utils;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static kyrobi.cynagengpaddon.Menu.ClaimsOption.claimsOptionMenu;
import static kyrobi.cynagengpaddon.Menu.ClaimsTrust.showClaimManagers;

public class ClaimsMember {

    public static void claimsMembersMenu(Player player, long claimID){

        Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);


        if(claim == null){
            return;
        }


        ArrayList<String> builders = new ArrayList<>();
        ArrayList<String> containers = new ArrayList<>();
        ArrayList<String> accessors = new ArrayList<>();
        ArrayList<String> managers = new ArrayList<>();


        claim.getPermissions(builders, containers, accessors, managers);



        ChestGui gui = new ChestGui(6, "Permissions");


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
            claimsOptionMenu((Player) event.getWhoClicked(), claimID);

            event.setCancelled(true);
        }), 4, 5 ); // Indexed 4 to the right, Index 5 down



        /*
        Managers
         */
        ArrayList<String> managerButtonLore = new ArrayList<>();
        managerButtonLore.add("");
        managerButtonLore.add(ChatColor.GRAY + "▸ Players with manager access are");
        managerButtonLore.add(ChatColor.GRAY + "▸ allowed to grant other players trust");
        managerButtonLore.add(ChatColor.GRAY + "▸ based on the highest trust they");
        managerButtonLore.add(ChatColor.GRAY + "▸ themself have.");
        managerButtonLore.add("");
        managerButtonLore.add(ChatColor.GRAY + "▸ This won't grant them any access to your");
        managerButtonLore.add(ChatColor.GRAY + "▸ claim by default. You will still need to");
        managerButtonLore.add(ChatColor.GRAY + "▸ give them builder, container, or accessor.");
        managerButtonLore.add(ChatColor.GRAY + "▸ Grant with: " + ChatColor.GREEN + "/permissiontrust");
        ItemStack managerButton = Utils.itemGenerator(Material.COMPARATOR, ChatColor.GREEN + "Managers", managerButtonLore);
        navigation.addItem(new GuiItem(managerButton, event -> {
            event.setCancelled(true);
            showClaimManagers(managers, player, ClaimsTrust.TRUST_TYPE.MANAGER ,claimID);
        }), 4, 2 );

        /*
        Builders
         */
        ArrayList<String> builderButtonLore = new ArrayList<>();
        builderButtonLore.add("");
        builderButtonLore.add(ChatColor.GRAY + "▸ Players with builder access are");
        builderButtonLore.add(ChatColor.GRAY + "▸ allowed full access to your claim.");
        builderButtonLore.add("");
        builderButtonLore.add(ChatColor.GRAY + "▸ Grant with: " + ChatColor.GREEN + "/trust");
        ItemStack builderButton = Utils.itemGenerator(Material.CRAFTING_TABLE, ChatColor.GREEN + "Builders", builderButtonLore);
        navigation.addItem(new GuiItem(builderButton, event -> {
            event.setCancelled(true);
            showClaimManagers(builders, player, ClaimsTrust.TRUST_TYPE.BUILDER ,claimID);
        }), 2, 3 );


        /*
        Containers
         */
        ArrayList<String> containerButtonLore = new ArrayList<>();
        containerButtonLore.add("");
        containerButtonLore.add(ChatColor.GRAY + "▸ Players with container access are");
        containerButtonLore.add(ChatColor.GRAY + "▸ allowed to use your buttons,");
        containerButtonLore.add(ChatColor.GRAY + "▸ levers, beds, crafting gear,");
        containerButtonLore.add(ChatColor.GRAY + "▸ containers, and animals.");
        containerButtonLore.add("");
        containerButtonLore.add(ChatColor.GRAY + "▸ Grant with: " + ChatColor.GREEN + "/containertrust");
        ItemStack containerButton = Utils.itemGenerator(Material.CHEST, ChatColor.GREEN + "Containers", containerButtonLore);
        navigation.addItem(new GuiItem(containerButton, event -> {
            event.setCancelled(true);
            showClaimManagers(containers, player, ClaimsTrust.TRUST_TYPE.CONTAINER ,claimID);
        }), 4, 3 );


        /*
        Access
         */
        ArrayList<String> accessButtonLore = new ArrayList<>();
        accessButtonLore.add("");
        accessButtonLore.add(ChatColor.GRAY + "▸ Gives a player permission to use");
        accessButtonLore.add(ChatColor.GRAY + "▸ your buttons, levers, and beds.");
        accessButtonLore.add("");
        accessButtonLore.add(ChatColor.GRAY + "▸ Grant with: " + ChatColor.GREEN + "/accesstrust");
        ItemStack accessButton = Utils.itemGenerator(Material.RED_BED, ChatColor.GREEN + "Accessors", accessButtonLore);
        navigation.addItem(new GuiItem(accessButton, event -> {
            event.setCancelled(true);
            showClaimManagers(accessors, player, ClaimsTrust.TRUST_TYPE.ACCESSOR ,claimID);
        }), 6, 3 );



        gui.addPane(navigation);
        gui.show(player);
    }

}
