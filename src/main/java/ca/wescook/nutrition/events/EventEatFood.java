package ca.wescook.nutrition.events;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import ca.wescook.nutrition.data.PlayerDataHandler;
import ca.wescook.nutrition.effects.EffectsManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientUtils;
import ca.wescook.nutrition.proxy.ClientProxy;
import ca.wescook.nutrition.utility.Config;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventEatFood {

    // Detect eating cake
    @SubscribeEvent
    public void rightClickBlock(PlayerInteractEvent event) {
        EntityPlayer player = (EntityPlayer) event.entity;

        // Get info
        World world = event.world;
        int x = event.x, y = event.y, z = event.z;
        Block block = world.getBlock(x, y, z);

        // Get out if not cake
        if (!(block instanceof BlockCake)) {
            return;
        }

        // Should we let them eat cake?
        if (player.canEat(false) || Config.allowOverEating) {
            // Calculate nutrition
            ItemStack itemStack = new ItemStack(block);
            List<Nutrient> foundNutrients = NutrientUtils.getFoodNutrients(itemStack);
            float nutritionValue = NutrientUtils.calculateNutrition(itemStack, foundNutrients);

            // todo eventually, once syncing is more tested, simplify this down by changing PlayerDataHandler to return
            // either/or
            // Add to each nutrient
            if (!player.getEntityWorld().isRemote) { // Server
                PlayerDataHandler.getForPlayer(player)
                    .add(foundNutrients, nutritionValue);
            } else { // Client
                ClientProxy.localNutrition.add(foundNutrients, nutritionValue);
            }

            // If full but over-eating, simulate cake eating. Copied from BlockCake
            if (!player.getEntityWorld().isRemote && !player.canEat(false) && Config.allowOverEating) {
                int cakeBites = world.getBlockMetadata(x, y, z) + 1;
                if (cakeBites >= 6) {
                    world.setBlockToAir(x, y, z);
                } else {
                    world.setBlockMetadataWithNotify(x, y, z, cakeBites, 2);
                }
            }
        }
    }

    // Allow food to be consumed regardless of hunger level
    @SubscribeEvent
    public void startUsingItem(PlayerUseItemEvent event) {
        // Only run on server
        EntityPlayer player = (EntityPlayer) event.entity;
        if (player.getEntityWorld().isRemote) return;

        // Interacting with item?
        ItemStack itemStack = event.item;
        if (itemStack == null) return;

        // Is item food?
        Item item = itemStack.getItem();
        if (!(item instanceof ItemFood)) return;

        // If config allows, mark food as edible
        if (Config.allowOverEating) {
            ((ItemFood) item).setAlwaysEdible();
        }
    }

    // Calculate nutrition after finishing eating and reapply effects if appropriate
    @SubscribeEvent
    public void finishUsingItem(PlayerUseItemEvent.Finish event) {
        // Only check against players
        if (!(event.entity instanceof EntityPlayer player)) {
            return;
        }

        // Get ItemStack of eaten food
        ItemStack itemStack = event.item;
        int stackSize = itemStack.stackSize;
        itemStack.stackSize = 1; // Temporarily setting stack size to 1 so .copy works for stack sizes of 0
        ItemStack dummyStack = itemStack.copy(); // Create dummy copy to not affect original item
        itemStack.stackSize = stackSize; // Restore original stack size

        // Apply actions to item
        applyNutrition(player, dummyStack);
        reapplyEffectsFromMilk(player, dummyStack);
    }

    // Add found nutrients to player
    private void applyNutrition(EntityPlayer player, ItemStack itemStack) {
        // Get out if not food item
        if (!(itemStack.getItem() instanceof ItemFood || itemStack.getItem() instanceof ItemBucketMilk)) {
            return;
        }

        // Calculate nutrition
        List<Nutrient> foundNutrients = NutrientUtils.getFoodNutrients(itemStack); // Nutrient list for that food
        float nutritionValue = NutrientUtils.calculateNutrition(itemStack, foundNutrients); // Nutrition value for that
                                                                                            // food

        // Add to each nutrient
        if (!player.getEntityWorld().isRemote) { // Server
            PlayerDataHandler.getForPlayer(player)
                .add(foundNutrients, nutritionValue);
        } else { // Client
            ClientProxy.localNutrition.add(foundNutrients, nutritionValue);
        }
    }

    // If milk clears effects, reapply immediately
    private void reapplyEffectsFromMilk(EntityPlayer player, ItemStack itemStack) {
        // Server only
        if (player.getEntityWorld().isRemote) {
            return;
        }

        // Only continue if milk bucket (curative item)
        if (!(itemStack.getItem() instanceof ItemBucketMilk)) {
            return;
        }

        // Reapply effects
        EffectsManager.reapplyEffects(player);
    }
}
