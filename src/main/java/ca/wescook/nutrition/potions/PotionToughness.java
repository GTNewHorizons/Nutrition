package ca.wescook.nutrition.potions;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

public class PotionToughness extends PotionCustom {

    PotionToughness(int id, ResourceLocation icon) {
        super(id, false, icon);
    }

    // Multiply effects based on amplifier
    @Override
    public double func_111183_a(int amplifier, AttributeModifier modifier) {
        // Multiply health
        if (modifier.getID()
            .equals(ModPotions.TOUGHNESS_HEALTH)) {
            return (amplifier + 1) * 4D; // 4 = two hearts
        }

        // Multiply armor toughness
        if (modifier.getID()
            .equals(ModPotions.TOUGHNESS_KNOCKBACK)) {
            return (amplifier + 1) * 2D;
        }

        return 0D;
    }
}
