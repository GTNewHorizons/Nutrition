package ca.wescook.nutrition.api;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import ca.wescook.nutrition.NutritionConfig;
import ca.wescook.nutrition.nutrients.Nutrient;

/**
 * Nutrition data manager. Used to access and modify nutrient values for players.<br>
 * <br>
 * Will automatically sync nutrient data to clients when modified on the server.<br>
 * <br>
 * Client modifications are not recommended, if needed you will need to sync this to the server.
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
     * @return a map of all nutrient values for the player, or null if none are available.
     */
    @Nullable
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
    default boolean setAll(EntityPlayer player, Map<Nutrient, Float> values) {
        return setAll(player, values, true);
    }

    /**
     * Set all the provided nutrient values for a player. Does not need to be an all-encompassing set of values.
     *
     * @param player The player to set nutrient values for.
     * @param values The values to set.
     * @param sync   Whether the data change should be synced to the client.
     * @return true if any nutrient value was changed.
     */
    boolean setAll(EntityPlayer player, Map<Nutrient, Float> values, boolean sync);

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
     * Normalize a player's nutrient values to a specified amount.
     *
     * @param player  The player to normalize nutrient values for.
     * @param toValue The value to normalize to, typically 50.
     * @param delta   The delta to normalize by, typically the hunger/saturation change passed to
     *                {@link ca.wescook.nutrition.nutrients.NutrientUtils#getNutrientValue
     *                NutrientUtils#getNutrientValue}
     * @return true if any of the player's nutrient values were changed.
     */
    boolean normalize(EntityPlayer player, float toValue, float delta);

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
        return set(player, nutrient, NutritionConfig.nutrition.startingNutrition);
    }
}
