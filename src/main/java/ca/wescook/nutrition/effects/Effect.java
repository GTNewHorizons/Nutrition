package ca.wescook.nutrition.effects;

import java.util.Arrays;
import java.util.List;

import net.minecraft.potion.Potion;

import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.utility.Log;

// todo clean up unnecessary parts of this class and better comment things, as well as value bounds checking
public class Effect {

    public final String name;
    public final Potion potion;
    public int amplifier = 0;
    public int minimum;
    public int maximum;
    public EnumDetectionType detectionType;
    public List<Nutrient> nutrients = NutrientList.get();
    public int cumulativeModifier = 1;

    private Effect(String name, Potion potion) {
        this.name = name;
        this.potion = potion;
    }

    public enum EnumDetectionType {
        ANY, // Any nutrient may be in the threshold.
        AVERAGE, // The average of all nutrients must be in the threshold.
        ALL, // All nutrients must be in the threshold.
        CUMULATIVE // For each nutrient within the threshold, the amplifier increases by one.
    }

    public static Builder builder(String name, Potion potion) {
        return new Builder(name, potion);
    }

    public static class Builder {

        private final Effect effect;

        private Builder(String name, Potion potion) {
            this.effect = new Effect(name, potion);
        }

        public Builder amplifier(int amplifier) {
            effect.amplifier = amplifier;
            return this;
        }

        public Builder nutrientRange(int minimum, int maximum) {
            effect.minimum = minimum;
            effect.maximum = maximum;
            return this;
        }

        public Builder detectionType(EnumDetectionType detectionType) {
            effect.detectionType = detectionType;
            return this;
        }

        public Builder nutrients(Nutrient... nutrients) {
            effect.nutrients = Arrays.asList(nutrients);
            return this;
        }

        public Builder cumulativeModifier(int cumulativeModifier) {
            if (effect.detectionType != EnumDetectionType.CUMULATIVE) {
                Log.error("Cannot use cumulative modifier if detection type is not cumulative! Skipping call...");
                return this;
            }
            effect.cumulativeModifier = cumulativeModifier;
            return this;
        }

        public Effect build() {
            if (effect.detectionType == null) {
                Log.fatal("Must set a detection type for effect " + effect.name + "!");
                throw new IllegalStateException("Must set a detection type for effect " + effect.name + "!");
            }
            return effect;
        }
    }
}
