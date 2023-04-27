package ca.wescook.nutrition.effects;

import ca.wescook.nutrition.effects.Effect.EnumDetectionType;
import ca.wescook.nutrition.potions.ModPotions;
import net.minecraft.potion.Potion;

import java.util.ArrayList;
import java.util.List;

// Maintains information about effects (name, potion, nutrient conditions)
// Stored client and server-side
public class EffectsList {

    private static final List<Effect> EFFECTS = new ArrayList<>();

    // Return all parsed effects
    public static List<Effect> get() {
        return EFFECTS;
    }

    public static void registerEffects() {
        EFFECTS.add(Effect.builder("mining_fatigue", Potion.digSlowdown)
            .nutrientRange(0, 20)
            .detectionType(EnumDetectionType.AVERAGE)
            .build());

        EFFECTS.add(Effect.builder("toughness", ModPotions.toughness)
            .nutrientRange(90, 100)
            .detectionType(EnumDetectionType.CUMULATIVE)
            .build());

        EFFECTS.add(Effect.builder("strength", Potion.damageBoost)
            .nutrientRange(90, 100)
            .detectionType(EnumDetectionType.AVERAGE)
            .build());

        EFFECTS.add(Effect.builder("weakness", Potion.weakness)
            .nutrientRange(0, 10)
            .detectionType(EnumDetectionType.AVERAGE)
            .build());

        EFFECTS.add(Effect.builder("resistance", Potion.resistance)
            .nutrientRange(80, 100)
            .detectionType(EnumDetectionType.AVERAGE)
            .build());
    }
}
