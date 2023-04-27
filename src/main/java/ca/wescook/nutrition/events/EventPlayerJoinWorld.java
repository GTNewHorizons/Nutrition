package ca.wescook.nutrition.events;

import ca.wescook.nutrition.network.Sync;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventPlayerJoinWorld {

    // Sync on first join
    @SubscribeEvent
    public void entityJoinWorldEvent(EntityJoinWorldEvent event) {
        // Only check against players
        if (!(event.entity instanceof EntityPlayer)) return;

        // Server only
        if (event.world.isRemote) return;

        // Update nutrition on first join, and on death
        Sync.serverRequest((EntityPlayer) event.entity);
    }
}
