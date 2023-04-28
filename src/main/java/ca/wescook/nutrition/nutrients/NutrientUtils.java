package ca.wescook.nutrition.nutrients;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraftforge.oredict.OreDictionary;

import ca.wescook.nutrition.utility.Config;
import ca.wescook.nutrition.utility.Log;
import cpw.mods.fml.common.Loader;
import squeek.applecore.api.food.FoodValues;
import squeek.applecore.api.food.IEdible;

public class NutrientUtils {

    // Returns list of nutrients that food belongs to
    public static List<Nutrient> getFoodNutrients(ItemStack eatingFood) {
        List<Nutrient> nutrientsFound = new ArrayList<>();

        // Loop through nutrients to look for food
        foodSearch: for (Nutrient nutrient : NutrientList.get()) { // All nutrients
            // Search foods
            for (ItemStack listedFood : nutrient.foodItems) { // All foods in that category
                if (listedFood.isItemEqual(eatingFood)) {
                    nutrientsFound.add(nutrient); // Add nutrient
                    continue foodSearch; // Skip rest of search in this nutrient, try others
                }
            }

            // Search ore dictionary
            for (String listedOreDict : nutrient.foodOreDict) { // All ore dicts in that nutrient
                for (ItemStack itemStack : OreDictionary.getOres(listedOreDict)) { // All items that match that oredict
                    // (eg. listAllmilk)
                    if (itemStack.isItemEqual(eatingFood)) { // Our food matches oredict
                        nutrientsFound.add(nutrient); // Add nutrient
                        continue foodSearch; // Skip rest of search in this nutrient, try others
                    }
                }
            }
        }

        return nutrientsFound;
    }

    // Calculate nutrition value for supplied food
    // Requires nutrient list from that food for performance reasons (see getFoodNutrients)
    public static float calculateNutrition(ItemStack itemStack, List<Nutrient> nutrients) {
        // Get item/block
        Item item = itemStack.getItem();

        // Base food value
        int foodValue = 0;
        if (item instanceof ItemFood) {
            if (Loader.isModLoaded("AppleCore")) {
                foodValue = FoodValues.get(itemStack).hunger;
            } else {
                foodValue = ((ItemFood) item).func_150905_g(itemStack); // Number of half-drumsticks food heals
            }
        } else if (Loader.isModLoaded("AppleCore") && item instanceof IEdible edible) {
            FoodValues appleCoreValue = edible.getFoodValues(itemStack);
            if (appleCoreValue != null) foodValue = appleCoreValue.hunger;
        } else if (item instanceof ItemBlock || item instanceof ItemReed) { // Cake, most likely
            foodValue = 2; // Hardcoded value from vanilla
        } else if (item instanceof ItemBucketMilk) {
            foodValue = 4; // Hardcoded milk value
        }

        // Apply multipliers
        float adjustedFoodValue = (float) (foodValue * 0.5); // Halve to start at reasonable starting point
        adjustedFoodValue = adjustedFoodValue * Config.nutritionMultiplier; // Multiply by config value
        float lossPercentage = (float) Config.lossPerNutrient / 100; // Loss percentage from config file
        float foodLoss = (adjustedFoodValue * lossPercentage * (nutrients.size() - 1)); // Lose 15% (configurable) for
        // each nutrient added after the
        // first nutrient

        return Math.max(0, adjustedFoodValue - foodLoss);
    }

    // Verify it meets a valid type
    // Little bit of guesswork in this one...
    public static boolean isValidFood(ItemStack itemStack) {
        Item item = itemStack.getItem();

        // Regular ItemFood
        if (item instanceof ItemFood) {
            return true;
        }

        // Milk Bucket
        if (item instanceof ItemBucketMilk) {
            return true;
        }

        // Cake - Vanilla
        if (item == Items.cake) {
            return true;
        }

        // Cake - Modded
        if (item instanceof ItemReed && Block.getBlockFromItem(item) instanceof BlockCake) {
            return true;
        }

        // AppleCore food
        if (Loader.isModLoaded("AppleCore")) {
            if (item instanceof IEdible edible && edible.getFoodValues(itemStack) != null) {
                return true;
            }
        }

        // add more special cases here if needed to mark as valid foods

        return false;
    }

    // List all foods registered in-game without nutrients
    public static void findRegisteredFoods() {
        for (Object object : Item.itemRegistry) {
            Item item = (Item) object;
            ItemStack itemStack = new ItemStack(item);
            if (isValidFood(itemStack) && getFoodNutrients(itemStack).size() == 0)
                Log.warn("Registered food without nutrients: " + item.getUnlocalizedName());
        }
    }
}
