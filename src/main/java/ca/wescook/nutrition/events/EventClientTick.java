package ca.wescook.nutrition.events;

import ca.wescook.nutrition.network.Sync;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.nutrients.NutrientUtils;
import ca.wescook.nutrition.proxy.ClientProxy;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class EventClientTick {

    @SubscribeEvent
    public void clientTickEvent(TickEvent.ClientTickEvent event) {
        // Only run during end phase (post-vanilla)
        if (event.phase != TickEvent.Phase.END) return;

        // Update nutrition if some non-food stat effect was applied.
        // Normalize stats towards "50" in all values
        int hungerModified = ClientProxy.getUnappliedHungerValues();
        if (hungerModified > 0) {
            // Use value as if food was actually eaten, which gave this amount of hunger for all nutrients
            float amountToChange = NutrientUtils.getNutrientValue(
                hungerModified,
                NutrientList.get()
                    .size());
            Sync.normalizeOnServer(amountToChange);
        }
    }
}
