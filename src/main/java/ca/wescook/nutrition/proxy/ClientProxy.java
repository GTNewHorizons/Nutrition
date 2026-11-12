package ca.wescook.nutrition.proxy;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import ca.wescook.nutrition.NutritionConfig;
import ca.wescook.nutrition.events.EventNutritionButton;
import ca.wescook.nutrition.events.EventNutritionKey;
import ca.wescook.nutrition.events.EventTooltip;
import ca.wescook.nutrition.nutrients.Nutrient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class ClientProxy extends CommonProxy {

    public static KeyBinding keyNutritionGui;

    /**
     * Nutrient values of the local client player
     */
    private final Map<Nutrient, Float> clientNutrients = new HashMap<>();

    @Override
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        if (NutritionConfig.gui.enable) {
            ClientRegistry
                .registerKeyBinding(keyNutritionGui = new KeyBinding("key.nutrition", Keyboard.KEY_NONE, "Nutrition"));
            FMLCommonHandler.instance()
                .bus()
                .register(new EventNutritionKey());
            if (NutritionConfig.gui.enableButton) {
                MinecraftForge.EVENT_BUS.register(new EventNutritionButton());
            }
        }

        if (NutritionConfig.gui.enableTooltips) {
            MinecraftForge.EVENT_BUS.register(new EventTooltip());
        }
    }

    @Override
    public Map<Nutrient, Float> getClientNutrients() {
        return clientNutrients;
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Minecraft.getMinecraft()
            .func_152344_a(clientNutrients::clear);
    }
}
