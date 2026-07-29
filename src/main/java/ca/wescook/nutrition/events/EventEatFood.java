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

/**
 * Class has a complex hierarchy of calls to these events, as depending on how stats are changed, different things need
 * to be done.
 * "Normal food" follows the event order:
 * - FoodStatsAddition -> FoodEaten -> UseItem.Finish
 * <br>
 * "Stat modifying" items such as healing axe, IC2 food cans, talisman of nourishment, etc. follows event order:
 * - FoodStatsAddition -> UseItem.Finish (SOMETIMES, depending on the specific item)
 * <br>
 * However, FoodStatsAddition, the only common event here, does not provide the Food ItemStack, so there is no way to
 * gather nutrients,
 * nor discern if this is a Food or some other direct modification method.
 * <br>
 * As a result, we need to know if stats were modified directly without eating an actual food, so that
 * nutrition values are modified somehow to a "neutral state" by direct-modification methods.
 * <br>
 * This is achieved with a Stack, held in {@link NutritionManager}.
 * Hunger value stat changes are pushed to the stack, then popped when food is eaten. This results in
 * a "normal" food pushing the value, then popping it immediately after in the next event.
 * However, something which directly modifies hunger stat will never pop the change.
 * Those changes will be popped by {@link EventWorldTick#serverTickEvent}
 * at the end of each server game tick.
 */
public class EventEatFood {

    @SubscribeEvent
    public void onFoodStatsChanged(FoodEvent.FoodStatsAddition event) {
        if (event.player.getEntityWorld().isRemote) return;

        int hungerValue = event.foodValuesToBeAdded.hunger;
        if (hungerValue <= 0) return;

        // Set that stats have been changed, but food has not yet been eaten
        ((NutritionManager) NutritionManager.instance()).pushNonFoodHungerChange(event.player, hungerValue);
    }

    @SubscribeEvent
    public void onFoodEaten(FoodEvent.FoodEaten event) {
        if (event.player.getEntityWorld().isRemote) return;

        // Calculate nutrition
        List<Nutrient> foundNutrients = NutrientUtils.getFoodNutrients(event.food);
        float nutritionValue = NutrientUtils.calculateNutrition(event.foodValues, foundNutrients);

        // Add to each nutrient
        NutritionManager.instance()
            .addAll(event.player, foundNutrients, nutritionValue);
    }

    // Handle drinking milk
    @SubscribeEvent
    public void finishUsingItem(PlayerUseItemEvent.Finish event) {
        // Only check against players
        if (!(event.entity instanceof EntityPlayer player)) return;
        if (player.getEntityWorld().isRemote) return;

        if (event.item.getItem() instanceof ItemBucketMilk) {
            // reapply effects on server side only
            EffectsManager.reapplyEffects(player);
            NutritionManager.instance()
                .add(player, NutrientList.getByName("dairy"), 1.5F);
        }
    }
}
