package ca.wescook.nutrition.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.MathHelper;

import com.emoniph.witchery.common.ExtendedPlayer;

import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.utility.Config;

public class NutrientManager {

    // Stored nutrition for the attached player
    private final Map<Nutrient, Float> nutrition = new HashMap<>();

    public NutrientManager() {
        this(new HashMap<>());
    }

    public NutrientManager(Map<Nutrient, Float> nutrientData) {
        update(nutrientData);
    }

    // Return all nutrients and values
    public Map<Nutrient, Float> get() {
        return nutrition;
    }

    // Return value of specific nutrient
    public Float get(Nutrient nutrient) {
        return nutrition.get(nutrient);
    }

    // Set value of specific nutrient
    public void set(Nutrient nutrient, Float value) {
        nutrition.put(nutrient, value);
    }

    /**
     *
     * @return true if any nutrition change
     */
    public boolean setVampireFoodAmount(ExtendedPlayer extendedPlayer) {
        float maxNutrition = Config.vampireMinNutrition
            + (Config.vampireMaxNutrition - Config.vampireMinNutrition) * extendedPlayer.getVampireLevel() / 10;
        float nutritionValue = ((float) extendedPlayer.getBloodPower() / extendedPlayer.getMaxBloodPower())
            * maxNutrition;

        boolean change = false;
        for (Nutrient nutrient : NutrientList.get()) {
            if (MathHelper.abs(nutrition.get(nutrient) - nutritionValue) > 0.01f) {
                nutrition.put(nutrient, nutritionValue);
                change = true;
            }
        }
        return change;
    }

    // Update all nutrients
    public void set(Map<Nutrient, Float> nutrientData) {
        nutrition.putAll(nutrientData);
    }

    // Increase specific nutrient by amount
    public void add(Nutrient nutrient, float amount) {
        float currentAmount = nutrition.get(nutrient);
        nutrition.put(nutrient, MathHelper.clamp_float(currentAmount + amount, 0, 100));
    }

    // Increase list of nutrients by amount
    public void add(List<Nutrient> nutrientData, float amount) {
        for (Nutrient nutrient : nutrientData) {
            nutrition.put(nutrient, MathHelper.clamp_float(nutrition.get(nutrient) + amount, 0, 100));
        }
    }

    // Decrease specific nutrient by amount
    public void subtract(Nutrient nutrient, float amount) {
        float currentAmount = nutrition.get(nutrient);
        nutrition.put(nutrient, MathHelper.clamp_float(currentAmount - amount, 0, 100));
    }

    // Decrease list of nutrients by amount
    public void subtract(List<Nutrient> nutrientData, float amount) {
        for (Nutrient nutrient : nutrientData) {
            nutrition.put(nutrient, MathHelper.clamp_float(nutrition.get(nutrient) - amount, 0, 100));
        }
    }

    // Reset specific nutrient to default nutrition
    public void reset(Nutrient nutrient) {
        set(nutrient, (float) Config.startingNutrition);
    }

    // Reset all nutrients to default nutrition
    public void reset() {
        for (Nutrient nutrient : nutrition.keySet()) // Loop through player's nutrients
            reset(nutrient);
    }

    public void update() {
        update(nutrition);
    }

    // todo rewrite this!!
    public void update(Map<Nutrient, Float> data) {
        // Copy map by value, not by reference
        Map<Nutrient, Float> nutritionOld = new HashMap<>(data);

        nutrition.clear();
        loop: for (Nutrient nutrient : NutrientList.get()) {
            for (Map.Entry<Nutrient, Float> nutrientOld : nutritionOld.entrySet()) {
                if (nutrient.name.equals(nutrientOld.getKey().name)) {
                    nutrition.put(nutrient, nutrientOld.getValue());
                    continue loop;
                }
            }
            nutrition.put(nutrient, (float) Config.startingNutrition);
        }
    }
}
