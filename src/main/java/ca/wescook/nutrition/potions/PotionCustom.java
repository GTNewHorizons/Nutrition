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

    // Potion rendering
    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        mc.getTextureManager()
            .bindTexture(icon);
        Gui.func_146110_a(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
    }
}
