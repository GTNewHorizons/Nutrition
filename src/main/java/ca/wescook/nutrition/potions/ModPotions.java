package ca.wescook.nutrition.potions;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class ModPotions {

    private static int nextFreePotionId = 0;

    public static PotionToughness toughness;
    public static PotionMalnourished malnourished;
    public static PotionNourished nourished;

    static final UUID TOUGHNESS_HEALTH = UUID.fromString("d80b5ec3-8cf9-4b74-bc0d-6f3ef7b48b2e");
    static final UUID TOUGHNESS_KNOCKBACK = UUID.fromString("f42431e4-8efc-44d2-8249-fea2a2cb418e");
    static final UUID NOURISHMENT_HEALTH = UUID.fromString("bdafe0c7-5881-4505-802e-e18f6c419554");
    static final UUID MALNOURISHMENT_HEALTH = UUID.fromString("ea9cebf7-7c7a-4a89-a04f-221dab8ffdf7");

    public static void createPotions() {
        // Toughness
        toughness = new PotionToughness(
            findPotionId(),
            new ResourceLocation("nutrition", "textures/potions/toughness.png"));
        toughness.setPotionName("potion.nutrition:toughness");
        toughness.func_111184_a(SharedMonsterAttributes.maxHealth, TOUGHNESS_HEALTH.toString(), 0D, 0);
        toughness.func_111184_a(SharedMonsterAttributes.knockbackResistance, TOUGHNESS_KNOCKBACK.toString(), 0D, 0);

        // Nourished
        nourished = new PotionNourished(
            findPotionId(),
            new ResourceLocation("nutrition", "textures/potions/nourished.png"));
        nourished.setPotionName("potion.nutrition:nourished");
        nourished.func_111184_a(SharedMonsterAttributes.maxHealth, NOURISHMENT_HEALTH.toString(), 0D, 0);

        // Malnourished
        malnourished = new PotionMalnourished(
            findPotionId(),
            new ResourceLocation("nutrition", "textures/potions/malnourished.png"));
        malnourished.setPotionName("potion.nutrition:malnourished");
        malnourished.func_111184_a(SharedMonsterAttributes.maxHealth, MALNOURISHMENT_HEALTH.toString(), 0D, 0);
    }

    private static int findPotionId() {
        while (nextFreePotionId < Potion.potionTypes.length && Potion.potionTypes[nextFreePotionId] != null) {
            nextFreePotionId++;
        }
        return nextFreePotionId;
    }
}
