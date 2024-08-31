package kyrobi.cynagengpaddon.Menu.ClaimOptions;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import kyrobi.cynagengpaddon.CynagenGPAddon;
import kyrobi.cynagengpaddon.Storage.ClaimData;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;

public class ClaimsRename implements Listener {

    public static HashMap<String, String> nameCache = new HashMap<>();
    private static Map<UUID, Consumer<String>> chatInputCallbacks = new HashMap<>();

    public ClaimsRename(CynagenGPAddon plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void claimsRenamingMenu(Player player, InventoryClickEvent invClick, long claimID){

        ClaimData claimData = myDataStore.getOrDefault(claimID, new ClaimData(claimID, player));

        player.sendMessage(ChatColor.GREEN + "Enter a name for the claim: ");
        chatInputCallbacks.put(player.getUniqueId(), message -> {
            if(message.length() > 16){
                player.sendMessage(ChatColor.RED + "Names cannot be over 16 characters. You had " + message.length() + " characters");
                return;
            }
            else {
                if(message.equals("")){
                    claimData.setClaimName("No name");
                    player.sendMessage(ChatColor.RED + "Warning: Name cannot contain special characters or be empty.");
                } else{
                    claimData.setClaimName(message);
                    player.sendMessage(ChatColor.GREEN + "Claim renamed to: " + ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', message));
                }

                myDataStore.put(claimID, claimData);
            }


        });
        player.closeInventory();

    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (chatInputCallbacks.containsKey(playerUUID)) {
            // String filteredString = event.getMessage().replaceAll("§[a-z]", "").trim();
            // System.out.println("MEESSAGE: " + filteredString);
            event.setCancelled(true);

            Consumer<String> callback = chatInputCallbacks.get(playerUUID);
            chatInputCallbacks.remove(playerUUID);

            callback.accept(event.getMessage());
        }
    }
}
