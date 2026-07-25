package ca.wescook.nutrition.events;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.event.entity.player.PlayerEvent;

import ca.wescook.nutrition.NutritionConfig;
import ca.wescook.nutrition.api.INutritionManager;
import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventPlayerDeath {

    // Copy player nutrition when "cloned" (death, teleport from End)
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.entityPlayer.getEntityWorld().isRemote) return;

        INutritionManager manager = NutritionManager.instance();

        // On death, apply nutrition penalty
        // This is synced automatically in EventPlayerJoinWorld#EntityJoinWorldEvent
        if (event.wasDeath) {
            Map<Nutrient, Float> values = new HashMap<>();

            for (Nutrient nutrient : NutrientList.get()) {
                float value = manager.get(event.original, nutrient);

                // If reset is disabled, only reduce to cap when above its value
                if (NutritionConfig.death.deathPenaltyReset || value > NutritionConfig.death.deathPenaltyMin) {
                    // Subtract death penalty from each nutrient, to cap
                    value = Math
                        .max(NutritionConfig.death.deathPenaltyMin, value - NutritionConfig.death.deathPenaltyLoss);
                }
                values.put(nutrient, value);
            }

            // Remove the old player data, and insert the new player data,
            // considering if the EntityPlayer object has been remade.
            manager.evict(event.original);
            manager.setAll(event.entityPlayer, values);

        } else {
            // If not death, re-insert the old values in case
            // the EntityPlayer object has been remade.
            manager.setAll(event.entityPlayer, manager.evict(event.original));
        }
    }
}
