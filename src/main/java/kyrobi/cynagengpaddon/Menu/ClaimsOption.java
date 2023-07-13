package kyrobi.cynagengpaddon.Menu;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

import static kyrobi.cynagengpaddon.Menu.ClaimsList.claimsListMenu;

public class ClaimsOption {

    public static void claimsOptionMenu(Player player){

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
            // event.getWhoClicked().closeInventory();
            claimsListMenu((Player) event.getWhoClicked());
            event.setCancelled(true);
        }), 4, 2 );

        gui.addPane(navigation);
        gui.show(player);

    }
}
