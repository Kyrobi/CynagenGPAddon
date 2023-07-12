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

public class ClaimsOption {

    public static void claimsOptionMenu(Player player){

        ChestGui gui = new ChestGui(6, "Shop");

        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);
        pages.populateWithItemStacks(Arrays.asList(
                new ItemStack(Material.GOLDEN_SWORD),
                new ItemStack(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, 16),
                new ItemStack(Material.COOKED_COD, 64)
        ));
        pages.setOnClick(event -> {
            //buy item
        });

        gui.addPane(pages);

        OutlinePane background = new OutlinePane(0, 5, 9, 1);
        background.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
        background.setRepeat(true);
        background.setPriority(Pane.Priority.LOWEST);

        gui.addPane(background);

        ItemStack backButton = Utils.itemGenerator(Material.RED_WOOL, ChatColor.RED + "Back");
        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.addItem(new GuiItem(backButton, event ->
                event.getWhoClicked().closeInventory()), 4, 0);

        gui.addPane(navigation);
        gui.show(player);

    }
}
