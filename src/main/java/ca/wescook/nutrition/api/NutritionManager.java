package ca.wescook.nutrition.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import ca.wescook.nutrition.Nutrition;
import ca.wescook.nutrition.network.SPacketNutrients;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.utility.Config;

public final class NutritionManager implements INutritionManager {

    private static final NutritionManager INSTANCE = new NutritionManager();

    private final Table<EntityPlayer, Nutrient, Float> nutrients = HashBasedTable.create();

    private NutritionManager() {/**/}

    public static INutritionManager instance() {
        return INSTANCE;
    }

    @Override
    public float get(EntityPlayer player, Nutrient nutrient) {
        Float value = nutrients.get(player, nutrient);
        return value == null ? Config.startingNutrition : value;
    }

    // Internal-use only
    public Map<Nutrient, Float> getAll(EntityPlayer player) {
        return nutrients.row(player);
    }

    @Override
    public Map<Nutrient, Float> evict(EntityPlayer player) {
        Map<Nutrient, Float> values = new HashMap<>(nutrients.row(player));
        nutrients.row(player)
            .clear();
        return values;
    }

    @Override
    public boolean set(EntityPlayer player, Nutrient nutrient, float value) {
        float oldValue = get(player, nutrient);
        if (oldValue == value) return false;

        nutrients.put(player, nutrient, value);
        if (Nutrition.proxy.isServer()) {
            SPacketNutrients.send(player);
        }

        return true;
    }

    @Override
    public boolean setAll(EntityPlayer player, Map<Nutrient, Float> values) {
        boolean updated = false;

        for (var entry : values.entrySet()) {
            if (!nutrients.contains(player, entry.getKey()) || get(player, entry.getKey()) != entry.getValue()) {
                updated = true;
            }
            nutrients.put(player, entry.getKey(), entry.getValue());
        }

        if (updated) {
            if (Nutrition.proxy.isServer()) {
                SPacketNutrients.send(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean normalize(EntityPlayer player, float toValue, float delta) {
        toValue = MathHelper.clamp_float(toValue, 0, 100);

        // Normalize values towards the provided value
        Map<Nutrient, Float> values = new HashMap<>();
        for (Nutrient nutrient : NutrientList.get()) {
            float currentValue = get(player, nutrient);
            if (currentValue > toValue) {
                values.put(nutrient, MathHelper.clamp_float(Math.max(toValue, currentValue - delta), 0, 100));
            } else if (currentValue < toValue) {
                values.put(nutrient, MathHelper.clamp_float(Math.min(toValue, currentValue + delta), 0, 100));
            }
        }

        return setAll(player, values);
    }

    @Override
    public boolean reset(EntityPlayer player) {
        float newValue = (float) Config.startingNutrition;
        boolean updated = false;

        for (Nutrient nutrient : NutrientList.get()) {
            if (get(player, nutrient) != newValue) updated = true;
            nutrients.put(player, nutrient, newValue);
        }

        if (updated) {
            if (Nutrition.proxy.isServer()) {
                SPacketNutrients.send(player);
            }
            return true;
        }
        return false;
    }
}
