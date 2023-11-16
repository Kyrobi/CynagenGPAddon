package kyrobi.cynagengpaddon.Menu.ClaimOptions;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

import static kyrobi.cynagengpaddon.Utils.setClaimName;

public class ClaimsRename implements Listener {

    public static HashMap<String, String> nameCache = new HashMap<>();
    private static Map<UUID, Consumer<String>> chatInputCallbacks = new HashMap<>();

    public ClaimsRename(CynagenGPAddon plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void claimsRenamingMenu(Player player, InventoryClickEvent invClick, long claimID){

        player.sendMessage(ChatColor.GREEN + "Enter a name for the claim: ");
        chatInputCallbacks.put(player.getUniqueId(), message -> {
            if(message.length() > 16){
                player.sendMessage(ChatColor.RED + "Names cannot be over 16 characters. You had " + message.length() + " characters");
                return;
            }

            if(message.equals("")){
                setClaimName(claimID, "No name");
                player.sendMessage(ChatColor.RED + "Warning: Name cannot contain special characters or be empty.");
            } else{
                setClaimName(claimID, message);
                player.sendMessage(ChatColor.GREEN + "Claim renamed to: " + ChatColor.GRAY + message);
            }

        });
        player.closeInventory();
//        invClick.setCancelled(true);
//
//        AnvilGui anvilGui = new AnvilGui("Type in a name");
//        anvilGui.setCost((short)0);
//        anvilGui.setOnGlobalClick(inventoryClickEvent -> invClick.setCancelled(true));
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
//        fillerButtonMeta.setDisplayName(ChatColor.GRAY + "Change Claim Name");
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
//            if(nameCache.get(invClick.getWhoClicked().getName()).equals("")){
//                setClaimName(claimID, "No name");
//                confirmEvent.getWhoClicked().sendMessage(ChatColor.RED + "Warning: Name cannot contain special characters or be empty.");
//            } else{
//                setClaimName(claimID, nameCache.get(invClick.getWhoClicked().getName()));
//                confirmEvent.getWhoClicked().sendMessage(ChatColor.GREEN + "Claim renamed to: " + ChatColor.GRAY + nameCache.get(invClick.getWhoClicked().getName()));
//            }
//
//            nameCache.remove(invClick.getWhoClicked().getName());
//        }), 0, 0);
//
//
//        anvilGui.getFirstItemComponent().addPane(fillerPane);
//        anvilGui.getResultComponent().addPane(confirmPane);
//        anvilGui.show(player);
//
//        anvilGui.setOnNameInputChanged(s -> {
//            String cleanedString = s.replaceAll("[^\\x00-\\x7F]", "");
//            nameCache.put(invClick.getWhoClicked().getName(), cleanedString);
//        });

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
