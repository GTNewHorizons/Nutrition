package ca.wescook.nutrition.modules.witchery;

import ca.wescook.nutrition.modules.ModuleConfig;
import ca.wescook.nutrition.utility.IModHelper;
import cpw.mods.fml.common.Loader;

public class WitcheryHelper implements IModHelper {

    public static final String WITCHERY = "witchery";
    private static boolean isWitcheryActive = false;

    public static boolean isActive() {
        return isWitcheryActive;
    }

    public void preInit() {
        if (Loader.isModLoaded(WITCHERY) && (ModuleConfig.witcheryModuleActive)) {
            isWitcheryActive = true;
        }
    }

    public void init() {}

    public void postInit() {}
}
