package ca.wescook.nutrition.utility;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import ca.wescook.nutrition.api.INutritionManager;
import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.nutrients.JsonNutrient;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.nutrients.NutrientUtils;

// Handles JSON and API data loading
public class DataImporter {

    private static final Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
        .setPrettyPrinting()
        .create();

    // Loads nutrients from JSONs and API
    // Runs initially during Post-Init, or from /reload command
    // Always call updatePlayerCapabilitiesOnServer() afterward if world is loaded
    public static void reload() {
        NutrientList.register(DataParser.parseNutrients(loadJsonNutrients()));

        // List all foods registered in-game without nutrients
        if (Config.logMissingNutrients) NutrientUtils.findRegisteredFoods();
    }

    // Updates player capabilities on server so object IDs match those in NutrientList
    public static void updatePlayerCapabilitiesOnServer(MinecraftServer server) {
        for (EntityPlayerMP player : server.getConfigurationManager().playerEntityList) {
            if (!server.worldServerForDimension(0).isRemote) {
                INutritionManager manager = NutritionManager.instance();
                Map<Nutrient, Float> nutritionOld = manager.evict(player);
                Map<Nutrient, Float> nutritionNew = new HashMap<>();

                loop: for (Nutrient nutrient : NutrientList.get()) {
                    if (nutritionOld != null) {
                        for (Map.Entry<Nutrient, Float> nutrientOld : nutritionOld.entrySet()) {
                            if (nutrient.name.equals(nutrientOld.getKey().name)) {
                                nutritionNew.put(nutrient, nutrientOld.getValue());
                                continue loop;
                            }
                        }
                    }
                    nutritionNew.put(nutrient, (float) Config.startingNutrition);
                }

                manager.setAll(player, nutritionNew);
            }
        }
    }

    //////////////////////////////////////////////////

    // Creates and parses nutrient json files into objects, returned as list
    private static List<JsonNutrient> loadJsonNutrients() {
        List<String> nutrientFiles = Lists
            .newArrayList("dairy.json", "example.json", "fruit.json", "grain.json", "protein.json", "vegetable.json");
        List<JsonNutrient> jsonObjectList = new ArrayList<>();
        ClassLoader loader = Thread.currentThread()
            .getContextClassLoader();
        for (String file : nutrientFiles) {
            try (InputStream inputStream = loader.getResourceAsStream("assets/nutrition/configs/nutrients/" + file)) {
                JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
                jsonObjectList.add(gson.fromJson(jsonReader, JsonNutrient.class));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                Log.fatal("The file " + file + " has invalid JSON and could not be loaded.");
                throw new IllegalArgumentException("Unable to load " + file + ".  Is the JSON valid?", e);
            }
        }
        return jsonObjectList;
    }
}
