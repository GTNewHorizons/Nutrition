package ca.wescook.nutrition.potions;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

public class PotionNourished extends PotionCustom {

    PotionNourished(int id, ResourceLocation icon) {
        super(id, false, icon);
    }

    // Multiply effect based on amplifier
    @Override
    public double func_111183_a(int amplifier, AttributeModifier modifier) {
        // Increase health
        if (modifier.getID()
            .equals(ModPotions.NOURISHMENT_HEALTH)) {
            return (amplifier + 1); // Half-heart per level
        }

        return 0D;
    }
}
