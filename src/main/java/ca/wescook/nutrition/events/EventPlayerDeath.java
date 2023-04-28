package ca.wescook.nutrition.events;

import net.minecraftforge.event.entity.player.PlayerEvent;

import ca.wescook.nutrition.data.NutrientManager;
import ca.wescook.nutrition.data.PlayerDataHandler;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.utility.Config;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventPlayerDeath {

    // Copy player nutrition when "cloned" (death, teleport from End)
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        // Duplicate nutrition capability data on server
        NutrientManager nutritionOld = PlayerDataHandler.getForPlayer(event.original); // Get old nutrition
        NutrientManager nutritionNew = new NutrientManager(nutritionOld.get()); // Create new nutrition

        // On death, apply nutrition penalty
        // This is synced automatically in EventPlayerJoinWorld#EntityJoinWorldEvent
        if (event.wasDeath) {
            for (Nutrient nutrient : NutrientList.get()) {
                // If reset is disabled, only reduce to cap when above its value
                if (Config.deathPenaltyReset || nutritionNew.get(nutrient) > Config.deathPenaltyMin) {
                    // Subtract death penalty from each nutrient, to cap
                    nutritionNew.set(
                        nutrient,
                        Math.max(Config.deathPenaltyMin, nutritionNew.get(nutrient) - Config.deathPenaltyLoss));
                }
            }
        }
        PlayerDataHandler.setForPlayer(event.entityPlayer, nutritionNew, true);
    }
}
