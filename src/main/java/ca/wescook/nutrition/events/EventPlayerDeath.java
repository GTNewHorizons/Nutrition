package ca.wescook.nutrition.events;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.event.entity.player.PlayerEvent;

import ca.wescook.nutrition.Nutrition;
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
        if (!Nutrition.proxy.isServer()) return;

        INutritionManager manager = NutritionManager.instance();
        Map<Nutrient, Float> oldValues = manager.evict(event.original);

        // On death, apply nutrition penalty
        // This is synced automatically in EventPlayerJoinWorld#EntityJoinWorldEvent
        if (event.wasDeath) {
            Map<Nutrient, Float> newValues = new HashMap<>();
            for (Nutrient nutrient : NutrientList.get()) {
                float value = oldValues.get(nutrient);

                // If reset is disabled, only reduce to cap when above its value
                if (Config.deathPenaltyReset || oldValues.get(nutrient) > Config.deathPenaltyMin) {
                    // Subtract death penalty from each nutrient, to cap
                    value = Math.max(Config.deathPenaltyMin, value - Config.deathPenaltyLoss);
                }
                newValues.put(nutrient, value);
            }
            manager.setAll(event.entityPlayer, newValues);
        }
    }
}
