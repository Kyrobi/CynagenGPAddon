package kyrobi.cynagengpaddon.commands;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.Utils;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Claims implements CommandExecutor {

    private CynagenGPAddon plugin;
    Plugin griefPreventionPlugin = Bukkit.getServer().getPluginManager().getPlugin("GriefPrevention");

    public Claims(final CynagenGPAddon plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args){
        Player player = (Player) commandSender;
        Utils utilClass = new Utils();

        // We grab all the claims of the player
        List<Claim> playerClaims = new ArrayList<>();
        if (player != null) {
            PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
            if (playerData != null) {
                playerClaims = playerData.getClaims();
            }
        }


        // Sort the claims in the array by their ID
        playerClaims.sort(new Comparator<Claim>(){

            @Override
            public int compare(Claim a, Claim b){
                int first = a.getID().intValue();
                int second = b.getID().intValue();

                return Integer.compare(first, second);
            }
        });

        // Loop through each claim and add it to the GUI
        List<ItemStack> allClaims = new ArrayList<>();
        for(Claim i: playerClaims){
            ItemStack itemStack;
            if(i.getLesserBoundaryCorner().getWorld().getEnvironment().equals(World.Environment.NORMAL)){
                itemStack = new ItemStack(Material.GRASS_BLOCK);
            }

            else if(i.getLesserBoundaryCorner().getWorld().getEnvironment().equals(World.Environment.NETHER)){
                itemStack = new ItemStack(Material.NETHERRACK);
            }

            else if(i.getLesserBoundaryCorner().getWorld().getEnvironment().equals(World.Environment.THE_END)){
                itemStack = new ItemStack(Material.END_STONE);
            }

            else{
                itemStack = new ItemStack(Material.GRASS_BLOCK);
            }

            ItemMeta itemMeta = itemStack.getItemMeta();

            // Change the lore and name of the object added
            itemMeta.setDisplayName(ChatColor.GRAY + Utils.getClaimName(i.getID()));

            Location lesserCorner = i.getLesserBoundaryCorner();
            Location greaterCorner = i.getGreaterBoundaryCorner();

            int lesserX = lesserCorner.getBlockX();
            int lesserZ = lesserCorner.getBlockZ();
            int greaterX = greaterCorner.getBlockX();
            int greaterZ = greaterCorner.getBlockZ();

            int middleX = (lesserX + greaterX) / 2;
            int middleZ = (lesserZ + greaterZ) / 2;

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "▸ ID: " + ChatColor.WHITE + i.getID());
            lore.add(ChatColor.GRAY + "▸ Center: " + ChatColor.WHITE + "x: " + middleX + ChatColor.GRAY + ", " + ChatColor.WHITE + "z: " + middleZ);
            lore.add(ChatColor.GRAY + "▸ Corners:");
            lore.add(ChatColor.GRAY + "    x: " + ChatColor.WHITE + lesserX + ChatColor.GRAY + ", z: " + ChatColor.WHITE + lesserZ);
            lore.add(ChatColor.GRAY + "    x: " + ChatColor.WHITE + greaterX + ChatColor.GRAY + ", z: " + ChatColor.WHITE + greaterX);
            lore.add(ChatColor.GRAY + "▸ Area: " + ChatColor.WHITE + i.getArea() + ChatColor.GRAY + " blocks");
            itemMeta.setLore(lore);

            itemStack.setItemMeta(itemMeta);

            allClaims.add(
                    itemStack
            );
        }

        ChestGui gui = new ChestGui(6, "Your claims");

        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);
        pages.populateWithItemStacks(allClaims);
        pages.setOnClick(event -> {
            //buy item
        });

        gui.addPane(pages);

        OutlinePane background = new OutlinePane(0, 5, 9, 1);
        background.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
        }));
        background.setRepeat(true);
        background.setPriority(Pane.Priority.LOWEST);
        gui.addPane(background);

        StaticPane navigation = new StaticPane(0, 5, 9, 1);


        ItemStack backButton = utilClass.itemGenerator(Material.RED_WOOL, ChatColor.RED+"Previous Page");
        navigation.addItem(new GuiItem(backButton, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);

                gui.update();
            }
            event.setCancelled(true);
        }), 0, 0);


        ItemStack nextButton = utilClass.itemGenerator(Material.GREEN_WOOL, ChatColor.GREEN+"Next Page");
        navigation.addItem(new GuiItem(nextButton, event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);

                gui.update();
            }
            event.setCancelled(true);
        }), 8, 0);

        ItemStack exitButton = utilClass.itemGenerator(Material.BARRIER, ChatColor.GREEN+"Exit");
        navigation.addItem(new GuiItem(exitButton, event ->
                event.getWhoClicked().closeInventory()), 4, 0
        );

        gui.addPane(navigation);
        gui.show(player);


        return false;
    }
}
