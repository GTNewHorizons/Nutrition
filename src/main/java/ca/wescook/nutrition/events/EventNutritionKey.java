package ca.wescook.nutrition.events;

import net.minecraft.client.Minecraft;

import ca.wescook.nutrition.gui.NutritionGui;
import ca.wescook.nutrition.proxy.ClientProxy;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventNutritionKey {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void keyInput(InputEvent.KeyInputEvent event) {
        if (ClientProxy.keyNutritionGui.isPressed()) {
            Minecraft.getMinecraft()
                .displayGuiScreen(new NutritionGui());
        }
    }

}
