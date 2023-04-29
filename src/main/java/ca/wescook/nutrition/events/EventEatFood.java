package ca.wescook.nutrition.events;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucketMilk;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import ca.wescook.nutrition.data.PlayerDataHandler;
import ca.wescook.nutrition.effects.EffectsManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.nutrients.NutrientUtils;
import ca.wescook.nutrition.proxy.ClientProxy;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import squeek.applecore.api.food.FoodEvent;

public class EventEatFood {

    @SubscribeEvent
    public void onFoodEaten(FoodEvent.FoodEaten event) {
        // Calculate nutrition
        List<Nutrient> foundNutrients = NutrientUtils.getFoodNutrients(event.food);
        float nutritionValue = NutrientUtils.calculateNutrition(event.foodValues, foundNutrients);

        // Add to each nutrient
        if (!event.player.getEntityWorld().isRemote) { // Server
            PlayerDataHandler.getForPlayer(event.player)
                .add(foundNutrients, nutritionValue);
        } else { // Client
            ClientProxy.localNutrition.add(foundNutrients, nutritionValue);
        }
    }

    // Handle drinking milk
    @SubscribeEvent
    public void finishUsingItem(PlayerUseItemEvent.Finish event) {
        // Only check against players
        if (!(event.entity instanceof EntityPlayer player)) {
            return;
        }

        if (event.item.getItem() instanceof ItemBucketMilk) {
            if (!player.getEntityWorld().isRemote) {
                // reapply effects on server side only
                EffectsManager.reapplyEffects(player);
                PlayerDataHandler.getForPlayer(player)
                    .add(NutrientList.getByName("dairy"), 1.5F);
            } else {
                ClientProxy.localNutrition.add(NutrientList.getByName("dairy"), 1.5F);
            }
        }
    }
}
