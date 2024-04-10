package ca.wescook.nutrition;

import net.minecraftforge.common.MinecraftForge;

import ca.wescook.nutrition.effects.EffectsList;
import ca.wescook.nutrition.events.*;
import ca.wescook.nutrition.modules.witchery.WitcheryHelper;
import ca.wescook.nutrition.modules.witchery.events.WitcheryEventHandler;
import ca.wescook.nutrition.network.ModPacketHandler;
import ca.wescook.nutrition.potions.ModPotions;
import ca.wescook.nutrition.proxy.CommonProxy;
import ca.wescook.nutrition.utility.ChatCommand;
import ca.wescook.nutrition.utility.Config;
import ca.wescook.nutrition.utility.DataImporter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "nutrition", name = "Nutrition", version = Tags.VERSION)
public class Nutrition {

    @Instance
    public static Nutrition instance;

    @SidedProxy(
        clientSide = "ca.wescook.nutrition.proxy.ClientProxy",
        serverSide = "ca.wescook.nutrition.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.registerConfigs(event.getModConfigurationDirectory());
        ModPacketHandler.registerMessages();

        ModPotions.createPotions();
        MinecraftForge.EVENT_BUS.register(new EventPlayerJoinWorld());
        MinecraftForge.EVENT_BUS.register(new EventPlayerDeath());
        MinecraftForge.EVENT_BUS.register(new EventEatFood());
        FMLCommonHandler.instance()
            .bus()
            .register(new EventWorldTick());

        // only register if allow over-eating is true
        if (Config.allowOverEating) {
            MinecraftForge.EVENT_BUS.register(new EventAllowOvereating());
        }
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        if (WitcheryHelper.isActive()) {
            WitcheryEventHandler witcheryEventHandler = new WitcheryEventHandler();
            MinecraftForge.EVENT_BUS.register(witcheryEventHandler);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Nutrition.proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        DataImporter.reload();
        EffectsList.registerEffects();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new ChatCommand());
    }
}
