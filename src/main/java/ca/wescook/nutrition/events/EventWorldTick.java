package ca.wescook.nutrition.events;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import ca.wescook.nutrition.NutritionConfig;
import ca.wescook.nutrition.api.INutritionManager;
import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.effects.EffectsManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.nutrients.NutrientUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

public class EventWorldTick {

    // Track food level between ticks
    private final Map<EntityPlayer, Integer> playerFoodLevels = new HashMap<>();
    // Count ticks to reapply potion effects
    private int potionCounter = 0;

    @SubscribeEvent
    public void serverTickEvent(TickEvent.WorldTickEvent event) {
        // Only run during end phase (post-vanilla) and on the server
        if (event.phase != TickEvent.Phase.END) return;
        if (event.side != Side.SERVER) return;

        for (EntityPlayer player : event.world.playerEntities) {
            // Apply decay check each tick
            if (NutritionConfig.decay.enable) {
                nutritionDecay(player);
            }

            // Handle any non-food hunger value changes each tick
            handleNonFoodHungerChanges(player);
        }

        // Reapply potion effects every 5 seconds
        potionTicking(event.world);
    }

    private void handleNonFoodHungerChanges(EntityPlayer player) {
        int hungerChange = ((NutritionManager) NutritionManager.instance()).getNonFoodHungerChange(player);
        if (hungerChange > 0) {
            // Use value as if food was actually eaten, which gave this amount of hunger for all nutrients
            float amountToChange = NutrientUtils.getNutrientValue(
                hungerChange,
                NutrientList.get()
                    .size());

            NutritionManager.instance()
                .normalize(player, 50.0f, amountToChange);
        }
    }

    private void nutritionDecay(EntityPlayer player) {
        // Get player food levels
        int foodLevelNew = player.getFoodStats()
            .getFoodLevel(); // Current food level
        Integer foodLevelOld = playerFoodLevels.get(player); // Food level last tick

        // If food level has reduced, also lower nutrition
        if (foodLevelOld != null && foodLevelNew < foodLevelOld) {
            int difference = foodLevelOld - foodLevelNew; // Difference in food level
            calculateDecay(player, difference);
        }

        // Update for the next pass
        playerFoodLevels.put(player, foodLevelNew);
    }

    // return whether nutrition changed
    private void calculateDecay(EntityPlayer player, int difference) {
        INutritionManager manager = NutritionManager.instance();
        Map<Nutrient, Float> newValues = new HashMap<>();

        for (Nutrient nutrient : NutrientList.get()) {
            // Lower number for reasonable starting point, then apply multiplier from config
            float decay = (float) (difference * 0.075 * nutrient.decay);
            float newValue = MathHelper.clamp_float(manager.get(player, nutrient) - decay, 0, 100);
            if (manager.get(player, nutrient) != newValue) {
                newValues.put(nutrient, newValue);
            }
        }

        if (!newValues.isEmpty()) {
            // If changed, manually update the server. We are directly updating the
            // table and doing this check here instead of letting the api handle
            // this to batch changes in one packet.
            manager.setAll(player, newValues);
        }
    }

    private void potionTicking(World world) {
        if (potionCounter > 110) {
            for (EntityPlayer player : FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .getConfigurationManager().playerEntityList) { // All players on server
                EffectsManager.reapplyEffects(player);
            }
            potionCounter = 0;
        }

        // Only increment on world 0, as this value is global
        if (world.provider.dimensionId == 0) {
            potionCounter++;
        }
    }
}
