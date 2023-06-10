package ca.wescook.nutrition.events;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucketMilk;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import ca.wescook.nutrition.Nutrition;
import ca.wescook.nutrition.data.PlayerDataHandler;
import ca.wescook.nutrition.effects.EffectsManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.nutrients.NutrientUtils;
import ca.wescook.nutrition.proxy.ClientProxy;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import squeek.applecore.api.food.FoodEvent;

/**
 * Class has a complex hierarchy of calls to these events, as depending on how stats are changed, different things need
 * to be done.
 * "Normal food" follows the event order:
 * - FoodStatsAddition -> FoodEaten -> UseItem.Finish
 * <br>
 * "Stat modifying" items such as healing axe, IC2 food cans, etc. follows event order:
 * - FoodStatsAddition -> UseItem.Finish (SOMETIMES, depending on the specific item)
 * <br>
 * However, FoodStatsAddition, the only common event here, does not provide the Food ItemStack, so there is no way to
 * gather nutrients,
 * nor discern if this is a Food or some other direct modification method.
 * <br>
 * As a result, we need to know if stats were modified directly without eating an actual food, so that
 * nutrition values are modified somehow to a "neutral state" by direct-modification methods.
 * <br>
 * This is achieved with the {@link State} enum, which tracks if stats were changed, but food was not eaten.
 * See {@link EventWorldTick#clientTickEvent(TickEvent.ClientTickEvent)} for details on this check.
 */
public class EventEatFood {

    @SubscribeEvent
    public void onFoodStatsChanged(FoodEvent.FoodStatsAddition event) {
        if (Nutrition.proxy.isClient()) {
            // set that stats have been changed, but food has not yet been eaten
            ClientProxy.eatenState = State.STATS_CHANGED;
        }
    }

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
            // set that food has now been eaten
            ClientProxy.eatenState = State.WAITING;
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

    public enum State {
        WAITING, // Nothing happening, no stats changed or food eaten
        STATS_CHANGED // Stats have been changed, food has not yet been eaten (or was not done by food)
    }
}
