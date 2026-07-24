package ca.wescook.nutrition.events;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.event.entity.player.PlayerEvent;

import ca.wescook.nutrition.api.INutritionManager;
import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.utility.Config;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventPlayerDeath {

    // Copy player nutrition when "cloned" (death, teleport from End)
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.entityPlayer.getEntityWorld().isRemote) return;

        INutritionManager manager = NutritionManager.instance();
        Map<Nutrient, Float> oldValues = manager.evict(event.original);

        // On death, apply nutrition penalty
        // This is synced automatically in EventPlayerJoinWorld#EntityJoinWorldEvent
        if (event.wasDeath) {
            Map<Nutrient, Float> newValues = new HashMap<>();

            for (Nutrient nutrient : NutrientList.get()) {
                float value = oldValues != null ? oldValues.get(nutrient) : Config.startingNutrition;

                // If reset is disabled, only reduce to cap when above its value
                if (Config.deathPenaltyReset || value > Config.deathPenaltyMin) {
                    // Subtract death penalty from each nutrient, to cap
                    value = Math.max(Config.deathPenaltyMin, value - Config.deathPenaltyLoss);
                }
                newValues.put(nutrient, value);
            }
            manager.setAll(event.entityPlayer, newValues);
        } else if (oldValues != null) {
            // If not death, re-insert the old values for the (potentially) new
            // player entity, or just cleaning up from the evict call above.
            manager.setAll(event.entityPlayer, oldValues);
        }
    }
}
