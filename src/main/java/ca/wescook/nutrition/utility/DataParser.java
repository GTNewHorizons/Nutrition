package ca.wescook.nutrition.utility;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.math.NumberUtils;

import ca.wescook.nutrition.nutrients.JsonNutrient;
import ca.wescook.nutrition.nutrients.Nutrient;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import squeek.applecore.api.AppleCoreAPI;

public class DataParser {

    // Accepts a list of raw JSON objects, which are returned as cleaned Nutrients
    public static List<Nutrient> parseNutrients(List<JsonNutrient> jsonNutrients) {
        List<Nutrient> nutrients = new ArrayList<>();

        for (JsonNutrient nutrientRaw : jsonNutrients) {
            // Skip if nutrient is not enabled, or if field omitted (null)
            if (nutrientRaw.enabled != null && !nutrientRaw.enabled) continue;

            // Copying and cleaning data
            Nutrient nutrient = new Nutrient();

            // Name, icon color
            try {
                nutrient.name = nutrientRaw.name;
                nutrient.icon = getItemByName(nutrientRaw.name, nutrientRaw.icon).getItemStack(); // Create ItemStack
                                                                                                  // used to represent
                                                                                                  // icon
                nutrient.color = Integer.parseUnsignedInt("ff" + nutrientRaw.color, 16); // Convert hex string to int
            } catch (NullPointerException e) {
                Log.fatal("Missing or invalid JSON.  A name, icon, and color are required.");
                throw e;
            }

            // Decay rate multiplier
            // Determined either by global rate, or optional override in nutrient file
            if (nutrientRaw.decay == null) nutrient.decay = Config.decayMultiplier; // Set to global value
            else if (nutrientRaw.decay >= -100 && nutrientRaw.decay <= 100) nutrient.decay = nutrientRaw.decay;
            else {
                nutrient.decay = 0;
                Log.error("Decay rate must be between -100 and 100 (" + nutrient.name + ").");
                continue;
            }

            // Nutrient Visibility
            nutrient.visible = (nutrientRaw.visible == null || nutrientRaw.visible);

            // Food - Ore Dictionary
            if (nutrientRaw.food.oredict != null) nutrient.foodOreDict = nutrientRaw.food.oredict; // Ore dicts remains
                                                                                                   // as strings

            // Food Items
            if (nutrientRaw.food.items != null) {
                for (String fullName : nutrientRaw.food.items) {
                    ItemData data = getItemByName(nutrient.name, fullName, true);
                    if (data == null) continue;

                    // Get item
                    Item item = data.getItem();

                    // Item ID not found, issue warning and skip adding item
                    if (item == null) {
                        if (Config.logMissingFood && Loader.isModLoaded(data.modid)) {
                            Log.warn("Food with nutrients doesn't exist: " + fullName + " (" + nutrient.name + ")");
                        }
                        continue;
                    }

                    // Add to nutrient, or report error
                    ItemStack itemStack = new ItemStack(item, 1, data.metadata);
                    if (AppleCoreAPI.accessor.isFood(itemStack)) {
                        nutrient.foodItems.add(itemStack);
                    } else {
                        Log.warn(data.name + " is not a valid food (" + fullName + ")");
                    }
                }
            }

            // Register nutrient
            nutrients.add(nutrient);
        }

        return nutrients;
    }

    private static ItemData getItemByName(String nutrientName, String fullName, boolean throwException) {
        String modid;
        String name;
        int metadata = 0;
        // Null check input string
        if (fullName == null) {
            Log.fatal(
                "There is a null item in the '" + nutrientName + "' JSON.  Check for a trailing comma in the file.");
            if (throwException) {
                throw new NullPointerException(
                    "There is a null item in the '" + nutrientName
                        + "' JSON.  Check for a trailing comma in the file.");
            }
            return null;
        }

        String[] splitName = fullName.split(":");
        if (splitName.length <= 1) {
            Log.fatal(
                "There is an item missing a modid in the '" + nutrientName
                    + "' JSON. Ensure names are formatted like 'minecraft:golden_apple'");
            if (throwException) {
                throw new NullPointerException(
                    "There is an item missing a modid in the '" + nutrientName
                        + "' JSON. Ensure names are formatted like 'minecraft:golden_apple'");
            }
            return null;
        }
        modid = splitName[0];
        name = splitName[1];
        if (splitName.length > 2) {
            if (NumberUtils.isNumber(splitName[2])) {
                metadata = Integer.decode(splitName[2]);
            } else {
                Log.warn(fullName + " does not contain valid metadata");
                return null;
            }
        }
        return new ItemData(modid, name, metadata);
    }

    private static ItemData getItemByName(String nutrientName, String fullName) {
        return getItemByName(nutrientName, fullName, false);
    }

    private static class ItemData {

        private final String modid, name;
        private final int metadata;

        private ItemData(String modid, String name, int metadata) {
            this.modid = modid;
            this.name = name;
            this.metadata = metadata;
        }

        public Item getItem() {
            return GameRegistry.findItem(modid, name);
        }

        public ItemStack getItemStack() {
            return new ItemStack(getItem(), 1, metadata);
        }
    }
}
