package ca.wescook.nutrition.events;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucketMilk;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.effects.EffectsManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.nutrients.NutrientUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import squeek.applecore.api.food.FoodEvent;

public class EventEatFood {

    @SubscribeEvent
    public void onFoodEaten(FoodEvent.FoodEaten event) {
        if (!event.player.getEntityWorld().isRemote) return;

        // Calculate nutrition
        List<Nutrient> foundNutrients = NutrientUtils.getFoodNutrients(event.food);
        float nutritionValue = NutrientUtils.calculateNutrition(event.foodValues, foundNutrients);

        // Add to each nutrient
        for (Nutrient nutrient : foundNutrients) {
            NutritionManager.instance()
                .add(event.player, nutrient, nutritionValue);
        }
    }

    // Handle drinking milk
    @SubscribeEvent
    public void finishUsingItem(PlayerUseItemEvent.Finish event) {
        // Only check against players
        if (!(event.entity instanceof EntityPlayer player)) return;
        if (!player.getEntityWorld().isRemote) return;

        if (event.item.getItem() instanceof ItemBucketMilk) {
            // reapply effects on server side only
            EffectsManager.reapplyEffects(player);
            NutritionManager.instance()
                .add(player, NutrientList.getByName("dairy"), 1.5F);
        }
    }
}
