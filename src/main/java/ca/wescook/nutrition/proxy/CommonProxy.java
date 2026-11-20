package ca.wescook.nutrition.proxy;

import java.util.Collections;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

import ca.wescook.nutrition.nutrients.Nutrient;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class CommonProxy {

    public void init(FMLInitializationEvent event) {}

    public Map<Nutrient, Float> getClientNutrients(EntityPlayer player) {
        return Collections.emptyMap();
    }
}
