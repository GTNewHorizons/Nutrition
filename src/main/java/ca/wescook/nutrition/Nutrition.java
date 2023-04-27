package ca.wescook.nutrition;

import ca.wescook.nutrition.capabilities.CapabilityManager;
import ca.wescook.nutrition.events.*;
import ca.wescook.nutrition.gui.ModGuiHandler;
import ca.wescook.nutrition.network.ModPacketHandler;
import ca.wescook.nutrition.potions.ModPotions;
import ca.wescook.nutrition.proxy.IProxy;
import ca.wescook.nutrition.utility.ChatCommand;
import ca.wescook.nutrition.utility.Config;
import ca.wescook.nutrition.utility.DataImporter;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraftforge.common.MinecraftForge;

@Mod(
    modid = Tags.MODID,
    name = Tags.MODNAME,
    version = Tags.VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    acceptableRemoteVersions = "*"
)

public class Nutrition {

    // Create instance of mod
    @Instance
    public static Nutrition instance;

    // Create instance of proxy
    // This will vary depending on if the client or server is running
    @SidedProxy(
        clientSide = "ca.wescook.nutrition.proxy.ClientProxy",
        serverSide = "ca.wescook.nutrition.proxy.ServerProxy"
    )
    public static IProxy proxy;

    // Events
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.registerConfigs(event.getModConfigurationDirectory()); // Load Config file
        ModPacketHandler.registerMessages(); // Register network messages
        CapabilityManager.register(); // Register capability

        ModPotions.createPotions(); // Register custom potions
        MinecraftForge.EVENT_BUS.register(new EventRegistry()); // Register custom potions
        MinecraftForge.EVENT_BUS.register(new EventPlayerJoinWorld()); // Attach capability to player
        MinecraftForge.EVENT_BUS.register(new EventPlayerDeath()); // Player death and warping
        MinecraftForge.EVENT_BUS.register(new EventEatFood()); // Register use item event
        MinecraftForge.EVENT_BUS.register(new EventWorldTick()); // Register update event for nutrition decay and potion effects

        Nutrition.proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(Nutrition.instance, new ModGuiHandler()); // Register GUI handler

        Nutrition.proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        DataImporter.reload(); // Load nutrients and effects

        Nutrition.proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new ChatCommand());
    }
}
