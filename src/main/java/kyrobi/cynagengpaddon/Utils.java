package kyrobi.cynagengpaddon;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static kyrobi.cynagengpaddon.CynagenGPAddon.folderDirectory;

public class Utils {

    //String jsonFilePath = "data.json"; // Replace "data.json" with the path to your JSON file

    public ItemStack itemGenerator(Material materialType, String itemName, String ... lore){
        ItemStack myItem = new ItemStack(materialType);
        ItemMeta myMeta = myItem.getItemMeta();

        ArrayList<String> myLore = new ArrayList<>(Arrays.asList(lore));

        myMeta.setDisplayName(itemName);
        myMeta.setLore(myLore);
        myItem.setItemMeta(myMeta);
        return myItem;
    }

    public ItemStack itemGenerator(Material materialType, String itemName){
        ItemStack myItem = new ItemStack(materialType);
        ItemMeta myMeta = myItem.getItemMeta();

        myMeta.setDisplayName(itemName);
        myItem.setItemMeta(myMeta);
        return myItem;
    }

    public ItemStack itemGenerator(Material materialType){
        ItemStack myItem = new ItemStack(materialType);
        ItemMeta myMeta = myItem.getItemMeta();

        myItem.setItemMeta(myMeta);
        return myItem;
    }

    public static String getClaimName(long ID){

        String name = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(folderDirectory))) {
            Gson gson = new Gson();

            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            Map<String, String> idNameMap = gson.fromJson(reader, type);

            // Print the ID and Name pairs
            if(idNameMap == null){
                return "null";
            }

            name = idNameMap.getOrDefault(Long.toString(ID), "Unnamed");


        } catch (IOException e) {
            e.printStackTrace();
        }

        if(name == null){
            return "Unnamed";
        }
        return name;

    }

    public void setClaimName(long ID, String name){

    }
}
