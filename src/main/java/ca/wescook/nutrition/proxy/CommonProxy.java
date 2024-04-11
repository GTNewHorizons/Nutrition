package ca.wescook.nutrition.proxy;

import ca.wescook.nutrition.modules.ModHelperManager;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        ModHelperManager.preInit();
    }

    public void init(FMLInitializationEvent event) {
        ModHelperManager.init();
    }

    public void postInit(FMLPostInitializationEvent event) {
        ModHelperManager.postInit();
    }

    public boolean isClient() {
        return false;
    }
}
