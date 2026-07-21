package ca.wescook.nutrition.events;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import com.emoniph.witchery.common.ExtendedPlayer;

import ca.wescook.nutrition.api.INutritionManager;
import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.utility.Config;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WitcheryEventHandler {

    @SubscribeEvent
    public void livingTick(LivingUpdateEvent event) {
        if (event.entity.worldObj.isRemote) return;

        if ((event.entity instanceof EntityPlayer player)) {
            ExtendedPlayer exPlayer = (ExtendedPlayer) player.getExtendedProperties("WitcheryExtendedPlayer");
            if (exPlayer.isVampire()) {
                INutritionManager manager = NutritionManager.instance();
                Map<Nutrient, Float> newValues = new HashMap<>();

                float maxNutrition = Config.vampireMinNutrition
                    + (Config.vampireMaxNutrition - Config.vampireMinNutrition) * exPlayer.getVampireLevel() / 10;
                float nutritionValue = ((float) exPlayer.getBloodPower() / exPlayer.getMaxBloodPower()) * maxNutrition;

                for (Nutrient nutrient : NutrientList.get()) {
                    if (MathHelper.abs(manager.get(player, nutrient) - nutritionValue) > 0.01f) {
                        newValues.put(nutrient, nutritionValue);
                    }
                }
                manager.setAll(player, newValues);
            }
        }
    }

}
