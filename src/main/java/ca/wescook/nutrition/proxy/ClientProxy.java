package ca.wescook.nutrition.proxy;

import java.util.Stack;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

import ca.wescook.nutrition.data.NutrientManager;
import ca.wescook.nutrition.events.EventNutritionButton;
import ca.wescook.nutrition.events.EventNutritionKey;
import ca.wescook.nutrition.events.EventTooltip;
import ca.wescook.nutrition.utility.Config;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    public static NutrientManager localNutrition; // Holds local copy of data/methods for client-side prediction
    public static KeyBinding keyNutritionGui;

    private static final Stack<Integer> hungerValues = new Stack<>();

    @Override
    public void init(FMLInitializationEvent event) {
        if (Config.enableGui) { // If GUI is enabled
            ClientRegistry.registerKeyBinding(keyNutritionGui = new KeyBinding("key.nutrition", 49, "Nutrition")); // Register
                                                                                                                   // Nutrition
                                                                                                                   // keybind,
                                                                                                                   // default
                                                                                                                   // to
                                                                                                                   // "N"
            MinecraftForge.EVENT_BUS.register(new EventNutritionKey()); // Register key input event to respond to
                                                                        // keybind
            if (Config.enableGuiButton) {
                MinecraftForge.EVENT_BUS.register(new EventNutritionButton()); // Register GUI button event
            }
        }

        if (Config.enableTooltips) {
            MinecraftForge.EVENT_BUS.register(new EventTooltip()); // Register tooltip event
        }
    }

    @Override
    public boolean isClient() {
        return true;
    }

    public static void pushHungerChange(int hungerValue) {
        hungerValues.push(hungerValue);
    }

    public static void popHungerChange() {
        if (!hungerValues.empty()) {
            hungerValues.pop();
        }
    }

    public static int getUnappliedHungerValues() {
        int unapplied = 0;
        while (!hungerValues.empty()) {
            unapplied += hungerValues.pop();
        }
        return unapplied;
    }
}
