package ca.wescook.nutrition.potions;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

public class PotionMalnourished extends PotionCustom {

    PotionMalnourished(int id, ResourceLocation icon) {
        super(id, true, icon);
    }

    // Multiply effect based on amplifier
    @Override
    public double func_111183_a(int amplifier, AttributeModifier modifier) {
        // Reduce health
        if (modifier.getID()
            .equals(ModPotions.MALNOURISHMENT_HEALTH)) {
            return -(amplifier + 1); // Half-heart per level
        }

        return 0D;
    }
}
