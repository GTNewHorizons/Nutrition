package ca.wescook.nutrition.events;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import squeek.applecore.api.food.IEdibleBlock;

public class EventAllowOvereating {

    // Allows cake and cake-like blocks to be consumed regardless of hunger level
    @SubscribeEvent
    public void rightClickBlock(PlayerInteractEvent event) {
        // Get info
        World world = event.world;
        int x = event.x, y = event.y, z = event.z;
        Block block = world.getBlock(x, y, z);

        // Get out if not cake (or similar)
        if (block instanceof IEdibleBlock edibleBlock) {
            edibleBlock.setEdibleAtMaxHunger(true);
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

        if (itemStack.getItem() instanceof ItemFood itemFood) {
            itemFood.setAlwaysEdible();
        }
    }
}
