package kyrobi.cynagengpaddon.Menu;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.Utils;
import me.ryanhamshire.GPFlags.event.PlayerPreClaimBorderEvent;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static kyrobi.cynagengpaddon.Menu.ClaimsOption.claimsOptionMenu;
import static kyrobi.cynagengpaddon.Utils.claimsDateCache;
import static kyrobi.cynagengpaddon.Utils.getClaimDate;

public class ClaimsList {

    public static void claimsListMenu(Player player){
        // We grab all the claims of the player
        List<Claim> playerClaims = new ArrayList<>();
        PlayerData playerData = null;
        if (player != null) {
            playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
            if (playerData != null) {
                playerClaims = playerData.getClaims();
            } else {
                player.sendMessage(ChatColor.RED + "Error accessing your claims list...");
                return;
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
        int claimsCounter = 0;
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
            // long startTime = System.nanoTime();
            itemMeta.setDisplayName(ChatColor.GOLD + Utils.getClaimName(i.getID()));
//            long endTime = System.nanoTime();
//            long duration = (endTime - startTime) / 1000000;
//            long durationN = (endTime - startTime);
//            System.out.println("Duration:" + duration + "ms" + "  (" + durationN + ")");

            Location lesserCorner = i.getLesserBoundaryCorner();
            Location greaterCorner = i.getGreaterBoundaryCorner();

            int lesserX = lesserCorner.getBlockX();
            int lesserZ = lesserCorner.getBlockZ();
            int greaterX = greaterCorner.getBlockX();
            int greaterZ = greaterCorner.getBlockZ();

            int middleX = (lesserX + greaterX) / 2;
            int middleZ = (lesserZ + greaterZ) / 2;

            int width = Math.abs(greaterX - lesserX) + 1;
            int length = Math.abs(greaterZ - lesserZ) + 1;

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "▸ ID: " + ChatColor.WHITE + i.getID());
            lore.add(ChatColor.GRAY + "▸ Center: " + "x: " + ChatColor.WHITE + middleX + ChatColor.GRAY + ", " + "z: " + ChatColor.WHITE + middleZ);
            lore.add(ChatColor.GRAY + "▸ Corners:");
            lore.add(ChatColor.GRAY + "    x: " + ChatColor.WHITE + lesserX + ChatColor.GRAY + ", z: " + ChatColor.WHITE + lesserZ);
            lore.add(ChatColor.GRAY + "    x: " + ChatColor.WHITE + greaterX + ChatColor.GRAY + ", z: " + ChatColor.WHITE + greaterX);
            lore.add(ChatColor.GRAY + "▸ Area: " + ChatColor.WHITE + i.getArea() + ChatColor.GRAY + " blocks ");
            lore.add(ChatColor.GRAY + "    (" + ChatColor.WHITE + width + ChatColor.GRAY + "x" + ChatColor.WHITE + length + ChatColor.GRAY + ")");
            lore.add(ChatColor.GRAY + "▸ Creation date: ");
            lore.add(ChatColor.WHITE + "    " + getClaimDate(i.getID()));

            /*
            Enchant the block if the player is standing inside that claim to make it easier to see
             */
            Claim claimAtPlayer = GriefPrevention.instance.dataStore.getClaim(i.getID());
            if(claimAtPlayer.contains(player.getLocation(), true, false)){
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                lore.add("");
                lore.add(ChatColor.GREEN + "You are currently in this claim");
            }
            itemMeta.setLore(lore);

            itemStack.setItemMeta(itemMeta);

            allClaims.add(
                    itemStack
            );

            if(claimsCounter > 1000){
                player.sendMessage(ChatColor.RED + "WARNING: You have over 500 claims. We have limited the amount\n" +
                        "of claims shown to you to 500.\nConsider removing some unused claims.");
                break;
            }

            claimsCounter++;
        }

        ChestGui gui = new ChestGui(6, "Your claims");

        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);
        pages.populateWithItemStacks(allClaims);
        pages.setOnClick(event -> {
            long claimID = 0;

            if(event.getCurrentItem() == null){
                return;
            }

            String itemLoreFirstLine = event.getCurrentItem().getItemMeta().getLore().get(0);
            // System.out.println("Uncleaned: " + itemLoreFirstLine);
            // Using the substring to cut out the first digit since it's leftover from color code
            String cleanedUpString = itemLoreFirstLine.replaceAll("[^\\p{N}]", "").substring(1);
            //event.getWhoClicked().sendMessage(cleanedUpString);

            claimsOptionMenu((Player) event.getWhoClicked(), Long.parseLong(cleanedUpString));

        });

        gui.addPane(pages);

        OutlinePane background = new OutlinePane(0, 5, 9, 1);
        ItemStack borderBlock = Utils.itemGenerator(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY+"-");
        background.addItem(new GuiItem(borderBlock, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
        }));
        background.setRepeat(true);
        background.setPriority(Pane.Priority.LOWEST);
        gui.addPane(background);

        StaticPane navigation = new StaticPane(0, 5, 9, 1);


        ItemStack backButton = Utils.itemGenerator(Material.ARROW, ChatColor.GRAY + "Previous Page");
        navigation.addItem(new GuiItem(backButton, event -> {
            if (pages.getPage() > 0) {
                pages.setPage(pages.getPage() - 1);

                gui.update();
            }
            event.setCancelled(true);
        }), 0, 0);


        ItemStack nextButton = Utils.itemGenerator(Material.ARROW, ChatColor.GRAY + "Next Page");
        navigation.addItem(new GuiItem(nextButton, event -> {
            if (pages.getPage() < pages.getPages() - 1) {
                pages.setPage(pages.getPage() + 1);

                gui.update();
            }
            event.setCancelled(true);
        }), 8, 0);

        long totalClaimBlocksUsed = 0;

        for(Claim i: playerClaims){
            totalClaimBlocksUsed += i.getArea();
        }

        ArrayList<String> exitButtonLore = new ArrayList<>();
        exitButtonLore.add(ChatColor.GRAY + "Stats");
        exitButtonLore.add(ChatColor.GRAY + "▸ Claims: " + ChatColor.WHITE + playerClaims.size());
        exitButtonLore.add(ChatColor.GRAY + "▸ Total used: " + ChatColor.WHITE + totalClaimBlocksUsed);
        exitButtonLore.add(ChatColor.GRAY + "▸ Total remaining: " + ChatColor.GREEN + playerData.getRemainingClaimBlocks());
        exitButtonLore.add(ChatColor.GRAY + "--- Source ---");
        exitButtonLore.add(ChatColor.GRAY + "▸ From playtime: " + ChatColor.WHITE + playerData.getAccruedClaimBlocks());
        exitButtonLore.add(ChatColor.GRAY + "▸ From voting/admin: " + ChatColor.WHITE + playerData.getBonusClaimBlocks());
        exitButtonLore.add(ChatColor.GRAY + "▸ Total: " + ChatColor.WHITE + (playerData.getAccruedClaimBlocks() + playerData.getBonusClaimBlocks()));
        ItemStack exitButton = Utils.itemGenerator(Material.RED_WOOL, ChatColor.RED+"Exit", exitButtonLore);
        navigation.addItem(new GuiItem(exitButton, event ->
                event.getWhoClicked().closeInventory()), 4, 0
        );

        gui.addPane(navigation);
        gui.show(player);
    }
}
