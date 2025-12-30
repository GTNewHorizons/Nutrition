package ca.wescook.nutrition.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import com.emoniph.witchery.common.ExtendedPlayer;

import ca.wescook.nutrition.data.NutrientManager;
import ca.wescook.nutrition.data.PlayerDataHandler;
import ca.wescook.nutrition.network.Sync;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Events are subscribed when both witchery is present and the config enable the compatibility
 */
public class WitcheryEventHandler {

    @SubscribeEvent
    public void livingTick(LivingUpdateEvent event) {
        if (event.entity.worldObj.isRemote) {
            return;
        }

        if ((event.entity instanceof EntityPlayer player)) {
            final ExtendedPlayer prop = (ExtendedPlayer) player.getExtendedProperties("WitcheryExtendedPlayer");
            if (prop.isVampire()) {
                NutrientManager manager = PlayerDataHandler.getForPlayer(player);
                if (manager.setVampireFoodAmount(prop)) {
                    Sync.serverRequest(player);
                }
            }
        }
    }

}
