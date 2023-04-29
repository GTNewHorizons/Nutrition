package ca.wescook.nutrition.events;

import java.util.List;
import java.util.StringJoiner;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import ca.wescook.nutrition.Tags;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import squeek.applecore.api.AppleCoreAPI;
import squeek.applecore.api.food.FoodValues;

public class EventTooltip {

    @SubscribeEvent
    public void tooltipEvent(ItemTooltipEvent event) {
        ItemStack itemStack = event.itemStack;
        String tooltip = null;

        // Get out if not a food item
        if (!AppleCoreAPI.accessor.isFood(itemStack)) return;

        // Create readable list of nutrients
        StringJoiner stringJoiner = new StringJoiner(", ");
        List<Nutrient> foundNutrients = NutrientUtils.getFoodNutrients(itemStack);
        for (Nutrient nutrient : foundNutrients) // Loop through nutrients from food
            if (nutrient.visible) stringJoiner.add(I18n.format("nutrient." + Tags.MODID + ":" + nutrient.name));
        String nutrientString = stringJoiner.toString();

        // Get nutrition value
        FoodValues foodValues = AppleCoreAPI.accessor.getFoodValuesForPlayer(itemStack, event.entityPlayer);
        float nutritionValue = NutrientUtils.calculateNutrition(foodValues, foundNutrients);

        // Build tooltip
        if (!nutrientString.equals("")) {
            tooltip = I18n.format("tooltip." + Tags.MODID + ":nutrients") + " "
                + EnumChatFormatting.DARK_GREEN
                + nutrientString
                + EnumChatFormatting.DARK_AQUA
                + " ("
                + String.format("%.1f", nutritionValue)
                + "%)";
        }

        // Add to item tooltip
        if (tooltip != null) event.toolTip.add(tooltip);
    }
}
