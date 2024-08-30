package kyrobi.cynagengpaddon;

import kyrobi.cynagengpaddon.Features.ProtectAnimals;
import kyrobi.cynagengpaddon.Flags.AllowPvP;
import kyrobi.cynagengpaddon.Flags.DenyEntry;
import kyrobi.cynagengpaddon.Flags.EnterExitMessage;
import kyrobi.cynagengpaddon.Listeners.ClaimCreate;
import kyrobi.cynagengpaddon.Listeners.ClaimVisualizer;
import kyrobi.cynagengpaddon.Listeners.CommandOverride;
import kyrobi.cynagengpaddon.Listeners.ShovelHover;
import kyrobi.cynagengpaddon.Menu.ClaimOptions.ClaimsRename;
import kyrobi.cynagengpaddon.Menu.ClaimOptions.FlagsPage.ClaimMessage;
import kyrobi.cynagengpaddon.Menu.ClaimOptions.FlagsPage.NoPlayerEnter;
import kyrobi.cynagengpaddon.Storage.ClaimData;
import kyrobi.cynagengpaddon.Storage.Datastore;
import kyrobi.cynagengpaddon.commands.Claims;
import kyrobi.cynagengpaddon.commands.Eject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static kyrobi.cynagengpaddon.Storage.Datastore.myDataStore;
import static kyrobi.cynagengpaddon.Utils.*;

public final class CynagenGPAddon extends JavaPlugin {

    public static CynagenGPAddon plugin;

    static File dbFile = new File("");
    public static String claimNamesFile = String.valueOf(
            new File(dbFile.getAbsolutePath() + File.separator + "plugins" + File.separator + "CynagenGPAddon" + File.separator + "name.json"));

    public static String claimDatesFile = String.valueOf(
            new File(dbFile.getAbsolutePath() + File.separator + "plugins" + File.separator + "CynagenGPAddon" + File.separator + "dates.json"));

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        readClaimsIntoMemory();
        readDatesIntoMemory();

        Datastore.initialize();
//        for (Map.Entry<Integer, ClaimData> entry : myDataStore.entrySet()) {
//            Integer claimID = entry.getKey();
//            ClaimData claimData = entry.getValue();
//
//            System.out.println("ClaimID " + claimID);
//        }

        File nameFile = new File(dbFile.getAbsolutePath() + File.separator + "plugins" + File.separator + "CynagenGPAddon" + File.separator + "name.json");
        if(!nameFile.exists()){
            try {
                nameFile.createNewFile(); // if file already exists will do nothing
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                FileOutputStream oFile = new FileOutputStream(nameFile, false);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        File dateFile = new File(dbFile.getAbsolutePath() + File.separator + "plugins" + File.separator + "CynagenGPAddon" + File.separator + "dates.json");
        if(!dateFile.exists()){
            try {
                dateFile.createNewFile(); // if file already exists will do nothing
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                FileOutputStream oFile = new FileOutputStream(dateFile, false);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        Bukkit.getConsoleSender().sendMessage("CynagenGPAddon");

         new ClaimVisualizer(this);
         new ClaimCreate(this);
         new ClaimMessage(this);
         new CommandOverride(this);

         new NoPlayerEnter(this);
         new ClaimsRename(this);
         new ProtectAnimals(this);
         new ShovelHover(this);

        /*
        Flags
         */
        new AllowPvP(this);
        new DenyEntry(this);
        new EnterExitMessage(this);

         this.getCommand("claims").setExecutor((CommandExecutor)new Claims(this));
         this.getCommand("eject").setExecutor((CommandExecutor)new Eject(this));
    }

    @Override
    public void onDisable() {
        writeClaimsToDisk();
        writeDatesToDisk();
        Datastore.uninitialize();
    }
}
