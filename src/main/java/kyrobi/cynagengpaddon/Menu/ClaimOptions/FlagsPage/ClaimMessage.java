package kyrobi.cynagengpaddon.Menu.ClaimOptions.FlagsPage;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Utils;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
            showClaimFlags((Player) event.getWhoClicked(), claimID);
        }), 4, 5 ); // Indexed 4 to the right, Index 5 down


        /*
        Set claim enter message
         */
        FlagDefinition enterFlag = manager.getFlagDefinitionByName("EnterMessage");

        ArrayList<String> setClaimEnterMessageButtonLore = new ArrayList<>();
        setClaimEnterMessageButtonLore.add(ChatColor.GRAY + "Set message to show when a player enters your claim");
        setClaimEnterMessageButtonLore.add(" ");
        setClaimEnterMessageButtonLore.add(ChatColor.GRAY + "▸ Current enter message: ");

        if(manager.getFlag(claim, enterFlag) == null){
            setClaimEnterMessageButtonLore.add(ChatColor.WHITE + "None");

        }
        else if (manager.getFlag(claim, enterFlag).getParametersArray().length == 0) {
            setClaimEnterMessageButtonLore.add(ChatColor.WHITE + "None");
        }
        else {
            StringBuilder message = new StringBuilder();
            for(String i: manager.getFlag(claim, enterFlag).getParametersArray()){
                message.append(i + " ");
            }
            setClaimEnterMessageButtonLore.add(ChatColor.WHITE + message.toString());
        }

        ItemStack setClaimEnterMessageButton = Utils.itemGenerator(Material.OAK_SIGN, ChatColor.GREEN + "Set claim enter message", setClaimEnterMessageButtonLore);
        navigation.addItem(new GuiItem(setClaimEnterMessageButton, event -> {
            event.setCancelled(true);
            setClaimEnterMessage((Player) event.getWhoClicked(), event ,claimID);

        }), 3, 2 );

        /*
        Set claim leave message
         */
        FlagDefinition exitFlag = manager.getFlagDefinitionByName("ExitMessage");

        ArrayList<String> setClaimLeaveMessageButtonLore = new ArrayList<>();
        setClaimLeaveMessageButtonLore.add(ChatColor.GRAY + "Set message to show when a player exists your claim");
        setClaimLeaveMessageButtonLore.add(" ");
        setClaimLeaveMessageButtonLore.add(ChatColor.GRAY + "▸ Current exist message: ");

        if(manager.getFlag(claim, exitFlag) == null){
            setClaimLeaveMessageButtonLore.add(ChatColor.WHITE + "None");
        }
        else if (manager.getFlag(claim, exitFlag).getParametersArray().length == 0) {
            setClaimLeaveMessageButtonLore.add(ChatColor.WHITE + "None");
        }
        else {
            StringBuilder message = new StringBuilder();
            for(String i: manager.getFlag(claim, exitFlag).getParametersArray()){
                message.append(i + " ");
            }
            setClaimLeaveMessageButtonLore.add(ChatColor.WHITE + message.toString());
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

            Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);
            FlagManager manager = GPFlags.getInstance().getFlagManager();
            FlagDefinition flag = manager.getFlagDefinitionByName("EnterMessage");
            manager.setFlag(claim, flag, true, message);
            player.sendMessage(ChatColor.GREEN + "Claim enter message set to: " + ChatColor.WHITE + message);
            manager.save();
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

            Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);
            FlagManager manager = GPFlags.getInstance().getFlagManager();
            FlagDefinition flag = manager.getFlagDefinitionByName("ExitMessage");
            manager.setFlag(claim, flag, true, message);
            player.sendMessage(ChatColor.GREEN + "Claim leave message set to: " + ChatColor.WHITE + message);
            manager.save();
        });
        player.closeInventory();
    }

    public static void unsetClaimEnterMessage(Player player, InventoryClickEvent invEvent, long claimID){
        Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);
        FlagManager manager = GPFlags.getInstance().getFlagManager();
        FlagDefinition flag = manager.getFlagDefinitionByName("EnterMessage");
        manager.unSetFlag(claim, flag);
        player.sendMessage(ChatColor.GREEN + "Cleared enter message");
        manager.save();
        showClaimMessageMenu(player, claimID);
    }

    public static void unsetClaimLeaveMessage(Player player, InventoryClickEvent invEvent, long claimID){
        Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);
        FlagManager manager = GPFlags.getInstance().getFlagManager();
        FlagDefinition flag = manager.getFlagDefinitionByName("ExitMessage");
        manager.unSetFlag(claim, flag);
        player.sendMessage(ChatColor.GREEN + "Cleared exist message");
        manager.save();
        showClaimMessageMenu(player, claimID);
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
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
















/*
Chat Input Handling Mechanism:

The core of the code revolves around capturing player input from the chat
and using it in the context of your GUI. Here's how it works step by step:

When the setClaimEnterMessage or setClaimLeaveMessage methods are called,
a message is sent to the player telling them to enter the desired claim message in the chat.

The chatInputCallbacks map is then used to store a callback function that
will be executed when the player sends a chat message. This callback function takes the player's input as a parameter and handles it.

In the onChat event handler, the plugin listens for chat events. When a
player sends a chat message, the plugin checks if the player has a pending callback stored in the chatInputCallbacks map.

If a callback is found, the player's chat message is passed to the corresponding
callback function. The callback function handles the input, which, in this case, involves setting or modifying the claim message.

After handling the input, the callback function is removed from the
chatInputCallbacks map. This ensures that the chat input is only processed once for the intended purpose.

Integration with GUI:

In your GUI setup, when a player clicks on buttons related to setting
claim messages, instead of immediately handling the logic to set the messages, you instruct the plugin
to wait for the player's input. You do this by storing a callback function in the chatInputCallbacks map and
then closing the inventory. This effectively puts the plugin in a "waiting for input" state.

Why It Works:

The key to this mechanism is the use of callbacks and the event system. The Bukkit event system allows your
plugin to listen for various in-game events, such as player interactions or chat messages. By combining this event
system with the concept of callbacks, you create a flexible way to handle asynchronous input.

This approach is commonly used in event-driven programming, where you don't know exactly when certain
events will occur. Instead of blocking the code and waiting for input (which could freeze the game), you initiate the process, set
up a callback to handle the result, and let the rest of the game continue. When the expected event (in this
case, the chat message) happens, the callback is executed to process the result.

In summary, the code works by leveraging the Bukkit event system and callbacks to handle asynchronous
player input effectively. It allows players to provide input at their own pace without blocking the game's execution,
making for a smoother and more interactive experience.
 */
