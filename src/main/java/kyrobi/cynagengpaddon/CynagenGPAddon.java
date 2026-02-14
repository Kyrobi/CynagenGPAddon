package kyrobi.cynagengpaddon;

import kyrobi.cynagengpaddon.Features.ProtectAnimals;
import kyrobi.cynagengpaddon.Fixes.MilkBucketDupe;
import kyrobi.cynagengpaddon.Flags.AllowPvP;
import kyrobi.cynagengpaddon.Flags.DenyEntry;
import kyrobi.cynagengpaddon.Flags.EnterExitMessage;
import kyrobi.cynagengpaddon.Listeners.*;
import kyrobi.cynagengpaddon.Menu.ClaimOptions.ClaimsRename;
import kyrobi.cynagengpaddon.Menu.ClaimOptions.FlagsPage.ClaimMessage;
import kyrobi.cynagengpaddon.Menu.ClaimOptions.FlagsPage.NoPlayerEnter;
import kyrobi.cynagengpaddon.Storage.Datastore;
import kyrobi.cynagengpaddon.commands.Claims;
import kyrobi.cynagengpaddon.commands.Eject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class CynagenGPAddon extends JavaPlugin {

    public static CynagenGPAddon plugin;

    @Override
    public void onEnable() {
        plugin = this;
        this.saveDefaultConfig();

        Datastore.initialize();
//        for (Map.Entry<Integer, ClaimData> entry : myDataStore.entrySet()) {
//            Integer claimID = entry.getKey();
//            ClaimData claimData = entry.getValue();
//
//            System.out.println("ClaimID " + claimID);
//        }


        Bukkit.getConsoleSender().sendMessage("CynagenGPAddon");

         new ClaimVisualizer(this);
         new EntityDamage(this);
         new ClaimCreate(this);
         new ClaimMessage(this);
         new CommandOverride(this);

         new NoPlayerEnter(this);
         new ClaimsRename(this);
         new ProtectAnimals(this);
         new ShovelHover(this);
         new ClaimTime(this);

        /*
        Flags
         */
        new AllowPvP(this);
        new DenyEntry(this);
        new EnterExitMessage(this);

        this.getCommand("claims").setExecutor((CommandExecutor)new Claims(this));
        this.getCommand("eject").setExecutor((CommandExecutor)new Eject(this));

        /*
        Fixes
         */
        new MilkBucketDupe(this);
    }

    public static CynagenGPAddon getPluginInstance(){
        return plugin;
    }

    @Override
    public void onDisable() {
        Datastore.uninitialize();
    }
}
