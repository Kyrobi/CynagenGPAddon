package kyrobi.cynagengpaddon;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import static kyrobi.cynagengpaddon.CynagenGPAddon.folderDirectory;

public class Utils {

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

    public static void setClaimName(long ID, String name){
        String filePath = folderDirectory;
        String key = String.valueOf(ID);
        String value = name;

        try {
            // Read the JSON file and parse it into a JsonObject
            JsonParser parser = new JsonParser();
            JsonReader reader = new JsonReader(new FileReader(filePath));
            JsonObject jsonObject = parser.parse(reader).getAsJsonObject();

            // Check if the key already exists in the JsonObject
            if (jsonObject.has(key)) {
                // Key already exists, update the value
                jsonObject.addProperty(key, value);
            } else {
                // Key does not exist, add a new key-value pair
                jsonObject.addProperty(key, value);
            }

            // Convert the updated JsonObject back to a JSON string
            Gson gson = new Gson();
            String jsonString = gson.toJson(jsonObject);

            // Write the JSON string back to the file, replacing the existing content
            FileWriter fileWriter = new FileWriter(new File(filePath));
            fileWriter.write(jsonString);
            fileWriter.close();

            System.out.println("JSON file updated successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
