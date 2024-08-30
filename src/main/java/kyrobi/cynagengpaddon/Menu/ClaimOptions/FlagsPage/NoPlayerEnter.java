package kyrobi.cynagengpaddon.Menu.ClaimOptions.FlagsPage;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Menu.ClaimOptions.ClaimsTrust;
import kyrobi.cynagengpaddon.Storage.ClaimData;
import kyrobi.cynagengpaddon.Utils;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.function.Consumer;

import static kyrobi.cynagengpaddon.Menu.ClaimOptions.ClaimsFlags.showClaimFlags;
import static kyrobi.cynagengpaddon.Menu.ClaimOptions.ClaimsRename.claimsRenamingMenu;
import static kyrobi.cynagengpaddon.Menu.ClaimsOption.claimsOptionMenu;
import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;

public class NoPlayerEnter implements Listener {
    private static HashMap<String, String> nameCache = new HashMap<>();
    private static Map<UUID, Consumer<String>> chatInputCallbacks = new HashMap<>();

    public NoPlayerEnter(CynagenGPAddon plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /*
    Menu to show what the player wants to do. View the list of blocked members or add them
     */
    public static void claimsNoPlayerEnterOption(Player player, InventoryClickEvent invClick, long claimID){


        ChestGui gui = new ChestGui(6, "Blacklist Player Options");

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
            showClaimFlags((Player) event.getWhoClicked(), claimID);
        }), 4, 5 ); // Indexed 4 to the right, Index 5 down


        /*
        View players
         */
        ItemStack membersButton = Utils.itemGenerator(Material.FLOWER_BANNER_PATTERN, ChatColor.GREEN + "View blacklisted players");
        navigation.addItem(new GuiItem(membersButton, event -> {
            event.setCancelled(true);
            claimsNoPlayerEnterMemberList((Player) event.getWhoClicked(), claimID);

        }), 3, 2 );


        /*
        Blacklist player
         */
        ItemStack flagsButton = Utils.itemGenerator(Material.NAME_TAG, ChatColor.GREEN + "Blacklist a new player");
        navigation.addItem(new GuiItem(flagsButton, event -> {
            event.setCancelled(true);
            claimsNoPlayerEnterMenuAddPlayer((Player) event.getWhoClicked(), event, claimID);
        }), 5, 2);


        gui.addPane(navigation);
        gui.show(player);
    }

    /*
    Adding a new player to the blocked list
     */
    public static void claimsNoPlayerEnterMenuAddPlayer(Player player, InventoryClickEvent invClick, long claimID){

        player.sendMessage(ChatColor.GREEN + "Enter the name of the player you wish to block from entering: ");
        chatInputCallbacks.put(player.getUniqueId(), message -> {
            if(message.length() > 16){
                player.sendMessage(ChatColor.RED + "Names cannot be over 16 characters. You had " + message.length() + " characters");
                return;
            }

            Player onlinePlayerExact = Bukkit.getPlayerExact(message);
            if(onlinePlayerExact == null){
                player.sendMessage(ChatColor.RED + message + " is not online");
                player.closeInventory();

            } else {

                Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);
                ClaimData claimData = myDataStore.getOrDefault(claim.getID(), new ClaimData(claimID, player));

                claimData.getNoEnterPlayer().add(onlinePlayerExact.getUniqueId().toString());
                System.out.println("Adding " + onlinePlayerExact.getUniqueId().toString());
                myDataStore.put(claimID, claimData);

                player.sendMessage(ChatColor.GREEN + "Blocked " + message + " from your claim");
            }
        });
        player.closeInventory();

//        AnvilGui anvilGui = new AnvilGui("Type in a name");
//        anvilGui.setCost((short)0);
//        anvilGui.setOnGlobalClick(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
//
//        // Pane for the confirmation button
//        StaticPane confirmPane = new StaticPane(0, 0, 1, 1);
//        ItemStack confirmButton = new ItemStack(Material.GREEN_WOOL);
//        ItemMeta confirmButtonMeta = confirmButton.getItemMeta();
//        confirmButtonMeta.setDisplayName(ChatColor.GREEN + "Confirm");
//        confirmButton.setItemMeta(confirmButtonMeta);
//
//        // Pane for the name
//        StaticPane fillerPane = new StaticPane(0, 0, 1, 1);
//        ItemStack fillerButton = new ItemStack(Material.PAPER);
//        ItemMeta fillerButtonMeta = fillerButton.getItemMeta();
//        fillerButtonMeta.setDisplayName(ChatColor.GRAY + "Block user from claim");
//        fillerButton.setItemMeta(fillerButtonMeta);
//
//        // Filler button
//        fillerPane.addItem(new GuiItem(fillerButton, fillerPanelEvent -> {
//            fillerPanelEvent.setCancelled(true);
//        }), 0, 0);
//
//
//        // Confirm button
//        confirmPane.addItem(new GuiItem(confirmButton, confirmEvent -> {
//            confirmEvent.setCancelled(true);
//            confirmEvent.getWhoClicked().closeInventory();
//
//            Player onlinePlayerExact = Bukkit.getPlayerExact(nameCache.get(confirmEvent.getWhoClicked().getName()));
//            if(onlinePlayerExact == null){
//                confirmEvent.getWhoClicked().sendMessage(ChatColor.RED + nameCache.get(confirmEvent.getWhoClicked().getName()) + " is not online");
//                confirmEvent.setCancelled(true);
//                player.closeInventory();
//
//            } else{
//
//                Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);
//                FlagManager manager = GPFlags.getInstance().getFlagManager();
//                FlagDefinition flag = manager.getFlagDefinitionByName("NoEnterPlayer");
//
//                // If the flag doesn't exist prior, make one and add the player
//                if(manager.getFlag(claim, "NoEnterPlayer") == null){
//                    manager.setFlag(claim, flag, true, nameCache.get(confirmEvent.getWhoClicked().getName()));
//                    manager.save();
//                } else {
//                    String[] blockedMembers = manager.getFlag(claim, "NoEnterPlayer").getParametersArray();
//                    List<String> list = new ArrayList<>(Arrays.asList(blockedMembers));
//                    list.add(onlinePlayerExact.getName());
//                    String[] updatedBlockedMembers = list.toArray(new String[0]);
//
//                    manager.setFlag(claim, flag, true, updatedBlockedMembers);
//                    manager.save();
//                }
//                confirmEvent.getWhoClicked().sendMessage(ChatColor.GREEN + "Blocked " + ChatColor.GRAY + nameCache.get(confirmEvent.getWhoClicked().getName()) + ChatColor.GREEN + " from your claim.");
//            }
//
//            nameCache.remove(confirmEvent.getWhoClicked().getName());
//        }), 0, 0);
//
//
//        anvilGui.getFirstItemComponent().addPane(fillerPane);
//        anvilGui.getResultComponent().addPane(confirmPane);
//        anvilGui.show(player);
//
//        invClick.setCancelled(true);
//
//        anvilGui.setOnNameInputChanged(s -> {
//            String cleanedString = s.replaceAll("[^\\x00-\\x7F]", "");
//            nameCache.put(invClick.getWhoClicked().getName(), cleanedString);
//        });
    }

    /*
    Lists all players that are banned
     */
    public static void claimsNoPlayerEnterMemberList(Player player, long claimID){
        List<ItemStack> allMembers = new ArrayList<>();
        Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);
        if(claim == null){
            return;
        }

        ClaimData claimData = myDataStore.getOrDefault(claimID, new ClaimData(claimID, player));
        Set<String> blockedMembers = claimData.getNoEnterPlayer();

        // boolean noEnterPlayerEnabled = manager.getFlag(claim, "NoEnterPlayer").getSet();
        if(blockedMembers.isEmpty()){
            /*
            Do nothing. Add nothing to the menu.
             */
        } else {

            for(String i: blockedMembers){
                System.out.println("Trying to convert: [" + i + "]");
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(i));

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
                lore.add(ChatColor.RED + "Click to remove from ban list");
                itemMeta.setLore(lore);
                playerIcon.setItemMeta(itemMeta);

                allMembers.add(playerIcon);
            }
        }


        ChestGui gui = new ChestGui(6, "Blocked Members");
        PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);
        pages.populateWithItemStacks(allMembers);
        pages.setOnClick(e -> {
            e.setCancelled(true);
            if(e.getCurrentItem() == null){
                return;
            }

            OfflinePlayer playerToRemove = Bukkit.getOfflinePlayer(e.getCurrentItem().getItemMeta().getLore().get(0));

            blockedMembers.remove(playerToRemove.getUniqueId().toString());
            // Everyone has been removed from this claim. If so, just remove the flag entirely
            if(blockedMembers.isEmpty()){
                claimsNoPlayerEnterMemberList(player, claimID);
                return;
            }

            claimsNoPlayerEnterMemberList(player, claimID);
        });

        gui.addPane(pages);

        /*
        Adds the borders
        */
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
            claimsNoPlayerEnterOption(player, event, claimID);
        }), 4, 5 ); // Indexed 4 to the right, Index 5 down
        gui.addPane(navigation);
        gui.show(player);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (chatInputCallbacks.containsKey(playerUUID)) {
            String filteredString = event.getMessage().replaceAll("§[a-z]", "").trim();
            System.out.println("MEESSAGE: " + filteredString);
            event.setCancelled(true);

            Consumer<String> callback = chatInputCallbacks.get(playerUUID);
            chatInputCallbacks.remove(playerUUID);

            callback.accept(filteredString);
        }
    }
}
