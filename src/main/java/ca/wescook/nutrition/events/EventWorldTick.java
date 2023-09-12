package ca.wescook.nutrition.events;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import ca.wescook.nutrition.data.PlayerDataHandler;
import ca.wescook.nutrition.effects.EffectsManager;
import ca.wescook.nutrition.gui.NutritionGui;
import ca.wescook.nutrition.network.Sync;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.nutrients.NutrientUtils;
import ca.wescook.nutrition.proxy.ClientProxy;
import ca.wescook.nutrition.utility.Config;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventWorldTick {

    private final Map<Pair<EntityPlayer, Boolean>, Integer> playerFoodLevels = new HashMap<>(); // Track food level
    // between ticks
    private int potionCounter = 0; // Count ticks to reapply potion effects

    @SubscribeEvent
    public void serverTickEvent(TickEvent.WorldTickEvent event) {
        // Only run during end phase (post-vanilla)
        if (event.phase != TickEvent.Phase.END) return;

        // Apply decay check each tick
        if (Config.enableDecay) {
            for (EntityPlayer player : event.world.playerEntities) {
                nutritionDecay(player);
            }
        }

        // Reapply potion effects every 5 seconds
        potionTicking(event.world);
    }

    @SideOnly(Side.CLIENT)
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

    private void nutritionDecay(EntityPlayer player) {
        // To prevent client/server conflicts, we use a unique ID that stores both the player and their side
        Pair<EntityPlayer, Boolean> playerSidedID = new ImmutablePair<>(player, player.getEntityWorld().isRemote);

        // Get player food levels
        int foodLevelNew = player.getFoodStats()
            .getFoodLevel(); // Current food level
        Integer foodLevelOld = playerFoodLevels.get(playerSidedID); // Food level last tick

        // If food level has reduced, also lower nutrition
        if (foodLevelOld != null && foodLevelNew < foodLevelOld) {
            int difference = foodLevelOld - foodLevelNew; // Difference in food level

            // Server
            Map<Nutrient, Float> playerNutrition;
            if (!player.getEntityWorld().isRemote) {
                playerNutrition = PlayerDataHandler.getForPlayer(player)
                    .get();
                calculateDecay(playerNutrition, difference);
            } else { // Client
                playerNutrition = ClientProxy.localNutrition.get();
                calculateDecay(playerNutrition, difference);

                // If Nutrition GUI is open, update GUI
                GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
                if (currentScreen instanceof NutritionGui) {
                    ((NutritionGui) currentScreen).redrawLabels();
                }
            }
        }

        // Update for the next pass
        playerFoodLevels.put(playerSidedID, foodLevelNew);
    }

    private void calculateDecay(Map<Nutrient, Float> playerNutrition, int difference) {
        for (Map.Entry<Nutrient, Float> entry : playerNutrition.entrySet()) {
            float decay = (float) (difference * 0.075 * entry.getKey().decay); // Lower number for reasonable starting
            // point, then apply multiplier from
            // config
            entry.setValue(MathHelper.clamp_float(entry.getValue() - decay, 0, 100)); // Subtract decay from nutrient
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
