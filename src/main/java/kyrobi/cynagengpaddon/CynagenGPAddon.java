package kyrobi.cynagengpaddon;

import kyrobi.cynagengpaddon.commands.Claims;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public final class CynagenGPAddon extends JavaPlugin {

    static File dbFile = new File("");
    public static String folderDirectory = String.valueOf(
            new File(dbFile.getAbsolutePath() + File.separator + "plugins" + File.separator + "CynagenGPAddon" + File.separator + "data.json"));

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        File yourFile = new File(dbFile.getAbsolutePath() + File.separator + "plugins" + File.separator + "CynagenGPAddon" + File.separator + "data.json");
        if(!yourFile.exists()){
            try {
                yourFile.createNewFile(); // if file already exists will do nothing
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                FileOutputStream oFile = new FileOutputStream(yourFile, false);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        Bukkit.getConsoleSender().sendMessage("CynagenGPAddon");


        this.getCommand("claims").setExecutor((CommandExecutor)new Claims(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
