package ca.wescook.nutrition.modules.witchery.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import com.emoniph.witchery.common.ExtendedPlayer;

import ca.wescook.nutrition.data.NutrientManager;
import ca.wescook.nutrition.data.PlayerDataHandler;
import ca.wescook.nutrition.modules.witchery.WitcheryHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WitcheryEventHandler {

    public static final PlayerCapabilities genericPlayerCapabilities = new PlayerCapabilities();

    @SubscribeEvent
    public void livingTick(LivingUpdateEvent event) {
        if (!WitcheryHelper.isActive()) {
            return;
        }

        if ((event.entity instanceof EntityPlayer player)) {
            final ExtendedPlayer prop = (ExtendedPlayer) player.getExtendedProperties("WitcheryExtendedPlayer");
            if (prop.isVampire()) {
                NutrientManager nutritionOld = PlayerDataHandler.getForPlayer(player); // Get old nutrition
                NutrientManager nutritionNew = new NutrientManager(nutritionOld.get()); // Create new nutrition
                nutritionNew.vampireFloor();
                PlayerDataHandler.setForPlayer(player, nutritionNew, true);
            }
        }
    }

}
