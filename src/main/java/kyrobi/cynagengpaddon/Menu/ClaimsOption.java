package kyrobi.cynagengpaddon.Menu;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.slf4j.helpers.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static kyrobi.cynagengpaddon.Menu.ClaimsList.claimsListMenu;
import static kyrobi.cynagengpaddon.Utils.setClaimName;

public class ClaimsOption {

    public static HashMap<String, String> nameCache = new HashMap<>();

    public static void claimsOptionMenu(Player player, long claimID){

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



        // We add the renaming option
        ItemStack renameButton = Utils.itemGenerator(Material.NAME_TAG, ChatColor.GREEN + "Rename");
        navigation.addItem(new GuiItem(renameButton, event -> {


            AnvilGui anvilGui = new AnvilGui("Type in a name");
            anvilGui.setCost((short)0);
            anvilGui.setOnGlobalClick(inventoryClickEvent -> event.setCancelled(true));

            // Pane for the confirmation button
            StaticPane confirmPane = new StaticPane(0, 0, 1, 1);
            ItemStack confirmButton = new ItemStack(Material.GREEN_WOOL);
            ItemMeta confirmButtonMeta = confirmButton.getItemMeta();
            confirmButtonMeta.setDisplayName(ChatColor.GREEN + "Confirm");
            confirmButton.setItemMeta(confirmButtonMeta);

            // Pane for the name
            StaticPane fillerPane = new StaticPane(0, 0, 1, 1);
            ItemStack fillerButton = new ItemStack(Material.PAPER);
            ItemMeta fillerButtonMeta = fillerButton.getItemMeta();
            fillerButtonMeta.setDisplayName(ChatColor.GRAY + "Change Claim Name");
            fillerButton.setItemMeta(fillerButtonMeta);

            // Filler button
            fillerPane.addItem(new GuiItem(fillerButton, fillerPanelEvent -> {
                fillerPanelEvent.setCancelled(true);
            }), 0, 0);


            // Confirm button
            confirmPane.addItem(new GuiItem(confirmButton, confirmEvent -> {
                confirmEvent.setCancelled(true);
                confirmEvent.getWhoClicked().closeInventory();

                setClaimName(claimID, nameCache.get(event.getWhoClicked().getName()));
                nameCache.remove(event.getWhoClicked().getName());
            }), 0, 0);


            anvilGui.getFirstItemComponent().addPane(fillerPane);
            anvilGui.getResultComponent().addPane(confirmPane);
            anvilGui.show(player);

            event.setCancelled(true);

            anvilGui.setOnNameInputChanged(s -> {
                nameCache.put(event.getWhoClicked().getName(), s);
            });

        }), 4, 2 );

        gui.addPane(navigation);
        gui.show(player);


    }
}
