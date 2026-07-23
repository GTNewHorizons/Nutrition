package ca.wescook.nutrition.proxy;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import ca.wescook.nutrition.events.EventNutritionButton;
import ca.wescook.nutrition.events.EventNutritionKey;
import ca.wescook.nutrition.events.EventTooltip;
import ca.wescook.nutrition.utility.Config;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    public static KeyBinding keyNutritionGui;

    @Override
    public void init(FMLInitializationEvent event) {
        if (Config.enableGui) { // If GUI is enabled
            ClientRegistry
                .registerKeyBinding(keyNutritionGui = new KeyBinding("key.nutrition", Keyboard.KEY_NONE, "Nutrition"));
            FMLCommonHandler.instance()
                .bus()
                .register(new EventNutritionKey());
            if (Config.enableGuiButton) {
                MinecraftForge.EVENT_BUS.register(new EventNutritionButton());
            }
        }

        if (Config.enableTooltips) {
            MinecraftForge.EVENT_BUS.register(new EventTooltip()); // Register tooltip event
        }
    }
}
