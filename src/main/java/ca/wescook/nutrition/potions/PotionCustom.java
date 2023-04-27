package ca.wescook.nutrition.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PotionCustom extends Potion {

    private final ResourceLocation icon;

    PotionCustom(int id, boolean isBadEffect, ResourceLocation icon) {
        super(id, isBadEffect, 0);
        this.icon = icon;
    }

    // Inventory potion rendering
    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        if (mc.currentScreen != null) {
            mc.getTextureManager()
                .bindTexture(icon);
            Gui.func_146110_a(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
        }
    }

    // On-screen HUD rendering
    // @Override
    // @SideOnly(Side.CLIENT)
    // public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
    // Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
    // Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
    // }
}
