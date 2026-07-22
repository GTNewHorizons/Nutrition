package ca.wescook.nutrition.data;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.utility.Config;

public class PlayerDataHandler {

    private static final String NBT_NUTRIENT_DATA = "Nutrients";

    public static void saveForPlayer(EntityPlayer player, NBTTagCompound tagCompound) {
        NBTTagCompound playerData = new NBTTagCompound();
        for (Nutrient nutrient : NutrientList.get()) {
            float value = NutritionManager.instance()
                .get(player, nutrient);
            playerData.setFloat(nutrient.name, value);
        }
        tagCompound.setTag(NBT_NUTRIENT_DATA, playerData);
    }

    public static void initializeForPlayer(EntityPlayer player, NBTTagCompound tagCompound) {
        if (tagCompound.hasKey(NBT_NUTRIENT_DATA)) {
            NBTTagCompound nutrientTag = tagCompound.getCompoundTag(NBT_NUTRIENT_DATA);
            Map<Nutrient, Float> values = new HashMap<>();

            for (Nutrient nutrient : NutrientList.get()) {
                float value;
                if (nutrientTag.hasKey(nutrient.name)) {
                    value = nutrientTag.getFloat(nutrient.name);
                } else {
                    value = (float) Config.startingNutrition;
                }
                values.put(nutrient, value);
            }

            NutritionManager.instance()
                .setAll(player, values);
        }
    }
}
