package ca.wescook.nutrition.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import ca.wescook.nutrition.network.SPacketNutrients;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventPlayerJoinWorld {

    // Sync on first join
    @SubscribeEvent
    public void entityJoinWorldEvent(EntityJoinWorldEvent event) {
        // Server only
        if (event.world.isRemote) return;

        // Only check against players
        if (!(event.entity instanceof EntityPlayer player)) return;

        // Update nutrition on first join, and on death
        SPacketNutrients.send(player);
    }
}
