package ca.wescook.nutrition.api;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.utility.Config;

/**
 * Nutrition data manager. Used to access and modify nutrient values for players.<br>
 * <br>
 * Will automatically sync nutrient data to clients when modified on the server.<br>
 * <br>
 * Client modifications are not recommended, but if needed, must be manually synced.<br>
 * See {@link ca.wescook.nutrition.network.CPacketNutrientChange CPacketNutrientChange}
 * for manual syncing from client to server.
 *
 * @author serenibyss
 * @since 1.0
 */
public interface INutritionManager {

    /**
     * Get a player's nutrient value.
     *
     * @param player   The player to get a nutrient value for.
     * @param nutrient The nutrient to find a value for.
     * @return the nutrient value.
     */
    float get(EntityPlayer player, Nutrient nutrient);

    /**
     * Get all of a player's nutrient values, while also clearing all nutrition data for this player.<br>
     * <br>
     * <strong>Should only be called from the server!</strong>
     *
     * @param player The player to get nutrient values for.
     * @return a map of all nutrient values for the player.
     */
    Map<Nutrient, Float> evict(EntityPlayer player);

    /**
     * Set a player's single nutrient value.<br>
     * If setting multiple on the server, highly recommended to use {@link #setAll} to avoid unnecessary packets.
     *
     * @param player   The player to update the nutrient value for.
     * @param nutrient The nutrient to update.
     * @param value    The new nutrient value.
     * @return true if the nutrient value was changed.
     */
    boolean set(EntityPlayer player, Nutrient nutrient, float value);

    /**
     * Set all the provided nutrient values for a player. Does not need to be an all-encompassing set of values.
     *
     * @param player The player to set nutrient values for.
     * @param values The values to set.
     * @return true if any nutrient value was changed.
     */
    boolean setAll(EntityPlayer player, Map<Nutrient, Float> values);

    /**
     * Add a value to an existing nutrient.
     *
     * @param player   The player to add nutrient value to.
     * @param nutrient The nutrient to add value to.
     * @param value    The amount of nutrient value to add.
     * @return true if the player's nutrient value was changed.
     */
    default boolean add(EntityPlayer player, Nutrient nutrient, float value) {
        float current = get(player, nutrient);
        float clamp = MathHelper.clamp_float(current + value, 0, 100);
        return set(player, nutrient, clamp);
    }

    /**
     * Remove some value from an existing nutrient.
     *
     * @param player   The player to remove nutrient value from.
     * @param nutrient The nutrient to remove value from.
     * @param value    The amount of nutrient value to remove.
     * @return true if the player's nutrient value was changed.
     */
    default boolean subtract(EntityPlayer player, Nutrient nutrient, float value) {
        float current = get(player, nutrient);
        float clamp = MathHelper.clamp_float(current - value, 0, 100);
        return set(player, nutrient, clamp);
    }

    /**
     * Reset all of a player's nutrient values.
     *
     * @param player The player to reset nutrient values for.
     * @return true if any nutrient value was changed.
     */
    boolean reset(EntityPlayer player);

    /**
     * Reset a player's specific nutrient value.
     *
     * @param player   The player to reset the nutrient value for.
     * @param nutrient The nutrient to reset.
     * @return true if the nutrient value was changed.
     */
    default boolean reset(EntityPlayer player, Nutrient nutrient) {
        return set(player, nutrient, Config.startingNutrition);
    }
}
