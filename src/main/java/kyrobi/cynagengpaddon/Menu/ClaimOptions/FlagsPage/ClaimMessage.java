package kyrobi.cynagengpaddon.Menu.ClaimOptions.FlagsPage;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Storage.ClaimData;
import kyrobi.cynagengpaddon.Storage.Datastore;
import kyrobi.cynagengpaddon.Utils;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static kyrobi.cynagengpaddon.Menu.ClaimOptions.ClaimsFlags.showClaimFlags;
import static kyrobi.cynagengpaddon.Menu.ClaimOptions.FlagsPage.NoPlayerEnter.claimsNoPlayerEnterOption;
import static kyrobi.cynagengpaddon.Menu.ClaimsOption.claimsOptionMenu;
import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class ClaimMessage implements Listener {

    private CynagenGPAddon plugin;
    public ClaimMessage(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private static Map<UUID, Consumer<String>> chatInputCallbacks = new HashMap<>();


    public static void showClaimMessageMenu(Player player, long claimID){
        Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);
        ClaimData claimData = myDataStore.getOrDefault(claimID, new ClaimData(claimID, player));


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
            showClaimFlags((Player) event.getWhoClicked(), claimID);
        }), 4, 5 ); // Indexed 4 to the right, Index 5 down


        /*
        Set claim enter message
         */


        ArrayList<String> setClaimEnterMessageButtonLore = new ArrayList<>();
        setClaimEnterMessageButtonLore.add(ChatColor.GRAY + "Set message to show when a player enters your claim");
        setClaimEnterMessageButtonLore.add(" ");
        setClaimEnterMessageButtonLore.add(ChatColor.GRAY + "▸ Current enter message: ");

        if(claimData.getEnterMessage() == null){
            setClaimEnterMessageButtonLore.add(ChatColor.WHITE + "None");
        }

        else if (claimData.getEnterMessage().isEmpty()) {
            setClaimEnterMessageButtonLore.add(ChatColor.WHITE + "None");
        }
        else {
            String message = claimData.getEnterMessage();
            setClaimEnterMessageButtonLore.add(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message));
        }

        ItemStack setClaimEnterMessageButton = Utils.itemGenerator(Material.OAK_SIGN, ChatColor.GREEN + "Set claim enter message", setClaimEnterMessageButtonLore);
        navigation.addItem(new GuiItem(setClaimEnterMessageButton, event -> {
            event.setCancelled(true);
            setClaimEnterMessage((Player) event.getWhoClicked(), event ,claimID);

        }), 3, 2 );

        /*
        Set claim leave message
         */

        ArrayList<String> setClaimLeaveMessageButtonLore = new ArrayList<>();
        setClaimLeaveMessageButtonLore.add(ChatColor.GRAY + "Set message to show when a player exists your claim");
        setClaimLeaveMessageButtonLore.add(" ");
        setClaimLeaveMessageButtonLore.add(ChatColor.GRAY + "▸ Current exist message: ");

        if(claimData.getExitMessage() == null){
            setClaimLeaveMessageButtonLore.add(ChatColor.WHITE + "None");
        }
        else if (claimData.getExitMessage().isEmpty()) {
            setClaimLeaveMessageButtonLore.add(ChatColor.WHITE + "None");
        }
        else {
            String message = claimData.getExitMessage();
            setClaimLeaveMessageButtonLore.add(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message));
        }

        ItemStack setClaimLeaveMessageButton = Utils.itemGenerator(Material.OAK_SIGN, ChatColor.GREEN + "Set claim leave message", setClaimLeaveMessageButtonLore);
        navigation.addItem(new GuiItem(setClaimLeaveMessageButton, event -> {
            event.setCancelled(true);
            setClaimLeaveMessage((Player) event.getWhoClicked(), event ,claimID);

        }), 5, 2 );

        /*
        Unset claim enter message
         */
        ArrayList<String> unsetClaimEnterMessageButtonLore = new ArrayList<>();
        unsetClaimEnterMessageButtonLore.add(ChatColor.GRAY + "Clear the message to show when a player enters your claim");
        ItemStack unsetClaimEnterMessageButton = Utils.itemGenerator(Material.OAK_SIGN, ChatColor.GREEN + "Unset claim enter message", unsetClaimEnterMessageButtonLore);
        navigation.addItem(new GuiItem(unsetClaimEnterMessageButton, event -> {
            event.setCancelled(true);
            unsetClaimEnterMessage((Player) event.getWhoClicked(), event ,claimID);

        }), 3, 3 );

        /*
        Unset claim leave message
         */
        ArrayList<String> unsetClaimLeaveMessageButtonLore = new ArrayList<>();
        unsetClaimLeaveMessageButtonLore.add(ChatColor.GRAY + "Clear the message to show when a player leaves your claim");
        ItemStack unsetClaimLeaveMessageButton = Utils.itemGenerator(Material.OAK_SIGN, ChatColor.GREEN + "Unset claim leave message", unsetClaimLeaveMessageButtonLore);
        navigation.addItem(new GuiItem(unsetClaimLeaveMessageButton, event -> {
            event.setCancelled(true);
            unsetClaimLeaveMessage((Player) event.getWhoClicked(), event ,claimID);

        }), 5, 3 );

        gui.addPane(navigation);
        gui.show(player);
    }

    public static void setClaimEnterMessage(Player player, InventoryClickEvent invEvent, long claimID){
        player.sendMessage(ChatColor.GREEN + "Please enter your claim enter message in chat:");
        chatInputCallbacks.put(player.getUniqueId(), message -> {
            if(message.length() > 64){
                player.sendMessage(ChatColor.RED + "Message cannot be over 64 characters. You had " + message.length() + " characters");
                return;
            }

            ClaimData claimData = myDataStore.getOrDefault(claimID, new ClaimData(claimID, player));
            claimData.setEnterMessage(message);
            myDataStore.put(claimID, claimData);
            player.sendMessage(ChatColor.GREEN + "Claim enter message set to: " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message));
        });
        player.closeInventory();
    }

    public static void setClaimLeaveMessage(Player player, InventoryClickEvent invEvent, long claimID){
        player.sendMessage(ChatColor.GREEN + "Please enter your claim leave message in chat:");
        chatInputCallbacks.put(player.getUniqueId(), message -> {
            if(message.length() > 64){
                player.sendMessage(ChatColor.RED + "Message cannot be over 64 characters. You had " + message.length() + " characters");
                return;
            }

            ClaimData claimData = myDataStore.getOrDefault(claimID, new ClaimData(claimID, player));
            claimData.setExitMessage(message);
            myDataStore.put(claimID, claimData);
            player.sendMessage(ChatColor.GREEN + "Claim leave message set to: " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message));

        });
        player.closeInventory();
    }

    public static void unsetClaimEnterMessage(Player player, InventoryClickEvent invEvent, long claimID){
        ClaimData claimData = myDataStore.getOrDefault(claimID, new ClaimData(claimID, player));
        claimData.setEnterMessage("");
        player.sendMessage(ChatColor.GREEN + "Cleared enter message");
        myDataStore.put(claimID, claimData);
        showClaimMessageMenu(player, claimID);
    }

    public static void unsetClaimLeaveMessage(Player player, InventoryClickEvent invEvent, long claimID){
        ClaimData claimData = myDataStore.getOrDefault(claimID, new ClaimData(claimID, player));
        claimData.setExitMessage("");
        player.sendMessage(ChatColor.GREEN + "Cleared exit message");
        myDataStore.put(claimID, claimData);
        showClaimMessageMenu(player, claimID);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (chatInputCallbacks.containsKey(playerUUID)) {
            event.setCancelled(true);

            Consumer<String> callback = chatInputCallbacks.get(playerUUID);
            chatInputCallbacks.remove(playerUUID);

            callback.accept(event.getMessage());
        }
    }
}