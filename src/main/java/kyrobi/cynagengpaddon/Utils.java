package kyrobi.cynagengpaddon;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import static kyrobi.cynagengpaddon.CynagenGPAddon.folderDirectory;


public class Utils {

    public static HashMap<Long, String> claimsNameCache = new HashMap<>();

    //String jsonFilePath = "data.json"; // Replace "data.json" with the path to your JSON file

    public static ItemStack itemGenerator(Material materialType, String itemName, String ... lore){
        ItemStack myItem = new ItemStack(materialType);
        ItemMeta myMeta = myItem.getItemMeta();

        ArrayList<String> myLore = new ArrayList<>(Arrays.asList(lore));

        myMeta.setDisplayName(itemName);
        myMeta.setLore(myLore);
        myItem.setItemMeta(myMeta);
        return myItem;
    }

    public static ItemStack itemGenerator(Material materialType, String itemName, ArrayList<String> lore){
        ItemStack myItem = new ItemStack(materialType);
        ItemMeta myMeta = myItem.getItemMeta();

        myMeta.setDisplayName(itemName);
        myMeta.setLore(lore);
        myItem.setItemMeta(myMeta);
        return myItem;
    }

    public static ItemStack itemGenerator(Material materialType, String itemName){
        ItemStack myItem = new ItemStack(materialType);
        ItemMeta myMeta = myItem.getItemMeta();

        myMeta.setDisplayName(itemName);
        myItem.setItemMeta(myMeta);
        return myItem;
    }

    public static ItemStack itemGenerator(Material materialType){
        ItemStack myItem = new ItemStack(materialType);
        ItemMeta myMeta = myItem.getItemMeta();

        myItem.setItemMeta(myMeta);
        return myItem;
    }

//    public static String getClaimName(long ID){
//
//        String name = null;
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(folderDirectory))) {
//            Gson gson = new Gson();
//
//            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
//            Map<String, String> idNameMap = gson.fromJson(reader, type);
//
//            // Print the ID and Name pairs
//            if(idNameMap == null){
//                return "null";
//            }
//
//            name = idNameMap.getOrDefault(Long.toString(ID), "Unnamed");
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if(name == null){
//            return "Unnamed";
//        }
//        return name;
//    }

    public static String getClaimName(long ID){
        if(claimsNameCache == null){
            System.out.println("Hashmap is null");
            return "Unnamed";
        }

        else if(claimsNameCache.isEmpty()){
            System.out.println("Hashmap is empty");
            return "Unnamed";
        }

        return claimsNameCache.getOrDefault(ID, "Unnamed");
    }

    public static void setClaimName(long ID, String name){
        claimsNameCache.put(ID, name);
    }

    public static void readClaimsIntoMemory(){
        try (BufferedReader reader = new BufferedReader(new FileReader(folderDirectory))) {
            Gson gson = new Gson();

            Type type = new TypeToken<HashMap<Long, String>>() {}.getType();
            claimsNameCache = gson.fromJson(reader, type);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeClaimsToDisk(){
        // Specify the file path where you want to write the JSON data
        String filePath = folderDirectory;

        // Convert HashMap to JSON and write to the file
        try (FileWriter writer = new FileWriter(filePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(claimsNameCache, writer);
            System.out.println("HashMap values have been written to the JSON file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
